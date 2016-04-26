package com.floppyinfant.android.game.libs;

import java.io.File;
import java.io.FileOutputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;

import com.floppyinfant.android.game.R;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * OpenCV4Android
 * Computer Vision
 * 
 * http://opencv.org/platforms/android.html
 * http://docs.opencv.org/
 * http://docs.opencv.org/java/3.0.0/
 * 
 * PacktLib:
 * https://www.packtpub.com/application-development/android-application-programming-opencv-3
 * https://www.packtpub.com/application-development/opencv-android-programming-example
 * https://www.packtpub.com/application-development/mastering-opencv-android-application-programming
 * 
 * https://www.packtpub.com/application-development/learning-opencv-3-computer-vision-python-second-edition
 * 
 * https://www.packtpub.com/application-development/opencv-computer-vision-application-programming-video
 * https://www.packtpub.com/application-development/opencv-computer-vision-application-programming-cookbook-second-edition
 * 
 * 
 * HOWTO:
 * include Project Dependency "/OpenCV Library - 3.0.0"
 * class implements CvCameraViewListener2
 * 
 * @see OpenCV/android-sdk/samples
 * 
 * 
 * Notes:
 * Feature Detection
 * Descriptor Extraction
 * Descriptor Matching Algorithms
 * 
 * 
 * @author TM
 *
 */
public class OpenCV extends Activity implements CvCameraViewListener2, PictureCallback {
	
	private static String TAG = OpenCV.class.getSimpleName();
	
	private CameraBridgeViewBase mOpenCvCameraView;
	
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

	private Camera mCamera;

	private File mPictureFileName;
    
    // -------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.tutorial3_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial3_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
		
        // Android API
		mCamera = Camera.open(0);
		mCamera.release();
		
		mCamera.getParameters().getSupportedPictureSizes();
		
		mCamera.startPreview();
        mCamera.setPreviewCallback(null);
        mCamera.takePicture(null, null, this);
        
		
		Log.i(TAG, "started Activity");
	}

	// -------------------------------------------------------------------------

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

	// -------------------------------------------------------------------------

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		return null;
	}
	

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(null);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);

            fos.write(data);
            fos.close();

        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

    }
}
