package com.androidrecipes.cameraoverlay;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import java.util.Arrays;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NewCameraManager extends CameraDevice.StateCallback {
    private final Handler mCameraHandler;
    private final CameraManager mCameraManager;
    private Surface mSurface;
    private CameraCaptureSession.StateCallback mStateCallback;
    private CaptureRequest mCaptureRequest;

    public NewCameraManager(Context context) {
        HandlerThread cameraThread = new HandlerThread("Camera");
        cameraThread.start();
        mCameraHandler = new Handler(cameraThread.getLooper());
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        String frontCameraId = getFrontFacingCamera(mCameraManager);
        if (frontCameraId != null) {
            mCameraManager.openCamera(frontCameraId, this, mCameraHandler);
        }
    }

    private String getFrontFacingCamera(CameraManager manager) throws CameraAccessException {
        String[] cameraIds = manager.getCameraIdList();
        for (String cameraId : cameraIds) {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (CameraCharacteristics.LENS_FACING_FRONT == facing) {
                return cameraId;
            }
        }
        return null;
    }

    @Override
    public void onOpened(CameraDevice cameraDevice) {
        mCaptureRequest = cameraDevice
                .createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                .build();
        mStateCallback = new CaptureSessionStateCallback();
        cameraDevice.createCaptureSession(Arrays.asList(mSurface), mStateCallback, mCameraHandler);
    }

    @Override
    public void onDisconnected(CameraDevice cameraDevice) {

    }

    @Override
    public void onError(CameraDevice cameraDevice, int i) {

    }

    private class CaptureSessionStateCallback extends CameraCaptureSession.StateCallback {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            cameraCaptureSession.capture(mCaptureRequest, new CameraCaptureCallback(), mCameraHandler);
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

        }
    }

    private class CameraCaptureCallback extends CameraCaptureSession.CaptureCallback {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            result.
        }
    }
}
