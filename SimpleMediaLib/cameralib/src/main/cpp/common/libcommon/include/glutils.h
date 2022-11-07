//
// Created by 蔡泽南 on 2021/12/7.
//

#ifndef __GLUTILS_H__
#define __GLUTILS_H__

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

class GLUtils{
public:
    static GLuint compileShader(GLenum type, const char *sources);
    static GLuint genTexture(GLuint target);
    static bool checkGlError();
    static int initShaders(GLuint& vertShader,GLuint& fragShader,const char*vertexShader,const char*fragmentShader);
    static int useProgram(GLuint& program,GLuint& vertShader,GLuint& fragShader);
    static void jpeg2Texture(const char* path);
};
#endif //SIMPLEMEDIALIB_GLUTILS_H
