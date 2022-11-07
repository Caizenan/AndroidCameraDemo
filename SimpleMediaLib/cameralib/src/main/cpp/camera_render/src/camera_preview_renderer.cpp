//
// Created by 蔡泽南 on 2021/11/9.
//
#include "sml_log.hpp"
#include "camera_preview_renderer.h"
#include <ext.hpp>
#include "iostream"


using namespace std;

CameraPreviewRenderer::CameraPreviewRenderer() {

}
CameraPreviewRenderer::~CameraPreviewRenderer() {
    if(renderThread != nullptr){
        //关闭线程
        running = false;
        delete renderThread;
        renderThread = nullptr;
    }
    //释放渲染所需对象
    dealloc();
}

bool CameraPreviewRenderer::prepare(void* win,int left,int top,int width,int height){
    eglEnv = new EGLCore();
    if(eglEnv != nullptr){
        if(!eglEnv->create(CONFIG_PIXEL_BUFFER, shareContext,win)){
            SML_LOGE("native-EGL环境创建失败");
            return false;
        }
    }else{
        SML_LOGE("native-EGLCore为空");
        return false;
    }
    resetRenderSize(left,top,width,height);
    SML_LOGD("native-创建渲染线程");
    renderThread = new std::thread(&CameraPreviewRenderer::onDraw,this);
    running = true;
    renderThread->detach();
    return true;
}

void CameraPreviewRenderer::render() {
    //glViewport(_backingLeft,_backingTop,_backingWidth,_backingHeight);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glUseProgram(program);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D,y_textureId);
    glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,preViewWidth,preViewHeight,0,
                 GL_LUMINANCE, GL_UNSIGNED_BYTE, data);
    glUniform1i(y_textureLocation, 0);

    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D,uv_textureId);
    glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE_ALPHA,preViewWidth>>1,preViewHeight>>1,0,
                 GL_LUMINANCE_ALPHA, GL_UNSIGNED_BYTE, data+(preViewWidth*preViewHeight));
    glUniform1i(uv_textureLoaction, 1);

    glVertexAttribPointer(a_position, 4, GL_FLOAT, 0, 0, _vertices);
    glEnableVertexAttribArray(a_position);

    glVertexAttribPointer(a_texCoord, 2, GL_FLOAT, 0, 0, _texCoords);
    glEnableVertexAttribArray(a_texCoord);

    glUniformMatrix4fv(u_cameraMat,1,false,glm::value_ptr(cameraMat));


    glDrawArrays(GL_TRIANGLE_STRIP,0,4);

    glDisableVertexAttribArray(a_position);
    glDisableVertexAttribArray(a_texCoord);

    //解绑定纹理
    glBindTexture(GL_TEXTURE_2D,0);
    glBindTexture(GL_TEXTURE_2D,0);
    //停用程序
    glUseProgram(0);
}

void CameraPreviewRenderer::onDraw() {
    if(!eglEnv->makeCurrent()){
        SML_LOGE("native-EGL环境与线程绑定失败");
    }
    SML_LOGD("native-渲染线程启动");
    if(!init()){
        SML_LOGE("渲染程序初始化失败");
    }
    for(;;){
        if(!running){
            return;
        }
        std::unique_lock < std::mutex > uniqlock(mutex);
        condition.wait(uniqlock);
        SML_LOGD("native-进行一帧渲染");
        render();
        eglEnv->swapBuffers();
    }
}

bool CameraPreviewRenderer::init(){
    vertShader = 0;
    fragShader = 0;
    program = 0;
    int ret = 0;
    if((ret = GLUtils::initShaders(vertShader,fragShader,CAMERA_PREVIEW_VERTEX_SHADER,CAMERA_PREVIEW_FRAG_SHADER)) < 0){
        SML_LOGE("native-编译shader失败");
        dealloc();
        return false;
    }
    if((ret = GLUtils::useProgram(program,vertShader,fragShader)) < 0){
        SML_LOGE("native-渲染程序链接失败");
        dealloc();
        return false;
    }

    a_position = glGetAttribLocation(program,"a_positionLocation");
    a_texCoord = glGetAttribLocation(program,"a_texCoord");
    y_textureLocation = glGetUniformLocation(program, "y_textureLocation");
    uv_textureLoaction = glGetUniformLocation(program, "uv_textureLoaction");

    cameraMat = glm::mat4(1.0f);
    cameraModel = glm::mat4(1.0f);
    cameraView = glm::mat4(1.0f);
    cameraProject = glm::mat4(1.0f);

    cameraModel = glm::rotate(cameraModel,glm::radians(-90.0f),glm::vec3(0.0f,0.0f,1.0f));
    cameraView = glm::translate(cameraView,glm::vec3(0.0f,0.0f,-3.0f));
    cameraProject = glm::perspective(glm::radians(45.0f), 0.5f, 0.1f, 100.0f);


    cameraMat = cameraProject*cameraView*cameraModel;
    u_cameraMat = glGetUniformLocation(program,"u_cameraMat");

    y_textureId = GLUtils::genTexture(GL_TEXTURE_2D);
    uv_textureId = GLUtils::genTexture(GL_TEXTURE_2D);
    return true;
}


void CameraPreviewRenderer::resetRenderSize(int left, int top, int width, int height) {
    _backingLeft = left;
    _backingTop = top;
    _backingWidth = width;
    _backingHeight = height;
    SML_LOGD("native-重置视口大小");
}

void CameraPreviewRenderer::dealloc() {
    if (vertShader)
        glDeleteShader(vertShader);

    if (fragShader)
        glDeleteShader(fragShader);

    if (program) {
        glDeleteProgram(program);
        program = 0;
    }

    if(y_textureId){
        glDeleteTextures(1,&y_textureId);
    }

    if(uv_textureId){
        glDeleteTextures(1,&uv_textureId);
    }
}

void CameraPreviewRenderer::draw(unsigned char* buffer,u_long len,u_long preWidth,u_long preHeight) {
    data = buffer;
    length = len;
    preViewWidth = preWidth;
    preViewHeight = preHeight;
    SML_LOGD("native-请求渲染一帧");
    SML_LOGD("数据长度: %lu",length);
    if(data == nullptr){
        SML_LOGE("无数据");
    }
    std::unique_lock<std::mutex> uniqueLock(mutex);
    condition.notify_one();
}



