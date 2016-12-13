//JNI APIs, was included by our custom header before
#include <jni.h>

#include <android/log.h>
#include <cpu-features.h>

static jint native_getCpuCount(JNIEnv *env, jclass clazz)
{
    return android_getCpuCount();
}

static jstring native_getCpuFamily(JNIEnv *env, jclass clazz)
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

//Construct a table mapping Java method signatures to native function pointers
static JNINativeMethod method_table[] = {
	{ "getCpuCount", "()I", (void *) native_getCpuCount },
	{ "getCpuFamily", "()Ljava/lang/String;", (void *) native_getCpuFamily }
};

//Use the OnLoad initializer to register the method table with the runtime
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env;
	if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) {
		return JNI_ERR;
	} else {
		jclass clazz = (*env)->FindClass(env,
				"com/androidrecipes/ndkjni/NativeLib");
		if (clazz) {
			jint ret = (*env)->RegisterNatives(env, clazz, method_table,
					sizeof(method_table) / sizeof(method_table[0]));
			if (ret == 0) {
				return JNI_VERSION_1_6;
			}
		}
		return JNI_ERR;
	}
}