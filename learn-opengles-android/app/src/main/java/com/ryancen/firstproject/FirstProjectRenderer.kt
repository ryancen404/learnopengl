package com.ryancen.firstproject

import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLES20.glViewport
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "FirstProjectRenderer"

class FirstProjectRenderer : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.i(TAG, "onSurfaceCreated: $p0, $p1")
        // 通 过 把 第 一 个 分 量 设 为 1 ， 其 余 设 为 0，我们把红色设置为最大强度，当屏幕被清空时，它就会显示红色
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
    }

    /**
     * 这个方法在每次surface尺寸变化时被调用，比如说当设备屏幕方向变化时。
     */
    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        // 告诉了 OpenGL 可以用来渲染的 surface 的大小
        glViewport(0, 0, width, height)
    }

    /**
     * 这个方法在绘制每一帧时被调用，我们在这里编写绘制代码。
     */
    override fun onDrawFrame(p0: GL10?) {
        // 在onDrawFame0 中调用gIClear(GL_COLOR BUFFER_BIT) 清空屏幕;
        // 这会擦除屏幕 上的所有颜色，并用之前gIClearColor( 调用定义的颜色填充整个屏幕
        glClear(GL10.GL_COLOR_BUFFER_BIT)
    }

}
