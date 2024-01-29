package com.photoeditor.photoeffect3d.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

public class TestRenderer extends CameraRenderer {
    private float b1 = 0.0f;
    private float b2 = 1.0f;
    private float g1 = 0.0f;
    private float g2 = 1.0f;
    private float offsetB = 0.5f;
    private float offsetG = 0.5f;
    private float offsetR = 0.5f;
    private float r1 = 1.0f;
    private float r2 = 0.0f;
    private float xT = 0.0f;
    private float yT = 0.05f;

    public TestRenderer(Context context, SurfaceTexture previewSurface, int width, int height) {
        super(context, previewSurface, width, height, "testcolor.frag.glsl", "testcolor.vert.glsl");
    }

    protected void setUniformsAndAttribs() {
        super.setUniformsAndAttribs();
        int offsetRLoc = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "offsetR");
        int offsetGLoc = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "offsetG");
        int offsetBLoc = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "offsetB");
        int offsetxT = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "xT");
        int offsetyT = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "yT");
        int offsetr1 = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "r1");
        int offsetg1 = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "g1");
        int offsetb1 = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "b1");
        int offsetr2 = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "r2");
        int offsetg2 = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "g2");
        int offsetb2 = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "b2");
        GLES20.glUniform1f(offsetxT, this.xT);
        GLES20.glUniform1f(offsetyT, this.yT);
        GLES20.glUniform1f(offsetr1, this.r1);
        GLES20.glUniform1f(offsetg1, this.g1);
        GLES20.glUniform1f(offsetb1, this.b1);
        GLES20.glUniform1f(offsetr2, this.r2);
        GLES20.glUniform1f(offsetg2, this.g2);
        GLES20.glUniform1f(offsetb2, this.b2);
        GLES20.glUniform1f(offsetRLoc, this.offsetR);
        GLES20.glUniform1f(offsetGLoc, this.offsetG);
        GLES20.glUniform1f(offsetBLoc, this.offsetB);
    }

    public void setDistance(float xT, float yT, float r1, float g1, float b1, float r2, float g2, float b2) {
        this.xT = xT;
        this.yT = yT;
        this.r1 = r1;
        this.g1 = g1;
        this.b1 = b1;
        this.r2 = r2;
        this.g2 = g2;
        this.b2 = b2;
    }
}
