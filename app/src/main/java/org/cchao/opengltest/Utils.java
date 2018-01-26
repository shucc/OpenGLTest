package org.cchao.opengltest;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.support.annotation.RawRes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by shucc on 18/1/24.
 * cc@cchao.org
 */
public class Utils {

    public static final int TEXTURE_NONE = -1;

    private Utils() {
        // util
    }

    public static String loadShader(@RawRes int resId) {
        StringBuilder builder = new StringBuilder();

        try {
            InputStream inputStream = App.getInstance().getResources().openRawResource(resId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line)
                        .append('\n');
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public static int createGLProgram(String vertexShaderSourceCode, String fragmentShaderSourceCode) {
        int glProgram = GLES20.glCreateProgram();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderSourceCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSourceCode);
        GLES20.glAttachShader(glProgram, vertexShader);
        GLES20.glAttachShader(glProgram, fragmentShader);
        GLES20.glLinkProgram(glProgram);
        //shaders can be deleted after the program is linked.
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        return glProgram;
    }

    private static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static int loadTextureFromBitmap(Bitmap bitmap) {
        int texture[] = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        return texture[0];
    }

    /**
     * Convert a 4x4 matrix that is stored in column-major order
     * to a string.
     * @param matrix
     * @return
     */
    public static String mat4ToString(float[] matrix) {
        if (matrix.length < 16) {
            return "not a 4x4 matrix";
        }
        StringBuilder str = new StringBuilder();
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                str.append(String.format("%.5f", matrix[4 * row + col]));
                str.append(',');
            }
            str.deleteCharAt(str.length() - 1);
            str.append('\n');
        }
        return str.toString();
    }
}
