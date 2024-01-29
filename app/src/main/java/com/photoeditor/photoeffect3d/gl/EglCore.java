package com.photoeditor.photoeffect3d.gl;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;

public final class EglCore {
    private static final int EGL_RECORDABLE_ANDROID = 12610;
    public static final int FLAG_RECORDABLE = 1;
    public static final int FLAG_TRY_GLES3 = 2;
    private static final String TAG = EglCore.class.getSimpleName();
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext;
    private EGLDisplay mEGLDisplay;
    private int mGlVersion;

    public EglCore() {
        this(null, 0);
    }

    public EglCore(EGLContext sharedContext, int flags) {
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLConfig = null;
        this.mGlVersion = -1;
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }
        if (sharedContext == null) {
            sharedContext = EGL14.EGL_NO_CONTEXT;
        }
        this.mEGLDisplay = EGL14.eglGetDisplay(0);
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }
        int[] version = new int[FLAG_TRY_GLES3];
        if (EGL14.eglInitialize(this.mEGLDisplay, version, 0, version, FLAG_RECORDABLE)) {
            EGLConfig config;
            EGLContext context;
            if ((flags & FLAG_TRY_GLES3) != 0) {
                config = getConfig(flags, 3);
                if (config != null) {
                    context = EGL14.eglCreateContext(this.mEGLDisplay, config, sharedContext, new int[]{12440, 3, 12344}, 0);
                    if (EGL14.eglGetError() == 12288) {
                        this.mEGLConfig = config;
                        this.mEGLContext = context;
                        this.mGlVersion = 3;
                    }
                }
            }
            if (this.mEGLContext == EGL14.EGL_NO_CONTEXT) {
                config = getConfig(flags, FLAG_TRY_GLES3);
                if (config == null) {
                    throw new RuntimeException("Unable to find a suitable EGLConfig");
                }
                context = EGL14.eglCreateContext(this.mEGLDisplay, config, sharedContext, new int[]{12440, FLAG_TRY_GLES3, 12344}, 0);
                checkEglError("eglCreateContext");
                this.mEGLConfig = config;
                this.mEGLContext = context;
                this.mGlVersion = FLAG_TRY_GLES3;
            }
            int[] values = new int[FLAG_RECORDABLE];
            EGL14.eglQueryContext(this.mEGLDisplay, this.mEGLContext, 12440, values, 0);
            Log.d(TAG, "EGLContext created, client version " + values[0]);
            return;
        }
        this.mEGLDisplay = null;
        throw new RuntimeException("unable to initialize EGL14");
    }

    private EGLConfig getConfig(int flags, int version) {
        int renderableType = 4;
        if (version >= 3) {
            renderableType = 4 | 64;
        }
        int[] attribList = new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, renderableType, 12344, 0, 12344};
        if ((flags & FLAG_RECORDABLE) != 0) {
            attribList[attribList.length - 3] = EGL_RECORDABLE_ANDROID;
            attribList[attribList.length - 2] = FLAG_RECORDABLE;
        }
        EGLConfig[] configs = new EGLConfig[FLAG_RECORDABLE];
        if (EGL14.eglChooseConfig(this.mEGLDisplay, attribList, 0, configs, 0, configs.length, new int[FLAG_RECORDABLE], 0)) {
            return configs[0];
        }
        Log.w(TAG, "unable to find RGB8888 / " + version + " EGLConfig");
        return null;
    }

    public void release() {
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(this.mEGLDisplay);
        }
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLConfig = null;
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                Log.w(TAG, "WARNING: EglCore was not explicitly released -- state may be leaked");
                release();
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    public void releaseSurface(EGLSurface eglSurface) {
        EGL14.eglDestroySurface(this.mEGLDisplay, eglSurface);
    }

    public EGLSurface createWindowSurface(Object surface) {
        if ((surface instanceof Surface) || (surface instanceof SurfaceTexture)) {
            int[] surfaceAttribs = new int[FLAG_RECORDABLE];
            surfaceAttribs[0] = 12344;
            EGLSurface eglSurface = EGL14.eglCreateWindowSurface(this.mEGLDisplay, this.mEGLConfig, surface, surfaceAttribs, 0);
            checkEglError("eglCreateWindowSurface");
            if (eglSurface != null) {
                return eglSurface;
            }
            throw new RuntimeException("surface was null");
        }
        throw new RuntimeException("invalid surface: " + surface);
    }

    public EGLSurface createOffscreenSurface(int width, int height) {
        EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(this.mEGLDisplay, this.mEGLConfig, new int[]{12375, width, 12374, height, 12344}, 0);
        checkEglError("eglCreatePbufferSurface");
        if (eglSurface != null) {
            return eglSurface;
        }
        throw new RuntimeException("surface was null");
    }

    public void makeCurrent(EGLSurface eglSurface) {
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.d(TAG, "NOTE: makeCurrent w/o display");
        }
        if (!EGL14.eglMakeCurrent(this.mEGLDisplay, eglSurface, eglSurface, this.mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public void makeCurrent(EGLSurface drawSurface, EGLSurface readSurface) {
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.d(TAG, "NOTE: makeCurrent w/o display");
        }
        if (!EGL14.eglMakeCurrent(this.mEGLDisplay, drawSurface, readSurface, this.mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent(draw,read) failed");
        }
    }

    public void makeNothingCurrent() {
        if (!EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public boolean swapBuffers(EGLSurface eglSurface) {
        return EGL14.eglSwapBuffers(this.mEGLDisplay, eglSurface);
    }

    public void setPresentationTime(EGLSurface eglSurface, long nsecs) {
        EGLExt.eglPresentationTimeANDROID(this.mEGLDisplay, eglSurface, nsecs);
    }

    public boolean isCurrent(EGLSurface eglSurface) {
        return this.mEGLContext.equals(EGL14.eglGetCurrentContext()) && eglSurface.equals(EGL14.eglGetCurrentSurface(12377));
    }

    public int querySurface(EGLSurface eglSurface, int what) {
        int[] value = new int[FLAG_RECORDABLE];
        EGL14.eglQuerySurface(this.mEGLDisplay, eglSurface, what, value, 0);
        return value[0];
    }

    public String queryString(int what) {
        return EGL14.eglQueryString(this.mEGLDisplay, what);
    }

    public int getGlVersion() {
        return this.mGlVersion;
    }

    public static void logCurrent(String msg) {
        EGLDisplay display = EGL14.eglGetCurrentDisplay();
        EGLContext context = EGL14.eglGetCurrentContext();
        Log.i(TAG, "Current EGL (" + msg + "): display=" + display + ", context=" + context + ", surface=" + EGL14.eglGetCurrentSurface(12377));
    }

    private void checkEglError(String msg) {
        int error = EGL14.eglGetError();
        if (error != 12288) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }
}
