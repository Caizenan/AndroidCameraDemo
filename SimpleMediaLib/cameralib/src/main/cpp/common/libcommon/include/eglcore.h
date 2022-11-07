//
// Created by 蔡泽南 on 2021/11/9.
//

#ifndef ANDROID_EGL_CORE_H_
#define ANDROID_EGL_CORE_H_

#include <android/native_window.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <KHR/khrplatform.h>
#include <android/log.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, "Camera", __VA_ARGS__)

static EGLint CONFIG_PIXEL_BUFFER[] = {EGL_RED_SIZE,8,
                                       EGL_GREEN_SIZE,8,
                                       EGL_BLUE_SIZE,8,
                                       EGL_SURFACE_TYPE,EGL_WINDOW_BIT,
                                       EGL_NONE};

class EGLCore {
public:

    EGLCore();
    virtual ~EGLCore();


    bool create(EGLint* attribs,EGLContext sharedContext,void* win);

    EGLSurface createWindowSurface(ANativeWindow* _window);
    EGLSurface createOffscreenSurface(int width, int height);

    bool makeCurrent();

    void doneCurrent();

    bool swapBuffers();

    int querySurface(EGLSurface surface, int what);

    int setPresentationTime(EGLSurface surface, khronos_stime_nanoseconds_t nsecs);

    void releaseSurface(EGLSurface eglSurface);

    void release();

    EGLContext getContext();
    EGLDisplay getDisplay();
    EGLConfig getConfig();

private:
    EGLDisplay eglDisplay;
    EGLConfig eglConfig;
    EGLContext eglContext;
    EGLSurface eglSurface;
};
#endif // ANDROID_EGL_CORE_H_
