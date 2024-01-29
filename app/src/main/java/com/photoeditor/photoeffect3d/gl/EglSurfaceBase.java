package com.photoeditor.photoeffect3d.gl;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.opengl.EGL14;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EglSurfaceBase {
    protected static final String TAG = EglSurfaceBase.class.getSimpleName();
    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
    protected EglCore mEglCore;
    private int mHeight = -1;
    private int mWidth = -1;

    protected EglSurfaceBase(EglCore eglCore) {
        this.mEglCore = eglCore;
    }

    public void createWindowSurface(Object surface) {
        if (this.mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }
        this.mEGLSurface = this.mEglCore.createWindowSurface(surface);
    }

    public void createOffscreenSurface(int width, int height) {
        if (this.mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }
        this.mEGLSurface = this.mEglCore.createOffscreenSurface(width, height);
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        if (this.mWidth < 0) {
            return this.mEglCore.querySurface(this.mEGLSurface, 12375);
        }
        return this.mWidth;
    }

    public int getHeight() {
        if (this.mHeight < 0) {
            return this.mEglCore.querySurface(this.mEGLSurface, 12374);
        }
        return this.mHeight;
    }

    public void releaseEglSurface() {
        this.mEglCore.releaseSurface(this.mEGLSurface);
        this.mEGLSurface = EGL14.EGL_NO_SURFACE;
        this.mHeight = -1;
        this.mWidth = -1;
    }

    public void makeCurrent() {
        this.mEglCore.makeCurrent(this.mEGLSurface);
    }

    public void makeCurrentReadFrom(EglSurfaceBase readSurface) {
        this.mEglCore.makeCurrent(this.mEGLSurface, readSurface.mEGLSurface);
    }

    public boolean swapBuffers() {
        boolean result = this.mEglCore.swapBuffers(this.mEGLSurface);
        if (!result) {
            Log.d(TAG, "WARNING: swapBuffers() failed");
        }
        return result;
    }

    public void setPresentationTime(long nsecs) {
        this.mEglCore.setPresentationTime(this.mEGLSurface, nsecs);
    }

    public void saveFrame(File file) throws IOException {
        Throwable th;
        if (this.mEglCore.isCurrent(this.mEGLSurface)) {
            String filename = file.toString();
            int width = getWidth();
            int height = getHeight();
            ByteBuffer buf = ByteBuffer.allocateDirect((width * height) * 4);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            GLES20.glReadPixels(0, 0, width, height, 6408, 5121, buf);
            GlUtil.checkGlError("glReadPixels");
            buf.rewind();
            BufferedOutputStream bos = null;
            try {
                BufferedOutputStream bos2 = new BufferedOutputStream(new FileOutputStream(filename));
                try {
                    Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                    bmp.copyPixelsFromBuffer(buf);
                    bmp.compress(CompressFormat.PNG, 90, bos2);
                    bmp.recycle();
                    if (bos2 != null) {
                        bos2.close();
                    }
                    Log.d(TAG, "Saved " + width + "x" + height + " frame as '" + filename + "'");
                    return;
                } catch (Throwable th2) {
                    th = th2;
                    bos = bos2;
                    if (bos != null) {
                        bos.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (bos != null) {
                    bos.close();
                }
//                throw th;
            }
        }
        throw new RuntimeException("Expected EGL context/surface is not current");
    }
}
