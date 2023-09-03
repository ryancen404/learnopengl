package com.ryancen.airhockey

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.GL_LINES
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.GL_TRIANGLES
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUniform4f
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLSurfaceView
import android.util.Log
import com.ryancen.myapplication.R
import com.ryancen.util.ShaderHelper
import com.ryancen.util.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer(context: Context): GLSurfaceView.Renderer {
    companion object {

        private const val TAG = "AirHockeyRenderer"

        private const val U_COLOR = "u_Color"
        private const val A_POSITION = "a_Position"

        const val POSITION_COMPONENT_COUNT = 2

        // 每个浮点数都占用4个字节
        const val BYTES_PER_FLOAT = 4
    }

    private var program: Int = 0
    private var uColorLocation: Int = 0
    private var aPositionLocation: Int = 0

    val vertexShaderSource = TextResourceReader.readTextFileFromResource(
        context, R.raw.simple_vertex_shader
    )
    val fragmentShaderSource = TextResourceReader.readTextFileFromResource(
        context,
        R.raw.simple_fragment_shader
    )


    // 定义顶点坐标
    private val tableVerticesWithTriangles = floatArrayOf(
        // 长方形
        // Triangle 1
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,

        // Triangle 2
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,

        // Line 1； 中间线
        -0.5f, 0f,
        0.5f, 0f,

        // Mallets; 木槌
        0f, -0.25f,
        0f, 0.25f,
    )

    // 顶点数据
    private val vertexData: FloatBuffer = ByteBuffer
        // 分配一块本地内存，这块内存不会被垃圾回收器管理
        .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
        // 指定字节顺序，保证数据排列正确
        .order(ByteOrder.nativeOrder())
        // 从ByteBuffer创建一个浮点缓冲区
        .asFloatBuffer()
        // 把数据从 虚拟机 的内存复制到本地内存
        .put(tableVerticesWithTriangles)


    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.i(TAG, "onSurfaceCreated: $p0, $p1")
        GLES20.glClearColor(0f, 0.0f, 0.0f, 0.0f)

        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)

//        if (BuildConfig.DEBUG) {
        ShaderHelper.validateProgram(program)
//        }
        // 调用 glUseProgram 告诉 OpenGL 在绘制任何东西到屏幕上的时候要使用这里定义的程序。
        GLES20.glUseProgram(program)

        /**
         * 我们要使用这个uniform设置将要绘制的东西的颜色;
         * 我们要绘制 一张桌子、 一个中间分隔线和两个木槌，并且我们要使用不同的颜色绘制它们。
         * 一个uniform 的位置在一个程序对象中是唯一的;
         * 当我们稍后要更新这个uniform 值的时候，我们会使用它
         * @see simple_fragment_shader.glsl
         */
        uColorLocation = glGetUniformLocation(program, U_COLOR)

        /**
         * 获取属性位置
         * 有了这个位置，就能告诉OpenGL 到哪里 去 找 到 这 个 属 性 对 应 的 数 据 了。
         * @see simple_vertex_shader.glsl
         */
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        // 把缓存的位置移动到开头，从这里开始读取数据
        vertexData.position(0)
        // 调用glVertexAttribPointer 告诉OpenGL，它可以在缓冲区vertexData中找 到a_Position 对应的数据
        glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexData
        )

        // 设置打开a_Position对应的属性
        glEnableVertexAttribArray(aPositionLocation)
    }

    /**
     * 这个方法在每次surface尺寸变化时被调用，比如说当设备屏幕方向变化时。
     */
    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        // 告诉了 OpenGL 可以用来渲染的 surface 的大小
        GLES20.glViewport(0, 0, width, height)
    }

    /**
     * 这个方法在绘制每一帧时被调用，我们在这里编写绘制代码。
     */
    override fun onDrawFrame(p0: GL10?) {
        // 在onDrawFame0 中调用gIClear(GL_COLOR BUFFER_BIT) 清空屏幕;
        // 这会擦除屏幕 上的所有颜色，并用之前gIClearColor( 调用定义的颜色填充整个屏幕
        glClear(GL_COLOR_BUFFER_BIT)

        /**
         * 我们首先通过调用 glUniform4f 更新着色器代码中的 uColor 的值。
         * 与属性不同， uniform 的分量没有默认值，
         * 因此，如果一个uniform 在着色器中被定义为vec4 类型，我们需要提供所有四个分量的值。
         * 分别对应 rgba 的值
         */
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
        /**
         * 绘制两个三角形
         * 即[tableVerticesWithTriangles]中的前六个顶点
         *
         * 第一个参数告诉OpenGL，我们想要画三角形。而要面三角形，我们需要给每个 三角形传递进去至少 三个顶点;
         * 第二个参数告诉OpenGL 从顶点数组的开头处开始读顶点;
         * 第三个参数是告诉OpenGL读人六个顶点。因为每个三角形有三个顶点 ，这个调用最终会画出两个三角形。
         */
        glDrawArrays(GL_TRIANGLES, 0, 6)

        /**
         * 绘制分隔线
         * 通过传递1.0f 给第一个分量(红色)及传递0.0f 给绿色和蓝色，我们把颜色设为红色。
         * 即[tableVerticesWithTriangles]中的第6个顶点开始，读取2个顶点
         */
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        glDrawArrays(GL_LINES, 6, 2)


        /**
         * 绘制木槌  1
         * 颜色设为蓝色
         * 即[tableVerticesWithTriangles]中的第8个顶点开始，读取1个顶点
         */
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
        glDrawArrays(GL_POINTS, 8, 1)

        /**
         * 绘制木槌 2
         * 颜色设为红色
         * 即[tableVerticesWithTriangles]中的第9个顶点开始，读取1个顶点
         */
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        glDrawArrays(GL_POINTS, 9, 1)
    }

}