package kr.photox.android.view;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.photox.android.R;
import kr.photox.android.manager.ApplicationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CShareActivity extends FragmentActivity {

	private static final String TAG = "Camera Share Activity";

	/**
	 * UI Reference
	 */
	private View mActionbar;
	private TextView mTitleTv;
	private ImageButton mBackBtn;
	private ImageButton mRightBtn;
	private TextView mSubtitleTv;

	private ImageView mIv;
	private TextView mLocationTv;
	private EditText mMessageEt;

	private View[] mShareView = new View[3];
	private Button[] mShareViewBtn = new Button[3];
	private ImageView[] mShareViewAppIv = new ImageView[3];
	private ImageView[] mShareViewConfirmIv = new ImageView[3];

	final String[] mShareViewPkn = { "com.facebook.katana",
			"com.twitter.android", "com.kakao.story" };

	private boolean[] mShareIsDone = { false, false, false };

	/**
	 * 
	 */

	private String path = "";
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
		setContentView(R.layout.c_share_activity);

		/*
		 * Intro pkg name
		 */
		int[] mShareViewBg = { R.drawable.camera_share_fb_bg,
				R.drawable.camera_share_twitter_bg,
				R.drawable.camera_share_ks_bg };
		int[] mShareViewLogo = { R.drawable.camera_share_fb,
				R.drawable.camera_share_twitter, R.drawable.camera_share_ks };

		/*
		 * 인텐트로부터 정보를 받아옵니다.
		 */

		String img_url = getIntent().getExtras().getString("img_url");
		String mission_title = getIntent().getExtras().getString(
				"mission_title");

		/*
		 * UI Reference
		 */

		mActionbar = (View) findViewById(R.id.actionbar);
		mTitleTv = (TextView) mActionbar.findViewById(R.id.title_tv);
		mBackBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_left_btn);
		mRightBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_right_btn);

		mSubtitleTv = (TextView) findViewById(R.id.subtitle_tv);

		LinearLayout mSummaryView = (LinearLayout) findViewById(R.id.summary_view);
		mIv = (ImageView) findViewById(R.id.iv);
		mMessageEt = (EditText) findViewById(R.id.message_et);
		TextView mTsTv = (TextView) findViewById(R.id.ts_tv);
		mLocationTv = (TextView) findViewById(R.id.location_tv);

		mShareView[0] = findViewById(R.id.fb_view);
		mShareView[1] = findViewById(R.id.twitter_view);
		mShareView[2] = findViewById(R.id.ks_view);

		LinearLayout[] mShareViewLl = new LinearLayout[3];

		Button mFinishBtn = (Button) findViewById(R.id.finish_btn);

		/*
		 * 아래 공유버튼 설정
		 */

		for (int i = 0; i < 3; i++) {
			mShareViewLl[i] = (LinearLayout) mShareView[i]
					.findViewById(R.id.ll);
			mShareViewLl[i].setBackgroundResource(mShareViewBg[i]);
			mShareViewBtn[i] = (Button) mShareView[i].findViewById(R.id.btn);

			final int position = i;
			mShareViewBtn[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startSharePackageActivity(position);
				}
			});
			mShareViewAppIv[i] = (ImageView) mShareView[i]
					.findViewById(R.id.app_iv);
			mShareViewAppIv[i].setImageResource(mShareViewLogo[i]);
			mShareViewConfirmIv[i] = (ImageView) mShareView[i]
					.findViewById(R.id.confirm_iv);
		}

		/*
		 * 레이아웃을 크기를 정하기 위해 필요한 사이즈들을 얻어 옵니다.
		 */

		Resources r = getResources();
		int margin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 38, r.getDisplayMetrics());
		int window_width = ((ApplicationManager) getApplicationContext())
				.getWindowSize("width");

		/*
		 * 오늘 날짜를 얻어옵니다.
		 */

		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
				"yy.MM.dd HH:mm", Locale.KOREA);
		Date currentTime = new Date();
		String mTs = mSimpleDateFormat.format(currentTime);

		/*
		 * Visibllity Setting
		 */

		mSubtitleTv.setText(mission_title);
		mTitleTv.setText("사진 공유하기");
		mBackBtn.setPadding(0, 0, 0, 0);
		mBackBtn.setImageResource(R.drawable.base_actionbar_dir_left);
		mRightBtn.setVisibility(View.INVISIBLE);
		mTsTv.setText(mTs);

		/*
		 * 뷰가 이쁘게 보이게 하기 위해서 설정한것임.
		 */

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				window_width - margin * 2 / 3, (window_width - margin) / 2);
		params.topMargin = margin / 3;
		params.bottomMargin = margin / 3;
		params.leftMargin = margin / 3;
		params.rightMargin = margin / 3;
		mSummaryView.setLayoutParams(params);

		/*
		 * 버튼 클릭 리스너를 지정합니다.
		 */
		mBackBtn.setOnClickListener(onClickListener);
		mFinishBtn.setOnClickListener(onClickListener);

		/*
		 * 이미지를 로드합니다.
		 */

		new ImageLoadingTask().execute(img_url);

	}

	@Override
	public void onResume() {
		super.onResume();

		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}

		for (int i = 0; i < 3; i++) {
			if (mShareIsDone[i]) {
				mShareViewConfirmIv[i]
						.setImageResource(R.drawable.camera_share_done);
			}
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
		case R.id.finish_btn:
			Intent intent = new Intent(CShareActivity.this, XActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);

			break;
		}
	}

	/**
	 * Intro Intent Setting
	 * 
	 * @param pkgName
	 */
	public void startSharePackageActivity(final int position) {
		String pkgName = mShareViewPkn[position];
		if (isPackageInstalled(this, pkgName)) {

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, mMessageEt.getText().toString());
			intent.setType("image/*");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path))); // Uri.parse(path)
			intent.setPackage(pkgName);
			startActivity(intent);

			mShareIsDone[position] = true;

		} else {
			Toast.makeText(this, "어플리케이션이 설치되어 있지 않습니다", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public static boolean isPackageInstalled(Context ctx, String pkgName) {
		try {
			ctx.getPackageManager().getPackageInfo(pkgName,
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 */

	private class ImageLoadingTask extends AsyncTask<String, Bitmap, String> {

		protected void onPreExecute() {
			dialog = ProgressDialog.show(CShareActivity.this, "",
					"이미지를 저장하는 중입니다...", true, true);
		}

		protected String doInBackground(String... url_str) {

			/*
			 * url_str을 URL객체로 변환합니다.
			 */

			URL url = null;

			try {
				url = new URL(url_str[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			/*
			 * 
			 */

			Bitmap bitmap = null;

			try {
				URLConnection conn = url.openConnection();
				conn.connect();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inDither = true;
				options.inPreferredConfig = Config.ARGB_8888;

				BufferedInputStream bis = new BufferedInputStream(
						conn.getInputStream());
				bitmap = BitmapFactory.decodeStream(bis, null, options);
				publishProgress(bitmap);
				bis.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

			/*
			 * 비트맵을 저장할 수 있는 형태로 바꿉니다.
			 */
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);

			/*
			 * 저장할 파일을 생성합니다.
			 */

			String path = String.format(Environment
					.getExternalStorageDirectory().toString()
					+ "/DCIM/photoX/%d.jpg", System.currentTimeMillis());
			File f = new File(path);
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			/*
			 * 파일에 이미지를 저장합니다.
			 */

			FileOutputStream fo;
			try {
				fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return path;

		}

		protected void onProgressUpdate(Bitmap... bitmap) {
			mIv.setImageBitmap(bitmap[0]);
		}

		protected void onCancelled() {

		}

		protected void onPostExecute(String path) {
			CShareActivity.this.path = path;

			if (dialog != null) {
				dialog.cancel();
				dialog = null;
			}

			/**
			 * reverse GeoCode Loading Task
			 */

			new reverseGeocodingTask().execute("");

		}
	}

	/**
	 * Get Address From Geocode
	 */

	public class reverseGeocodingTask extends AsyncTask<String, String, String> {

		protected void onPreExecute() {

		}

		protected String doInBackground(String... str) {

			/**
			 * 여기는 로케이션 받아오는 부분
			 */

			Location current_location = ((ApplicationManager) getApplicationContext())
					.getCurrentLocation();

			/**
			 * 리벌스 지오코딩 부분이
			 */
			String locations = "";
			Geocoder geocoder = new Geocoder(getApplicationContext(),
					Locale.getDefault());

			try {

				if (current_location != null) {
					List<Address> addrs = geocoder.getFromLocation(
							current_location.getLatitude(),
							current_location.getLongitude(), 1);

					for (Address addr : addrs) {
						locations += addr.getAddressLine(0);
					}

				}

				Log.d(TAG, "addrs success!");

			} catch (IOException e) {
				e.printStackTrace();
			}

			return locations;

		}

		protected void onProgressUpdate(String... str) {

		}

		protected void onCancelled() {

		}

		protected void onPostExecute(String location_name) {

			/*
			 * 최적화는 정말 나중에 합시다! 일단, 위치정보기반으로
			 */

			if (!location_name.isEmpty()) {
				location_name = location_name.substring(5);

				mLocationTv.setText(location_name);
			} else {
				mLocationTv.setText("위치를 불러올 수 없습니다.");
			}

		}

	}

	/**
	 * Back Key Listener
	 */

	@Override
	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called");

		finish();

	}

}
