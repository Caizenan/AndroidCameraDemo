//
// Created by 蔡泽南 on 2021/11/10.
//
#ifndef SML_LOG_H
#define SML_LOG_H

#ifdef ANDROID
#include <android/log.h>
#define SML_LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"SML",__VA_ARGS__)
#define SML_LOGI(...) __android_log_print(ANDROID_LOG_INFO,"SML",__VA_ARGS__)
#define SML_LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"SML",__VA_ARGS__)
#else
#define SML_LOGD(...) printf("SML",__VA_ARGS__)
#define SML_LOGI(...) printf("SML",__VA_ARGS__)
#define SML_LOGE(...) printf("SML",__VA_ARGS__)

#endif

#endif //SML_LOGD
