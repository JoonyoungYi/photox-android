package kr.photox.android.view;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.MainSearchApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Model;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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

import com.actionbarsherlock.app.SherlockFragment;

public class SearchFragment extends SherlockFragment {
	private final String TAG = "Search Fragment";

	/**
	 * 
	 */

	private Activity ctx;

	/**
	 * UI Reference
	 */

	private ListView mLv;
	private View mLoadingView;
	private View mNodataView;

	/**
	 * Adapter Init
	 */

	private LvAdapter mLvAdapter;
	private ArrayList<Model> mItems;
	private String[] mLv_label_colors;
	int mLv_label_color_position_offset = 0;

	/**
	 * On Create View
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.search_fragment, container, false);
		ctx = getActivity();

		/*
		 * ListView Default Setting
		 */

		mLv = (ListView) v.findViewById(R.id.lv);
		mLoadingView = (View) v.findViewById(R.id.loading_view);
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
				convertView = SearchFragment.this.ctx.getLayoutInflater()
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

			viewHolder.mIconIv
					.setBackgroundColor(Color
							.parseColor(mLv_label_colors[(mLv_label_color_position_offset + position)
									% mLv_label_colors.length]));

			if (getItem(position).getModelType() == "CAMPAIGN") {
				Campaign campaign = (Campaign) getItem(position);

				viewHolder.mTitleTv.setText(campaign.getTitle());
				viewHolder.mDescriptionTv.setText(Integer.toString(campaign
						.getTotal_mission_count()) + "개의 세부 미션");

				viewHolder.mScoreTv.setVisibility(View.GONE);
				viewHolder.mDividerTv.setVisibility(View.GONE);
				viewHolder.mDescriptionTv.setVisibility(View.VISIBLE);

			} else if (getItem(position).getModelType() == "MISSION") {
				Mission mission = (Mission) getItem(position);

				viewHolder.mTitleTv.setText(mission.getTitle());

				int distance = (int) mission.getDistance();

				if (distance == -1) {
					viewHolder.mDescriptionTv.setVisibility(View.GONE);
				} else {
					viewHolder.mDescriptionTv.setVisibility(View.VISIBLE);

					if (distance < 10) {
						viewHolder.mDescriptionTv.setText("10 m 이내");

					} else if (distance < 1000) {
						viewHolder.mDescriptionTv.setText(Integer
								.toString(distance) + " m");
					} else {
						viewHolder.mDescriptionTv.setText(Integer
								.toString(distance / 1000) + " km");

					}

				}

				viewHolder.mScoreTv
						.setText(Integer.toString(mission.getScore()));
				viewHolder.mScoreTv.setVisibility(View.VISIBLE);
				viewHolder.mDividerTv.setVisibility(View.VISIBLE);

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

				SearchActivity searchActivity = (SearchActivity) getActivity();
				int mission_id = ((Mission) mLvAdapter.models.get(position))
						.getId();
				searchActivity.startMissionSetActivity(mission_id);

			} else {
				Log.d(TAG, "campaign item is clicked");

				SearchActivity searchActivity = (SearchActivity) getActivity();
				int campaign_id = ((Campaign) mLvAdapter.models.get(position))
						.getId();
				String campaign_title = ((Campaign) mLvAdapter.models
						.get(position)).getTitle();

				searchActivity.startCampaignActivity(campaign_id,
						campaign_title);

			}

		}
	};

	/**
	 * Json Load
	 */

	public void JsonLoad(String keyword) {

		mLoadingView.setVisibility(View.VISIBLE);
		mLv.setVisibility(View.GONE);
		mNodataView.setVisibility(View.GONE);

		((ArrayAdapter<Model>) mLvAdapter).clear();
		Log.d("mLvAdapter", "clear");

		ApplicationManager am = ((ApplicationManager) getActivity()
				.getApplicationContext());
		Log.d("ApiController", "getInstance");
		am.setOnJsonLoadingCompletionListener(onJsonLoadingCompletionListener);
		Log.d("ApiController", "setOnJsonLoadingCompletionListener");

		MainSearchApi mainSearchApi = new MainSearchApi();
		mainSearchApi.setInput(keyword);

		am.addJsonLoadingTask(mainSearchApi);
		Log.d("ApiController", "addJsonLoadingTask");

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJsonLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {
			if (isCompleted) {
				Log.d("Models size", Integer.toString(models.size()));

				if (models.size() == 0) {
					mNodataView.setVisibility(View.VISIBLE);

				} else {

					/**
					 * ListView adapter item setting
					 */

					mLvAdapter.setItem(models);
					mLvAdapter.notifyDataSetChanged();
					mLv.setVisibility(View.VISIBLE);

				}

				mLoadingView.setVisibility(View.GONE);
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

}
