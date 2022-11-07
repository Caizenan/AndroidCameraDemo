//
// Created by 蔡泽南 on 2021/11/9.
//

#ifndef CAMERA_PREVIEW_RENDER_H
#define CAMERA_PREVIEW_RENDER_H
#include "GLES2/gl2.h"
#include "GLES2/gl2ext.h"
#include "thread"
#include "jni.h"
#include "math.h"
#include <eglcore.h>
#include <glutils.h>
#include <glm.hpp>
#include <gtc/matrix_transform.hpp>

static const char* CAMERA_PREVIEW_FRAG_SHADER =
        "#ifdef GL_ES\n"
        "precision highp float;\n"
        "#endif\n"
        "varying vec2 v_texCoord;\n"
        "uniform sampler2D y_textureLocation;\n"
        "uniform sampler2D uv_textureLoaction;\n"
        "void main (){\n"
        "   float r, g, b, y, u, v;\n"
        "   y = texture2D(y_textureLocation, v_texCoord).r;\n"
        "   u = texture2D(uv_textureLoaction, v_texCoord).a - 0.5;\n"
        "   v = texture2D(uv_textureLoaction, v_texCoord).r - 0.5;\n"
        "   r = y + 1.13983*v;\n"
        "   g = y - 0.39465*u - 0.58060*v;\n"
        "   b = y + 2.03211*u;\n"
        "   gl_FragColor = vec4(r, g, b, 1.0);\n"
        "}\n";

static const char* CAMERA_PREVIEW_VERTEX_SHADER =
        "uniform mat4 u_cameraMat;\n"
        "attribute vec4 a_positionLocation;\n"
        "attribute vec2 a_texCoord;\n"
        "varying vec2 v_texCoord;\n"
        "void main(){\n"
        "   gl_Position = u_cameraMat * a_positionLocation;\n"
        "   v_texCoord = a_texCoord;\n"
        "}\n";


static const GLfloat _vertices[] = { -1.0f, 1.0f, 1.0f, 1.0f,
                                     -1.0f, -1.0f, 1.0f, 1.0f,
                                     1.0f, 1.0f, 1.0f, 1.0f,
                                     1.0f, -1.0f, 1.0f, 1.0f};

static const GLfloat _texCoords[] = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f };

/**
 * Video OpenGL View
 */
class CameraPreviewRenderer {
private:
    //视口参数
    GLint _backingLeft;
    GLint _backingTop;
    GLint _backingWidth;
    GLint _backingHeight;

    //着色器，程序
    GLuint vertShader;
    GLuint fragShader;
    GLuint program;

    //渲染线程
    std::thread* renderThread;
    //egl环境
    EGLCore* eglEnv;
    //一帧是否渲染完成
    EGLContext* shareContext;
    //互斥锁
    std::mutex mutex;
    std::condition_variable condition;
    //帧缓冲
    unsigned char* data;
    u_long length;
    //yuv纹理
    GLuint y_textureId = 0;
    GLuint uv_textureId = 0;
    int y_textureLocation = 0;
    int uv_textureLoaction = 0;

    //相机矩阵
    GLuint u_cameraMat;
    glm::mat4 cameraMat;
    glm::mat4 cameraView;
    glm::mat4 cameraModel;
    glm::mat4 cameraProject;

    //顶点，纹理坐标属性
    int a_position;
    int a_texCoord;
    //预览宽，高
    u_long preViewWidth;
    u_long preViewHeight;
    //线程出口
    std::atomic<bool> running;

public:
    CameraPreviewRenderer();
    virtual ~CameraPreviewRenderer();
    virtual bool prepare(void* suface,int left,int top,int width,int height);
    virtual void render();
    virtual void onDraw();
    virtual void dealloc();
    virtual void resetRenderSize(int left, int top, int width, int height);
    virtual void draw(unsigned char* buffer,u_long len,u_long preWidht,u_long preHeight);
    virtual bool init();
};

#endif //PIC_PREVIEW_RENDER_H

