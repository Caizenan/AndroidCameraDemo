//
// Created by 蔡泽南 on 2021/11/9.
//
#include "eglcore.h"
#include "sml_log.hpp"

#define LOG_TAG "EGLCore"

EGLCore::EGLCore() {
    eglDisplay = EGL_NO_DISPLAY;
    eglContext = EGL_NO_CONTEXT;
}

EGLCore::~EGLCore() {
}

void EGLCore::release() {
    eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    LOGI("after eglMakeCurrent...");
    eglDestroyContext(eglDisplay, eglContext);
    LOGI("after eglDestroyContext...");
    eglDisplay = EGL_NO_DISPLAY;
    eglContext = EGL_NO_CONTEXT;
}

void EGLCore::releaseSurface(EGLSurface eglSurface) {
    eglDestroySurface(eglDisplay, eglSurface);
    eglSurface = EGL_NO_SURFACE;
}

EGLContext EGLCore::getContext(){
    LOGI("return EGLCore getContext...");
    return eglContext;
}

EGLDisplay EGLCore::getDisplay(){
    return eglDisplay;
}

EGLConfig EGLCore::getConfig(){
    return eglConfig;
}

EGLSurface EGLCore::createWindowSurface(ANativeWindow* _window) {
    EGLSurface surface = NULL;
    EGLint format;
    if (!eglGetConfigAttrib(eglDisplay, eglConfig, EGL_NATIVE_VISUAL_ID, &format)) {
        //LOGE("eglGetConfigAttrib() returned error %d", eglGetError());
        release();
        return surface;
    }
    ANativeWindow_setBuffersGeometry(_window, 0, 0, format);
    if (!(surface = eglCreateWindowSurface(eglDisplay, eglConfig, _window, 0))) {
        //LOGE("eglCreateWindowSurface() returned error %d", eglGetError());
    }
    return surface;
}

EGLSurface EGLCore::createOffscreenSurface(int width, int height) {
    EGLSurface surface;
    EGLint PbufferAttributes[] = { EGL_WIDTH, width, EGL_HEIGHT, height, EGL_NONE, EGL_NONE };
    if (!(surface = eglCreatePbufferSurface(eglDisplay, eglConfig, PbufferAttributes))) {
        //LOGE("eglCreatePbufferSurface() returned error %d", eglGetError());
    }
    return surface;
}

int EGLCore::setPresentationTime(EGLSurface surface, khronos_stime_nanoseconds_t nsecs) {
    //pfneglPresentationTimeANDROID(eglDisplay, surface, nsecs);
}

int EGLCore::querySurface(EGLSurface surface, int what) {
    int value = -1;
    eglQuerySurface(eglDisplay, surface, what, &value);
    return value;
}

bool EGLCore::swapBuffers() {
    return eglSwapBuffers(eglDisplay, eglSurface);
}

bool EGLCore::makeCurrent() {
    return eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
}

void EGLCore::doneCurrent() {
    eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
}

bool EGLCore::create(EGLint* attribs,EGLContext sharedContext,void* win) {

    ANativeWindow *anwin = (ANativeWindow*)win;

    if ((eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY)) == EGL_NO_DISPLAY) {
        SML_LOGE("eglGetDisplay() returned error %d", eglGetError());
        return false;
    }
    if (!eglInitialize(eglDisplay, 0, 0)) {
        //LOGE("eglInitialize() returned error %d", eglGetError());
        return false;
    }

    EGLint numConfigs;
    if (!eglChooseConfig(eglDisplay, attribs, &eglConfig, 1, &numConfigs)) {
        SML_LOGE("eglChooseConfig() returned error %d", eglGetError());
        release();
        return false;
    }

    eglSurface = eglCreateWindowSurface(eglDisplay,eglConfig,anwin,NULL);

    EGLint eglContextAttributes[] = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE };
    //nullptr == sharedContext ? EGL_NO_CONTEXT : sharedContext
    if (!(eglContext = eglCreateContext(eglDisplay, eglConfig,
                                        EGL_NO_CONTEXT, eglContextAttributes))) {
        SML_LOGE("eglCreateContext() returned error %d", eglGetError());
        release();
        return false;
    }


    return true;
}
