package kr.photox.android.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.photox.android.R;
import kr.photox.android.api.StatisticApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Campaign;
import kr.photox.android.model.Model;
import kr.photox.android.utils.ImageCache.ImageCacheParams;
import kr.photox.android.utils.ImageFetcher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;

public class XStatisticFragment extends Fragment implements
		PullToRefreshAttacher.OnRefreshListener {
	private static final String TAG = "X Statistic Fragment";

	/**
	 * 
	 */
	private PullToRefreshAttacher mPullToRefreshAttacher;

	/**
	 * Preference Auto Login
	 */

	private SharedPreferences user_prefs;
	private SharedPreferences.Editor prefs_editor;

	/**
	 * 
	 */

	private static final String IMAGE_CACHE_DIR = "thumbs";
	private int mImageThumbSize;
	private ImageFetcher mImageFetcher;

	/**
	 * UI Reference
	 */

	private TextView mLocationTv;
	private TextView mUserTotalScoreTv;
	private TextView mUserMissionCountTv;

	private View mRewardsView;
	private LinearLayout mRewardsViewFrame;

	/**
	 * 
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * 
		 */

		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);

		ImageCacheParams cacheParams = new ImageCacheParams(getActivity(),
				IMAGE_CACHE_DIR);

		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
													// app memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
		mImageFetcher
				.setLoadingImage(R.drawable.x_statistic_fragment_icon_default);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);

	}

	/**
	 * On Create View
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.x_statistic_fragment, container,
				false);

		/*
		 * ListView Header Setting
		 */

		View profile_view = v.findViewById(R.id.profile_view);
		ProfilePictureView profilePictureView = (ProfilePictureView) profile_view
				.findViewById(R.id.profilePicture);
		TextView mNameTv = (TextView) profile_view.findViewById(R.id.name_tv);
		mLocationTv = (TextView) profile_view.findViewById(R.id.location_tv);
		Button mHeaderBtn = (Button) profile_view.findViewById(R.id.header_btn);

		View overview_view = v.findViewById(R.id.overview_view);
		mUserTotalScoreTv = (TextView) overview_view
				.findViewById(R.id.score_tv);
		mUserMissionCountTv = (TextView) overview_view
				.findViewById(R.id.count_tv);

		mRewardsView = v.findViewById(R.id.rewards_view);
		mRewardsViewFrame = (LinearLayout) mRewardsView
				.findViewById(R.id.frame);

		/*
		 * header에 들어갈 정보를 받아옵니다.
		 */

		user_prefs = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_name = user_prefs.getString("user_name", "User");
		String user_id = user_prefs.getString("user_id", "");

		/*
		 * header에 정보를 넣습니다.
		 */

		profilePictureView.setCropped(true);
		mNameTv.setText(user_name);
		if (!user_id.equals("")) {
			profilePictureView.setProfileId(user_id);
		}

		/*
		 * 클릭리스너를 설정합니다.
		 */

		mHeaderBtn.setOnClickListener(onClickListener);

		/*
		 * 
		 */
		mPullToRefreshAttacher = ((XActivity) getActivity())
				.getPullToRefreshAttacher();
		PullToRefreshLayout ptrLayout = (PullToRefreshLayout) v
				.findViewById(R.id.ptr_layout);
		ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);

		/**
		 * 정보들을 요청합니다.
		 */

		new requestReverseGeocodingTask().execute(false);

		mPullToRefreshAttacher.setRefreshing(true);
		this.requestStatisticApi();

		return v;
	}

	/**
	 * 
	 */

	@Override
	public void onRefreshStarted(View view) {
		Log.d(TAG, "refresh started");

		/*
		 * 
		 */

		this.requestStatisticApi();

	}

	/**
	 * Get Address From Geocode
	 */

	public class requestReverseGeocodingTask extends
			AsyncTask<Boolean, Boolean, String> {

		private boolean showProgress = false;

		protected void onPreExecute() {

		}

		protected String doInBackground(Boolean... showProgress) {

			/**
			 * 프로그레스 바를 띄울지 결정합니다.
			 */

			publishProgress(showProgress[0]);

			/**
			 * 여기는 로케이션 받아오는 부분
			 */

			Location current_location = ((ApplicationManager) getActivity()
					.getApplicationContext()).getCurrentLocation();

			/**
			 * 리벌스 지오코딩 부분이
			 */
			String locations = "";
			Geocoder geocoder = new Geocoder(getActivity()
					.getApplicationContext(), Locale.getDefault());

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

		protected void onProgressUpdate(Boolean... showProgress) {
			if (showProgress[0]) {
				XActivity mMainActvity = (XActivity) getActivity();
				mMainActvity.showDialog("위치를 새로고침 하는 중입니다...");
				this.showProgress = true;
			}
		}

		protected void onCancelled() {

		}

		protected void onPostExecute(String result) {

			Log.d(TAG, result);

			/*
			 * 최적화는 정말 나중에 합시다! 일단, 위치정보기반으로
			 */

			if (!result.equals("")) {
				mLocationTv.setText(result);
			} else {
				mLocationTv.setText("현재 위치를 가져올 수 없습니다.");
			}
			/*
			 * 
			 */

			XActivity mMainActvity = (XActivity) getActivity();
			mMainActvity.hideDialog();

			if (this.showProgress) {
				Toast.makeText(getActivity(), "위치를 새로고침 했습니다.",
						Toast.LENGTH_SHORT).show();
			}

		}

	}

	/**
	 * Button onClick Listener
	 */

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			clickListenerHandler(v.getId());
		}
	};

	private void clickListenerHandler(int id) {
		switch (id) {
		case R.id.header_btn:
			requestReverseGeocoding();
			break;
		default:
			break;
		}
	};

	/**
	 * 
	 */

	private void requestReverseGeocoding() {
		new requestReverseGeocodingTask().execute(true);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 * 
	 * 
	 */

	private void requestStatisticApi() {

		// mLl.setVisibility(View.GONE);

		/*
		 * 
		 */
		ApplicationManager am = ((ApplicationManager) getActivity()
				.getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onStatisticApiCompletionListener);

		/*
		 * Api컨트롤러에 로그아웃 에이피아이를 추가합니다.
		 */

		am.addJsonLoadingTask(new StatisticApi());

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onStatisticApiCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {

				Log.d(TAG, "models size : " + models.size());
				Campaign campaign = (Campaign) models.get(0);

				Log.d(TAG,
						"total_mission_count : "
								+ campaign.getTotal_mission_count());
				Log.d(TAG, "total_score : " + campaign.getTotalScore());

				mUserTotalScoreTv.setText(Integer.toString(campaign
						.getTotalScore()) + " 점");
				mUserMissionCountTv.setText(Integer.toString(campaign
						.getTotal_mission_count()) + " 개");

				models.remove(0);

				/*
				 * 
				 */

				int models_size = models.size();

				if (models_size == 0) {
					mRewardsView.setVisibility(View.GONE);

				} else {
					int child_size = mRewardsViewFrame.getChildCount();
					for (int i = 2; i < child_size; i++) {
						mRewardsViewFrame.removeViewAt(2);
					}

					LayoutInflater inflater = getActivity().getLayoutInflater();
					int row_count = (Math.min(8, models_size) + 1) / 2;

					String[] mLv_label_colors = getResources().getStringArray(
							R.array.list_label_colors);
					int mLv_label_color_position_offset = (int) (Math.random() * mLv_label_colors.length);

					for (int i = 0; i < row_count; i++) {
						View mRewardRow = inflater.inflate(
								R.layout.x_statistic_fragment_rewards_row,
								null, false);

						/*
						 * 
						 */

						initRewardItem(
								mRewardRow.findViewById(R.id.reward_left),
								(Campaign) models.get(i * 2),
								Color.parseColor(mLv_label_colors[(mLv_label_color_position_offset + (i * 2))
										% mLv_label_colors.length]));

						if ((Math.min(8, models_size) % 2 == 0)
								|| (i < row_count - 1)) {
							initRewardItem(
									mRewardRow.findViewById(R.id.reward_right),
									(Campaign) models.get(i * 2 + 1),
									Color.parseColor(mLv_label_colors[(mLv_label_color_position_offset + (i * 2 + 1))
											% mLv_label_colors.length]));
						} else {
							mRewardRow.findViewById(R.id.reward_right)
									.setVisibility(View.INVISIBLE);
						}

						mRewardsViewFrame.addView(mRewardRow);

					}

					if (models_size > 8) {
						// TODO 더보기 버튼 만들어야 함.
					}

					mRewardsView.setVisibility(View.VISIBLE);

				}

			} else {
				Toast.makeText(getActivity(),
						"인터넷 연결이 불안정합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG)
						.show();
			}

			/*
			 * Simulate Refresh with 1 seconds sleep
			 */

			new delayTask().execute(1000);

		}
	};

	/**
	 * 
	 */

	private class delayTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... time) {
			try {
				Thread.sleep(time[0]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// Notify PullToRefreshAttacher that the refresh has finished
			mPullToRefreshAttacher.setRefreshComplete();
		}
	}

	/**
	 * 
	 * @param v
	 * @param pos
	 */

	private void initRewardItem(View v, Campaign campaign, int color) {

		/*
		 * 
		 */
		TextView mRewardTv = (TextView) v.findViewById(R.id.tv);
		Button mRewardBtn = (Button) v.findViewById(R.id.btn);
		ImageView mRewardIv = (ImageView) v.findViewById(R.id.iv);

		/*
		 * 이미지가 안예뻐서 잠깐 주석 처리.
		 */
		mRewardTv.setText(campaign.getTitle());
		mRewardTv.setBackgroundColor(color);
		// mImageFetcher.loadImage(campaign.getIconImgUrl(), mRewardIv);

		/*
		 * 
		 */

		final int campaign_id = campaign.getId();
		final String campaign_title = campaign.getTitle();

		mRewardBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				XActivity xActivity = (XActivity) getActivity();
				xActivity.startCampaignActivity(campaign_id, campaign_title);
			}
		});

	}

}
