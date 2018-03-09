package kr.photox.android.view;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.TodoListApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
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

public class XTodoFragment extends Fragment implements
		PullToRefreshAttacher.OnRefreshListener {
	private static final String TAG = "Todo Fragment";

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
	private int mLv_label_color_position_offset = 0;

	/**
	 * 
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.x_todo_fragment,
				container, false);

		/*
		 * ListView Default Setting
		 */

		mLv = (ListView) v.findViewById(R.id.lv);
		mNodataView = (View) v.findViewById(R.id.nodata_tv);

		/*
		 * ListView Setting
		 */

		// final ProfileActivity profileActivity = (ProfileActivity)
		// getActivity();
		mItems = new ArrayList<Model>();
		mLvAdapter = new LvAdapter(getActivity(), R.layout.base_campaign,
				mItems);

		mLv.setAdapter(mLvAdapter);
		mLv.setOnItemClickListener(onItemClickListener);

		/*
		 * 리스트뷰 라벨 색을 랜덤으로 출력하기 위한 offset을 랜덤으로 설정합니다.
		 */

		mLv_label_colors = getResources().getStringArray(
				R.array.list_label_colors);
		mLv_label_color_position_offset = (int) (Math.random() * mLv_label_colors.length);

		/*
		 * 
		 */
		mPullToRefreshAttacher = ((XActivity) getActivity())
				.getPullToRefreshAttacher();
		mPullToRefreshAttacher.addRefreshableView(mLv, this);
		mPullToRefreshAttacher.addRefreshableView(mNodataView, this);

		/*
		 * 
		 */

		mPullToRefreshAttacher.setRefreshing(true);
		requestTodoListApi();

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

		requestTodoListApi();
	}

	/**
	 * request todo list api
	 */

	private void requestTodoListApi() {

		ApplicationManager am = ((ApplicationManager) getActivity()
				.getApplicationContext());
		am.setOnJsonLoadingCompletionListener(onTodoListApiCompletionListener);
		am.addJsonLoadingTask(new TodoListApi());

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onTodoListApiCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {
			if (isCompleted) {
				Log.d(TAG, "Models size : " + Integer.toString(models.size()));
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
				convertView = XTodoFragment.this.getActivity()
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

				viewHolder.mScoreTv.setVisibility(View.GONE);
				viewHolder.mDividerTv.setVisibility(View.GONE);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}

			viewHolder.mIconIv
					.setBackgroundColor(Color
							.parseColor(mLv_label_colors[(mLv_label_color_position_offset + position)
									% mLv_label_colors.length]));

			/*
			 * Data Import and export
			 */

			if (getItem(position).getModelType() == "CAMPAIGN") {
				Campaign campaign = (Campaign) getItem(position);

				viewHolder.mTitleTv.setText(campaign.getTitle());
				viewHolder.mDescriptionTv.setText(Integer.toString(campaign
						.getTotal_mission_count()) + "개의 세부 미션");

				// viewHolder.mDescriptionTv.setVisibility(View.VISIBLE);

			} else if (getItem(position).getModelType() == "MISSION") {
				Mission mission = (Mission) getItem(position);

				viewHolder.mTitleTv.setText(mission.getTitle());

				// int distance = (int) mission.getDistance();

				// if (distance == -1) {
				viewHolder.mDescriptionTv.setVisibility(View.GONE);
				/*
				 * } else {
				 * viewHolder.mDescriptionTv.setVisibility(View.VISIBLE);
				 * 
				 * if (distance < 10) {
				 * viewHolder.mDescriptionTv.setText("10 m 이내");
				 * 
				 * } else if (distance < 1000) {
				 * viewHolder.mDescriptionTv.setText(Integer .toString(distance)
				 * + " m"); } else { viewHolder.mDescriptionTv.setText(Integer
				 * .toString(distance / 1000) + " km");
				 * 
				 * }
				 * 
				 * }
				 */

				// viewHolder.mScoreTv
				// .setText(Integer.toString(mission.getScore()));
				// viewHolder.mScoreTv.setVisibility(View.VISIBLE);
				// viewHolder.mDividerTv.setVisibility(View.VISIBLE);

			}

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
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long l_position) {
			if (mLvAdapter.models.get(position).getModelType() == "MISSION") {
				Log.d(TAG, "mission item is clicked");

				XActivity xActivity = (XActivity) getActivity();
				int mission_id = -((Mission) mLvAdapter.models.get(position))
						.getId();
				xActivity.startShampaignActivity(mission_id, null);

			} else {
				Log.d(TAG, "campaign item is clicked");

				XActivity xActivity = (XActivity) getActivity();
				// Campaign campaign = ((Campaign)
				// mLvAdapter.models.get(position));
				// searchActivity.startMissionSetActivity(campaign);

			}

		}
	};

}
