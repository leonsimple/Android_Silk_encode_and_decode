cmake_minimum_required(VERSION 3.4.1)
project(com.example.administrator.myapplication)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
include_directories("${ANDROID_NDK}/platforms/android-21/arch-arm/usr/include")
include_directories("include")
include_directories("libmp3lame")

file(GLOB foo_sources decoder.cpp *.c *.h include/*.h libmp3lame/*.h libmp3lame/*.c)

set(SOURCE_FILES ${foo_sources})
add_executable(com.example.administrator.myapplication ${SOURCE_FILES})