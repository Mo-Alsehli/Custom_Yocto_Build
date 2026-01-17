#include <cerrno>
#include <cstring>
#include <fcntl.h>
#include <fstream>
#include <iostream>
#include <optional>
#include <sstream>
#include <stdexcept>
#include <string>
#include <sys/ioctl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#include <linux/fs.h> // BLKGETSIZE64
#include <cstdint>

#define QEMU_ENV 1

namespace {

enum class Slot { A, B };

struct Plan {
    Slot active;
    Slot target;
    std::string targetDev;   // /dev/mmcblk0p2 or p3 OR /dev/vda2 or vda3
};

#if QEMU_ENV == 0
static const char* kDevA = "/dev/mmcblk0p2";
static const char* kDevB = "/dev/mmcblk0p3";
#else
static const char* kDevA = "/dev/vda2";
static const char* kDevB = "/dev/vda3";
#endif

// ------------------------------------------------------------
// Helpers
// ------------------------------------------------------------

std::string readTextFile(const std::string& path) {
    std::ifstream f(path);
    if (!f.is_open()) {
        throw std::runtime_error("Failed to open " + path + ": " + std::strerror(errno));
    }
    std::ostringstream ss;
    ss << f.rdbuf();
    return ss.str();
}

bool isRoot() {
    return geteuid() == 0;
}

// ------------------------------------------------------------
// Slot detection
// ------------------------------------------------------------

std::optional<Slot> detectActiveSlotFromCmdline(const std::string& cmdline) {
    if (cmdline.find("root=/dev/vda2") != std::string::npos) return Slot::A;
    if (cmdline.find("root=/dev/vda3") != std::string::npos) return Slot::B;
    if (cmdline.find("root=PARTLABEL=rootfsA") != std::string::npos) return Slot::A;
    if (cmdline.find("root=PARTLABEL=rootfsB") != std::string::npos) return Slot::B;
    return std::nullopt;
}

Plan buildPlan(Slot active) {
    Plan p{};
    p.active = active;
    p.target = (active == Slot::A) ? Slot::B : Slot::A;
    p.targetDev = (p.target == Slot::A) ? kDevA : kDevB;
    return p;
}

// ------------------------------------------------------------
// Size checks
// ------------------------------------------------------------

uint64_t fileSizeBytes(const std::string& path) {
    struct stat st{};
    if (stat(path.c_str(), &st) != 0) {
        throw std::runtime_error("stat failed: " + std::string(std::strerror(errno)));
    }
    if (!S_ISREG(st.st_mode)) {
        throw std::runtime_error("Image is not a regular file");
    }
    return static_cast<uint64_t>(st.st_size);
}

uint64_t blockDeviceSizeBytes(const std::string& devPath) {
    int fd = open(devPath.c_str(), O_RDONLY | O_CLOEXEC);
    if (fd < 0) {
        throw std::runtime_error("open(" + devPath + ") failed");
    }
    uint64_t bytes = 0;
    if (ioctl(fd, BLKGETSIZE64, &bytes) != 0) {
        close(fd);
        throw std::runtime_error("BLKGETSIZE64 failed");
    }
    close(fd);
    return bytes;
}

// ------------------------------------------------------------
// Atomic write
// ------------------------------------------------------------

void atomicWriteFile(const std::string& path, const std::string& content) {
    std::string tmp = path + ".tmp";
    int fd = open(tmp.c_str(), O_WRONLY | O_CREAT | O_TRUNC | O_CLOEXEC, 0644);
    if (fd < 0) {
        throw std::runtime_error("open tmp failed");
    }

    ssize_t written = 0;
    while (written < (ssize_t)content.size()) {
        ssize_t n = write(fd, content.data() + written,
                          content.size() - written);
        if (n < 0) {
            close(fd);
            throw std::runtime_error("write failed");
        }
        written += n;
    }

    fsync(fd);
    close(fd);

    if (rename(tmp.c_str(), path.c_str()) != 0) {
        throw std::runtime_error("rename failed");
    }
}

// ------------------------------------------------------------
// EXTlinux handling — SAFE METHOD
// ------------------------------------------------------------

std::string updateExtlinuxDefault(const std::string& extlinux,
                                  const std::string& newDefault) {
    std::istringstream in(extlinux);
    std::ostringstream out;
    std::string line;
    bool replaced = false;

    while (std::getline(in, line)) {
        if (line.rfind("DEFAULT ", 0) == 0) {
            out << "DEFAULT " << newDefault << "\n";
            replaced = true;
        } else {
            out << line << "\n";
        }
    }

    if (!replaced) {
        throw std::runtime_error("extlinux.conf missing DEFAULT line");
    }
    return out.str();
}

std::string labelForSlot(Slot s) {
    return (s == Slot::A) ? "linuxA" : "linuxB";
}

// ------------------------------------------------------------
// Image writer
// ------------------------------------------------------------

void streamWriteImageToBlock(const std::string& imagePath,
                             const std::string& devPath) {
    int in = open(imagePath.c_str(), O_RDONLY | O_CLOEXEC);
    int out = open(devPath.c_str(), O_WRONLY | O_CLOEXEC);

    if (in < 0 || out < 0) {
        throw std::runtime_error("open image/device failed");
    }

    constexpr size_t BUF = 4 * 1024 * 1024;
    std::string buf(BUF, '\0');

    while (true) {
        ssize_t r = read(in, buf.data(), buf.size());
        if (r == 0) break;
        if (r < 0) throw std::runtime_error("read failed");

        ssize_t off = 0;
        while (off < r) {
            ssize_t w = write(out, buf.data() + off, r - off);
            if (w < 0) throw std::runtime_error("write failed");
            off += w;
        }
    }

    fsync(out);
    close(in);
    close(out);
    sync();
}

// ------------------------------------------------------------
// Args
// ------------------------------------------------------------

struct Args {
    std::string image;
    std::string bootconf = "/boot/extlinux/extlinux.conf";
    bool dryRun = false;
};

Args parseArgs(int argc, char** argv) {
    Args a{};
    for (int i = 1; i < argc; ++i) {
        std::string k = argv[i];
        if (k == "--image" && i + 1 < argc) a.image = argv[++i];
        else if (k == "--bootconf" && i + 1 < argc) a.bootconf = argv[++i];
        else if (k == "--dry-run") a.dryRun = true;
        else {
            throw std::runtime_error("Unknown argument");
        }
    }
    if (a.image.empty()) throw std::runtime_error("Missing --image");
    return a;
}

// ------------------------------------------------------------
// MAIN
// ------------------------------------------------------------

} // namespace

