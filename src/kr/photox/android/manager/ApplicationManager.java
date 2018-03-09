package kr.photox.android.manager;

import java.util.ArrayList;
import java.util.LinkedList;

import kr.photox.android.api.Api;
import kr.photox.android.api.ApiBase;
import kr.photox.android.api.LogoutApi;
import kr.photox.android.api.MainSearchApi;
import kr.photox.android.model.Login;
import kr.photox.android.model.Model;
import kr.photox.android.model.Protocol;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class ApplicationManager extends Application {
	private static final String TAG = "Application Manager";

	/**
	 * Preference Auto Login
	 */

	SharedPreferences user_prefs;
	SharedPreferences.Editor prefs_editor;

	/**
	 * Data Cache
	 */
	private int mWindow_width = -1;
	private int mWindow_height = -1;
	private int mWindow_status_bar_height = -1;
	private int mWindow_navigation_bar_height = -1;

	/**
	 * Prepare HTTPA
	 */

	private String token;
	private String session_key;
	private String enc_key;
	private String enc_iv;

	/**
	 * Waiting Que
	 */

	private LinkedList<Api> waiting_apis = new LinkedList<Api>();

	/**
	 * MODEL REF
	 */

	public int CAMPAIGN_MODEL = 0;
	public int DECAL_MODEL = 1;
	public int MISSION_MODEL = 2;
	public int SHAMPAIGN_MODEL = 3;
	public int SHOT_MODEL = 4;

	/**
	 * 
	 */

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		/*
		 * Preference 선 셋팅
		 */

		user_prefs = getSharedPreferences("user_info", Context.MODE_PRIVATE);

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();

		Toast.makeText(getApplicationContext(),
				"여유 메모리가 부족합니다. 메모리를 정리하시거나 잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	/**
	 * Json Loading Completion Listener Init
	 */

	private ArrayList<OnJsonLoadingCompletionListener> onJsonLoadingCompletionListeners = new ArrayList<OnJsonLoadingCompletionListener>();

	public interface OnJsonLoadingCompletionListener {
		void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted);
	}

	public void setOnJsonLoadingCompletionListener(
			OnJsonLoadingCompletionListener onJsonLoadingCompletionListener) {
		this.onJsonLoadingCompletionListeners
				.add(onJsonLoadingCompletionListener);
	}

	/**
	 * add Json Loading Task
	 */

	public void addJsonLoadingTask(Api api) {

		Log.d(TAG, "addJsonLoadingTask : added api");
		waiting_apis.add(api);

		if (waiting_apis.size() == 1) {

			new JsonLoadingTask().execute("");
			Log.d(TAG, "new JsonLoadingTask isExectued");
		}

	}

	/**
	 * 
	 */

	public class JsonLoadingTask extends
			AsyncTask<String, Protocol, ArrayList<Model>> {

		private String API_TYPE;

		protected void onPreExecute() {
			if (!isOnline(getApplicationContext())) {
				cancel(true);
			}
		}

		protected ArrayList<Model> doInBackground(String... strs) {

			/*
			 * This Thread Loads JSONS. This is why it has Max Priority.
			 */

			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

			/*
			 * Data is exported
			 */

			Api api = waiting_apis.get(0);

			API_TYPE = api.getApiType();
			ArrayList<Model> models = new ArrayList<Model>();

			if (API_TYPE.equals(ApiBase.API.MAIN_SEARCH)) {
				((MainSearchApi) api).setCurrentLocation(getCurrentLocation());

			} else if (API_TYPE.equals(ApiBase.API.LOGOUT)) {
				((LogoutApi) api).setInput(ApplicationManager.this.session_key);

			}

			if (api.getProtocolType().equals("httpa")) {
				Log.d(TAG, "input token : " + ApplicationManager.this.token);
				api.setSecureKey(session_key, token, enc_key, enc_iv);
				if (getCurrentLocation() != null) {
					((ApiBase) api).setCurrentLocation(getCurrentLocation());
				}
			}

			/*
			 * 
			 */

			api.execute();

			/*
			 * 
			 */

			Protocol protocol = api.getProtocol();

			if (protocol.getResult_status().equals("ok")) {
				publishProgress(protocol);
				Log.d(TAG, "get JSON success!");

			} else {
				cancel(true);
				Log.d(TAG, "cancel true");

			}

			/*
			 * 
			 */

			models = api.getModels();

			Log.d(TAG, "Models size : " + Integer.toString(models.size()));

			return models;

		}

		protected void onCancelled() {
			onJsonLoadingCompletionListeners.get(0).onJsonLoadingCompletion(
					null, false);

			/*
			 * 대기하고 있던 열에서 리스너와 api를 제거해줍니다.
			 */

			onJsonLoadingCompletionListeners.remove(0);
			waiting_apis.remove(0);

			/*
			 * 만약 대기하고 있는 Api가 있다면, 다시 실행해줍니다.
			 */

			if (waiting_apis.size() != 0) {
				new JsonLoadingTask().execute("");

			}

		}

		protected void onProgressUpdate(Protocol... protocol) {
			if (!protocol[0].getToken().equals("")) {
				ApplicationManager.this.token = protocol[0].getToken();
				Log.d(TAG, "output token : " + ApplicationManager.this.token);

			}
		}

		protected void onPostExecute(ArrayList<Model> models) {
			Log.d(TAG, "Async Task on Post Execute!");

			/*
			 * 로그인 에이피아이인 경우 세션키와 enc_iv,enc_key값을 추출합니다.
			 */

			if (waiting_apis.get(0).getApiType().equals(ApiBase.API.LOGIN)) {

				Login login = (Login) models.get(0);

				ApplicationManager.this.enc_iv = login.getEnc_iv();
				ApplicationManager.this.enc_key = login.getEnc_key();
				ApplicationManager.this.session_key = login.getSession_key();

				/*
				 * 오토키를 내부저장소에 저장합니다.
				 */

				String auto_key = login.getAuto_key();
				prefs_editor = user_prefs.edit();
				prefs_editor.putString("auto_key", auto_key);
				prefs_editor.commit();
				Log.d("auto_key saved", auto_key);

				models.clear();
			}

			/*
			 * 
			 */

			Log.i(TAG, "models size : " + models.size());
			onJsonLoadingCompletionListeners.get(0).onJsonLoadingCompletion(
					models, true);

			/*
			 * 대기하고 있던 열에서 리스너와 api를 제거해줍니다.
			 */

			onJsonLoadingCompletionListeners.remove(0);
			waiting_apis.remove(0);

			/*
			 * 만약 대기하고 있는 Api가 있다면, 다시 실행해줍니다.
			 */

			if (waiting_apis.size() != 0) {
				new JsonLoadingTask().execute("");

			}

		}
	}

	/**
	 * Chk internet connection
	 */

	private boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			Log.d("JsonLoadingTask", "Internet connection true");
			return true;
		}

		Toast.makeText(context, "인터넷 연결을 확인해주세요", Toast.LENGTH_LONG).show();
		Log.d("JsonLoadingTask", "Internet connection false");
		return false;
	}

	/**
	 * Chk Memory Status 70%이상 사용중이면 날려버립니다. // KB로 만들고 싶으면 1024.0f로 나누면 됩니다.
	 * 둘다.
	 */

	public static Boolean isMemoryAvailable() {
		double maxMemory = Runtime.getRuntime().maxMemory();
		double allocateMemory = Debug.getNativeHeapAllocatedSize();

		Log.i("", "최대 메모리 : " + maxMemory);
		Log.i("", "사용 메모리 : " + allocateMemory);

		double current_memeory_usage = allocateMemory / maxMemory;

		if (current_memeory_usage > 0.7) {
			Log.d(TAG, "Memory is unavailable!");
			return false;
		} else {
			Log.d(TAG, "Memory is available!");
			return true;
		}

	}

	/**
	 * 화면 크기 정보에 대해서 요청했을 때, 값을 반환합니다.
	 */

	public int getWindowSize(String type) {
		int return_value = 0;

		if (type.equals("width")) {
			return_value = this.mWindow_width;
		} else if (type.equals("height")) {
			return_value = this.mWindow_height;
		} else if (type.equals("status_bar_height")) {
			return_value = this.mWindow_status_bar_height;
		} else if (type.equals("navigation_bar_height")) {
			return_value = this.mWindow_navigation_bar_height;
		}

		if (return_value == -1) {
			setWindowSizeInfo();
			return getWindowSize(type);
		}

		return return_value;
	}

	/**
	 * 
	 */

	private void setWindowSizeInfo() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		mWindow_width = displayMetrics.widthPixels;
		mWindow_height = displayMetrics.heightPixels;

		mWindow_status_bar_height = user_prefs.getInt("status_bar_height", 0);
		mWindow_navigation_bar_height = user_prefs.getInt(
				"navigation_bar_height", 0);

	}

	public void saveWindowSizeInfo(int status_bar_height,
			int navigation_bar_height) {
		this.mWindow_status_bar_height = status_bar_height;
		this.mWindow_navigation_bar_height = navigation_bar_height;

		prefs_editor = user_prefs.edit();
		prefs_editor.putInt("status_bar_height", status_bar_height);
		prefs_editor.putInt("navigation_bar_height", navigation_bar_height);
		prefs_editor.commit();

	}

	/**
	 * 현재 위치를 위도경도 값으로 얻어옵니다. 여기는 로케이션 받아오는 부분
	 */

	public Location getCurrentLocation() {
		LocationManager mgr = (LocationManager) getApplicationContext()
				.getSystemService("location");

		Location loc = mgr.getLastKnownLocation(getBestProvider(mgr));
		if (loc == null) {
			Log.d(TAG, "Test");
		}
		return loc;
	}

	private String getBestProvider(LocationManager locMgr) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(false);

		String provider = locMgr.getBestProvider(criteria, true);
		Log.d(TAG, provider);
		return provider;
	}

}
