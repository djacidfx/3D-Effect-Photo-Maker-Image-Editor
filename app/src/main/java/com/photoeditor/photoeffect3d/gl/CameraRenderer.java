package com.photoeditor.photoeffect3d.gl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaRecorder;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.Toast;

import com.photoeditor.photoeffect3d.fragments.CameraFragment;
import com.photoeditor.photoeffect3d.fragments.CameraFragment.OnViewportSizeUpdatedListener;

import com.photoeditor.photoeffect3d.utils.ShaderUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class CameraRenderer extends Thread implements OnFrameAvailableListener {
    public static final int MAX_TEXTURES = 16;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = CameraRenderer.class.getSimpleName();
    private static final String THREAD_NAME = "CameraRendererThread";
    private static final int VIDEO_BIT_RATE = 10000000;
    private static final int VIDEO_HEIGHT = 1280;
    private static final int VIDEO_WIDTH = 720;
    private static float squareSize = 1.0f;
    private static short[] drawOrder = new short[]{(short) 0, (short) 1, (short) 2, (short) 1, (short) 3, (short) 2};
    private static float[] squareCoords = new float[]{-squareSize, squareSize, squareSize, squareSize, -squareSize, -squareSize, squareSize, -squareSize};

    protected String DEFAULT_FRAGMENT_SHADER;
    protected String DEFAULT_VERTEX_SHADER;
    private ShortBuffer drawListBuffer;
    protected String fragmentShaderCode;
    private CameraFragment mCameraFragment;
    protected int mCameraShaderProgram;
    private float[] mCameraTransformMatrix;
    protected Context mContext;
    private EglCore mEglCore;
    private String mFragmentShaderPath;
    private RenderHandler mHandler;
    private boolean mIsRecording;
    private MediaRecorder mMediaRecorder;
    private OnRendererReadyListener mOnRendererReadyListener;
    private File mOutputFile;
    private SurfaceTexture mPreviewTexture;
    private WindowSurface mRecordSurface;
    protected float mSurfaceAspectRatio;
    protected int mSurfaceHeight;
    private SurfaceTexture mSurfaceTexture;
    protected int mSurfaceWidth;
    private File mTempOutputFile;
    private ArrayList<Texture> mTextureArray;
    private int[] mTextureConsts;
    private int[] mTexturesIds;
    private String mVertexShaderPath;
    private int mViewportHeight;
    private int mViewportWidth;
    private WindowSurface mWindowSurface;
    private int positionHandle;
    private FloatBuffer textureBuffer;
    private int textureCoordinateHandle;
    private float[] textureCoords;
    private FloatBuffer vertexBuffer;
    protected String vertexShaderCode;

    public interface OnRendererReadyListener {
        void onRendererFinished();

        void onRendererReady();
    }

    public static class RenderHandler extends Handler {
        private static final int MSG_SHUTDOWN = 0;
        private static final String TAG = RenderHandler.class.getSimpleName();
        private WeakReference<CameraRenderer> mWeakRenderer;

        public RenderHandler(CameraRenderer rt) {
            this.mWeakRenderer = new WeakReference(rt);
        }

        public void sendShutdown() {
            sendMessage(obtainMessage(0));
        }

        public void handleMessage(Message msg) {
            CameraRenderer renderer = (CameraRenderer) this.mWeakRenderer.get();
            if (renderer == null) {
                Log.w(TAG, "RenderHandler.handleMessage: weak ref is null");
                return;
            }
            int what = msg.what;
            switch (what) {
                case 0 /*0*/:
                    renderer.shutdown();
                    return;
                default:
                    throw new RuntimeException("unknown message " + what);
            }
        }
    }

    private class Texture {
        public int texId;
        public int texNum;
        public String uniformName;

        private Texture(int texNum, int texId, String uniformName) {
            this.texNum = texNum;
            this.texId = texId;
            this.uniformName = uniformName;
        }

        public String toString() {
            return "[Texture] num: " + this.texNum + " id: " + this.texId + ", uniformName: " + this.uniformName;
        }
    }

    static {
        ORIENTATIONS.append(0, 90);
        ORIENTATIONS.append(1, 0);
        ORIENTATIONS.append(2, 270);
        ORIENTATIONS.append(3, 180);
    }

    public CameraRenderer(Context context, SurfaceTexture texture, int width, int height) {
        this.DEFAULT_FRAGMENT_SHADER = "camera.frag.glsl";
        this.DEFAULT_VERTEX_SHADER = "camera.vert.glsl";
        this.textureCoords = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
        this.mTexturesIds = new int[MAX_TEXTURES];
        this.mTextureConsts = new int[]{33985, 33986, 33987, 33988, 33989, 33990, 33991, 33992, 33993, 33994, 33995, 33996, 33997, 33998, 33999, 34000};
        this.mCameraTransformMatrix = new float[MAX_TEXTURES];
        this.mIsRecording = false;
        this.mOutputFile = null;
        init(context, texture, width, height, this.DEFAULT_FRAGMENT_SHADER, this.DEFAULT_VERTEX_SHADER);
    }

    public CameraRenderer(Context context, SurfaceTexture texture, int width, int height, String fragPath, String vertPath) {
        this.DEFAULT_FRAGMENT_SHADER = "camera.frag.glsl";
        this.DEFAULT_VERTEX_SHADER = "camera.vert.glsl";
        this.textureCoords = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
        this.mTexturesIds = new int[MAX_TEXTURES];
        this.mTextureConsts = new int[]{33985, 33986, 33987, 33988, 33989, 33990, 33991, 33992, 33993, 33994, 33995, 33996, 33997, 33998, 33999, 34000};
        this.mCameraTransformMatrix = new float[MAX_TEXTURES];
        this.mIsRecording = false;
        this.mOutputFile = null;
        init(context, texture, width, height, fragPath, vertPath);
    }

    private void init(Context context, SurfaceTexture texture, int width, int height, String fragPath, String vertPath) {
        setName(THREAD_NAME);
        this.mContext = context;
        this.mSurfaceTexture = texture;
        this.mSurfaceWidth = width;
        this.mSurfaceHeight = height;
        this.mSurfaceAspectRatio = ((float) width) / ((float) height);
        this.mFragmentShaderPath = fragPath;
        this.mVertexShaderPath = vertPath;
    }

    private void initialize() {
        this.mTextureArray = new ArrayList();
        setupCameraFragment();
        setupMediaRecorder();
        setViewport(this.mSurfaceWidth, this.mSurfaceHeight);
        if (this.fragmentShaderCode == null || this.vertexShaderCode == null) {
            loadFromShadersFromAssets(this.mFragmentShaderPath, this.mVertexShaderPath);
        }
    }

    private void setupCameraFragment() {
        if (this.mCameraFragment == null) {
            throw new RuntimeException("CameraFragment is null! Please call setCameraFragment prior to initialization.");
        }
        this.mCameraFragment.setOnViewportSizeUpdatedListener(new OnViewportSizeUpdatedListener() {
            public void onViewportSizeUpdated(int viewportWidth, int viewportHeight) {
                CameraRenderer.this.mViewportWidth = viewportWidth;
                CameraRenderer.this.mViewportHeight = viewportHeight;
            }
        });
    }

    private void loadFromShadersFromAssets(String pathToFragment, String pathToVertex) {
        try {
            this.fragmentShaderCode = ShaderUtils.getStringFromFileInAssets(this.mContext, pathToFragment);
            this.vertexShaderCode = ShaderUtils.getStringFromFileInAssets(this.mContext, pathToVertex);
        } catch (IOException e) {
            Log.e(TAG, "loadFromShadersFromAssets() failed. Check paths to assets.\n" + e.getMessage());
        }
    }

    private void setupMediaRecorder() {
        try {
            this.mTempOutputFile = File.createTempFile("temp_mov", "mp4", this.mContext.getCacheDir());
            this.mMediaRecorder = new MediaRecorder();
            this.mMediaRecorder.setAudioSource(5);
            this.mMediaRecorder.setVideoSource(2);
            this.mMediaRecorder.setOutputFormat(2);
            this.mMediaRecorder.setOutputFile(this.mTempOutputFile.getPath());
            this.mMediaRecorder.setVideoEncoder(2);
            this.mMediaRecorder.setVideoEncodingBitRate(VIDEO_BIT_RATE);
            this.mMediaRecorder.setVideoSize(VIDEO_WIDTH, VIDEO_HEIGHT);
            this.mMediaRecorder.setVideoFrameRate(30);
            this.mMediaRecorder.setAudioEncoder(3);
            this.mMediaRecorder.setAudioEncodingBitRate(44800);
            Log.d(TAG, "orientation: " + ORIENTATIONS.get(((Activity) this.mContext).getWindowManager().getDefaultDisplay().getRotation()));
            this.mMediaRecorder.setOrientationHint(0);
            try {
                this.mMediaRecorder.prepare();
            } catch (IOException e) {
                Toast.makeText(this.mContext, "MediaRecorder failed on prepare()", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "MediaRecorder failed on prepare() " + e.getMessage());
            }
        } catch (IOException e2) {
            throw new RuntimeException("Temp file could not be created. Message: " + e2.getMessage());
        }
    }

    public void initGL() {
        this.mEglCore = new EglCore(null, 3);
        this.mWindowSurface = new WindowSurface(this.mEglCore, this.mSurfaceTexture);
        this.mWindowSurface.makeCurrent();
        this.mRecordSurface = new WindowSurface(this.mEglCore, this.mMediaRecorder.getSurface(), false);
        initGLComponents();
    }

    protected void initGLComponents() {
        onPreSetupGLComponents();
        setupVertexBuffer();
        setupTextures();
        setupCameraTexture();
        setupShaders();
        onSetupComplete();
    }

    public void deinitGL() {
        deinitGLComponents();
        this.mWindowSurface.release();
        this.mRecordSurface.release();
        this.mEglCore.release();
        if (this.mMediaRecorder != null) {
            this.mMediaRecorder.release();
        }
    }

    protected void deinitGLComponents() {
        GLES20.glDeleteTextures(MAX_TEXTURES, this.mTexturesIds, 0);
        GLES20.glDeleteProgram(this.mCameraShaderProgram);
        this.mPreviewTexture.release();
        this.mPreviewTexture.setOnFrameAvailableListener(null);
    }

    private void onPreSetupGLComponents() {
    }

    protected void setupVertexBuffer() {
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        this.drawListBuffer = dlb.asShortBuffer();
        this.drawListBuffer.put(drawOrder);
        this.drawListBuffer.position(0);
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        this.vertexBuffer = bb.asFloatBuffer();
        this.vertexBuffer.put(squareCoords);
        this.vertexBuffer.position(0);
    }

    protected void setupTextures() {
        ByteBuffer texturebb = ByteBuffer.allocateDirect(this.textureCoords.length * 4);
        texturebb.order(ByteOrder.nativeOrder());
        this.textureBuffer = texturebb.asFloatBuffer();
        this.textureBuffer.put(this.textureCoords);
        this.textureBuffer.position(0);
        GLES20.glGenTextures(MAX_TEXTURES, this.mTexturesIds, 0);
        checkGlError("Texture generate");
    }

    protected void setupCameraTexture() {
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, this.mTexturesIds[0]);
        checkGlError("Texture bind");
        this.mPreviewTexture = new SurfaceTexture(this.mTexturesIds[0]);
        this.mPreviewTexture.setOnFrameAvailableListener(this);
    }

    protected void setupShaders() {
        int vertexShaderHandle = GLES20.glCreateShader(35633);
        GLES20.glShaderSource(vertexShaderHandle, this.vertexShaderCode);
        GLES20.glCompileShader(vertexShaderHandle);
        checkGlError("Vertex shader compile");
        Log.d(TAG, "vertexShader info log:\n " + GLES20.glGetShaderInfoLog(vertexShaderHandle));
        int fragmentShaderHandle = GLES20.glCreateShader(35632);
        GLES20.glShaderSource(fragmentShaderHandle, this.fragmentShaderCode);
        GLES20.glCompileShader(fragmentShaderHandle);
        checkGlError("Pixel shader compile");
        Log.d(TAG, "fragmentShader info log:\n " + GLES20.glGetShaderInfoLog(fragmentShaderHandle));
        this.mCameraShaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(this.mCameraShaderProgram, vertexShaderHandle);
        GLES20.glAttachShader(this.mCameraShaderProgram, fragmentShaderHandle);
        GLES20.glLinkProgram(this.mCameraShaderProgram);
        checkGlError("Shader program compile");
        int[] status = new int[1];
        GLES20.glGetProgramiv(this.mCameraShaderProgram, 35714, status, 0);
        if (status[0] != 1) {
            Log.e("SurfaceTest", "Error while linking program:\n" + GLES20.glGetProgramInfoLog(this.mCameraShaderProgram));
        }
    }

    protected void onSetupComplete() {
        this.mOnRendererReadyListener.onRendererReady();
    }

    public synchronized void start() {
        initialize();
        if (this.mOnRendererReadyListener == null) {
            throw new RuntimeException("OnRenderReadyListener is not set! Set listener prior to calling start()");
        }
        super.start();
    }

    public void run() {
        Looper.prepare();
        this.mHandler = new RenderHandler(this);
        initGL();
        Looper.loop();
        deinitGL();
        this.mOnRendererReadyListener.onRendererFinished();
    }

    public void shutdown() {
        synchronized (this) {
            if (this.mIsRecording) {
                stopRecording();
            } else {
                this.mMediaRecorder.release();
            }
        }
        Looper.myLooper().quit();
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this) {
            boolean swapResult;
            updatePreviewTexture();
            if (this.mEglCore.getGlVersion() >= 3) {
                draw();
                if (this.mIsRecording) {
                    this.mRecordSurface.makeCurrentReadFrom(this.mWindowSurface);
                    GlUtil.checkGlError("before glBlitFramebuffer");
                    GLES30.glBlitFramebuffer(0, 0, this.mWindowSurface.getWidth(), this.mWindowSurface.getHeight(), 0, 0, this.mRecordSurface.getWidth(), this.mRecordSurface.getHeight(), 16384, 9728);
                    int err = GLES30.glGetError();
                    if (err != 0) {
                        Log.w(TAG, "ERROR: glBlitFramebuffer failed: 0x" + Integer.toHexString(err));
                    }
                    this.mRecordSurface.setPresentationTime(surfaceTexture.getTimestamp());
                    this.mRecordSurface.swapBuffers();
                }
                this.mWindowSurface.makeCurrent();
                swapResult = this.mWindowSurface.swapBuffers();
            } else {
                draw();
                if (this.mIsRecording) {
                    this.mRecordSurface.makeCurrent();
                    setViewport(this.mRecordSurface.getWidth(), this.mRecordSurface.getHeight());
                    draw();
                    this.mRecordSurface.setPresentationTime(surfaceTexture.getTimestamp());
                    this.mRecordSurface.swapBuffers();
                    setViewport(this.mWindowSurface.getWidth(), this.mWindowSurface.getHeight());
                }
                this.mWindowSurface.makeCurrent();
                swapResult = this.mWindowSurface.swapBuffers();
            }
            if (!swapResult) {
                Log.e(TAG, "swapBuffers failed, killing renderer thread");
                shutdown();
            }
        }
    }

    public void draw() {
        GLES20.glViewport(0, 0, this.mViewportWidth, this.mViewportHeight);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(16384);
        GLES20.glUseProgram(this.mCameraShaderProgram);
        setUniformsAndAttribs();
        setExtraTextures();
        drawElements();
        onDrawCleanup();
    }

    protected void updatePreviewTexture() {
        this.mPreviewTexture.updateTexImage();
        this.mPreviewTexture.getTransformMatrix(this.mCameraTransformMatrix);
    }

    protected void setUniformsAndAttribs() {
        int textureParamHandle = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "camTexture");
        int textureTranformHandle = GLES20.glGetUniformLocation(this.mCameraShaderProgram, "camTextureTransform");
        this.textureCoordinateHandle = GLES20.glGetAttribLocation(this.mCameraShaderProgram, "camTexCoordinate");
        this.positionHandle = GLES20.glGetAttribLocation(this.mCameraShaderProgram, "position");
        GLES20.glEnableVertexAttribArray(this.positionHandle);
        GLES20.glVertexAttribPointer(this.positionHandle, 2, 5126, false, 8, this.vertexBuffer);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, this.mTexturesIds[0]);
        GLES20.glUniform1i(textureParamHandle, 0);
        GLES20.glEnableVertexAttribArray(this.textureCoordinateHandle);
        GLES20.glVertexAttribPointer(this.textureCoordinateHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glUniformMatrix4fv(textureTranformHandle, 1, false, this.mCameraTransformMatrix, 0);
    }

    public int addTexture(int resource_id, String uniformName) {
        int texId = this.mTextureConsts[this.mTextureArray.size()];
        if (this.mTextureArray.size() + 1 < MAX_TEXTURES) {
            return addTexture(texId, BitmapFactory.decodeResource(this.mContext.getResources(), resource_id), uniformName, true);
        }
        throw new IllegalStateException("Too many textures! Please don't use so many :(");
    }

    public int addTexture(Bitmap bitmap, String uniformName) {
        int texId = this.mTextureConsts[this.mTextureArray.size()];
        if (this.mTextureArray.size() + 1 < MAX_TEXTURES) {
            return addTexture(texId, bitmap, uniformName, true);
        }
        throw new IllegalStateException("Too many textures! Please don't use so many :(");
    }

    public int addTexture(int texId, Bitmap bitmap, String uniformName, boolean recycle) {
        int num = this.mTextureArray.size() + 1;
        GLES20.glActiveTexture(texId);
        checkGlError("Texture generate");
        GLES20.glBindTexture(3553, this.mTexturesIds[num]);
        checkGlError("Texture bind");
        GLES20.glTexParameterf(3553, 10241, 9728.0f);
        GLES20.glTexParameterf(3553, 10240, 9728.0f);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        if (recycle) {
            bitmap.recycle();
        }
        Texture tex = new Texture(num, texId, uniformName);
        if (!this.mTextureArray.contains(tex)) {
            this.mTextureArray.add(tex);
            Log.d(TAG, "addedTexture() " + this.mTexturesIds[num] + " : " + tex);
        }
        return num;
    }

    public void updateTexture(int texNum, Bitmap drawingCache) {
        GLES20.glActiveTexture(this.mTextureConsts[texNum - 1]);
        checkGlError("Texture generate");
        GLES20.glBindTexture(3553, this.mTexturesIds[texNum]);
        checkGlError("Texture bind");
        GLUtils.texSubImage2D(3553, 0, 0, 0, drawingCache);
        checkGlError("Tex Sub Image");
        drawingCache.recycle();
    }

    protected void setExtraTextures() {
        for (int i = 0; i < this.mTextureArray.size(); i++) {
            Texture tex = (Texture) this.mTextureArray.get(i);
            int imageParamHandle = GLES20.glGetUniformLocation(this.mCameraShaderProgram, tex.uniformName);
            GLES20.glActiveTexture(tex.texId);
            GLES20.glBindTexture(3553, this.mTexturesIds[tex.texNum]);
            GLES20.glUniform1i(imageParamHandle, tex.texNum);
        }
    }

    protected void drawElements() {
        GLES20.glDrawElements(4, drawOrder.length, 5123, this.drawListBuffer);
    }

    protected void onDrawCleanup() {
        GLES20.glDisableVertexAttribArray(this.positionHandle);
        GLES20.glDisableVertexAttribArray(this.textureCoordinateHandle);
    }

    public void checkGlError(String op) {
        while (true) {
            int error = GLES20.glGetError();
            if (error != 0) {
                Log.e("SurfaceTest", op + ": glError " + GLUtils.getEGLErrorString(error));
            } else {
                return;
            }
        }
    }

    public void setViewport(int viewportWidth, int viewportHeight) {
        this.mViewportWidth = viewportWidth;
        this.mViewportHeight = viewportHeight;
    }

    public float[] getCameraTransformMatrix() {
        return this.mCameraTransformMatrix;
    }

    public SurfaceTexture getPreviewTexture() {
        return this.mPreviewTexture;
    }

    public RenderHandler getRenderHandler() {
        return this.mHandler;
    }

    public void setOnRendererReadyListener(OnRendererReadyListener listener) {
        this.mOnRendererReadyListener = listener;
    }

    public void startRecording(File outputFile) {
        this.mOutputFile = outputFile;
        if (this.mOutputFile == null) {
            throw new RuntimeException("No output file specified! Make sure to call setOutputFile prior to recording!");
        }
        synchronized (this) {
            this.mIsRecording = true;
            this.mMediaRecorder.start();
        }
    }

    public void stopRecording() {
        synchronized (this) {
            if (this.mIsRecording) {
                this.mMediaRecorder.stop();
                this.mMediaRecorder.release();
                this.mIsRecording = false;
                try {
                    copyFile(this.mTempOutputFile, this.mOutputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public boolean isRecording() {
        boolean z;
        synchronized (this) {
            z = this.mIsRecording;
        }
        return z;
    }

    protected void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    public void setCameraFragment(CameraFragment cameraFragment) {
        this.mCameraFragment = cameraFragment;
    }
}
