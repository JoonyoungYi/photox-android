package kr.photox.android.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.DecalListApi;
import kr.photox.android.lib.CameraPreview;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Decal;
import kr.photox.android.model.Model;
import kr.photox.android.utils.ImageCache;
import kr.photox.android.utils.ImageFetcher;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class CActivity extends FragmentActivity {
	private final String TAG = "Camera Activity";

	/**
	 * 인텐트 결과받는 종류들 구별하기 위한 식별
	 */

	private final int REQ_PICK_IMAGE = 1;

	/**
	 * UI Reference Init
	 */

	private RelativeLayout mRl;
	private CameraPreview mPreview;

	private View mSettingDetailView;
	private ImageButton mFlashBtn;
	private ImageButton mChangeBtn;

	private View mLoadingView;

	private ViewPager mPager;
	private PageIndicator mIndicator;

	private View mBottom;
	private ImageButton mAlbumBtn;
	private ImageButton mConfirmBtn;
	private ImageButton mSettingBtn;

	/**
	 * 카메라 상태 판정을 위한 변수들.
	 */

	private Boolean isProcessingCamera = false;
	private Boolean isProcessingGalleryImage = false;
	private Boolean isFrontCamera = false;
	private Boolean isFlashSupported = false;
	private Boolean isFlashOn = false;
	private Boolean isSettingOn = false;

	/**
	 * 
	 */

	int window_width = -1;
	int window_height = -1;

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
	 * View Pager 관련
	 */

	private CameraDecalFragmentAdapter mAdapter;
	private ArrayList<Model> models = new ArrayList<Model>();

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
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ApplicationManager am = (ApplicationManager) getApplicationContext();
		setContentView(R.layout.c_activity);

		/*
		 * 정보를 받아옵니다.
		 */

		Bundle bundle = getIntent().getExtras();
		this.mMissionId = bundle.getInt("mission_id", -1);
		this.mMissionTitle = bundle.getString("mission_title", "");
		this.mDecalPosition = bundle.getInt("decal_position", 0);
		this.mDecalMessage = bundle.getString("decal_message", "");

		isFlashSupported = getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH);

		/*
		 * UI Reference
		 */

		mRl = (RelativeLayout) findViewById(R.id.rl);

		View mStatusBar = (View) findViewById(R.id.status_bar);
		View mActionbar = (View) findViewById(R.id.actionbar);
		TextView mTitleTv = (TextView) mActionbar.findViewById(R.id.title_tv);
		ImageButton mBackBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_left_btn);
		ImageButton mRightBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_right_btn);
		TextView mSubTitleTv = (TextView) findViewById(R.id.sub_title_tv);

		mSettingDetailView = (View) findViewById(R.id.setting_detail_view);
		mFlashBtn = (ImageButton) findViewById(R.id.function_2_btn);
		mChangeBtn = (ImageButton) findViewById(R.id.function_1_btn);

		FrameLayout mFl = (FrameLayout) findViewById(R.id.fl);
		mLoadingView = (View) findViewById(R.id.loading);

		mPager = (ViewPager) findViewById(R.id.pager);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

		mBottom = (View) findViewById(R.id.bottom);
		mAlbumBtn = (ImageButton) mBottom.findViewById(R.id.left_btn);
		mConfirmBtn = (ImageButton) mBottom.findViewById(R.id.centre_btn);
		mSettingBtn = (ImageButton) mBottom.findViewById(R.id.right_btn);

		/*
		 * 레이아웃을 크기를 정하기 위해 필요한 사이즈들을 얻어 옵니다.
		 */

		window_width = am.getWindowSize("width");
		window_height = am.getWindowSize("height");
		int status_bar_height = am.getWindowSize("status_bar_height");
		Log.d(TAG, "status_bar_height : " + status_bar_height);
		int navigation_bar_height = am.getWindowSize("navigation_bar_height");
		Log.d(TAG, "navigation_bar_height : " + navigation_bar_height);
		int actionbar_height = getResources().getDimensionPixelSize(
				R.dimen.abs__action_bar_default_height);

		/*
		 * Visibllity Setting
		 */

		mTitleTv.setText("사진촬영");
		mBackBtn.setPadding(0, 0, 0, 0);
		mBackBtn.setImageResource(R.drawable.base_actionbar_dir_left);
		mRightBtn.setVisibility(View.INVISIBLE);

		mSubTitleTv.setText(this.mMissionTitle);
		mSettingDetailView.setVisibility(View.INVISIBLE);

		if (status_bar_height == 0) {
			mStatusBar.setVisibility(View.GONE);
		} else {
			mStatusBar.setVisibility(View.VISIBLE);
			mStatusBar.setLayoutParams(new LinearLayout.LayoutParams(
					window_width, status_bar_height));
		}

		if (window_height - window_width - status_bar_height
				- navigation_bar_height - actionbar_height * 2 < getResources()
				.getDimensionPixelSize(R.dimen.c_activity_bottom_min_height)) {
			mSubTitleTv.setVisibility(View.GONE);
		}

		mFl.setLayoutParams(new LinearLayout.LayoutParams(window_width,
				window_width));

		/*
		 * 버튼 클릭 리스너를 지정합니다.
		 */
		mBackBtn.setOnClickListener(onClickListener);

		mFlashBtn.setOnClickListener(onClickListener);
		mChangeBtn.setOnClickListener(onClickListener);

		mAlbumBtn.setOnClickListener(onClickListener);
		mSettingBtn.setOnClickListener(onClickListener);
		mConfirmBtn.setOnClickListener(onClickListener);
		mConfirmBtn.setOnLongClickListener(onLongClickListener);

		/*
		 * 
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
	 * OnResume Method
	 */

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume() called");

		/*
		 * 
		 */

		if ((dialog != null)) {
			dialog.cancel();
			dialog = null;
		}

		/*
		 * // Set the second argument by your choice. // Usually, 0 for
		 * back-facing camera, 1 for front-facing camera. 카메라가 기기에 없으면 오류냅니다.
		 */

		int camera_num = Camera.getNumberOfCameras();

		if (camera_num > 0) {

			mPreview = new CameraPreview(this, 0,
					CameraPreview.LayoutMode.FitToParent);
			LayoutParams previewLayoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mRl.addView(mPreview, 0, previewLayoutParams);

			if (camera_num > 1) {
				mChangeBtn.setVisibility(View.VISIBLE);
			} else {
				mChangeBtn.setVisibility(View.INVISIBLE);
			}

		} else {
			Toast.makeText(getApplicationContext(), "이용할 수 있는 카메라가 없습니다.",
					Toast.LENGTH_SHORT).show();
		}

		/*
		 * 데칼을 로드합다.
		 */
		if (!CActivity.this.isProcessingGalleryImage) {
			loadDecals(this.mMissionId, this.mDecalMessage);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mPreview != null) {
			mPreview.stop();
			mRl.removeView(mPreview);
			mPreview = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		CActivity.this.models.clear();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		loadDecals(mMissionId, mDecalMessage);

	}

	/*
	 * 액티비티가 종료 될 때에, 모델들과 어댑터를 없앱니다. (non-Javadoc)
	 * 
	 * @see Activity#onDestroy()
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAdapter = null;

	}

	/**
	 * 로드 데칼
	 * 
	 * @param id
	 */

	private void loadDecals(int id, String message) {

		/*
		 * 데칼을 로드하기 전에 미리 로딩뷰를 작게 띄워주어야 합니다.
		 */

		mLoadingView.setVisibility(View.VISIBLE);
		mPager.setVisibility(View.GONE);

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
				CActivity.this.models.clear();
				CActivity.this.models.addAll(models);
				models.clear();

				/**
				 * 
				 */
				mAdapter = null;
				mAdapter = new CameraDecalFragmentAdapter(
						CActivity.this.getSupportFragmentManager());
				mPager.setAdapter(mAdapter);
				mIndicator.setViewPager(mPager);

				/*
				 * 
				 */
				mPager.setCurrentItem(CActivity.this.mDecalPosition);

				/*
				 * 데칼을 로드하기 전에 미리 로딩뷰를 작게 띄워주어야 합니다. 그리고 해제합니다.
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
		et.setText(this.mDecalMessage);

		new AlertDialog.Builder(this).setTitle("텍스트 수정").setView(cameraDecalEt)
				.setNeutralButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (!mDecalMessage.equals(et.getText().toString())) {
							mDecalMessage = et.getText().toString();
							loadDecals(mMissionId, mDecalMessage);
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
			return CActivity.this.models.size();
		}

		@Override
		public Fragment getItem(int position) {
			return CDecalFragment.newInstance(((Decal) CActivity.this.models
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
		case R.id.actionbar_left_btn:
			onBackPressed();
			break;
		case R.id.function_2_btn: {
			changeFlashStatus();
			break;
		}
		case R.id.function_1_btn: {
			changeCamera();
			break;
		}
		case R.id.left_btn: {
			loadGallery();
			break;
		}
		case R.id.right_btn: {
			changeSettingDetailStatus();
			break;
		}
		case R.id.centre_btn: {
			takePicture();
			break;
		}
		default:
			break;
		}
	};

	/**
	 * Btn LongClickListener
	 */

	OnLongClickListener onLongClickListener = new OnLongClickListener() {
		public boolean onLongClick(View v) {
			longClickListenerHandler(v.getId());
			return true;
		}
	};

	private void longClickListenerHandler(int id) {
		switch (id) {
		case R.id.centre_btn: {
			mPreview.getCamera().autoFocus(new AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean arg0, Camera arg1) {
					takePicture();
				}
			});
			break;
		}
		default:
			break;
		}
	};

	/**
	 * setting detail on off
	 */

	private void changeSettingDetailStatus() {
		if (isSettingOn) {
			mSettingDetailView.setVisibility(View.INVISIBLE);
			isSettingOn = false;
		} else {
			mSettingDetailView.setVisibility(View.VISIBLE);
			isSettingOn = true;
		}
	}

	/**
	 * 플래시를 켜고 끕니다. 전면카메라인 경우에는 플래시를 켜고 끄는 것이 아니라, 토스트 창을 띄웁니다.
	 */

	private void changeFlashStatus() {
		if (isFrontCamera) {
			Toast.makeText(getApplicationContext(), "전면카메라는 플래시가 지원되지 않습니다",
					Toast.LENGTH_SHORT).show();

		} else {
			if (isFlashSupported) {
				Camera.Parameters parameters = mPreview.getCamera()
						.getParameters();

				if (isFlashOn) {
					mFlashBtn.setImageDrawable(getResources().getDrawable(
							R.drawable.c_setting_detail_flash_off));
					parameters.setFlashMode("off");
					mPreview.getCamera().setParameters(parameters);
					isFlashOn = false;
					Log.d(TAG, "isFlash off");
				} else {
					mFlashBtn.setImageDrawable(getResources().getDrawable(
							R.drawable.c_setting_detail_flash_on));
					parameters.setFlashMode("torch");
					mPreview.getCamera().setParameters(parameters);
					isFlashOn = true;
				}
			}
		}
	}

	/**
	 * 전후면 카메라 전환을 위한 버튼 클릭 리스너에 대한 설정입니다.
	 */

	private void changeCamera() {

		if (isFrontCamera) {

			//

			mPreview.stop();
			mRl.removeView(mPreview);

			mPreview = new CameraPreview(this, 0,
					CameraPreview.LayoutMode.FitToParent);
			LayoutParams previewLayoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			mRl.addView(mPreview, 0, previewLayoutParams);

			//

			Log.d(TAG, "back Camera is open");

			isFrontCamera = false;

			mFlashBtn.setImageDrawable(getResources().getDrawable(
					R.drawable.c_setting_detail_flash_off));
			isFlashOn = false;

		} else {

			//

			mPreview.stop();
			mRl.removeView(mPreview);

			mPreview = new CameraPreview(this, 1,
					CameraPreview.LayoutMode.FitToParent);
			LayoutParams previewLayoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			mRl.addView(mPreview, 0, previewLayoutParams);

			//

			Log.d(TAG, "front Camera is open");

			isFrontCamera = true;
		}

	}

	/**
	 * 사진을 찍습니다.카메라 프로세싱중이 아니면 사진을 찍습니다.카메라 프로세싱 중으로 만듭니다.
	 */

	private void takePicture() {
		if (!isProcessingCamera) {
			mPreview.takePicture(shutterCallback, rawCallback, jpegCallback);
			isProcessingCamera = true;

		}
	}

	/**
	 * 사진을 찍었을 때의 콜백들을 지정해줍니다.
	 */

	private ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "shutterCallback onShutter'd");
			dialog = ProgressDialog.show(CActivity.this, "", "이미지 처리중입니다...",
					true, true);

		}
	};

	private PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// Log.d(TAG, "onPictureTaken - raw");
		}
	};

	private PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {

			/*
			 * 받아온 byte array를 비트맵 이미지로 처리합니다.
			 */

			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

			/*
			 * 일반 카메라의 경우 90도 회전되었음을 명시하고, 매트릭스를 90도 회전합니다. 전면 카메라의 경우에는 다른 각도
			 * 입니다. 카메라는 사실 전체화면으로 떠있습니다. 그래서 위쪽에서 가려진 만큼 아래로 매트릭스를 조정합니다.
			 */

			CActivity.this.mRotation = 90;
			CActivity.this.mMatrix.postRotate(90,
					CActivity.this.window_width / 2,
					CActivity.this.window_width / 2);

			Resources r = getResources();
			int actionbar_height = r
					.getDimensionPixelSize(R.dimen.abs__action_bar_default_height);
			int status_bar_height = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 25, r.getDisplayMetrics());
			CActivity.this.mMatrix.postTranslate(0, -2 * actionbar_height
					- status_bar_height);

			/*
			 * 비트맵을 스트링으로 바꿉니다.
			 */

			new BitmapToFilePathTask().execute(bitmap);

			/*
			 * 카메라를 중지합니다.
			 */

			mPreview.stop();
			mRl.removeView(mPreview); // This is necessary.
			mPreview = null;

		}
	};

	/**
	 * 갤러리에서 이미지를 고릅니다.
	 */

	public void loadGallery() {
		this.isProcessingGalleryImage = true;

		/*
		 * 갤러리가 여러번 열리는 것을 방지하기 위해서 로딩바를 띄웁니다. 그리고 버튼 클릭도 안되게 합니다.
		 */

		mAlbumBtn.setClickable(false);

		/*
		 * 
		 */
		dialog = ProgressDialog.show(CActivity.this, "", "갤러리 로딩중입니다...", true,
				true);

		/*
		 * 
		 */

		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, REQ_PICK_IMAGE);

	}

	/**
	 * 액티비티의 결과를 받아서 처리합니다.
	 */

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d(TAG, "onActivity Result Called");

		/*
		 * 이 부분은 앨범에서 이미지를 받아오는 것을 처리하는 부분입니다.
		 */

		if (requestCode == REQ_PICK_IMAGE) {
			Log.d(TAG, "request Code : " + "REQ_PICK_IMAGE");
			if (resultCode == RESULT_OK) {
				if (intent != null) {

					/*
					 * Uri로부터 파일 경로를 얻어옵니다.
					 */

					Uri selectedImage = intent.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

					CActivity.this.mImgPath = cursor.getString(columnIndex);

					String filePath = cursor.getString(columnIndex);
					cursor.close();

					Log.d(TAG, "filePath : " + filePath);

					// Bitmap yourSelectedImage =
					// BitmapFactory.decodeFile(filePath);

					/*
					 * 회전각도 설정과 매트릭스를 그에 맞게 수정합니다.
					 */

					CActivity.this.mRotation = getRotation(filePath);
					CActivity.this.mMatrix.postRotate(CActivity.this.mRotation,
							CActivity.this.window_width / 2,
							CActivity.this.window_width / 2);

					Log.d(TAG, "rotation : " + mRotation);

					/*
					 * 이미지를 프로세싱합니다.
					 */

					new ProcessingGalleryImageTask().execute(filePath);

				}
			} else {
				/*
				 * 
				 */
				// if (dialog != null) {
				// dialog.cancel();
				// dialog = null;
				// }
			}
		}

		/*
		 * 데칼의 결과를 받아서 처리합니다.
		 */
		mAlbumBtn.setClickable(true);
		Log.d(TAG, "onActivityResult : finished");
	}

	/**
	 * Get Rotation
	 */

	private int getRotation(String path) {
		try {
			ExifInterface exif = new ExifInterface(path);
			String rotationAmount = exif
					.getAttribute(ExifInterface.TAG_ORIENTATION);
			if (!TextUtils.isEmpty(rotationAmount)) {
				int rotationParam = Integer.parseInt(rotationAmount);
				switch (rotationParam) {
				case ExifInterface.ORIENTATION_NORMAL:
					return 0;
				case ExifInterface.ORIENTATION_ROTATE_90:
					return 90;
				case ExifInterface.ORIENTATION_ROTATE_180:
					return 180;
				case ExifInterface.ORIENTATION_ROTATE_270:
					return 270;
				default:
					return 0;
				}
			}
			// String a = exif.getAttribute(ExifInterface.TAG_MODEL);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 
	 */

	private class ProcessingGalleryImageTask extends
			AsyncTask<String, Integer, Bitmap> {

		protected void onPreExecute() {

			/*
			 * 
			 */
			if (dialog != null) {
				dialog.cancel();
				dialog = null;
			}

			dialog = ProgressDialog.show(CActivity.this, "", "이미지 로딩중입니다...",
					true, true);

		}

		protected void onCancelled() {

		}

		protected Bitmap doInBackground(String... img_path) {

			/*
			 * 
			 */

			Bitmap bitmap = null;

			File imgFile = new File(img_path[0]);
			if (imgFile.exists()) {
				Log.d(TAG, "img path : " + imgFile.getAbsolutePath());
				// bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = calculateInSampleSize(options,
						window_width, window_width);

				bitmap = BitmapFactory.decodeFile(img_path[0], options);
			}

			return bitmap;

		}

		/*
		 * 샘플 사이즈를 반환합니다. 메모리여유가 적을 때에는 올림을 하며, 메모리 여유가 있으면, 반올림을 합니다.
		 * 
		 * @param options
		 * 
		 * @param reqWidth
		 * 
		 * @param reqHeight
		 * 
		 * @return
		 */

		public int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			float inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {

				// Calculate ratios of height and width to requested height and
				// width
				final float heightRatio = ((float) height / (float) reqHeight);
				final float widthRatio = ((float) width / (float) reqWidth);

				// Choose the smallest ratio as inSampleSize value, this will
				// guarantee
				// a final image with both dimensions larger than or equal to
				// the
				// requested height and width.
				inSampleSize = heightRatio < widthRatio ? heightRatio
						: widthRatio;
			}

			double maxMemory = Runtime.getRuntime().maxMemory();
			Log.d(TAG, "max Memory" + maxMemory);

			if (maxMemory > 127 * 1024 * 1024) {
				Log.d(TAG, "Good Device");
				return (int) inSampleSize;
			} else if (maxMemory < 69 * 1024 * 1024) {
				Log.d(TAG, "Poor Device");
				return Math.round(inSampleSize) + 1;
			} else {
				Log.d(TAG, "Normal Device");
				return (int) (inSampleSize) + 1;
			}

		}

		protected void onPostExecute(Bitmap bitmap) {
			new BitmapToFilePathTask().execute(bitmap);

		}

	}

	/**
	 * 비트맵 이미지를 파일 path로 변경해줍니다.
	 */

	public class BitmapToFilePathTask extends
			AsyncTask<Bitmap, Boolean, String> {

		protected void onPreExecute() {

		}

		protected void onCancelled() {

		}

		protected String doInBackground(Bitmap... original) {

			Bitmap bitmap = original[0];
			int bitmap_width = bitmap.getWidth();
			int bitmap_height = bitmap.getHeight();

			/*
			 * 전면카메라의 경우 좌우를 반전합니다. 저장할때만 반전합니다. 퍼포먼스가 떨어질 수도 있지만, 이것때문에 꼬이느니
			 * 차라리 퍼포먼스를 죽이기로 했습니다.
			 */
			if (isFrontCamera) {
				Matrix matrix = new Matrix();
				matrix.preScale(-1.0f, +1.0f);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap_width,
						bitmap_height, matrix, false);
			}
			/*
			 * 비트맵 이미지를 window_width를 기준으로 변경합니다. 원본사진의 가로길이가 비율상 큰 경우에는 리사이징을
			 * 높이 기준으로 해야 합니다. 원본사진의 세길이가 비율상 큰 경우에는 리사이징을 가로 기준으로 해야 합니다. 타겟의
			 * 가로세로 비율은 정방형이므로 1입니다. 작은 경우에도 무조건 저 크기로 만듭니다. 직사각형의 경우 작은변의 길이가
			 * window_width가 되도록 만드는 것입니다.
			 */
			float original_ratio = ((float) bitmap_width)
					/ ((float) bitmap_height);
			float resizing_ratio = 1;

			if (original_ratio > resizing_ratio) {
				bitmap = Bitmap.createScaledBitmap(bitmap, bitmap_width
						* window_width / bitmap_height, window_width, false);
			} else {
				bitmap = Bitmap.createScaledBitmap(bitmap, window_width,
						bitmap_height * window_width / bitmap_width, false);
			}

			/*
			 * 받아온 이미지가 저장될 경로를 미리 설정합니다. 비트맵을 리사이징 했으므로, 비트맵을 저장합니다.
			 */

			String file_path = String.format(Environment
					.getExternalStorageDirectory().toString()
					+ "/photoX/.temp/%d.jpg", System.currentTimeMillis());

			try {
				FileOutputStream out = new FileOutputStream(file_path);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "bitmap compress failed");
			}

			return file_path;

		}

		protected void onProgressUpdate(Boolean... bool) {

		}

		protected void onPostExecute(String file_path) {

			/*
			 * 전역에 있는 file_path를 저장합니다.
			 */

			CActivity.this.mImgPath = file_path;

			/*
			 *
			 */

			startCameraEditActivity();

		}
	}

	/**
	 * 데칼 액티비티를 엽니다. 기존의 액티비티는 종료합니다. 메모리 관리를 위해서 잘못된 접근일때의 엘스문 액티비티를 만들어야 합니다.
	 */

	public void startCameraEditActivity() {

		/*
		 * 카메라인지 앨범인지 구분합니다. 앨범에서 온 경우 앨범에서 왔다고 알려줍니다.
		 */

		Intent intent;
		if (isProcessingCamera) {
			isProcessingCamera = false;
			intent = new Intent(getApplicationContext(),
					CEditDecalActivity.class);
		} else {
			intent = new Intent(CActivity.this, CEditPhotoActivity.class);
			intent.putExtra("from_album", true);
		}

		/*
		 * 필요한 정보를 넣습니다. 매트릭스는 모두 float으로 넘깁니다.
		 */
		intent.putExtra("mission_id", this.mMissionId);
		intent.putExtra("mission_title", this.mMissionTitle);

		this.mDecalPosition = mPager.getCurrentItem();
		intent.putExtra("decal_position", this.mDecalPosition);
		intent.putExtra("decal_message", this.mDecalMessage);

		intent.putExtra("rotation", this.mRotation);
		intent.putExtra("img_path", this.mImgPath);

		float[] matrix_values = new float[9];
		this.mMatrix.getValues(matrix_values);
		for (int i = 0; i < 9; i++) {
			intent.putExtra("matrix_value" + Integer.toString(i),
					matrix_values[i]);
		}

		/*
		 * 액티비티를 실행합니다.
		 */
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		finish();
	}

}