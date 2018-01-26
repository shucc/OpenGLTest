package org.cchao.opengltest.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import org.cchao.opengltest.R;
import org.cchao.opengltest.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by shucc on 18/1/25.
 * cc@cchao.org
 */
public class OvalGLRenderer implements GLSurfaceView.Renderer {

    private final String TAG = getClass().getName();

    private final int COORDS_PER_VERTEX = 3;

    private int positionHandle;

    private int matrixHandle;

    private int colorHandle;

    private float[] viewMatrix = new float[16];
    private float[] projectMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private final int vertexStride = 0;

    private float radius = 1.0f;

    private int n = 360;

    private float height = 0.0f;

    private float[] shapePos;

    //设置颜色，依次为红绿蓝和透明通道
    private float color[] = {1.0f, 1.0f, 0f, 0f};

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        shapePos = createPositions();
        //绘制背景色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0f);
        //坐标数据转换为FloatBuffer，用于传入给OpenGL ES程序
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(shapePos.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(shapePos);
        vertexBuffer.position(0);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Utils.loadShader(R.raw.oval_vertex));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Utils.loadShader(R.raw.oval_frag));
        //创建一个空的OpenGL ES程序
        int program = GLES20.glCreateProgram();
        //顶点着色器加入到程序
        GLES20.glAttachShader(program, vertexShader);
        //片元着色器加入到程序
        GLES20.glAttachShader(program, fragmentShader);
        //连接到程序
        GLES20.glLinkProgram(program);
        //将程序加入到OpenGL ES 2.0环境
        GLES20.glUseProgram(program);
        matrixHandle = GLES20.glGetUniformLocation(program, "vMatrix");
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Log.d(TAG, "onSurfaceChanged: " + ratio);
        //设置透视投影
        Matrix.frustumM(projectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0f);
        //计算变换矩阵
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, shapePos.length / 3);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public void setMvpMatrix(float[] mvpMatrix) {
        this.mvpMatrix = mvpMatrix;
    }

    private float[] createPositions() {
        ArrayList<Float> data = new ArrayList<>();
        data.add(0.0f);
        data.add(0.0f);
        data.add(height);
        float angDegSpan = 360f / n;
        for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
            data.add((float) (radius * Math.sin(i * Math.PI / 180f)));
            data.add((float) (radius * Math.cos(i * Math.PI / 180f)));
            data.add(height);
        }
        float[] f = new float[data.size()];
        for (int i = 0; i < f.length; i++) {
            f[i] = data.get(i);
        }
        return f;
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
