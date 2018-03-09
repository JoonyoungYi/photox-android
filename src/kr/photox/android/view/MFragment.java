package kr.photox.android.view;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.MissionDetailApi;
import kr.photox.android.api.TodoAddApi;
import kr.photox.android.api.TodoDeleteApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;
import kr.photox.android.utils.ImageFetcher;
import kr.photox.android.utils.ImageWorker;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

public final class MFragment extends Fragment {
	private static final String TAG = "Mission Fragment";

	/**
	 * Cached Data
	 */

	private Mission mission;
	private int mission_id;

	/**
	 * Image Cache
	 */

	private static final String IMAGE_DATA_EXTRA = "extra_image_data";
	private ImageFetcher mImageFetcher;

	/**
	 * UI Reference
	 */

	private View mLoadingView;
	private ImageView mIv;
	private SlidingDrawer mSd;

	private TextView mTitleTv;
	private TextView mScoreTv;
	private ImageButton mCameraButton;

	private ListView mLv;
	private View mLvFooterView;

	private TextView mDescriptionTv;
	private ImageView mTodoIv;
	private Button mTodoBtn;
	private Button mLocationBtn;
	private Button mShareBtn;

	/**
	 * Status
	 */

	private boolean isTodoOn;

	/*
	 * 
	 */

	private LvAdapter mLvAdapter;
	private String[] mLv_label_colors;
	private int mLv_label_color_position_offset = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		/*
		 * 리스트뷰 라벨 색을 랜덤으로 출력하기 위한 offset을 랜덤으로 설정합니다.
		 */

