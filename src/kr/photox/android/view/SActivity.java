package kr.photox.android.view;

import java.io.File;
import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.LoginApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Model;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;

public class SActivity extends Activity {
	public static final String TAG = "S Activity";

	/**
	 * OnCreate Method
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s_activity);

		/*
		 * 이미지를 캐싱하거나 저장하는데 필요한 저장소를 미리 만들어 둡니다. 여기서도 필요없는 것 제거해야 합니다.
		 */

		File photoXDirectory = new File(Environment
				.getExternalStorageDirectory().toString() + "/photoX/");
		if (!photoXDirectory.isDirectory()) {
			photoXDirectory.mkdirs();
		}

		File photoXCacheDirectory = new File(Environment
				.getExternalStorageDirectory().toString() + "/photoX/.cache/");
		if (!photoXCacheDirectory.isDirectory()) {
			photoXCacheDirectory.mkdirs();
		}

		File photoXTempDirectory = new File(Environment
				.getExternalStorageDirectory().toString() + "/photoX/.temp/");
		if (!photoXTempDirectory.isDirectory()) {
			photoXTempDirectory.mkdirs();
		}

		File photoXGalleryDirectory = new File(Environment
				.getExternalStorageDirectory().toString() + "/DCIM/photoX/");
		if (!photoXGalleryDirectory.isDirectory()) {
			photoXGalleryDirectory.mkdirs();
		}

		/*
		 * 오토키를 받아옵니다. 오토키가 없으면, 로그인 화면으로 넘어갑니다. 오토키가 있으면, 오토키로 로그인을 시도합니다.
		 */

		SharedPreferences prefs = getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String auto_key = prefs.getString("auto_key", "");
		Log.d("auto_key", auto_key);

		if (auto_key.equals("")) {
			new StartLoginTask().execute();

		} else {
			doRequestLogin(auto_key);
		}

	}

	/**
	 * 카메라 멀티 스크린 지원을 위해 필요한 변수들을 찾습니다. 카메라는 기본적으로 풀스크린입니다. 사용자의 풀스크린 성향에 맞추어
	 * 변화하는 무언가가 필요합니다. 그래서 우리는 여기서 도입하기로 합니다. status_bar height 와 navigation
	 * bar height 를 재서 넘겨줄 것입니다.
	 */

	private void saveWindowSizeInfo() {
		Window window = this.getWindow();

		if (window != null) {

			Rect rect = new Rect();
			window.getDecorView().getWindowVisibleDisplayFrame(rect);
			ApplicationManager am = (ApplicationManager) getApplicationContext();

			int status_bar_height = rect.top;
			Log.d(TAG, "status_bar_height" + status_bar_height);
			int navigation_bar_height = am.getWindowSize("height")
					- rect.bottom;
			Log.d(TAG, "navigation_bar_height" + navigation_bar_height);

			am.saveWindowSizeInfo(status_bar_height, navigation_bar_height);
		}

	}

	/**
	 * 
	 * @author yearnning
	 * 
	 */

	private class StartLoginTask extends AsyncTask<String, String, String> {
		protected void onPreExecute() {

		}

		protected String doInBackground(String... strs) {

			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onProgressUpdate(String... string) {

		}

		protected void onPostExecute(String result) {
			saveWindowSizeInfo();

			Intent loginIntent = new Intent(getApplicationContext(),
					SInActivity.class);
			startActivity(loginIntent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			finish();

		}
	}

	/**
	 *  
	 * 
	 */

	private void doRequestLogin(String auto_key) {

		ApplicationManager am = ((ApplicationManager) getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onLoginRequestLoadingCompletionListener);
		LoginApi loginApi = new LoginApi();
		loginApi.setInput("auto", null, auto_key, null, null);

		am.addJsonLoadingTask(loginApi);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onLoginRequestLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {
			Log.i(TAG, "isCompleted : " + isCompleted);

			saveWindowSizeInfo();

			if (isCompleted) {

				/*
				 * 성공하면 메인 액티비티로 들어갑니다.
				 */

				Intent intent = new Intent(getApplicationContext(),
						XActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in,
						android.R.anim.fade_out);
				finish();

			} else {

				/*
				 * Fail이면 로그인 액티비티로 들어갑니다.
				 */

				Intent intent = new Intent(getApplicationContext(),
						SInActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in,
						android.R.anim.fade_out);
				finish();

			}

		}
	};

}
