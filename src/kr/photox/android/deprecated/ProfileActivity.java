package kr.photox.android.deprecated;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.StatisticApi;
import kr.photox.android.api.TodoListApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;
import kr.photox.android.model.Shot;
import kr.photox.android.view.XAlbumFragment;
import kr.photox.android.view.XCampaignFragment;
import kr.photox.android.view.XTodoFragment;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ProfileActivity extends SherlockFragmentActivity {
	private final String TAG = "Profile Activity";

	/**
	 * 
	 */
	private TextView[] mTabTv = new TextView[3];
	private TextView[] mTabIndicator = new TextView[3];

	private View mLoadingView;
	private ViewPager mPager;

	/**
	 * View Pager 관련
	 */

	ProfileFragmentAdapter mAdapter;

	/**
	 * 
	 */
	Mission mission;
	ArrayList<Model> todos = new ArrayList<Model>();
	String user_name;
	String location_name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);

		/*
		 * 이전 인텐트에서 기본 정보를 받아옵니다.
		 */

		user_name = getIntent().getExtras().getString("user_name", "User");
		location_name = getIntent().getExtras().getString("location_name",
				"현재 위치를 불러올 수 없습니다");

		/*
		 * UI Reference
		 */

		mTabTv[0] = (TextView) findViewById(R.id.tab_0_tv);
		mTabTv[1] = (TextView) findViewById(R.id.tab_1_tv);
		mTabTv[2] = (TextView) findViewById(R.id.tab_2_tv);

		mTabIndicator[0] = (TextView) findViewById(R.id.tab_0_indicator);
		mTabIndicator[1] = (TextView) findViewById(R.id.tab_1_indicator);
		mTabIndicator[2] = (TextView) findViewById(R.id.tab_2_indicator);

		mLoadingView = (View) findViewById(R.id.loading_view);
		mPager = (ViewPager) findViewById(R.id.pager);

		/*
		 * 
		 */

		requestHistoryListApi();

	}

	/**
	 * request history list api
	 */

	private void requestHistoryListApi() {

		ApplicationManager am = ((ApplicationManager) getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onHistoryListApiCompletionListener);
		am.addJsonLoadingTask(new StatisticApi());

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onHistoryListApiCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {
			Log.d(TAG, "Models size : " + Integer.toString(models.size()));

			mission = (Mission) models.get(0);

			requestTodoListApi();

		}
	};

	/**
	 * request todo list api
	 */

	private void requestTodoListApi() {

		ApplicationManager am = ((ApplicationManager) getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onTodoListApiCompletionListener);
		am.addJsonLoadingTask(new TodoListApi());

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onTodoListApiCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {
			Log.d(TAG, "Models size : " + Integer.toString(models.size()));

			todos = models;
			mLoadingView.setVisibility(View.GONE);

			/*
			 * 
			 */

			mAdapter = new ProfileFragmentAdapter(getSupportFragmentManager());
			mPager.setAdapter(mAdapter);
			mPager.setOffscreenPageLimit(3);

			mPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {

					for (int i = 0; i < 3; i++) {
						if (i == position) {
							mTabTv[position].setTextColor(0xFF000000);
							mTabIndicator[position].setVisibility(View.VISIBLE);
						} else {
							mTabTv[i].setTextColor(0xFF888888);
							mTabIndicator[i].setVisibility(View.INVISIBLE);
						}
					}

				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {

				}
			});

		}
	};

	/**
	 * 
	 * @author yearnning
	 * 
	 */

	private class ProfileFragmentAdapter extends FragmentStatePagerAdapter {

		public ProfileFragmentAdapter(FragmentManager fm) {
			super(fm);

		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				XTodoFragment fragment = new XTodoFragment();
				return fragment;
			} else if (position == 1) {
				XAlbumFragment fragment = new XAlbumFragment();
				return fragment;
			} else if (position == 2) {
				XCampaignFragment fragment = new XCampaignFragment();
				return fragment;
			} else {
				XTodoFragment fragment = new XTodoFragment();
				return fragment;
			}

		}

		@Override
		public int getCount() {
			return 3;
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
		default:
			break;
		}
	};

	/**
	 * 
	 */

	public ArrayList<Model> getTodos() {
		return this.todos;
	}

	public ArrayList<Shot> getShots() {
		return this.mission.getShots();
	}

	public String getUserName() {
		return this.user_name;
	}

	public String getLocationName() {
		return this.location_name;
	}

	/**
	 * Back Key Listener
	 */

	@Override
	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called");

		finish();
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);

	}

}
