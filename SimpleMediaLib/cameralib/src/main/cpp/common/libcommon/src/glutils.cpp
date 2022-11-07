//
// Created by 蔡泽南 on 2021/12/7.
//
#include <GLES2/gl2.h>
#include "glutils.h"
#include "sml_log.hpp"
//#include "../../libjpeg-turbo/jpeglib.h"

GLuint GLUtils::compileShader(GLenum type, const char *sources) {
    char infoLog[512];
    GLint status;
    GLuint shader = glCreateShader(type);
    if (shader == 0 || shader == GL_INVALID_ENUM) {
        SML_LOGE("Shader创建失败 %d", type);
        return 0;
    }
    glShaderSource(shader, 1, &sources, nullptr);
    glCompileShader(shader);
    glGetShaderiv(shader, GL_COMPILE_STATUS, &status);
    if (status == GL_FALSE) {
        glGetShaderInfoLog(shader,512,NULL,infoLog);
        glDeleteShader(shader);
        SML_LOGE("Shader编译失败 : %s\n", sources);
        SML_LOGE("错误信息 : %s\n", infoLog);
        return 0;
    }
    return shader;
}

GLuint GLUtils::genTexture(GLuint target){
    GLuint textureArray[1];
    glGenTextures(1,textureArray);
    int textureId = textureArray[0];
    glBindTexture(target,textureId);
    glTexParameterf(target,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameterf(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameterf(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    if(!checkGlError()){
        SML_LOGE("纹理创建失败");
        return -1;
    }
    return textureId;
}

bool GLUtils::checkGlError() {
    GLint error;
    for (error = glGetError(); error; error = glGetError()) {
        return false;
    }
    return true;
}

int GLUtils::initShaders(GLuint& vertShader,GLuint& fragShader,const char*vertexShader,const char*fragmentShader) {
    vertShader = compileShader(GL_VERTEX_SHADER, vertexShader);
    if (!vertShader){
        SML_LOGE("初始化顶点着色器失败");
        return -1;
    }
    fragShader = compileShader(GL_FRAGMENT_SHADER, fragmentShader);
    if (!fragShader){
        SML_LOGE("初始化片段着色器失败");
        return -1;
    }
    return 1;
}

int GLUtils::useProgram(GLuint& program,GLuint& vertShader,GLuint& fragShader) {
    program = glCreateProgram();
    glAttachShader(program, vertShader);
    glAttachShader(program, fragShader);
    glLinkProgram(program);
    GLint status;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status == GL_FALSE) {
        SML_LOGE("着色器链接失败");
        return -1;
    }
    return 1;
}

void GLUtils::jpeg2Texture(const char *path) {
//    // 封装 jpeg 相关操作
//    JpegHelper jpegHelper;
//    // 读取的图像内容
//    unsigned char *jpegData;
//    int jpegSize;
//    int jpegWidth;
//    int jpegHeight;
//    // 读取操作
//    jpegHelper.read_jpeg_file(path, &jpegData, &jpegSize, &jpegWidth, &jpegHeight);
}