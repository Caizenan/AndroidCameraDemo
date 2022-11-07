//
// Created by 蔡泽南 on 2021/12/8.
//
#include "../include/SkyBoxController.h"
#include "../include/SkyBox.h"
#include "android/native_window_jni.h"
#include "android/native_window.h"
#ifdef __cplusplus
extern "C" {
#endif
static const char *const pClassPathName = "com/example/cameralib/SkyBoxController";
SkyBox* skyBox;
JNIEXPORT jboolean JNICALL skyBoxInit
        (JNIEnv *env, jobject obj, jobject surface){
    ANativeWindow *anwin = ANativeWindow_fromSurface(env, surface);
    skyBox = new SkyBox();
    return skyBox->prepare(anwin);
}

JNIEXPORT void JNICALL skyBoxRenderResetSize
(JNIEnv *env, jobject obj, jint left, jint top, jint width, jint height){
    skyBox->resetRenderSize(left,top,width,height);
}

JNIEXPORT void JNICALL skyBoxRelease
(JNIEnv *, jobject) {
    if(skyBox != nullptr){
        delete skyBox;
        skyBox = nullptr;
    }
}

const static JNINativeMethod gMethods[] = {
        {"skyBoxInit",          "(Ljava/lang/Object;)Z",        (void *) skyBoxInit},
        {"skyBoxRenderResetSize",     "(IIII)V",               (void *) skyBoxRenderResetSize},
        {"skyBoxRelease",          "()V",                   (void *) skyBoxRelease},
};

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;
    // 判断是否正确
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return result;
    }

    jclass jClassName = env->FindClass(pClassPathName);
    //开始注册
    jint ret = env->RegisterNatives(jClassName, gMethods, 3);
    //如果注册失败，打印日志
    if (ret != JNI_OK) {
        __android_log_print(ANDROID_LOG_DEBUG, "JNITag", "jni_register Error");
        return -1;
    }
    return JNI_VERSION_1_6;
}
#ifdef __cplusplus
}
#endif