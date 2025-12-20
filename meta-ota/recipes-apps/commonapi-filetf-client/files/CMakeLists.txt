cmake_minimum_required(VERSION 3.13)
project(Simple-FileTransfer)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread -Wall -O0")


find_package(CommonAPI REQUIRED CONFIG)
find_package(CommonAPI-SomeIP REQUIRED CONFIG)
find_package(vsomeip3 REQUIRED)
find_package(Boost COMPONENTS system thread REQUIRED)


include_directories(
    ${CMAKE_SOURCE_DIR}/src-gen/core
    ${CMAKE_SOURCE_DIR}/src-gen/someip
    ${COMMONAPI_INCLUDE_DIRS}
    ${COMMONAPI_SOMEIP_INCLUDE_DIRS}
    ${VSOMEIP_INCLUDE_DIRS}
    ${Boost_INCLUDE_DIRS}
)

file(GLOB CORE_GEN ${CMAKE_SOURCE_DIR}/src-gen/core/v0/filetransfer/example/*.cpp)
file(GLOB SOMEIP_GEN ${CMAKE_SOURCE_DIR}/src-gen/someip/v0/filetransfer/example/*.cpp)


add_executable(FileTransferClient
    FileTransferClient.cpp
    ${CORE_GEN}
    ${SOMEIP_GEN}
)

target_link_libraries(FileTransferClient
    CommonAPI
    CommonAPI-SomeIP
    vsomeip3
    ${Boost_LIBRARIES}
)
