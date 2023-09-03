package com.ryancen.util

import android.opengl.GLES20
import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glCompileShader
import android.opengl.GLES20.glCreateProgram
import android.opengl.GLES20.glCreateShader
import android.opengl.GLES20.glDeleteProgram
import android.opengl.GLES20.glGetProgramiv
import android.opengl.GLES20.glGetShaderiv
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glValidateProgram
import android.util.Log

object ShaderHelper {

    private const val TAG = "ShaderHelper"

    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
    }

    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
    }


    /**
     * glCreateShader() 创建一个新的着色器对象，并返回对象的指针，后面要引用到这个对象就要把这个值传回 opengl
     * glCreateShader 是一个 native 方法
     */
    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderObjectId = glCreateShader(type)
        /**
         * 返回值 0，表示这个对象创建失败，它类似于Java代码中返回 null 值。
         * 如果对象创建失败，就给调用代码返回0 。为什么返回又而不是拋出一个异常呢?这是 因为OpenGL内部实际不会拋出任何异常;
         * 相反，我们会得到返回值0，并且OpenGL 通过 glGetError 告诉我们这个错误，
         * 这个方法可以让我们询问OpenGL 是不是某个API 调用导 致 了错误。我们会一直遵从这个惯例。
         */
        if (shaderObjectId == 0) {
            Log.w(TAG, "Could not create new shader")
            return 0
        }
        glShaderSource(shaderObjectId, shaderCode)
        glCompileShader(shaderObjectId)

        val compileStatus = IntArray(1)
        glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        /**
         * 我们可以通过调用 glGetShaderInfoLog 获得一个可读的消息。如果OpenGL 有什么关于着色器的有用内容，它就 会把消息存到着色器的信息日志里。
         *
         */
        Log.v(
            TAG,
            "Results of compiling source: \n$shaderCode\n:${GLES20.glGetShaderInfoLog(shaderObjectId)}"
        )
        if (compileStatus[0] == 0) {
            // 如果编译失败，删除这个着色器对象
            GLES20.glDeleteShader(shaderObjectId)
            Log.w(TAG, "Compilation of shader failed.")
            return 0
        }
        return shaderObjectId
    }


    /**
     * 链接到 opengl 程序
     */
    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = glCreateProgram()
        if (programObjectId == 0) {
            Log.w(TAG, "Could not create new program")
            return 0
        }
        // 把顶点着色器和片段着色器都附加到程序对象上
        glAttachShader(programObjectId, vertexShaderId)
        glAttachShader(programObjectId, fragmentShaderId)
        // 把这些着色器联合起来了
        glLinkProgram(programObjectId)
        val linkStatus = IntArray(1)
        glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        Log.v(
            TAG,
            "Results of linking program: \n${GLES20.glGetProgramInfoLog(programObjectId)}"
        )
        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId)
            Log.v(TAG, "Linking of program failed.")
            return 0
        }
        return programObjectId
    }


    /**
     * 验证 opengl 程序的对象
     * 我们应该只有在开发或调试应用的时候才去验证它
     */
    fun validateProgram(programObjectId: Int): Boolean {
        glValidateProgram(programObjectId)
        val validateStatus = IntArray(1)
        glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)
        Log.v(
            TAG,
            "Results of validating program: ${validateStatus[0]}\nLog:${GLES20.glGetProgramInfoLog(
                programObjectId
            )}"
        )
        return validateStatus[0] != 0
    }

}