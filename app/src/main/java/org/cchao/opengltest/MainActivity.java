package org.cchao.opengltest;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.cchao.opengltest.renderer.BallGLRenderer;
import org.cchao.opengltest.renderer.ImageGLRenderer;
import org.cchao.opengltest.renderer.RectGLRenderer;
import org.cchao.opengltest.renderer.WordGLRenderer;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        glSurfaceView = findViewById(R.id.gl_surface);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        //glSurfaceView.setRenderer(new WordGLRenderer("君不见黄河之水天上来，奔流到海不复回。"));
        glSurfaceView.setRenderer(new ImageGLRenderer(this));
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != glSurfaceView) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != glSurfaceView) {
            glSurfaceView.onResume();
        }
    }
}
