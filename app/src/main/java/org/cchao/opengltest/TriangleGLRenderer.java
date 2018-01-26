package org.cchao.opengltest;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by shucc on 18/1/25.
 * cc@cchao.org
 */
public class TriangleGLRenderer implements GLSurfaceView.Renderer {

    private final int COORDS_PER_VERTEX = 3;

    private final float[] triangleCoords = new float[] {
            0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private final float[] color = new float[] {1.0f, 0f, 0f, 0f};

    private FloatBuffer vertexBuffer;

    private int program;

    private int positionHandle;

    private int colorHandle;

    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //绘制背景色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0f);
        //坐标数据转换为FloatBuffer，用于传入给OpenGL ES程序
        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(triangleCoords);
        vertexBuffer.position(0);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Utils.loadShader(R.raw.triangle_vertex));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Utils.loadShader(R.raw.triangle_frag));
        //创建一个空的OpenGL ES程序
        program = GLES20.glCreateProgram();
        //顶点着色器加入到程序
        GLES20.glAttachShader(program, vertexShader);
        //片元着色器加入到程序
        GLES20.glAttachShader(program, fragmentShader);
        //连接到程序
        GLES20.glLinkProgram(program);
        //将程序加入到OpenGL ES 2.0环境
        GLES20.glUseProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
