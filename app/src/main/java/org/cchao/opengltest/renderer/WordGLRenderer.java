package org.cchao.opengltest.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import org.cchao.opengltest.R;
import org.cchao.opengltest.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by shucc on 18/1/29.
 * cc@cchao.org
 */
public class WordGLRenderer implements GLSurfaceView.Renderer {

    private final String TAG = getClass().getName();

    //顶点坐标
    private final float[] sPos = new float[]{
            -1.0f, 1.0f,    //左上角
            -1.0f, -1.0f,   //左下角
            1.0f, 1.0f,     //右上角
            1.0f, -1.0f     //右下角
    };

    private final float[] sCoord = new float[]{
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    private float[] viewMatrix = new float[16];
    private float[] projectMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private FloatBuffer bPos;
    private FloatBuffer bCoord;

    private Bitmap bitmap;

    private int glPosition;
    private int glCoordinate;
    private int glTexture;
    private int glMatrix;

    private String content;

    private int height;

    public WordGLRenderer(String content) {
        this.content = content;
        initFontBitmap();
        bPos = ByteBuffer.allocateDirect(sPos.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(sPos);
        bPos.position(0);
        bCoord = ByteBuffer.allocateDirect(sCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(sCoord);
        bCoord.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Utils.loadShader(R.raw.image_vertex));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Utils.loadShader(R.raw.image_frag));
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
        glPosition = GLES20.glGetAttribLocation(program, "vPosition");
        glCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate");
        glTexture = GLES20.glGetUniformLocation(program, "vTexture");
        glMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        //uXY = sWidthHeight;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(projectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 5);
            } else {
                Matrix.orthoM(projectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 5);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(projectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 5);
            } else {
                Matrix.orthoM(projectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUniformMatrix4fv(glMatrix, 1, false, mvpMatrix, 0);
        GLES20.glEnableVertexAttribArray(glPosition);
        GLES20.glEnableVertexAttribArray(glCoordinate);
        GLES20.glUniform1i(glTexture, 0);
        createTexture();
        GLES20.glVertexAttribPointer(glPosition, 2, GLES20.GL_FLOAT, false, 0, bPos);
        GLES20.glVertexAttribPointer(glCoordinate, 2, GLES20.GL_FLOAT, false, 0, bCoord);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private int createTexture() {
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }

    /**
     * android中绘制字体，使用画布canvas
     */
    public void initFontBitmap(){
        Bitmap originBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(originBitmap);
        //背景颜色
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        TextPaint p = new TextPaint();
        //消除锯齿
        p.setAntiAlias(true);
        //字体为红色
        p.setColor(Color.RED);
        p.setTextSize(14);
        StaticLayout staticLayout = new StaticLayout(content, p, canvas.getWidth()
                , Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f
                , false);
        staticLayout.draw(canvas);
        Paint.FontMetricsInt fontMetricsInt = p.getFontMetricsInt();
        int textHeight = fontMetricsInt.bottom - fontMetricsInt.top;
        bitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), textHeight * staticLayout.getLineCount());
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