int main(int argc, char** argv) {
    try {
        if (!isRoot()) {
            std::cerr << "ERROR: must run as root\n";
            return 10;
        }

        Args args = parseArgs(argc, argv);

        auto activeOpt = detectActiveSlotFromCmdline(
            readTextFile("/proc/cmdline"));
        if (!activeOpt) {
            std::cerr << "ERROR: cannot detect active slot\n";
            return 20;
        }

        Plan plan = buildPlan(*activeOpt);

        std::cerr << "Active slot: " << ((*activeOpt == Slot::A) ? "A" : "B") << "\n";
        std::cerr << "Target device: " << plan.targetDev << "\n";

        if (fileSizeBytes(args.image) > blockDeviceSizeBytes(plan.targetDev)) {
            std::cerr << "ERROR: image too large\n";
            return 30;
        }

        std::string extlinux = readTextFile(args.bootconf);
        std::string newDefault = labelForSlot(plan.target);
        std::string updated = updateExtlinuxDefault(extlinux, newDefault);

        if (args.dryRun) {
            std::cerr << "DRY RUN: would set DEFAULT " << newDefault << "\n";
            return 0;
        }

        std::cerr << "Writing image...\n";
        streamWriteImageToBlock(args.image, plan.targetDev);

        std::cerr << "Updating extlinux DEFAULT to " << newDefault << "\n";
        atomicWriteFile(args.bootconf, updated);

        std::cerr << "SUCCESS: OTA applied, reboot required\n";
        return 0;

    } catch (const std::exception& e) {
        std::cerr << "FATAL: " << e.what() << "\n";
        return 50;
    }
}
