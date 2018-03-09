package kr.photox.android.deprecated;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.CampaignDetailApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Campaign;
import kr.photox.android.model.Model;
import kr.photox.android.view.XMainFragment;
import kr.photox.android.view.MActivity;
import kr.photox.android.view.SearchActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivityTemp extends SherlockFragmentActivity {

	private final String TAG = "Main Activity";

	ProgressDialog dialog = null;
	/**
	 * UI Reference
	 */
	private Fragment mContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.x_activity);
		Log.d(TAG, "onCreated!");
		
		
		
		

		/*
		 * UI Setting
		 *//*
			 * mActionbar = (View) findViewById(R.id.actionbar); mTitleTv =
			 * (TextView) mActionbar.findViewById(R.id.title_tv); mSettingBtn =
			 * (ImageButton) mActionbar .findViewById(R.id.actionbar_left_btn);
			 * mSearchBtn = (ImageButton) mActionbar
			 * .findViewById(R.id.actionbar_right_btn);
			 * 
			 * /*
			 */

		/*
		 * Visibllity Setting
		 */

		// mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);

		/**
		 * Initialize the Content Fragment
		 */

		if (mContent == null) {
			mContent = new XMainFragment();

		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_frame, mContent).commit();

	}

	@Override
	public void onResume() {
		super.onResume();

		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}

	}

	/**
	 * Switch Content
	 * 
	 * @param fragment
	 */

	public void switchContent(final Fragment fragment) {
		mContent = fragment;
		// mContent_id = fragment_id;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_frame, fragment).commit();

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
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.actionbar_right_btn:
			Intent intentSearch = new Intent(this, SearchActivity.class);
			startActivity(intentSearch);
			break;
		default:
			break;
		}
	};

	/**
	 * Shampaign Fragment Start
	 */

	public void onShampaignFragmentCreated() {
		// Intent intent = new Intent("AsyncTaskMananger");
		// intent.putExtra("async_task_assinged_num", 0);
		// startService(intent);
	}

	/**
	 * 
	 */

	public void shampaignActivityStart(Model model) {
		dialog = ProgressDialog.show(MainActivityTemp.this, "", "로딩중입니다...", true);
		Log.d(TAG, "shampaign Activity Start");

		/*
		 * 데이터를 넣어 새로운 액티비티를 실행합니다.
		 */

		Intent intent = new Intent(MainActivityTemp.this, MActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putSerializable("shampaigns", shampaigns);
		// intent.putExtras(bundle);

		Campaign shampaign = (Campaign) model;

		//intent.putExtra("campaign", shampaign);

		startActivity(intent);

	}

	/**
	 * 
	 */

	public void headerButtonClicked(String user_name, String location) {
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra("user_name", user_name);
		intent.putExtra("location_name", location);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	/**
	 * 미션셋 액티비티를 실행합니다. 캠페인을 넣었을 때
	 */

	public void startMissionSetActivity(Campaign campaign) {
		dialog = ProgressDialog.show(MainActivityTemp.this, "", "로딩중입니다...", true);
		Log.d(TAG, "startMissionSetActivity Started!");

		loadCampaign(campaign.getId());

	}

	private void loadCampaign(int id) {
		ApplicationManager am = (ApplicationManager) getApplicationContext();
		am.setOnJsonLoadingCompletionListener(onJsonLoadingCompletionListener);
		CampaignDetailApi api = new CampaignDetailApi();
		api.setInput(id);
		am.addJsonLoadingTask(api);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJsonLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {

		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {
			Log.d(TAG, "Models size : " + Integer.toString(models.size()));

			Campaign campaign = (Campaign) models.get(0);

			/*
			 * 데이터를 넣어 새로운 액티비티를 실행합니다.
			 */

			Intent intent = new Intent(MainActivityTemp.this,
					MActivity.class);
		//intent.putExtra("campaign", campaign);

			startActivity(intent);

		}
	};

}
