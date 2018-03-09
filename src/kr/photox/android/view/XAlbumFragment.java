package kr.photox.android.view;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.ShotListApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Model;
import kr.photox.android.model.Shot;
import kr.photox.android.utils.ImageCache.ImageCacheParams;
import kr.photox.android.utils.ImageFetcher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class XAlbumFragment extends Fragment implements
		PullToRefreshAttacher.OnRefreshListener {
	private static final String TAG = "Album Fragment";

	/**
	 * 
	 */
	private PullToRefreshAttacher mPullToRefreshAttacher;

	/**
	 * 
	 */

	private static final String IMAGE_CACHE_DIR = "thumbs";

	private int mImageThumbSpacing;
	private ImageFetcher mImageFetcher;

	/**
	 * UI Reference
	 */

	private GridView mGv;
	private View mNodataView;

	/**
	 * 
	 */
	private GvAdapter mGvAdapter;

	/**
	 * 
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * 
		 */

		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
		int window_width = ((ApplicationManager) getActivity()
				.getApplicationContext()).getWindowSize("width");

		ImageCacheParams cacheParams = new ImageCacheParams(getActivity(),
				IMAGE_CACHE_DIR);

		cacheParams.setMemCacheSizePercent(0.03f);

		mImageFetcher = new ImageFetcher(getActivity(),
				(window_width - 7 * mImageThumbSpacing) / 2);
		mImageFetcher.setLoadingImage(R.drawable.base_empty_photo);
		mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);

	}

	/**
	 * 
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.x_album_fragment,
				container, false);

		/*
		 * 액티비티에서 필요한 정보 받아오기
		 */

		ArrayList<Model> shots = new ArrayList<Model>();

		/*
		 * GridView Header Setting
		 */

		mGv = (GridView) v.findViewById(R.id.gv);
		mNodataView = (View) v.findViewById(R.id.nodata_tv);

		/*
		 * 
		 */
		mPullToRefreshAttacher = ((XActivity) getActivity())
				.getPullToRefreshAttacher();
		mPullToRefreshAttacher.addRefreshableView(mGv, this);
		mPullToRefreshAttacher.addRefreshableView(mNodataView, this);

		/*
		 * GridView Setting
		 */

		mGvAdapter = new GvAdapter(getActivity(), R.layout.x_album_fragment_gv,
				shots);
		mGv.setAdapter(mGvAdapter);
		mGv.setOnItemClickListener(onItemClickListener);

		mGv.setOnScrollListener(new AbsListView.OnScrollListener() {
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

				Log.d(TAG, "first visible item : " + firstVisibleItem);
				Log.d(TAG, "visible item count : " + visibleItemCount);
				Log.d(TAG, "total item count : " + totalItemCount);
			}
		});

		/*
		 * 
		 */
		mPullToRefreshAttacher.setRefreshing(true);
		requestShotListApi();

		return v;
	}

	/**
	 * 
	 */

	@Override
	public void onRefreshStarted(View view) {
		Log.d(TAG, "refresh started");

		/*
		 */

		this.requestShotListApi();

	}

	/**
	 * 
	 */

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
		mGvAdapter.notifyDataSetChanged();
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
	 * request history list api
	 */

	private void requestShotListApi() {

		ApplicationManager am = ((ApplicationManager) getActivity()
				.getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onShotListApiCompletionListener);
		am.addJsonLoadingTask(new ShotListApi());

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onShotListApiCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {
			if (isCompleted) {
				Log.d(TAG, "Shots size : " + Integer.toString(models.size()));
				mGvAdapter.setItem(models);
				mGvAdapter.notifyDataSetChanged();

				if (models.size() == 0) {
					mGv.setVisibility(View.GONE);
					mNodataView.setVisibility(View.VISIBLE);
				} else {
					mGv.setVisibility(View.VISIBLE);
					mNodataView.setVisibility(View.GONE);
				}
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
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
	 * ListView Apdater Setting
	 */

	private class GvAdapter extends ArrayAdapter<Model> {

		private ViewHolder viewHolder = null;
		private Context context;
		public ArrayList<Model> models;
		private int textViewResourceId;

		public GvAdapter(Activity context, int textViewResourceId,
				ArrayList<Model> models) {
			super(context, textViewResourceId, models);

			this.context = context;
			this.textViewResourceId = textViewResourceId;
			this.models = models;

		}

		public void setItem(ArrayList<Model> models) {
			this.models = models;
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
				convertView = getActivity().getLayoutInflater().inflate(
						textViewResourceId, null);

				viewHolder = new ViewHolder();
				viewHolder.mIv = (ImageView) convertView.findViewById(R.id.iv);
				viewHolder.mTitleTv = (TextView) convertView
						.findViewById(R.id.title_tv);
				viewHolder.mTsTv = (TextView) convertView
						.findViewById(R.id.ts_tv);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}

			/*
			 * Data Import and export
			 */

			Shot shot = (Shot) models.get(position);

			viewHolder.mTitleTv.setText(shot.getMission_title());
			viewHolder.mTsTv.setText(shot.getCreated().substring(0, 10));

			if (!isMemoryAvailable()) {
				// mImageFetcher.setPauseWork(false);
				// mImageFetcher.flushCache();
			}

			mImageFetcher.loadImage(shot.getImgUrl(), viewHolder.mIv);

			return convertView;
		}

		private class ViewHolder {

			ImageView mIv;
			TextView mTitleTv;
			TextView mTsTv;

		}

	}

	/**
	 * Chk Memory Status 70%이상 사용중이면 날려버립니다. // KB로 만들고 싶으면 1024.0f로 나누면 됩니다.
	 * 둘다.
	 */

	private Boolean isMemoryAvailable() {
		double maxMemory = Runtime.getRuntime().maxMemory();
		double allocateMemory = Debug.getNativeHeapAllocatedSize();

		Log.i("", "최대 메모리 : " + maxMemory);
		Log.i("", "사용 메모리 : " + allocateMemory);

		double current_memeory_usage = allocateMemory / maxMemory;

		if (current_memeory_usage > 0.25f) {
			Log.d(TAG, "Memory is unavailable!");
			return false;
		} else {
			Log.d(TAG, "Memory is available!");
			return true;
		}

	}

	/**
	 * 
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long l_position) {

			Shot shot = (Shot) mGvAdapter.models.get(position);

			((XActivity) getActivity()).startShotActivity(shot.getId(),
					shot.getMission_title(), shot.getImgUrl());

		}
	};

}
