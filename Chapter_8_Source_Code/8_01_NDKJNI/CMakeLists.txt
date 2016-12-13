cmake_minimum_required(VERSION 3.4.1)

add_library(features SHARED src/main/c/NativeLibAlternate.c )

include_directories(${ANDROID_NDK}/sources/android/cpufeatures )

add_library(cpufeatures STATIC
            ${ANDROID_NDK}/sources/android/cpufeatures/cpu-features.c )

find_library(log-lib log )

target_link_libraries(features cpufeatures ${log-lib} )
