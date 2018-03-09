package kr.photox.android.view;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.CampaignListApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Campaign;
import kr.photox.android.model.Model;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class XCampaignFragment extends Fragment implements
		PullToRefreshAttacher.OnRefreshListener {
	private static final String TAG = "X Ongoing Campaign Fragment";
	/**
	 * 
	 */
	private PullToRefreshAttacher mPullToRefreshAttacher;

	/**
	 * UI Reference
	 */

	private ListView mLv;
	private View mNodataView;

	/**
	 * Adapter Init
	 */

	private LvAdapter mLvAdapter;
	private ArrayList<Model> mItems;
	private String[] mLv_label_colors;
	int mLv_label_color_position_offset = 0;

	/**
	 * 
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(
				R.layout.x_campaign_fragment, container, false);

		/*
		 * ListView Default Setting
		 */

		mLv = (ListView) v.findViewById(R.id.lv);
		mNodataView = (View) v.findViewById(R.id.nodata_tv);

		/*
		 * ListView Setting
		 */

		mItems = new ArrayList<Model>();
		mLvAdapter = new LvAdapter(getActivity(), R.layout.base_campaign,
				mItems);

		mLv.setAdapter(mLvAdapter);
		mLv.setOnItemClickListener(onItemClickListener);

		/*
		 * 
		 */
		mPullToRefreshAttacher = ((XActivity) getActivity())
				.getPullToRefreshAttacher();
		PullToRefreshLayout ptrLayout = (PullToRefreshLayout) v
				.findViewById(R.id.ptr_layout);
		ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);

		/*
		 * 
		 */
		mPullToRefreshAttacher.setRefreshing(true);
		requestCampaignListApi();

		/*
		 * 
		 */

		mLv_label_colors = getResources().getStringArray(
				R.array.list_label_colors);
		mLv_label_color_position_offset = (int) (Math.random() * mLv_label_colors.length);
		return v;
	}

	/**
	 * ListView Apdater Setting
	 */

	private class LvAdapter extends ArrayAdapter<Model> {

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
				convertView = XCampaignFragment.this.getActivity()
						.getLayoutInflater().inflate(textViewResourceId, null);

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

			viewHolder.mIconIv
					.setBackgroundColor(Color
							.parseColor(mLv_label_colors[(mLv_label_color_position_offset + position)
									% mLv_label_colors.length]));

			Campaign campaign = (Campaign) getItem(position);

			viewHolder.mTitleTv.setText(campaign.getTitle());
			viewHolder.mDescriptionTv.setText("총 "
					+ Integer.toString(campaign.getTotal_mission_count())
					+ "개의 세부 미션 중 "
					+ Integer.toString(campaign.getCompleted_mission_count())
					+ "개 달성! ("
					+ Integer.toString(campaign.getWaiting_mission_count())
					+ "개 대기중)\n지금까지 총 "
					+ Integer.toString(campaign.getTotalScore()) + "점 중 "
					+ Integer.toString(campaign.getCompleted_score())
					+ "점을 획득하셨습니다.");

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
	 * 
	 */

	@Override
	public void onRefreshStarted(View view) {
		Log.d(TAG, "refresh started");

		/*
		 * 
		 */

		requestCampaignListApi();
	}

	/**
	 * 
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long l_position) {

			Log.d(TAG, "campaign item is clicked");

			XActivity xActivity = (XActivity) getActivity();
			Campaign campaign = ((Campaign) mLvAdapter.models.get(position));

			int campaign_id = campaign.getId();
			String campaign_title = campaign.getTitle();
			Log.d(TAG, "campaign_id" + campaign_id);
			Log.d(TAG, "campaign_title" + campaign_title);
			xActivity.startCampaignActivity(campaign_id, campaign_title);

		}
	};

	/**
	 * request history list api
	 */

	private void requestCampaignListApi() {
		ApplicationManager am = ((ApplicationManager) getActivity()
				.getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onCampaignListApiCompletionListener);
		am.addJsonLoadingTask(new CampaignListApi());

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onCampaignListApiCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {
				mLvAdapter.setItem(models);
				mLvAdapter.notifyDataSetChanged();

				if (models.size() == 0) {
					mLv.setVisibility(View.GONE);
					mNodataView.setVisibility(View.VISIBLE);
				} else {
					mLv.setVisibility(View.VISIBLE);
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

}
