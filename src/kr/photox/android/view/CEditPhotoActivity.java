package kr.photox.android.view;

import java.io.File;

import kr.photox.android.R;
import kr.photox.android.manager.ApplicationManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CEditPhotoActivity extends Activity {
	private static final String TAG = "Camera Edit Photo Activity";

	/**
	 * UI Reference Init
	 */
	private ImageView mIv;

	private View mBottom;
	private ImageButton mCancelBtn;
	private ImageButton mConfirmBtn;
	private ImageButton mSettingBtn;

	/**
	 * 줌인 줌아웃을 위한 변수들입니다. 전역변수로 사용해야만 합니다.
	 */

	private Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;

	/**
	 * 이미지 위치 편집을 위한 캐싱을 해둡니다.
	 */
	private int window_width = -1;
	private int img_width = -1;
	private int img_height = -1;

	/**
	 * 사진 이미지 관련
	 */

	private int mMissionId = -1;
	private String mMissionTitle = "";
	private String mDecalMessage = "";
	private int mDecalPosition = -1;
	private String mImgPath = "";
	private Matrix mMatrix = new Matrix();
	private int mRotation = 0;
	private boolean fromAlbum = false;

	/**
	 * 뒤로 돌아가는 경우를 대비해 사진이미지 관련 변수의 old_version을 준비합니다.
	 */

	private Matrix mMatrixOld = new Matrix();
	private int mRotationOld = 0;

	/**
	 * onCreate Method
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_edit_photo_activity);
		Log.d("camera edit photo activity", "called");

		/*
		 * 이미지 데이터를 얻어옵니다.
		 */

		Bundle bundle = getIntent().getExtras();

		mMissionId = bundle.getInt("mission_id", -1);
		mMissionTitle = bundle.getString("mission_title", "");
		mDecalMessage = bundle.getString("decal_message");
		mDecalPosition = bundle.getInt("decal_position");
		mImgPath = bundle.getString("img_path", "");
		mRotation = bundle.getInt("rotation");
		mRotationOld = mRotation;

		float[] matrix_values = new float[9];
		for (int i = 0; i < 9; i++) {
			matrix_values[i] = bundle.getFloat("matrix_value"
					+ Integer.toString(i));
		}
		mMatrix.setValues(matrix_values);
		mMatrixOld.setValues(matrix_values);

		fromAlbum = bundle.getBoolean("from_album");

		/*
		 * UI Reference
		 */

		View mActionbar = (View) findViewById(R.id.actionbar);
		TextView mTitleTv = (TextView) mActionbar.findViewById(R.id.title_tv);
		ImageButton mBackBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_left_btn);
		ImageButton mRightBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_right_btn);
		TextView mSubtitleTv = (TextView) findViewById(R.id.subtitle_tv);

		mIv = (ImageView) findViewById(R.id.iv);

		mBottom = (View) findViewById(R.id.bottom);
		mCancelBtn = (ImageButton) mBottom.findViewById(R.id.left_btn);
		mConfirmBtn = (ImageButton) mBottom.findViewById(R.id.centre_btn);
		mSettingBtn = (ImageButton) mBottom.findViewById(R.id.right_btn);
		Log.d(TAG, "findViewById completed");

		/*
		 * 기본 UI의 Visibillity를 설정합니다.
		 */

		mTitleTv.setText("사진편집");
		mSubtitleTv.setText(this.mMissionTitle);
		mBackBtn.setPadding(0, 0, 0, 0);
		mBackBtn.setImageResource(R.drawable.base_actionbar_dir_left);
		mBackBtn.setVisibility(View.INVISIBLE);
		mRightBtn.setVisibility(View.INVISIBLE);

		mCancelBtn.setImageResource(R.drawable.camera_cancel);
		mCancelBtn.setBackgroundResource(android.R.color.transparent);
		mConfirmBtn.setImageResource(R.drawable.camera_confirm);
		mSettingBtn.setImageResource(R.drawable.camera_rotate_right);

		/*
		 * 이미지 위치 편집을 위한 상수를 초기화 해둡니다.
		 */

		window_width = ((ApplicationManager) getApplicationContext())
				.getWindowSize("width");

		/*
		 * UI size Init 1. 정사각형 프레임을 만들기 위해 mFl을 가로사이즈로 고정하였습니다. 2. 카메라 뷰의 비율
		 * 문제를 해결하기 위해 mIv를 윈도우 크기 그대로 설정하였습니다.
		 */

		mIv.setLayoutParams(new LinearLayout.LayoutParams(window_width,
				window_width));

		/*
		 * 버튼 클릭 리스너를 지정합니다.
		 */
		// mRotateBtn.setOnClickListener(onClickListener);

		mCancelBtn.setOnClickListener(onClickListener);
		mSettingBtn.setOnClickListener(onClickListener);
		mConfirmBtn.setOnClickListener(onClickListener);

		/*
		 * 이미지를 지정합니다.
		 */

		new ProcessingGalleryImageTask().execute(mImgPath);
		mIv.setImageMatrix(mMatrix);

	}

	/**
	 * 
	 */

	private class ProcessingGalleryImageTask extends
			AsyncTask<String, Integer, Bitmap> {

		protected void onPreExecute() {

		}

		protected void onCancelled() {

		}

		protected Bitmap doInBackground(String... img_path) {

			Bitmap bitmap = null;

			File imgFile = new File(img_path[0]);
			if (imgFile.exists()) {
				bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			} else {
				Log.e(TAG, "there is no file");
			}

			return bitmap;

		}

		protected void onPostExecute(Bitmap bitmap) {
			/*
			 * 이미지뷰에 비트맵을 설정합니다.
			 */

			Log.d(TAG, "이미지설정 완료 ");
			mIv.setImageBitmap(bitmap);

			/*
			 * 
			 */

			img_width = bitmap.getWidth();
			img_height = bitmap.getHeight();

			/*
			 * 이미지 줌인 줌아웃 리스너를 설정합니다.
			 */
			mIv.setOnTouchListener(onTouchListener);
		}

	}

	/*
	 * Pause (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */

	@Override
	protected void onPause() {
		super.onPause();

		// ((ApplicationManager) getApplicationContext()).setCameraImg(null);

	}

	/**
	 * \ Btn OnClickListener
	 */

	OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			clickListenerHandler(v.getId());
		}
	};

	private void clickListenerHandler(int id) {
		switch (id) {
		case R.id.function_2_btn:

			break;
		case R.id.left_btn:
			onBackPressed();
			break;
		case R.id.right_btn:
			mRotation += 90;
			if (mRotation == 360) {
				mRotation = 0;
			}
			mMatrix.postRotate(90, window_width / 2, window_width / 2);
			mIv.setImageMatrix(mMatrix);
			break;
		case R.id.centre_btn:
			startEditDecalActivity(false);
			break;
		default:
			break;
		}
	};

	/**
	 * 이미지 줌인 줌아웃을 구현하는 부분입니다.
	 */

	private OnTouchListener onTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			ImageView view = (ImageView) v;
			float scale;

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: // first finger down only
				savedMatrix.set(mMatrix);
				start.set(event.getX(), event.getY());
				Log.d(TAG, "mode=DRAG");
				mode = DRAG;
				break;

			case MotionEvent.ACTION_UP: // first finger lifted

			case MotionEvent.ACTION_POINTER_UP: // second finger lifted

				mode = NONE;
				Log.d(TAG, "mode=NONE");
				break;

			case MotionEvent.ACTION_POINTER_DOWN: // first and second finger
													// down

				oldDist = spacing(event);
				Log.d(TAG, "oldDist=" + oldDist);

				// if (oldDist > 5f) {
				savedMatrix.set(mMatrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");

				break;

			case MotionEvent.ACTION_MOVE:

				if (mode == DRAG) {
					mMatrix.set(savedMatrix);
					mMatrix.postTranslate(event.getX() - start.x, event.getY()
							- start.y);

				} else if (mode == ZOOM) {
					float newDist = spacing(event);
					Log.d(TAG, "newDist=" + newDist);

					scale = newDist / oldDist;

					mMatrix.set(savedMatrix);
					mMatrix.postScale(scale, scale, mid.x, mid.y);

				}
				break;
			}

			mMatrix = getValidMatrix(mMatrix);

			view.setImageMatrix(mMatrix);

			return true;
		}
	};

	/**
	 * Matrix를 유효한 Matrix로 바꾸어 줍니다. 여기는 재귀함수 입니다. 주의하십시오.
	 */

	private Matrix getValidMatrix(Matrix mMatrix) {

		/*
		 * 현재 매트릭스의 값을 알아봅니다.
		 */

		float[] f = new float[9];
		mMatrix.getValues(f);
		float trans_x = f[Matrix.MTRANS_X];
		Log.d("trans_x", Float.toString(trans_x));
		float trans_y = f[Matrix.MTRANS_Y];
		Log.d("trans_y", Float.toString(trans_y));
		float scale_current = Math.abs(f[Matrix.MSCALE_X]);
		Log.d("scale_current",
				"MSCALE_X : " + Float.toString(f[Matrix.MSCALE_X]));
		Log.d("scale_current",
				"MSCALE_Y : " + Float.toString(f[Matrix.MSCALE_Y]));
		float skew_current = Math.abs(f[Matrix.MSKEW_X]);
		Log.d("scale_current", "MSKEW_X : " + Float.toString(f[Matrix.MSKEW_X]));
		Log.d("scale_current", "MSKEW_Y : " + Float.toString(f[Matrix.MSKEW_Y]));

		/*
		 * 비율을 체크하고 수정합니다.
		 */

		if (mRotation == 0) {
			if (scale_current < 1) {
				mMatrix.postScale(1 / scale_current, 1 / scale_current, mid.x,
						mid.y);
				return getValidMatrix(mMatrix);
			} else if (trans_x > 0) {
				mMatrix.postTranslate(-trans_x, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_y > 0) {
				mMatrix.postTranslate(0, -trans_y);
				return getValidMatrix(mMatrix);
			} else if (trans_x < window_width - img_width * scale_current) {
				mMatrix.postTranslate(window_width - img_width * scale_current
						- trans_x, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_y < window_width - img_height * scale_current) {
				mMatrix.postTranslate(0, window_width - img_height
						* scale_current - trans_y);
				return getValidMatrix(mMatrix);
			}

		} else if (mRotation == 180) {
			if (scale_current < 1) {
				mMatrix.postScale(1f / scale_current, 1f / scale_current,
						mid.x, mid.y);
				return getValidMatrix(mMatrix);
			} else if (trans_x < window_width) {
				mMatrix.postTranslate(window_width - trans_x, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_y < window_width) {
				mMatrix.postTranslate(0, window_width - trans_y);
				return getValidMatrix(mMatrix);
			} else if (trans_x > img_width * (scale_current)) {
				mMatrix.postTranslate(img_width * (scale_current) - trans_x, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_y > img_height * (scale_current)) {
				mMatrix.postTranslate(0, img_height * (scale_current) - trans_y);
				return getValidMatrix(mMatrix);
			}

		} else if (mRotation == 90) {
			if (skew_current < 1) {
				mMatrix.postScale(1f / skew_current, 1f / skew_current, 0, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_x < img_height) {
				mMatrix.postTranslate(img_height - trans_x, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_y > 0) {
				mMatrix.postTranslate(0, -trans_y);
				return getValidMatrix(mMatrix);
			} else if (trans_x > img_height * (skew_current)) {
				mMatrix.postTranslate(img_height * (skew_current) - trans_x, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_y < -img_width * (skew_current) + window_width) {
				mMatrix.postTranslate(0, -img_width * (skew_current)
						+ window_width - trans_y);
				return getValidMatrix(mMatrix);
			}
		} else if (mRotation == 270) {
			if (skew_current < 1) {
				mMatrix.postScale(1f / skew_current, 1f / skew_current, 0, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_x > 0) {
				mMatrix.postTranslate(-trans_x, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_y < window_width) {
				mMatrix.postTranslate(0, window_width - trans_y);
				return getValidMatrix(mMatrix);
			} else if (trans_x < window_width - img_height * skew_current) {
				mMatrix.postTranslate(window_width - img_height * skew_current
						- trans_x, 0);
				return getValidMatrix(mMatrix);
			} else if (trans_y > img_width * (skew_current)) {
				mMatrix.postTranslate(0, img_width * (skew_current) - trans_y);
				return getValidMatrix(mMatrix);
			}
		}
		return mMatrix;

	}

	/*
	 * --------------------------------------------------------------------------
	 * Method: spacing Parameters: MotionEvent Returns: float Description:
	 * checks the spacing between the two fingers on touch
	 * ----------------------------------------------------
	 */

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/*
	 * --------------------------------------------------------------------------
	 * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
	 * Description: calculates the midpoint between the two fingers
	 * ------------------------------------------------------------
	 */

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/**
	 * Back Key Listener
	 */

	@Override
	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called");

		if (fromAlbum) {
			startCameraActivity();
		} else {
			startEditDecalActivity(true);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			finish();
		}
	}

	/**
	 * 
	 */

	public void startEditDecalActivity(boolean isOld) {
		// if (mFromAlbum) {
		Intent intent = new Intent(getApplicationContext(),
				CEditDecalActivity.class);
		/*
		 * 필요한 정보를 넣습니다. 매트릭스는 모두 float으로 넘깁니다.
		 */
		intent.putExtra("mission_id", this.mMissionId);
		intent.putExtra("mission_title", this.mMissionTitle);

		intent.putExtra("decal_position", this.mDecalPosition);
		intent.putExtra("decal_message", this.mDecalMessage);

		intent.putExtra("img_path", this.mImgPath);

		float[] matrix_values = new float[9];

		if (isOld) {
			intent.putExtra("rotation", this.mRotationOld);
			this.mMatrixOld.getValues(matrix_values);

		} else {
			intent.putExtra("rotation", this.mRotation);
			this.mMatrix.getValues(matrix_values);

		}

		for (int i = 0; i < 9; i++) {
			intent.putExtra("matrix_value" + Integer.toString(i),
					matrix_values[i]);
		}
		startActivity(intent);

		finish();
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	/**
	 * Camera Activity Start
	 * 
	 * @param id
	 */

	public void startCameraActivity() {

		/*
		 * 카메라 실행에 필요한 값을 대입합니다.
		 */

		Intent intent = new Intent(CEditPhotoActivity.this, CActivity.class);
		intent.putExtra("mission_id", this.mMissionId);
		intent.putExtra("mission_title", this.mMissionTitle);
		intent.putExtra("decal_position", this.mDecalPosition);
		intent.putExtra("decal_message", this.mDecalMessage);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		finish();

	}
}
