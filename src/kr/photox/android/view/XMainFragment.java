package kr.photox.android.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.photox.android.R;
import kr.photox.android.api.MainListApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;
import kr.photox.android.utils.ImageCache.ImageCacheParams;
import kr.photox.android.utils.ImageFetcher;
import kr.photox.android.utils.ImageWorker;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;

public class XMainFragment extends Fragment implements
		PullToRefreshAttacher.OnRefreshListener {
	private static final String TAG = "Main Fragment";

	/**
	 * 
	 */
	private PullToRefreshAttacher mPullToRefreshAttacher;

	/**
	 * 
	 */

	private static final String IMAGE_CACHE_DIR = "thumbs";

	private int mImageThumbSize;
	private ImageFetcher mImageFetcher;

	/**
	 * 
	 */

	private DisplayMetrics displayMetrics = null;

	/**
	 * 
	 */

	// private Location mCurrent_location = null;

	/**
	 * 
	 */

	private Activity ctx;

	/**
	 * UI Reference
	 */
	private ProfilePictureView profilePictureView;
	private ListView mLv;
	TextView mLocationTv;

	/**
	 * Adapter Init
	 */

	private LvAdapter mLvAdapter;
	private ArrayList<Model> mModels_waiting = new ArrayList<Model>();
	private String[] mLv_label_colors;
	int mLv_label_color_position_offset = 0;
	/**
	 * 
	 */

	private int window_width;
	private int window_height;

	// private boolean isFirst = true;

	/**
	 * 
	 */
	SharedPreferences user_prefs;

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

		cacheParams.setMemCacheSizePercent(0.07f); // Set memory cache to 25% of
													// app memory

		int window_width = ((ApplicationManager) getActivity()
				.getApplicationContext()).getWindowSize("width");

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(getActivity(), window_width);
		mImageFetcher.setLoadingImage(R.drawable.base_empty_photo);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);

		/*
		 * 리스트뷰 라벨 색을 랜덤으로 출력하기 위한 offset을 랜덤으로 설정합니다.
		 */

		mLv_label_colors = getResources().getStringArray(
				R.array.list_label_colors);
		mLv_label_color_position_offset = (int) (Math.random() * mLv_label_colors.length);

	}

	/**
	 * On Create View
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.x_main_fragment, container, false);
		ctx = getActivity();

		/**
		 * ListView Default Setting
		 */

		mLv = (ListView) v.findViewById(R.id.lv);
		/**
		 * 
		 */
		window_width = ((ApplicationManager) getActivity()
				.getApplicationContext()).getWindowSize("width");

		/*
		 * 
		 */

		View header = (View) inflater.inflate(R.layout.base_profile_header,
				null);
		initLvHeader(header);
		mLv.addHeaderView(header);

		/*
		 * ListView Setting
		 */

		ArrayList<Model> items = new ArrayList<Model>();
		mLvAdapter = new LvAdapter(getActivity(), R.layout.x_main_fragment_lv,
				items);

		mLv.setAdapter(mLvAdapter);

		/*
		 * 
		 */

		mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView,
					int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					mImageFetcher.setPauseWork(true);
				} else {
					mImageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					addModelsInLvAdapter(5);
				}

			}
		});

		mLv.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {

						/*
						 * 
						 * if (mAdapter.getNumColumns() == 0) { final int
						 * numColumns = (int) Math.floor( mGridView.getWidth() /
						 * (mImageThumbSize + mImageThumbSpacing)); if
						 * (numColumns > 0) { final int columnWidth =
						 * (mGridView.getWidth() / numColumns) -
						 * mImageThumbSpacing;
						 * mAdapter.setNumColumns(numColumns);
						 * mAdapter.setItemHeight(columnWidth); if
						 * (BuildConfig.DEBUG) { Log.d(TAG,
						 * "onCreateView - numColumns set to " + numColumns); }
						 * } }
						 */
					}
				});

		/*
		 * 
		 */
		mPullToRefreshAttacher = ((XActivity) getActivity())
				.getPullToRefreshAttacher();
		PullToRefreshLayout ptrLayout = (PullToRefreshLayout) v
				.findViewById(R.id.ptr_layout);
		ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);

		/**
		 * reverse GeoCode Loading Task
		 */
		new requestReverseGeocodingTask().execute(false);

		/**
		 * 
		 */

		mPullToRefreshAttacher.setRefreshing(true);
		requestMainListApi();

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

		requestMainListApi();

	}

	/**
	 * ListView Header Setting
	 */

	private void initLvHeader(View header) {
		/*
		 * ListView Header Setting
		 */

		Button mHeaderBtn = (Button) header.findViewById(R.id.header_btn);
		profilePictureView = (ProfilePictureView) header
				.findViewById(R.id.profilePicture);
		TextView mNameTv = (TextView) header.findViewById(R.id.name_tv);
		mLocationTv = (TextView) header.findViewById(R.id.location_tv);

		/*
		 * 
		 */

		user_prefs = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		String user_name = user_prefs.getString("user_name", "User");
		String user_id = user_prefs.getString("user_id", "");

		/*
		 * 
		 */

		profilePictureView.setCropped(true);
		mNameTv.setText(user_name);

		if (!user_id.equals("")) {
			profilePictureView.setProfileId(user_id);
		}

		/*
		 * 
		 */

		mHeaderBtn.setOnClickListener(onClickListener);
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

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
		mLvAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
		mImageFetcher.setPauseWork(false);
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	/**
	 * DP to PX
	 * 
	 * @param dp
	 * @return
	 */

	public int dpToPx(int dp) {
		if (displayMetrics == null) {
			displayMetrics = getActivity().getResources().getDisplayMetrics();
		}

		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	/**
	 * Json Load
	 */

	private void requestMainListApi() {

		ApplicationManager am = (ApplicationManager) getActivity()
				.getApplicationContext();
		am.setOnJsonLoadingCompletionListener(onJsonLoadingCompletionListener);
		am.addJsonLoadingTask(new MainListApi());

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJsonLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			Log.d("Models size", Integer.toString(models.size()));

			/*
			 * 전송할수 없는 항목을 제외합니다.
			 */

			// int campaign_id = ((Campaign) models.get(0)).getId();
			// Log.i(TAG, "campaign_id : " + campaign_id);

			if (isCompleted) {

				((ArrayAdapter<Model>) mLvAdapter).clear();
				mModels_waiting.clear();
				mModels_waiting.addAll(models);

				/*
				 * ListView adapter item setting
				 */

				addModelsInLvAdapter(5);

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

	private void addModelsInLvAdapter(int num) {

		if (mModels_waiting.size() > 0) {

			for (int i = 0; i < Math.min(num, mModels_waiting.size()); i++) {
				mLvAdapter.models.add(mModels_waiting.get(0));
				XMainFragment.this.mModels_waiting.remove(0);

			}

			mLvAdapter.notifyDataSetChanged();
		}
	}

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
	 * ListView Apdater Setting
	 */

	private class LvAdapter extends ArrayAdapter<Model> {
		private static final String TAG = "Lv Adapter";

		private final int mPreparedMissionViewNumber = 1;
		private ViewHolder viewHolder = null;
		private Context context;
		public ArrayList<Model> models;
		private int textViewResourceId;

		public LvAdapter(Activity context, int textViewResourceId,
				ArrayList<Model> models) {
			super(context, textViewResourceId, models);

			this.context = context;
			this.textViewResourceId = textViewResourceId;
			this.models = models;

		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public int getCount() {
			return models.size();
		}

		@Override
		public Model getItem(int position) {
			return models.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			/*
			 * UI Initiailizing : View Holder
			 */

			if (convertView == null) {
				convertView = XMainFragment.this.ctx.getLayoutInflater()
						.inflate(textViewResourceId, null);

				viewHolder = new ViewHolder();

				viewHolder.mLabelView = convertView
						.findViewById(R.id.label_view);
				viewHolder.mLabelBg = (LinearLayout) viewHolder.mLabelView
						.findViewById(R.id.bg);
				viewHolder.mLabelTv = (TextView) viewHolder.mLabelView
						.findViewById(R.id.tv);
				viewHolder.mLabelBtn = (Button) viewHolder.mLabelView
						.findViewById(R.id.label_btn);

				viewHolder.mHsv = (HorizontalScrollView) convertView
						.findViewById(R.id.hsv);
				viewHolder.mHsv.setLayoutParams(new LinearLayout.LayoutParams(
						window_width, window_width * 62 / 100)); // 황금비 셋팅

				viewHolder.mMissionViews[0] = (View) convertView
						.findViewById(R.id.mission_view_0);

				for (int i = 0; i < mPreparedMissionViewNumber; i++) {
					/*
					 * 안에 있는 뷰들을 찾습니다.
					 */

					viewHolder.mMissionTvs[i] = (TextView) viewHolder.mMissionViews[i]
							.findViewById(R.id.tv);
					viewHolder.mMissionScoreTvs[i] = (TextView) viewHolder.mMissionViews[i]
							.findViewById(R.id.score_tv);
					viewHolder.mMissionIvs[i] = (ImageView) viewHolder.mMissionViews[i]
							.findViewById(R.id.iv);
					viewHolder.mMissionBtns[i] = (Button) viewHolder.mMissionViews[i]
							.findViewById(R.id.btn);

					/*
					 * 뷰들의 크기를 설정합니다.
					 */

					viewHolder.mMissionViews[i]
							.setLayoutParams(new LinearLayout.LayoutParams(
									window_width,
									LinearLayout.LayoutParams.MATCH_PARENT));
				}

				convertView.setTag(viewHolder);

			} else {

				viewHolder = (ViewHolder) convertView.getTag();
			}

			Campaign shampaign = (Campaign) this.getItem(position);

			/*
			 * 라벨 설정
			 */

			viewHolder.mLabelBg
					.setBackgroundColor(Color
							.parseColor(mLv_label_colors[(mLv_label_color_position_offset + position)
									% mLv_label_colors.length]));
			viewHolder.mLabelTv.setText(shampaign.getTitle());

			/*
			 * Data Import and export
			 */

			Log.i(TAG, "shampaign_id : " + shampaign.getId());

			ArrayList<Mission> missions = new ArrayList<Mission>();
			missions = shampaign.getMissions();

			/*
			 * Visibillity Setting
			 */

			setDataAndVisibillity(viewHolder, missions, position);

			/*
			 * 이미지 셋팅
			 */
			ImageWorker.cancelWork(viewHolder.mMissionIvs[0]);
			viewHolder.mMissionIvs[0].setImageDrawable(null);

			mImageFetcher.loadImage(((Campaign) models.get(position))
					.getMissions().get(0).getCoverImgUrl(),
					viewHolder.mMissionIvs[0]);

			/*
			 * 
			 */

			viewHolder.mMissionBtns[0]
					.setOnClickListener(getLvItemClickListener(position));
			viewHolder.mLabelBtn
					.setOnClickListener(getLvItemClickListener(position));

			return convertView;
		}

		private void setDataAndVisibillity(ViewHolder viewHolder,
				ArrayList<Mission> missions, int position) {
			for (int i = 0; i < mPreparedMissionViewNumber; i++) {
				if (i < missions.size()) {
					/*
					 * 뷰를 보이게 설정합니다.
					 */
					viewHolder.mMissionViews[i].setVisibility(View.VISIBLE);

					/*
					 * 텍스트를 설정합니다.
					 */

					viewHolder.mMissionTvs[i].setText(missions.get(i)
							.getTitle());
					viewHolder.mMissionScoreTvs[i].setText(Integer
							.toString(missions.get(i).getScore()));

					/*
					 * 이미지를 설정합니다.
					 */
					/*
					 * if (i == 0) { viewHolder.mMissionIvs[0]
					 * .setImageBitmap(getBitmapFromUrl(missions
					 * .get(i).getCoverImgUrl(), position, i));
					 * 
					 * }
					 * 
					 * /* 클릭 리스너를 설정합니다.
					 */
					// setOnLvItemClickListener(viewHolder.mLabelBtn, position);
					// setOnLvItemClickListener(viewHolder.mMissionBtns[0],
					// position);

				} else {
					/*
					 * 뷰를 보이지 않게 설정합니다.
					 */
					viewHolder.mMissionViews[i].setVisibility(View.GONE);
				}
			}
		}

		private class ViewHolder {

			View mLabelView;
			LinearLayout mLabelBg;
			TextView mLabelTv;
			Button mLabelBtn;

			HorizontalScrollView mHsv;
			// View mLl;

			View[] mMissionViews = new View[mPreparedMissionViewNumber];
			TextView[] mMissionTvs = new TextView[mPreparedMissionViewNumber];
			TextView[] mMissionScoreTvs = new TextView[mPreparedMissionViewNumber];
			ImageView[] mMissionIvs = new ImageView[mPreparedMissionViewNumber];
			Button[] mMissionBtns = new Button[mPreparedMissionViewNumber];
		}

	}

	/**
	 * 
	 */

	private OnClickListener getLvItemClickListener(final int position) {
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {

				/*
				 * 전송할수 없는 항목을 제외합니다.
				 */

				int campaign_id = ((Campaign) mLvAdapter.models.get(position))
						.getId();

				if (campaign_id == -1) {
					campaign_id = -((Campaign) mLvAdapter.models.get(position))
							.getMissions().get(0).getId();
				}

				String campaign_title = ((Campaign) mLvAdapter.models
						.get(position)).getTitle();
				Log.i(TAG, "campaign_id" + campaign_id);

				/*
				 * 보냅니다.
				 */

				XActivity mainActivity = (XActivity) getActivity();
				mainActivity
						.startShampaignActivity(campaign_id, campaign_title);

				Log.d(TAG, "onItemClicked!");

			}
		};

		return onClickListener;
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
		case R.id.header_btn:
			new requestReverseGeocodingTask().execute(true);
			break;
		default:
			break;
		}
	};

}