		mLv_label_colors = getResources().getStringArray(
				R.array.list_label_colors);
		mLv_label_color_position_offset = (int) (Math.random() * mLv_label_colors.length);
	}

	/**
	 * On Created View Method
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ApplicationManager am = (ApplicationManager) getActivity()
				.getApplicationContext();
		View v = inflater.inflate(R.layout.m_fragment, container, false);

		/*
		 * 
		 */

		mLoadingView = (View) v.findViewById(R.id.loading_view);
		mIv = (ImageView) v.findViewById(R.id.iv);
		mSd = (SlidingDrawer) v.findViewById(R.id.sd);

		View mHeaderView = (View) v.findViewById(R.id.header_view);
		mTitleTv = (TextView) mHeaderView.findViewById(R.id.title_tv);
		mScoreTv = (TextView) mHeaderView.findViewById(R.id.score_tv);
		mCameraButton = (ImageButton) mHeaderView.findViewById(R.id.camera_btn);

		mLv = (ListView) v.findViewById(R.id.lv);
		mLvFooterView = (View) inflater.inflate(R.layout.base_loading_footer,
				null);

		/*
		 * 
		 */
		ImageWorker.cancelWork(mIv);
		mIv.setImageDrawable(null);

		/*
		 * 
		 */

		mLvAdapter = new LvAdapter(getActivity(), R.layout.base_campaign,
				new ArrayList<Campaign>());

		/*
		 * 
		 */

		mSd.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				if (mLvAdapter.campaigns.size() == 0) {
					mLvAdapter.campaigns.addAll(mission.getCampaigns());
					mLvAdapter.notifyDataSetChanged();
					mLvFooterView.setVisibility(View.GONE);
				}

			}
		});

		/*
		 * 크기 설정
		 */

		int window_width = am.getWindowSize("width");
		int window_height = am.getWindowSize("height");
		int status_bar_height = am.getWindowSize("status_bar_height");
		int navigation_bar_height = am.getWindowSize("navigation_bar_height");
		int actionbar_height = getResources().getDimensionPixelSize(
				R.dimen.abs__action_bar_default_height);
		mIv.setLayoutParams(new FrameLayout.LayoutParams(window_width,
				window_height - status_bar_height - navigation_bar_height
						- actionbar_height
						* ((MActivity) getActivity()).getActionbarSizeNumber()));

		/*
		 * 클릭 리스너 설정
		 */

		mCameraButton.setOnClickListener(onClickListener);

		/*
		 * 미션 세부 항목을 로드합니다.
		 */

		JsonLoad();

		return v;

	}

	/**
	 * 
	 */

	public void setMissionId(final int id) {
		this.mission_id = id;
	}

	public int getMissionId() {
		return this.mission_id;
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
		MActivity mShampaignActivity = (MActivity) getActivity();

		switch (id) {
		case R.id.camera_btn:
			mShampaignActivity.startCameraActivity(this.mission_id,
					this.mission.getTitle());
			break;
		case R.id.share_btn:
			Toast.makeText(getActivity(), "준비중인 기능입니다.", Toast.LENGTH_LONG)
					.show();
			break;
		default:
			break;
		}
	};

	/**
	 * Json Load
	 */

	private void JsonLoad() {
		ApplicationManager am = ((ApplicationManager) getActivity()
				.getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onJsonLoadingCompletionListener);
		MissionDetailApi api = new MissionDetailApi();
		api.setInput(this.mission_id);
		am.addJsonLoadingTask(api);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJsonLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {

				Log.d(TAG, "Models size" + Integer.toString(models.size()));
				MFragment.this.mission = (Mission) models.get(0);

				// updateDetailView();

				/*
				 * 데이터를 넣습니다.
				 */

				mTitleTv.setText(MFragment.this.mission.getTitle());
				mScoreTv.setText(Integer.toString(MFragment.this.mission
						.getScore()) + " point");

				/*
				 * 이미지가 존재한다면, 이미지를 로드합니다.
				 */

				if (MFragment.this.mission.getCoverImgUrl() != null) {

					// Use the parent activity to load the image asynchronously
					// into the ImageView (so a single
					// cache can be used over all pages in the ViewPager
					// if (ImageDetailActivity.class.isInstance(getActivity()))
					// {

					if ((getActivity() != null)
							&& (getActivity() instanceof MActivity)) {
						mImageFetcher = ((MActivity) getActivity())
								.getImageFetcher();
						mImageFetcher.loadImage(
								MFragment.this.mission.getCoverImgUrl(), mIv);

					}

					Log.d(TAG, "there is thumbnail img url");
				}

				/*
			 * 
			 */

				mIv.setVisibility(View.VISIBLE);
				mSd.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.GONE);

				/*
				 * 헤더뷰를 미리 추가해둡니다. 만약 최적화를 더 하고 싶다면, 이부분을 백그라운드에서 돌려서 뷰자체를 받아오면
				 * 될것 같습니다. 아마도 어싱크태스크는 안될겁니다. 핸들러 사용하고 핸들러 사용시 주의점을 자세히 읽어서 오류가
				 * 나지 않게 하길 바랍니다.
				 */

				addListViewHeader();
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	/**
	 * 
	 */

	private void addListViewHeader() {
		View detailView = (View) getActivity().getLayoutInflater().inflate(
				R.layout.m_fragment_detail, null);
		mDescriptionTv = (TextView) detailView
				.findViewById(R.id.description_tv);
		mTodoIv = (ImageView) detailView.findViewById(R.id.todo_iv);
		mTodoBtn = (Button) detailView.findViewById(R.id.todo_btn);
		mLocationBtn = (Button) detailView.findViewById(R.id.location_btn);
		mShareBtn = (Button) detailView.findViewById(R.id.share_btn);

		/*
		 * 상세 텍스트뷰에 상세설명을 넣습니다.
		 */

		this.mDescriptionTv.setText(this.mission.getDescription());

		/*
		 * 투두의 별모양을 수정합니다.
		 */

		// TODO 헤더라서 이거 토글로 바꿔야 합니다.

		if (this.mission.getIs_todo()) {
			this.mTodoIv.setImageResource(R.drawable.mission_fragment_todo_on);
			this.isTodoOn = true;
		} else {
			this.mTodoIv.setImageResource(R.drawable.mission_fragment_todo_off);
			this.isTodoOn = false;
		}

		this.mTodoBtn.setOnClickListener(todoOnClickListener);

		/*
		 * 로케이션 정보를 이용해 로케이션 버튼에 콜백을 겁니다.
		 */

		if (this.mission.getLocation() != null) {
			Log.d(TAG, "location is not null");

			Location loc = this.mission.getLocation();
			final double latitude = loc.getLatitude();
			final double longitude = loc.getLongitude();

			this.mLocationBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// BackgroundManager.getInstance(getActivity().getApplicationContext()).onStopAllImageLoadingTask();
					Log.d(TAG, "mLocation Btn onClicked");
					MActivity mShampaignActivity = (MActivity) getActivity();
					mShampaignActivity.startMapActivity(latitude, longitude);

				}
			});
		} else {
			this.mLocationBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity().getApplicationContext(),
							"미션에 지정된 위치가 없습니다.", Toast.LENGTH_LONG).show();

				}
			});
		}

		/*
		 * 
		 */

		mShareBtn.setOnClickListener(onClickListener);

		/*
		 * 
		 */

		mLv.addHeaderView(detailView);
		mLv.addFooterView(mLvFooterView);
		mLv.setAdapter(mLvAdapter);
		mLv.setOnItemClickListener(onItemClickListener);
		mLvAdapter.notifyDataSetChanged();

	}

	/**
	 * ListView Apdater Setting
	 */

	private class LvAdapter extends ArrayAdapter<Campaign> {
		private static final String TAG = "Lv Adapter";

		private ViewHolder viewHolder = null;
		private Context context;
		public ArrayList<Campaign> campaigns;
		private int textViewResourceId;

		public LvAdapter(Activity context, int textViewResourceId,
				ArrayList<Campaign> campaigns) {
			super(context, textViewResourceId, campaigns);

			this.context = context;
			this.textViewResourceId = textViewResourceId;
			this.campaigns = campaigns;

		}

		public void setItem(ArrayList<Model> models) {
			this.campaigns = campaigns;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public int getCount() {
			return campaigns.size();
		}

		@Override
		public Campaign getItem(int position) {
			return campaigns.get(position);
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
				convertView = MFragment.this.getActivity().getLayoutInflater()
						.inflate(textViewResourceId, null);

				viewHolder = new ViewHolder();

				viewHolder.mIconIv = (ImageView) convertView
						.findViewById(R.id.icon_iv);
				viewHolder.mTitleTv = (TextView) convertView
						.findViewById(R.id.title_tv);
				viewHolder.mDescriptionTv = (TextView) convertView
						.findViewById(R.id.description_tv);
				viewHolder.mDividerTv = (TextView) convertView
						.findViewById(R.id.divider_tv);
				viewHolder.mScoreTv = (TextView) convertView
						.findViewById(R.id.score_tv);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			/*
			 * Data Import and export
			 */

			Campaign campaign = (Campaign) getItem(position);

			viewHolder.mIconIv
					.setBackgroundColor(Color
							.parseColor(mLv_label_colors[(mLv_label_color_position_offset + position)
									% mLv_label_colors.length]));
			viewHolder.mTitleTv.setText(campaign.getTitle());
			viewHolder.mDescriptionTv.setText(Integer.toString(campaign
					.getTotal_mission_count()) + "개의 세부 미션");

			viewHolder.mScoreTv.setVisibility(View.GONE);
			viewHolder.mDividerTv.setVisibility(View.GONE);
			viewHolder.mDescriptionTv.setVisibility(View.VISIBLE);

			return convertView;
		}

		private class ViewHolder {

			ImageView mIconIv;
			TextView mTitleTv;
			TextView mDescriptionTv;
			TextView mDividerTv;
			TextView mScoreTv;

		}

	}

	/**
	 * 헤더도 포지션에 포함되므로, 이렇게 만들었습니다.
	 */

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long l_position) {

			Log.d(TAG, "campaign item is clicked");

			MActivity missionSetActivity = (MActivity) getActivity();
			int campaign_id = MFragment.this.mission.getCampaigns()
					.get(position - 1).getId();
			String campaign_title = MFragment.this.mission.getCampaigns()
					.get(position - 1).getTitle();

			missionSetActivity.startCampaignActivity(campaign_id,
					campaign_title);

		}
	};

	/**
	 * 할일에 미션을 추가합니다. 해제합니다.
	 */

	OnClickListener todoOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			if (isTodoOn) {
				isTodoOn = false;

				deleteTodo(MFragment.this.mission_id);
			} else {
				isTodoOn = true;

				addTodo(MFragment.this.mission_id);
			}

		}
	};

	public void addTodo(int id) {
		MActivity mShampaignActivity = (MActivity) getActivity();
		mShampaignActivity.showLoadingDialog();

		ApplicationManager apiManager = ((ApplicationManager) getActivity()
				.getApplicationContext());
		apiManager
				.setOnJsonLoadingCompletionListener(onAddTodoCompletionListener);
		TodoAddApi api = new TodoAddApi();
		api.setInput("mission", id);
		apiManager.addJsonLoadingTask(api);
	}

	private ApplicationManager.OnJsonLoadingCompletionListener onAddTodoCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {
				Log.d(TAG, "Models size" + Integer.toString(models.size()));

				Toast.makeText(getActivity().getApplicationContext(),
						"할일에 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show();

				MActivity mShampaignActivity = (MActivity) getActivity();
				mShampaignActivity.hideLoadingDialog();

				MFragment.this.mTodoIv
						.setImageResource(R.drawable.mission_fragment_todo_on);
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

	public void deleteTodo(int id) {
		MActivity mShampaignActivity = (MActivity) getActivity();
		mShampaignActivity.showLoadingDialog();

		ApplicationManager am = ((ApplicationManager) getActivity()
				.getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onDeleteTodoCompletionListener);
		TodoDeleteApi api = new TodoDeleteApi();
		api.setInput("mission", id);
		am.addJsonLoadingTask(api);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onDeleteTodoCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {
			if (isCompleted) {
				Log.d(TAG, "Models size" + Integer.toString(models.size()));

				Toast.makeText(getActivity().getApplicationContext(),
						"할일에서 성공적으로 해제되었습니다.", Toast.LENGTH_SHORT).show();

				MActivity mShampaignActivity = (MActivity) getActivity();
				mShampaignActivity.hideLoadingDialog();

				MFragment.this.mTodoIv
						.setImageResource(R.drawable.mission_fragment_todo_off);
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	/**
	 * 
	 */

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mIv != null) {
			// Cancel any pending image work
			ImageWorker.cancelWork(mIv);
			mIv.setImageDrawable(null);
		}
	}

}