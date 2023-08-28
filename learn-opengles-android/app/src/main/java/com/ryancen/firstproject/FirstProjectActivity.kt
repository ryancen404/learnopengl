package com.ryancen.firstproject

import android.app.ActivityManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ryancen.myapplication.R

private const val TAG = "FirstProjectActivity"

class FirstProjectActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLSurfaceView
    private var rendererSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_project)
        glSurfaceView = findViewById(R.id.gl_surface_view)

        val activityManager: ActivityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000

        Log.i(TAG, "supportsEs2: $supportsEs2")

        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(FirstProjectRenderer())
            rendererSet = true
        } else {
            Toast.makeText(this, "This device don't support OpenGL ES 2.0", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * 这些方法非常重要，有了它们，这个surface 视图才能正确暂停并继续后合谊染线程， 同时释放和续用OpenGL 上下文。
     * 如果它没有做这些，应用程序可能会崩溃，并被Android 终止;我们还要保证渲染器也设置了，否则调用这些方法也会引起程序崩溃。
     */
    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            glSurfaceView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            glSurfaceView.onPause()
        }
    }


}
