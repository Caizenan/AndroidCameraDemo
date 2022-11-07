//
// Created by 蔡泽南 on 2021/12/7.
//

#include <ext.hpp>
#include "SkyBox.h"
#include "sml_log.hpp"
#include "glutils.h"
#include <chrono>

using namespace std::chrono;

SkyBox::SkyBox(){

}

SkyBox::~SkyBox(){
    dealloc();
    if(renderThread != nullptr){
        running = false;
        delete renderThread;
        renderThread = nullptr;
    }
}

bool SkyBox::prepare(void* win){
    eglEnv = new EGLCore();
    if(nullptr != eglEnv){
        if(!eglEnv->create(CONFIG_PIXEL_BUFFER,shareContext,win)){
            SML_LOGE("SkyBox-EGL环境创建失败");
            return false;
        }
    }else{
        SML_LOGE("SkyBox-EGLCore为空");
        return false;
    }
    resetRenderSize(left,top,width,height);
    SML_LOGD("SkyBox-创建渲染线程");
    renderThread = new std::thread(&SkyBox::onDraw, this);
    running = true;
    renderThread->detach();
    return true;

}

bool SkyBox::initGLEnv(){
    vertShader = 0;
    fragShader = 0;
    program = 0;
    int ret = 0;
    if((ret = GLUtils::initShaders(vertShader,fragShader,SKYBOX_VERTEX_SHADER,SKYBOX_FRAGMENT_SHADER)) < 0){
        SML_LOGE("SkyBox-编译shader失败");
        dealloc();
        return false;
    }
    if((ret = GLUtils::useProgram(program,vertShader,fragShader)) < 0){
        SML_LOGE("SkyBox-渲染程序链接失败");
        dealloc();
        return false;
    }

    a_positionLocation = glGetAttribLocation(program,"a_position");

    modelMat = glm::mat4(1.0f);
    viewMat = glm::mat4(1.0f);
    projectionMat = glm::mat4(1.0f);

    viewMat = glm::translate(viewMat,glm::vec3(0.0f,0.0f,-3.0f));
    projectionMat = glm::perspective(glm::radians(45.0f), 1.0f, 0.1f, 100.0f);

    u_modelMatLocation = glGetUniformLocation(program,"u_modelMat");
    u_viewMatLocation = glGetUniformLocation(program,"u_viewMat");
    u_projectionMatLocation = glGetUniformLocation(program,"u_projectionMat");

    glGenBuffers(1,&skyBoxVBO);
    glBindBuffer(GL_ARRAY_BUFFER,skyBoxVBO);
    glBufferData(GL_ARRAY_BUFFER,sizeof(_vertices),_vertices,GL_STATIC_DRAW);

    glGenFramebuffers(1,&skyBoxFBO);

    glEnable(GL_DEPTH_TEST);
    return true;
}

void SkyBox::render() {

    static auto time = steady_clock::now();
    static float angle = 0.0f;

    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glUseProgram(program);

    glVertexAttribPointer(0, 3, GL_FLOAT, 0, 0, (void*)0);
    glEnableVertexAttribArray(0);

    auto duration = duration_cast<milliseconds>(steady_clock::now() - time);

    if(duration.count() >= 200){
        time = steady_clock::now();
        angle += 5.0f;
        if(angle >= 360)
            angle = 0.0f;
        modelMat = glm::rotate(modelMat,glm::radians(angle),glm::vec3(1.0f,0.0f,1.0f));
    }

    glUniformMatrix4fv(u_modelMatLocation, 1, false, glm::value_ptr(modelMat));
    glUniformMatrix4fv(u_viewMatLocation, 1, false, glm::value_ptr(viewMat));
    glUniformMatrix4fv(u_projectionMatLocation, 1, false, glm::value_ptr(projectionMat));

    glDrawArrays(GL_TRIANGLES,0,36);

    glDisableVertexAttribArray(a_positionLocation);

    glUseProgram(0);
}

void SkyBox::onDraw() {
    if(!eglEnv->makeCurrent()){
        SML_LOGE("SkyBox-EGL环境与线程绑定失败");
    }
    SML_LOGD("SkyBox-渲染线程启动");
    if(!initGLEnv()){
        SML_LOGE("SkyBox-渲染程序初始化失败");
    }
    for(;;){
        if(!running){
            return;
        }
        SML_LOGD("SkyBox-进行一帧渲染");
        render();
        eglEnv->swapBuffers();
    }
}

void SkyBox::resetRenderSize(int left, int top, int width, int height) {
    this->left = left;
    this->top = top;
    this->width = width;
    this->height = height;
    SML_LOGD("SkyBox-重置视口大小");
}

void SkyBox::dealloc() {
    if (vertShader)
        glDeleteShader(vertShader);

    if (fragShader)
        glDeleteShader(fragShader);

    if (program) {
        glDeleteProgram(program);
        program = 0;
    }
}