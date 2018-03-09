package kr.photox.android.lib;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class CameraView extends ViewGroup implements SurfaceHolder.Callback {
	private final String TAG = "Preview";

	SurfaceView mSurfaceView;
	SurfaceHolder mHolder;
	Size mPreviewSize;
	List<Size> mSupportedPreviewSizes;

	public Camera mCamera;

	public CameraView(Context context, SurfaceView sv) {
		super(context);

		mSurfaceView = sv;
		// addView(mSurfaceView);

		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	public void setCamera(Camera camera) {

		if (mCamera == camera) {
			return;
		}

		stopPreviewAndFreeCamera();

		mCamera = camera;

		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			requestLayout();

			// get Camera parameters
			Camera.Parameters params = mCamera.getParameters();

			try {
				mCamera.setPreviewDisplay(mHolder);
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<String> focusModes = params.getSupportedFocusModes();
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				// set the focus mode
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				// set Camera parameters
				mCamera.setParameters(params);
			}

			/*
			 * Important: Call startPreview() to start updating the preview
			 * surface. Preview must be started before you can take a picture.
			 */
			mCamera.startPreview();

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// We purposely disregard child measurements because act as a
		// wrapper to a SurfaceView that centers the camera preview instead
		// of stretching it.
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);

		Log.d("onMeasure width : ", Integer.toString(width));
		Log.d("onMeasure height : ", Integer.toString(height));

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
					height);
			// mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes,
			// height, width);

		}

		Log.d("mPreveiwSize width : ", Integer.toString(mPreviewSize.width));
		Log.d("mPreveiwSize height : ", Integer.toString(mPreviewSize.height));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			Log.d("onLayout width", Integer.toString(width));
			Log.d("onLayout height", Integer.toString(height));

			int previewWidth = width;
			int previewHeight = height;
			if (mPreviewSize != null) {
				previewWidth = mPreviewSize.width;
				previewHeight = mPreviewSize.height;
			}

			Log.d("mPreveiwSize width : ", Integer.toString(mPreviewSize.width));
			Log.d("mPreveiwSize height : ",
					Integer.toString(mPreviewSize.height));

			// Center the child SurfaceView within the parent.
			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height
						/ previewHeight;
				child.layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);

				Log.d("scaledChildWidth : ", Integer.toString(scaledChildWidth));

			} else {
				final int scaledChildHeight = previewHeight * width
						/ previewWidth;
				child.layout(0, (height - scaledChildHeight) / 2, width,
						(height + scaledChildHeight) / 2);

				Log.d("scaledChildHeight : ",
						Integer.toString(scaledChildHeight));
			}
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.

		// holder.setFixedSize(mPreviewSize.width, mPreviewSize.height);

		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
			}
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		if (mCamera != null) {
			/*
			 * Call stopPreview() to stop updating the preview surface.
			 */
			mCamera.stopPreview();
		}
	}

	/**
	 * When this function returns, mCamera will be null.
	 */
	public void stopPreviewAndFreeCamera() {

		if (mCamera != null) {
			/*
			 * Call stopPreview() to stop updating the preview surface.
			 */
			mCamera.stopPreview();

			/*
			 * Important: Call release() to release the camera for use by other
			 * applications. Applications should release the camera immediately
			 * in onPause() (and re-open() it in onResume()).
			 */
			mCamera.release();

			mCamera = null;
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.01;
		float targetRatio = (float) w / h;
		Log.d("target ratio", Float.toString(targetRatio));
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;
		Log.d("tartgetHeight", Integer.toString(h));

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			// double ratio = (double) size.width / size.height;
			float ratio = (float) size.height / size.width;

			Log.d("ratio", Double.toString(ratio));

			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
				Log.d("minDiff", Double.toString(minDiff));
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
					Log.d("minDiff", Double.toString(minDiff));
				}
			}
		}
		return optimalSize;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		if (mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			// parameters.setPreviewSize(mPreviewSize.width,
			// mPreviewSize.width);

			// holder.setFixedSize(mPreviewSize.width, mPreviewSize.height);

			// holder.setFixedSize(mPreviewSize.width, mPreviewSize.height);
			// holder.setSizeFromLayout();

			requestLayout();

			Log.d("mPreveiwSize width : ", Integer.toString(mPreviewSize.width));
			Log.d("mPreveiwSize height : ",
					Integer.toString(mPreviewSize.height));

			mCamera.setParameters(parameters);
			mCamera.startPreview();
		}
	}

	public Size getPreviewSize() {
		return this.mPreviewSize;
	}

}