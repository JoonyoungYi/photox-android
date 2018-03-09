package kr.photox.android.view;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.ShampaignDetailApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;
import kr.photox.android.utils.ImageCache;
import kr.photox.android.utils.ImageFetcher;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

public class MActivity extends SherlockFragmentActivity {
	private static final String TAG = "Mission Set Activity";

	/**
	 * 
	 */

	private static final String IMAGE_CACHE_DIR = "images";
	public static final String EXTRA_IMAGE = "extra_image";
	private ImageFetcher mImageFetcher;

	/**
	 * 
	 */
	ProgressDialog dialog = null;

	/**
	 * Cached Data
	 */

	ArrayList<Mission> missions = new ArrayList<Mission>();
	ArrayList<Integer> mission_ids = new ArrayList<Integer>();

	/**
	 * UI Reference
	 */

	private View mLoadingView;
	private PagerSlidingTabStrip mTabs;
	private ViewPager mPager;

	/**
	 * Status
	 */

	private int actionbar_size_num = 2;

	/**
	 * View Pager 관련
	 */

	private MissionFragmentAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.m_activity);
		Log.d(TAG, "Activity onCreated Started!");

		/**
		 * 
		 */

		int window_width = ((ApplicationManager) getApplicationContext())
				.getWindowSize("width");

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.10f); // Set memory cache to 25% of
													// app memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(this, window_width);
		mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
		mImageFetcher.setImageFadeIn(false);

		/*
		 * 
		 */

		int campaign_id = getIntent().getExtras().getInt("campaign_id", 0);
		String campaign_title = getIntent().getExtras().getString(
				"campaign_title", null);
		Log.i(TAG, "shampaign_id" + campaign_id);

		/*
		 * UI Setting
		 */
		View mActionbar = (View) findViewById(R.id.actionbar);
		TextView mTitleTv = (TextView) mActionbar.findViewById(R.id.title_tv);
		ImageButton mBackBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_left_btn);
		ImageButton mRightBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_right_btn);

		mLoadingView = (View) findViewById(R.id.loading_view);
		mPager = (ViewPager) findViewById(R.id.pager);
		mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

		/*
		 * View Pager Tabs Setting
		 */

		mTabs.setIndicatorColor(0xFF3e3e3e);
		mTabs.setTextColor(0xFF000000);
		Typeface tf = Typeface.createFromAsset(MActivity.this.getAssets(),
				"fonts/NanumGothic.otf");
		mTabs.setTypeface(tf, 0);

		/*
		 * 
		 */

		mAdapter = new MissionFragmentAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);

		/*
		 * pager의 페이지 리밋 설정 한번에 로드하는 화면의 개수를 결정합니다.
		 */

		mPager.setOffscreenPageLimit(2);

		/**
		 * 
		 */

		/*
		 * 
		 */
		/*
		 * mPager.setOnTouchListener(new View.OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 * v.getParent().requestDisallowInterceptTouchEvent(true); return false;
		 * } });
		 * 
		 * mPager.setOnPageChangeListener(new OnPageChangeListener() {
		 * 
		 * @Override public void onPageSelected(int arg0) { }
		 * 
		 * @Override public void onPageScrolled(int arg0, float arg1, int arg2)
		 * { mPager.getParent().requestDisallowInterceptTouchEvent(true); }
		 * 
		 * @Override public void onPageScrollStateChanged(int arg0) { } });
		 * 
		 * 
		 * 
		 * /* for (int i = 0; i < shampaign.getMissions().size(); i++) {
		 * JsonLoad(i); }
		 */

		/*
		 * Visibllity Setting
		 */

		if (campaign_title == null) {
			mActionbar.setVisibility(View.GONE);
			actionbar_size_num -= 1;
		} else {
			mTitleTv.setText(campaign_title);
		}

		mBackBtn.setPadding(0, 0, 0, 0);
		mBackBtn.setImageResource(R.drawable.base_actionbar_dir_left);
		mRightBtn.setVisibility(View.INVISIBLE);

		/*
		 * Click event
		 */

		mBackBtn.setOnClickListener(onClickListener);

		/*
		 * campaign_id가 음수이면 미션 아이디라고 약속합니다.
		 */

		if (campaign_id < 0) {

			mAdapter.addMissionId(-campaign_id);
			mAdapter.notifyDataSetChanged();

			/*
			 * Visibllity를 설정합니다.
			 */

			mPager.setVisibility(View.VISIBLE);
			mLoadingView.setVisibility(View.GONE);

			actionbar_size_num -= 1;

		} else {
			requestShampaignDetailApi(campaign_id);
		}

	}

	public int getActionbarSizeNumber() {
		return this.actionbar_size_num;
	}

	/**
	 * 
	 */

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);

		/*
		 * 
		 */
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	/**
	 * Called by the ViewPager child fragments to load images via the one
	 * ImageFetcher
	 */
	public ImageFetcher getImageFetcher() {
		return mImageFetcher;
	}

	/**
	 * request history list api
	 */

	private void requestShampaignDetailApi(int shampaign_id) {
		ApplicationManager am = ((ApplicationManager) getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onShampaignApiCompletionListener);
		ShampaignDetailApi api = new ShampaignDetailApi();
		api.setInput(shampaign_id);
		am.addJsonLoadingTask(api);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onShampaignApiCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {

				for (Model model : models) {
					mAdapter.addMissionId(((Mission) model).getId());
					mAdapter.addMissionTitle(((Mission) model).getTitle());
				}

				mAdapter.notifyDataSetChanged();
				mTabs.setViewPager(mPager);

				/*
				 * Visibllity를 설정합니다.
				 */
				if (models.size() == 1) {
					mTabs.setVisibility(View.GONE);
					actionbar_size_num -= 1;
				} else {
					mTabs.setVisibility(View.VISIBLE);

				}
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
		default:
			break;
		}
	};

	/**
	 * 
	 * @author yearnning
	 * 
	 */

	private class MissionFragmentAdapter extends FragmentStatePagerAdapter {
		private ArrayList<Integer> models_id = new ArrayList<Integer>();
		private ArrayList<String> models_title = new ArrayList<String>();

		public MissionFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			MFragment fragment = new MFragment();
			fragment.setMissionId(models_id.get(position));
			return fragment;

		}

		@Override
		public int getCount() {
			return models_id.size();
		}

		public void addMissionId(int id) {
			this.models_id.add(id);
		}

		public void addMissionTitle(String title) {
			this.models_title.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return models_title.get(position);
		}

		/*
		 * @Override public int getItemPosition(Object item) { MFragment
		 * fragment = (MFragment) item; int mission_id =
		 * fragment.getMissionId(); int position =
		 * models_id.indexOf(mission_id);
		 * 
		 * if (position >= 0) { return position; } else { return POSITION_NONE;
		 * }
		 * 
		 * }
		 */

	}

	/**
	 * 미션셋 액티비티를 실행합니다. 캠페인을 넣었을 때
	 */

	public void startCampaignActivity(int id, String title) {
		dialog = ProgressDialog.show(MActivity.this, "", "로딩중입니다...", true,
				true);

		Intent intent = new Intent(MActivity.this, NActivity.class);
		intent.putExtra("campaign_id", id);
		intent.putExtra("campaign_title", title);
		startActivity(intent);

	}

	/**
	 * Camera Activity Start
	 * 
	 * @param id
	 */

	public void startCameraActivity(final int mission_id,
			final String mission_title) {
		dialog = ProgressDialog.show(this, "", "로딩중입니다...", true, true);

		mImageFetcher.clearCache();

		/*
		 * 카메라 실행에 필요한 값을 대입하고, 카메라 액티비티를 실행합니다.
		 */

		Intent intent = new Intent(MActivity.this, CActivity.class);
		intent.putExtra("mission_id", mission_id);
		intent.putExtra("mission_title", mission_title);
		intent.putExtra("decal_position", 0);
		intent.putExtra("decal_message", "");
		startActivity(intent);

	}

	/**
	 * start map Activity
	 */

	public void startMapActivity(double latitude, double longitude) {
		dialog = ProgressDialog.show(this, "", "로딩중입니다...", true, true);

		Intent intent = new Intent(MActivity.this, MMapActivity.class);
		Log.d(TAG, "map Activity Start!");
		intent.putExtra("latitude", latitude);
		intent.putExtra("longitude", longitude);
		startActivity(intent);

	}

	/**
	 * 
	 */
	public void showLoadingDialog() {
		dialog = ProgressDialog.show(this, "", "로딩중입니다...", true, true);
	}

	public void hideLoadingDialog() {

		dialog.cancel();
		dialog = null;
	}

}
