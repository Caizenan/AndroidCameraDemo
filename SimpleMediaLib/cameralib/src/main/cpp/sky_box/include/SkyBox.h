//
// Created by 蔡泽南 on 2021/12/7.
//
#ifndef __SKYBOX_H__
#define __SKYBOX_H__

#include <thread>
#include <GLES2/gl2.h>
#include <eglcore.h>
#include <glm.hpp>

static const char * SKYBOX_VERTEX_SHADER =
        "uniform mat4 u_modelMat;\n"
        "uniform mat4 u_viewMat;\n"
        "uniform mat4 u_projectionMat;\n"
        "attribute vec3 a_position;\n"
        "void main(){\n"
        "   gl_Position = u_projectionMat * u_viewMat * u_modelMat * vec4(a_position,1.0);\n"
        "}\n";

static const char * SKYBOX_FRAGMENT_SHADER =
        "#ifdef GL_ES\n"
        "precision highp float;\n"
        "#endif\n"
        "void main (){\n"
        "   gl_FragColor = vec4(1.0,0.5,0.0,1.0);\n"
        "}\n";

static const GLfloat _vertices[] = {
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        -0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f, -0.5f,  0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        -0.5f, -0.5f,  0.5f,

        -0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,

        0.5f,  0.5f,  0.5f,
        0.5f,  0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,

        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f,  0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        0.5f,  0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f, -0.5f};

class SkyBox{

private:
    GLint left;
    GLint top;
    GLint width;
    GLint height;

    //着色器，程序
    GLuint vertShader;
    GLuint fragShader;
    GLuint program;

    //渲染线程
    std::thread* renderThread;

    //egl环境
    EGLCore* eglEnv;

    GLuint a_positionLocation;

    //坐标转换
    glm::mat4 modelMat;
    GLuint u_modelMatLocation;
    glm::mat4 viewMat;
    GLuint u_viewMatLocation;
    glm::mat4 projectionMat;
    GLuint u_projectionMatLocation;

    GLuint skyBoxVBO;
    GLuint skyBoxFBO;

    EGLContext* shareContext;

    std::atomic<bool> running;

public:
    SkyBox();
    virtual ~SkyBox();
    virtual bool prepare(void* win);
    virtual void resetRenderSize(int left, int top, int width, int height);
    virtual void dealloc();
private:
    virtual void render();
    virtual void onDraw();
    virtual bool initGLEnv();
};

#endif
