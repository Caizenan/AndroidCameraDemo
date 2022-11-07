//
// Created by 蔡泽南 on 2021/11/9.
//
#include "../include/SmlCameraPreviewController.h"
#include "../include/camera_preview_renderer.h"
#include "android/native_window_jni.h"
#include "android/native_window.h"

#ifdef __cplusplus
extern "C" {
#endif
static const char *const pClassPathName = "com/example/cameralib/SmlCameraPreviewController";
CameraPreviewRenderer *cameraPreviewRenderer;

JNIEXPORT jboolean JNICALL cameraRenderInit
        (JNIEnv *env, jobject obj, jobject surface, jint left, jint top, jint width, jint height){
    cameraPreviewRenderer = new CameraPreviewRenderer();
    ANativeWindow *anwin = ANativeWindow_fromSurface(env, surface);
    if (cameraPreviewRenderer->prepare(anwin,left,top,width,height)) {
        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}

JNIEXPORT void JNICALL cameraRenderResetSize
        (JNIEnv *env, jobject obj, jint left, jint top, jint width, jint height) {
    cameraPreviewRenderer->resetRenderSize(left, top, width, height);
}

JNIEXPORT void JNICALL cameraRenderStop
        (JNIEnv *env, jobject obj) {
}

JNIEXPORT void JNICALL requestCameraRender
        (JNIEnv *env, jobject obj, jbyteArray data, jlong len, jlong preWidth, jlong preHeight){
    unsigned char *pAddress = (unsigned char *)env->GetByteArrayElements(data, NULL);
    cameraPreviewRenderer->draw(pAddress,len,preWidth,preHeight);
}

const static JNINativeMethod gMethods[] = {
        {"cameraRenderInit",          "(Ljava/lang/Object;IIII)Z", (void *) cameraRenderInit},
        {"cameraRenderResetSize",     "(IIII)V",               (void *) cameraRenderResetSize},
        {"cameraRenderStop",          "()V",                   (void *) cameraRenderStop},
        {"requestCameraRender", "([BJJJ)V",                 (void *) requestCameraRender},
};

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;
    // 判断是否正确
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return result;
    }

    jclass jClassName = env->FindClass(pClassPathName);
    //开始注册
    jint ret = env->RegisterNatives(jClassName, gMethods, 4);
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