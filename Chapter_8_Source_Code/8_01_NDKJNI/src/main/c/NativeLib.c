#include "NativeLib.h"

#include <android/log.h>
#include <cpu-features.h>

JNIEXPORT jint JNICALL Java_com_androidrecipes_ndkjni_NativeLib_getCpuCount
  (JNIEnv *env, jclass clazz)
{
    return android_getCpuCount();
}

JNIEXPORT jstring JNICALL Java_com_androidrecipes_ndkjni_NativeLib_getCpuFamily
  (JNIEnv *env, jclass clazz)
{
    AndroidCpuFamily family = android_getCpuFamily();
    switch (family)
    {
        case ANDROID_CPU_FAMILY_ARM:
            return (*env)->NewStringUTF(env, "ARM (32-bit)");
        case ANDROID_CPU_FAMILY_X86:
            return (*env)->NewStringUTF(env, "x86 (32-bit)");
        case ANDROID_CPU_FAMILY_MIPS:
            return (*env)->NewStringUTF(env, "MIPS (32-bit)");
        case ANDROID_CPU_FAMILY_ARM64:
            return (*env)->NewStringUTF(env, "ARM (64-bit)");
        case ANDROID_CPU_FAMILY_X86_64:
            return (*env)->NewStringUTF(env, "x86 (64-bit)");
        case ANDROID_CPU_FAMILY_MIPS64:
            return (*env)->NewStringUTF(env, "MIPS (64-bit)");
        case ANDROID_CPU_FAMILY_UNKNOWN:
        default:
            return (*env)->NewStringUTF(env, "Vaporware");
    }
}