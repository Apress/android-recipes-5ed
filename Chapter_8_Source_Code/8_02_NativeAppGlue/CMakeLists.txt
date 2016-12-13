cmake_minimum_required(VERSION 3.4.1)

add_library(native-activity SHARED src/main/c/activity.c )

include_directories(${ANDROID_NDK}/sources/android/native_app_glue )

add_library(native_app_glue  STATIC
            ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c )

find_library(log-lib log )

find_library(android android )

find_library(egl-lib EGL )

find_library(gles-lib GLESv1_CM )

target_link_libraries(native-activity native_app_glue
                      ${log-lib} ${android} ${egl-lib} ${gles-lib} )
