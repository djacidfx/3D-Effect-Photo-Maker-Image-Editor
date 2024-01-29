package com.photoeditor.photoeffect3d.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CameraFragment extends Fragment {
    static final /* synthetic */ boolean $assertionsDisabled = (!CameraFragment.class.desiredAssertionStatus() ? true : false);
    public static final int CAMERA_FORWARD = 1;
    public static final int CAMERA_PRIMARY = 0;
    private static final String TAG = "CameraFragment";
    private static CameraFragment __instance;
    private CaptureCallback captureCallback = new CaptureCallback() {
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private CameraDevice mCameraDevice;
    private boolean mCameraIsOpen = $assertionsDisabled;
    private Semaphore mCameraOpenCloseLock = new Semaphore(CAMERA_FORWARD);
    protected int mCameraToUse = CAMERA_PRIMARY;
    private OnViewportSizeUpdatedListener mOnViewportSizeUpdatedListener;
    private Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private Size mPreviewSize;
    private SurfaceTexture mPreviewSurface;
    private float mPreviewSurfaceAspectRatio;
    private StateCallback mStateCallback = new StateCallback() {
        public void onOpened(CameraDevice cameraDevice) {
            CameraFragment.this.mCameraOpenCloseLock.release();
            CameraFragment.this.mCameraDevice = cameraDevice;
            CameraFragment.this.mCameraIsOpen = true;
            if (CameraFragment.this.mCameraDevice != null) {
                CameraFragment.this.startPreview();
            }
            if (CameraFragment.this.mTextureView != null) {
                CameraFragment.this.configureTransform(CameraFragment.this.mTextureView.getWidth(), CameraFragment.this.mTextureView.getHeight());
            }
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            CameraFragment.this.mCameraOpenCloseLock.release();
            cameraDevice.close();
            CameraFragment.this.mCameraDevice = null;
            CameraFragment.this.mCameraIsOpen = CameraFragment.$assertionsDisabled;
        }

        public void onError(CameraDevice cameraDevice, int error) {
            CameraFragment.this.mCameraOpenCloseLock.release();
            cameraDevice.close();
            CameraFragment.this.mCameraDevice = null;
            CameraFragment.this.mCameraIsOpen = CameraFragment.$assertionsDisabled;
            Log.e(CameraFragment.TAG, "CameraDevice.StateCallback onError() " + error);
            Activity activity = CameraFragment.this.getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    };
    private TextureView mTextureView;
    private Size mVideoSize;
    private float mVideoSizeAspectRatio;

    public static class ErrorDialog extends DialogFragment {
        @SuppressLint("ResourceType")
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity).setMessage("This device doesn't support Camera2 API.").setPositiveButton(17039370, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    activity.finish();
                }
            }).create();
        }
    }

    public interface OnViewportSizeUpdatedListener {
        void onViewportSizeUpdated(int i, int i2);
    }

    public static CameraFragment getInstance() {
        if (__instance == null) {
            __instance = new CameraFragment();
            __instance.setRetainInstance(true);
        }
        return __instance;
    }

    public void onResume() {
        super.onResume();
        startBackgroundThread();
    }

    public void onPause() {
        super.onPause();
        stopBackgroundThread();
    }

    private void startBackgroundThread() {
        this.mBackgroundThread = new HandlerThread("CameraBackground");
        this.mBackgroundThread.start();
        this.mBackgroundHandler = new Handler(this.mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        this.mBackgroundThread.quitSafely();
        try {
            this.mBackgroundThread.join();
            this.mBackgroundThread = null;
            this.mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void swapCamera() {
        closeCamera();
        if (this.mCameraToUse == CAMERA_FORWARD) {
            this.mCameraToUse = CAMERA_PRIMARY;
        } else {
            this.mCameraToUse = CAMERA_FORWARD;
        }
        openCamera();
    }

    public void openCamera() {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (this.mCameraDevice == null || !this.mCameraIsOpen) {
                CameraManager manager = (CameraManager) activity.getSystemService("camera");
                try {
                    if (this.mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                        String[] cameraList = manager.getCameraIdList();
                        if (this.mCameraToUse >= cameraList.length) {
                            this.mCameraToUse = CAMERA_PRIMARY;
                        }
                        String cameraId = cameraList[this.mCameraToUse];
                        StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) manager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        this.mVideoSize = chooseVideoSize(streamConfigurationMap.getOutputSizes(MediaRecorder.class));
                        this.mPreviewSize = chooseVideoSize(streamConfigurationMap.getOutputSizes(SurfaceTexture.class));
                        updateViewportSize(this.mVideoSizeAspectRatio, this.mPreviewSurfaceAspectRatio);
                        Log.i(TAG, "openCamera() videoSize: " + this.mVideoSize + " previewSize: " + this.mPreviewSize);
                        if (ActivityCompat.checkSelfPermission(getContext(), "android.permission.CAMERA") == 0) {
                            manager.openCamera(cameraId, this.mStateCallback, null);
                            return;
                        }
                        return;
                    }
                    throw new RuntimeException("Time out waiting to lock camera opening.");
                } catch (CameraAccessException e) {
                    Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_LONG).show();
                    activity.finish();
                } catch (NullPointerException e2) {
                    e2.printStackTrace();
                    new ErrorDialog().show(getFragmentManager(), "dialog");
                } catch (InterruptedException e3) {
                    throw new RuntimeException("Interrupted while trying to lock camera opening.");
                }
            }
        }
    }

    public void updateViewportSize(float videoAspect, float surfaceAspect) {
        int vpW;
        int vpH;
        int sw = this.mTextureView.getWidth();
        int sh = this.mTextureView.getHeight();
        if (videoAspect == surfaceAspect) {
            vpW = sw;
            vpH = sh;
        } else if (videoAspect < surfaceAspect) {
            float  ratio = ((float) sw) / ((float) this.mVideoSize.getHeight());
            vpW = (int) (((float) this.mVideoSize.getHeight()) * ratio);
            vpH = (int) (((float) this.mVideoSize.getWidth()) * ratio);
        } else {
            float   ratio = ((float) sw) / ((float) this.mVideoSize.getWidth());
            vpW = (int) (((float) this.mVideoSize.getWidth()) * ratio);
            vpH = (int) (((float) this.mVideoSize.getHeight()) * ratio);
        }
        if (this.mOnViewportSizeUpdatedListener != null) {
            this.mOnViewportSizeUpdatedListener.onViewportSizeUpdated(vpW, vpH);
        }
    }

    private Size chooseVideoSize(Size[] choices) {
        int sw = this.mTextureView.getWidth();
        this.mPreviewSurfaceAspectRatio = ((float) sw) / ((float) this.mTextureView.getHeight());
        Log.i(TAG, "chooseVideoSize() for landscape:" + (this.mPreviewSurfaceAspectRatio > 1.0f ? true : $assertionsDisabled) + " aspect: " + this.mPreviewSurfaceAspectRatio + " : " + Arrays.toString(choices));
        Size sizeToReturn = null;
        int length;
        int i;
        Size size;
        if (this.mPreviewSurfaceAspectRatio > 1.0f) {
            length = choices.length;
            for (i = CAMERA_PRIMARY; i < length; i += CAMERA_FORWARD) {
                size = choices[i];
                if (size.getHeight() == (size.getWidth() * 9) / 16 && size.getHeight() <= 1080) {
                    sizeToReturn = size;
                }
            }
            if (sizeToReturn == null) {
                sizeToReturn = choices[CAMERA_PRIMARY];
            }
            this.mVideoSizeAspectRatio = ((float) sizeToReturn.getWidth()) / ((float) sizeToReturn.getHeight());
        } else {
            ArrayList<Size> potentials = new ArrayList();
            length = choices.length;
            for (i = CAMERA_PRIMARY; i < length; i += CAMERA_FORWARD) {
                size = choices[i];
                if (((float) size.getHeight()) / ((float) size.getWidth()) == this.mPreviewSurfaceAspectRatio) {
                    potentials.add(size);
                }
            }
            Log.i(TAG, "---potentials: " + potentials.size());
            if (potentials.size() > 0) {
                Size potential;
                Iterator it = potentials.iterator();
                while (it.hasNext()) {
                    potential = (Size) it.next();
                    if (potential.getHeight() == sw) {
                        sizeToReturn = potential;
                        break;
                    }
                }
                if (sizeToReturn == null) {
                    Log.i(TAG, "---no perfect match, check for 'normal'");
                }
                it = potentials.iterator();
                while (it.hasNext()) {
                    potential = (Size) it.next();
                    if (potential.getHeight() != 1080) {
                        if (potential.getHeight() == 720) {
                        }
                    }
                    sizeToReturn = potential;
                }
                if (sizeToReturn == null) {
                    Log.i(TAG, "---no 'normal' match, return largest ");
                }
                if (sizeToReturn == null) {
                    sizeToReturn = (Size) potentials.get(CAMERA_PRIMARY);
                }
            }
            if (sizeToReturn == null) {
                sizeToReturn = choices[CAMERA_PRIMARY];
            }
            this.mVideoSizeAspectRatio = ((float) sizeToReturn.getHeight()) / ((float) sizeToReturn.getWidth());
        }
        return sizeToReturn;
    }

    public void closeCamera() {
        try {
            this.mCameraOpenCloseLock.acquire();
            if (this.mCameraDevice != null) {
                this.mCameraDevice.close();
                this.mCameraDevice = null;
                this.mCameraIsOpen = $assertionsDisabled;
            }
            this.mCameraOpenCloseLock.release();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } catch (Throwable th) {
            this.mCameraOpenCloseLock.release();
        }
    }

    private void startPreview() {
        if (this.mCameraDevice != null && this.mTextureView.isAvailable() && this.mPreviewSize != null) {
            try {
                this.mPreviewSurface.setDefaultBufferSize(this.mPreviewSize.getWidth(), this.mPreviewSize.getHeight());
                this.mPreviewBuilder = this.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                List<Surface> surfaces = new ArrayList();
                if ($assertionsDisabled || this.mPreviewSurface != null) {
                    Surface previewSurface = new Surface(this.mPreviewSurface);
                    surfaces.add(previewSurface);
                    this.mPreviewBuilder.addTarget(previewSurface);
                    this.mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            CameraFragment.this.mPreviewSession = cameraCaptureSession;
                            CameraFragment.this.updatePreview();
                        }

                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            Activity activity = CameraFragment.this.getActivity();
                            Log.e(CameraFragment.TAG, "config failed: " + cameraCaptureSession);
                            if (activity != null) {
                                Toast.makeText(activity, "CaptureSession Config Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, this.mBackgroundHandler);
                    return;
                }
                throw new AssertionError();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void updatePreview() {
        if (this.mCameraDevice != null) {
            try {
                this.mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, Integer.valueOf(CAMERA_FORWARD));
                this.mPreviewSession.setRepeatingRequest(this.mPreviewBuilder.build(), this.captureCallback, this.mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (this.mTextureView != null && this.mPreviewSize != null && activity != null) {
            Matrix matrix = new Matrix();
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            if (CAMERA_FORWARD == rotation || 3 == rotation) {
                RectF viewRect = new RectF(0.0f, 0.0f, (float) viewWidth, (float) viewHeight);
                float centerX = viewRect.centerX();
                float centerY = viewRect.centerY();
                RectF bufferRect = new RectF(0.0f, 0.0f, (float) this.mPreviewSize.getHeight(), (float) this.mPreviewSize.getWidth());
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                matrix.setRectToRect(viewRect, bufferRect, ScaleToFit.FILL);
                float scale = Math.max(((float) viewHeight) / ((float) this.mPreviewSize.getHeight()), ((float) viewWidth) / ((float) this.mPreviewSize.getWidth()));
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate((float) ((rotation - 2) * 90), centerX, centerY);
            }
            this.mTextureView.setTransform(matrix);
        }
    }

    public void setTextureView(TextureView textureView) {
        this.mTextureView = textureView;
    }

    public Size getVideoSize() {
        return this.mVideoSize;
    }

    public int getCurrentCameraType() {
        return this.mCameraToUse;
    }

    public void setCameraToUse(int camera_id) {
        this.mCameraToUse = camera_id;
    }

    public void setPreviewTexture(SurfaceTexture previewSurface) {
        this.mPreviewSurface = previewSurface;
    }

    public void setOnViewportSizeUpdatedListener(OnViewportSizeUpdatedListener listener) {
        this.mOnViewportSizeUpdatedListener = listener;
    }
}
