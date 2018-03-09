package kr.photox.android.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.DecalListApi;
import kr.photox.android.api.ShotAddApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Decal;
import kr.photox.android.model.Model;
import kr.photox.android.model.Shot;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class CEditDecalActivity extends SherlockFragmentActivity {
	private final String TAG = "Camera Edit Decal Activity";

	/**
	 * UI Reference Init
	 */
	private View mActionbar;
	private TextView mTitleTv;
	private ImageButton mBackBtn;
	private ImageButton mRightBtn;

	private FrameLayout mFl;
	private ImageView mIv;

	private ViewPager mPager;
	private PageIndicator mIndicator;

	private View mBottom;
	private ImageButton mCancelBtn;
	private ImageButton mConfirmBtn;
	private ImageButton mSettingBtn;

	private View mLoadingView;

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

	/**
	 * 
	 */

	int window_width = -1;
	int window_height = -1;

	int actionbar_height = -1;
	int status_bar_height = -1;

	/**
	 * View Pager 관련
	 */

	private ArrayList<Model> models = new ArrayList<Model>();
	private CameraDecalFragmentAdapter mAdapter;

	/**
	 * 
	 */

	private ProgressDialog dialog = null;

	/**
	 * onCreate Method
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_edit_decal_activity);
		ApplicationManager am = (ApplicationManager) getApplicationContext();
		Log.d("camera edit decal activity", "called");

		/*
		 * 이미지 데이터를 얻어옵니다.
		 */

		Bundle bundle = getIntent().getExtras();

		mMissionId = bundle.getInt("mission_id", -1);
		Log.d(TAG, "mission_id" + mMissionId);
		mMissionTitle = bundle.getString("mission_title", "");
		Log.d(TAG, "mission_title" + mMissionTitle);
		mDecalMessage = bundle.getString("decal_message");
		mDecalPosition = bundle.getInt("decal_position", 0);
		mImgPath = bundle.getString("img_path", "");
		Log.d(TAG, "img_path" + mImgPath);
		mRotation = bundle.getInt("rotation");

		mMatrix = new Matrix();
		float[] matrix_values = new float[9];
		for (int i = 0; i < 9; i++) {
			matrix_values[i] = bundle.getFloat("matrix_value"
					+ Integer.toString(i));
		}
		mMatrix.setValues(matrix_values);

		/*
		 * UI Reference
		 */

		mActionbar = (View) findViewById(R.id.actionbar);
		mTitleTv = (TextView) mActionbar.findViewById(R.id.title_tv);
		mBackBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_left_btn);
		mRightBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_right_btn);

		TextView mSubtitleTv = (TextView) findViewById(R.id.subtitle_tv);

		mFl = (FrameLayout) findViewById(R.id.fl);
		mIv = (ImageView) findViewById(R.id.iv);

		mPager = (ViewPager) findViewById(R.id.pager);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

		mBottom = (View) findViewById(R.id.bottom);
		mCancelBtn = (ImageButton) mBottom.findViewById(R.id.left_btn);
		mConfirmBtn = (ImageButton) mBottom.findViewById(R.id.centre_btn);
		mSettingBtn = (ImageButton) mBottom.findViewById(R.id.right_btn);

		mLoadingView = findViewById(R.id.loading_view);

		/*
		 * 기본 UI의 Visibillity를 설정합니다.
		 */

		mSubtitleTv.setText(this.mMissionTitle);
		mTitleTv.setText("데칼선택");
		mBackBtn.setPadding(0, 0, 0, 0);
		mBackBtn.setImageResource(R.drawable.base_actionbar_dir_left);
		mBackBtn.setVisibility(View.INVISIBLE);
		mRightBtn.setVisibility(View.INVISIBLE);

		mCancelBtn.setImageResource(R.drawable.camera_cancel);
		mCancelBtn.setBackgroundResource(android.R.color.transparent);
		mConfirmBtn.setImageResource(R.drawable.camera_confirm);
		mSettingBtn.setImageResource(R.drawable.camera_crop);

		/*
		 * 레이아웃을 크기를 정하기 위해 필요한 사이즈들을 얻어 옵니다.
		 */
		window_width = am.getWindowSize("width");
		window_height = am.getWindowSize("height");
		status_bar_height = am.getWindowSize("status_bar_height");

		actionbar_height = getResources().getDimensionPixelSize(
				R.dimen.abs__action_bar_default_height);
		/*
		 * UI size Init 1. 정사각형 프레임을 만들기 위해 mFl을 가로사이즈로 고정하였습니다. 2. 카메라 뷰의 비율
		 * 문제를 해결하기 위해 mIv를 윈도우 크기 그대로 설정하였습니다.
		 */

		mFl.setLayoutParams(new LinearLayout.LayoutParams(window_width,
				window_width));

		/*
		 * 버튼 클릭 리스너를 지정합니다.
		 */
		mBackBtn.setOnClickListener(onClickListener);

		mCancelBtn.setOnClickListener(onClickListener);
		mSettingBtn.setOnClickListener(onClickListener);
		mConfirmBtn.setOnClickListener(onClickListener);

		/*
		 * 이미지를 지정합니다.
		 */

		new ProcessingGalleryImageTask().execute(mImgPath);
		mIv.setImageMatrix(mMatrix);

		/*
		 * 데칼을 불러옵니다.
		 */

		loadDecals(mMissionId, mDecalMessage, true);

		/*
	 * 
	 */
		mPager.setOnTouchListener(new OnTouchListener() {
			private float pointX;
			private float pointY;
			private int tolerance = 50;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					return false;
				case MotionEvent.ACTION_DOWN:
					pointX = event.getX();
					pointY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					boolean sameX = pointX + tolerance > event.getX()
							&& pointX - tolerance < event.getX();
					boolean sameY = pointY + tolerance > event.getY()
							&& pointY - tolerance < event.getY();
					if (sameX && sameY) {
						showEtDialog();

					}
				}
				return false;
			}
		});

	}

	/**
	 * 액티비티가 종료 될 때에, 모델들과 어댑터를 없앱니다. (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onDestroy()
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAdapter = null;
		models.clear();

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

			}

			return bitmap;

		}

		protected void onPostExecute(Bitmap bitmap) {
			mIv.setImageBitmap(bitmap);
		}

	}

	/**
	 * onResume Method;
	 */

	@Override
	public void onResume() {
		super.onResume();

		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}

	}

	/**
	 * 로드 데칼
	 * 
	 * @param id
	 */

	private void loadDecals(int id, String message, boolean isFirst) {

		/*
		 * 
		 */
		mPager.setVisibility(View.GONE);
		mLoadingView.setVisibility(View.VISIBLE);

		/*
		 * 
		 */

		if (models.size() != 0) {
			this.mDecalPosition = mPager.getCurrentItem();

		}

		/*
		 * 
		 */

		ApplicationManager am = (ApplicationManager) getApplicationContext();
		am.setOnJsonLoadingCompletionListener(onLoadingDecalsCompletionListener);
		DecalListApi api = new DecalListApi();
		api.setInput(id, message);
		am.addJsonLoadingTask(api);

		api = null;
	}

	private ApplicationManager.OnJsonLoadingCompletionListener onLoadingDecalsCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {

		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {
				Log.d(TAG, "Models size : " + Integer.toString(models.size()));
				CEditDecalActivity.this.models.clear();
				CEditDecalActivity.this.models.addAll(models);
				models.clear();

				/**
				 * 
				 */

				mAdapter = null;
				mAdapter = new CameraDecalFragmentAdapter(
						CEditDecalActivity.this.getSupportFragmentManager());
				mPager.setAdapter(mAdapter);
				mIndicator.setViewPager(mPager);

				/*
				 * 
				 */

				mPager.setCurrentItem(CEditDecalActivity.this.mDecalPosition);

				/*
			 * 
			 */

				mPager.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.GONE);
			} else {
				Toast.makeText(getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

	/**
	 * 
	 */

	private void showEtDialog() {
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout cameraDecalEt = (LinearLayout) vi.inflate(
				R.layout.camera_decal_et, null);

		final EditText et = (EditText) cameraDecalEt.findViewById(R.id.et);
		et.setText(mDecalMessage);

		new AlertDialog.Builder(this).setTitle("텍스트 수정").setView(cameraDecalEt)
				.setNeutralButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (!mDecalMessage.equals(et.getText().toString())) {
							mDecalMessage = et.getText().toString();
							loadDecals(mMissionId, mDecalMessage, false);
						}

					}
				}).show();
	}

	/**
	 * The main adapter that backs the ViewPager. A subclass of
	 * FragmentStatePagerAdapter as there could be a large number of items in
	 * the ViewPager and we don't want to retain them all in memory at once but
	 * create/destroy them on the fly.
	 */
	private class CameraDecalFragmentAdapter extends FragmentStatePagerAdapter {

		public CameraDecalFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return CEditDecalActivity.this.models.size();
		}

		@Override
		public Fragment getItem(int position) {

			return CDecalFragment
					.newInstance(((Decal) CEditDecalActivity.this.models
							.get(position)).getImgPath());
		}
	}

	/**
	 * Btn OnClickListener
	 */

	OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			clickListenerHandler(v.getId());
		}
	};

	private void clickListenerHandler(int id) {
		switch (id) {
		case R.id.actionbar_left_btn: {
			onBackPressed();
			break;
		}
		case R.id.left_btn: {
			onBackPressed();
			break;
		}
		case R.id.right_btn: {
			startEditPhotoActivity();
			break;
		}
		case R.id.centre_btn: {
			dialog = ProgressDialog.show(CEditDecalActivity.this, "",
					"이미지를 처리하는 중입니다...", true, true);
			new uploadBitmapTask().execute(this.mImgPath);
			break;
		}
		default:
			break;
		}
	};

	/**
	 * Create Bitmap
	 */
	private class uploadBitmapTask extends AsyncTask<String, String, String> {

		protected void onPreExecute() {

		}

		protected String doInBackground(String... path) {

			/*
			 * 경로로부터 비트맵을 얻어옵니다.
			 */

			Bitmap bitmap = null;

			File imgFile = new File(path[0]);
			if (imgFile.exists()) {
				bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

			}

			/*
			 * 이미지를 행렬에 맞게 변환합니다.
			 */

			int bitmap_length = Math.min(bitmap.getWidth(), bitmap.getHeight());

			float[] matrix_values = new float[9];
			mMatrix.getValues(matrix_values);
			float trans_x = matrix_values[Matrix.MTRANS_X];
			float trans_y = matrix_values[Matrix.MTRANS_Y];
			float scale_current = Math.abs(matrix_values[Matrix.MSCALE_X]);
			float skew_current = Math.abs(matrix_values[Matrix.MSKEW_X]);

			int start_x = 0;
			int start_y = 0;

			if (mRotation == 0) {
				bitmap_length = (int) ((float) bitmap_length / scale_current);
				start_x = (int) (-trans_x / scale_current);
				start_y = (int) (-trans_y / scale_current);
			} else if (mRotation == 90) {
				bitmap_length = (int) ((float) bitmap_length / skew_current);
				start_x = (int) (-trans_y / skew_current);
				start_y = (int) ((trans_x - window_width) / skew_current);
			} else if (mRotation == 180) {
				bitmap_length = (int) ((float) bitmap_length / scale_current);
				start_x = (int) ((trans_x - window_width) / scale_current);
				start_y = (int) ((trans_y - window_width) / scale_current);
			} else {
				bitmap_length = (int) ((float) bitmap_length / skew_current);
				start_x = (int) ((trans_y - window_width) / skew_current);
				start_y = (int) (-trans_x / skew_current);
			}

			try {
				Bitmap converted = Bitmap.createBitmap(bitmap, start_x,
						start_y, bitmap_length, bitmap_length, mMatrix, true);
				if (bitmap != converted) {
					bitmap.recycle();
					bitmap = converted;
				}
				Log.d("Bitmap is converted", "true");
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

			/*
			 * 이미지를 BASE 64로 변환합니다. 화질은 89%로 축소합니다.
			 */

			Log.d(TAG, "bitmap width : " + bitmap.getWidth());
			Log.d(TAG, "bitmap height : " + bitmap.getHeight());

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 89, stream);

			byte[] byteArray = stream.toByteArray();
			String img_base64 = Base64.encodeToString(byteArray, 0).replace(
					"\n", "");

			// byte[] byteArrayTestDecoded = Base64.decode(img_base64, 0);
			// Bitmap bitmap_test = BitmapFactory.decodeByteArray( //
			// byteArrayTestDecoded, 0, byteArrayTestDecoded.length);
			// publishProgress(bitmap_test); // bitmap_test.recycle();

			/*
			 * RETURN
			 */
			byteArray = null;
			bitmap.recycle();

			return img_base64;
		}

		protected void onProgressUpdate(String... message) {
			// mIv.setImageMatrix(new Matrix());
			// mIv.setImageBitmap(bitmap[0]);
			Toast.makeText(getApplicationContext(), message[0],
					Toast.LENGTH_SHORT).show();

		}

		protected void onCancelled() {

		}

		protected void onPostExecute(String img_base64) {
			// Log.d(TAG, "mission_id" + mission_id);
			// Log.d(TAG, "decal_id " + ((Decal)
			// models.get(mPager.getCurrentItem())).getId());
			// Log.d(TAG, "decal_message" + decal_message);

			requestShotAddApi(mMissionId,
					((Decal) models.get(mPager.getCurrentItem())).getId(),
					mDecalMessage, img_base64);

			// JsonLoad(mission_id, 3, "", img_base64);

			img_base64 = null;

		}
	}

	/**
	 * Json Load
	 */

	private void requestShotAddApi(int mission_id, int decal_id,
			String message, String img_base64) {

		ApplicationManager am = ((ApplicationManager) getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onJsonLoadingCompletionListener);

		ShotAddApi api = new ShotAddApi();
		api.setInput(mission_id, decal_id, message, img_base64);
		Log.i(TAG, "base64 length " + img_base64.getBytes().length);

		am.addJsonLoadingTask(api);

		/*
		 * 필요없는 값은 삭제해줍니다.
		 */

		api = null;
		am = null;
	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJsonLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {
				Log.d("Models size", Integer.toString(models.size()));

				if (dialog != null) {
					dialog.cancel();
					dialog = null;
				}

				Intent intent = new Intent(CEditDecalActivity.this,
						CShareActivity.class);
				intent.putExtra("mission_title", mMissionTitle);
				intent.putExtra("img_url", ((Shot) models.get(0)).getImgUrl());
				startActivity(intent);
			} else {
				Toast.makeText(getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

	/**
	 * 
	 */

	private void startEditPhotoActivity() {
		Intent intent = new Intent(this, CEditPhotoActivity.class);

		/*
		 * 필요한 정보를 넣습니다. 매트릭스는 모두 float으로 넘깁니다.
		 */
		intent.putExtra("mission_id", this.mMissionId);
		intent.putExtra("mission_title", this.mMissionTitle);

		intent.putExtra("decal_position", this.mDecalPosition);
		intent.putExtra("decal_message", this.mDecalMessage);

		intent.putExtra("rotation", this.mRotation);
		intent.putExtra("img_path", this.mImgPath);

		intent.putExtra("fromAlbum", false);

		float[] matrix_values = new float[9];
		this.mMatrix.getValues(matrix_values);
		for (int i = 0; i < 9; i++) {
			intent.putExtra("matrix_value" + Integer.toString(i),
					matrix_values[i]);
		}

		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		finish();
	}

	/**
	 * Back Key Listener
	 */

	@Override
	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called");

		startCameraActivity();
		finish();
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

		Intent intent = new Intent(CEditDecalActivity.this, CActivity.class);
		intent.putExtra("mission_id", this.mMissionId);
		intent.putExtra("mission_title", this.mMissionTitle);
		intent.putExtra("decal_position", this.mDecalPosition);
		intent.putExtra("decal_message", this.mDecalMessage);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);

	}

}