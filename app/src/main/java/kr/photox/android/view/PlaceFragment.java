package kr.photox.android.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.ApiBase;
import kr.photox.android.api.PlaceDetailApi;
import kr.photox.android.model.Mission;
import kr.photox.android.utils.Argument;

public class PlaceFragment extends Fragment {
    private static final String TAG = "Place Fragment";

    /*

    */

    private View mErrorView;
    private TextView mErrorTv;
    private ProgressBar mErrorPb;

    private ListView mLv;
    private LvAdapter mLvAdapter;
    /**
     *
     */

    private PlaceDetailApiTask mPlaceDetailApiTask = null;

    /**
     *
     */
    public PlaceFragment() {
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.place_fragment, container, false);

        /*

         */
        mLv = (ListView) rootView.findViewById(R.id.lv);
        mErrorView = rootView.findViewById(R.id.error_view);
        mErrorTv = (TextView) mErrorView.findViewById(R.id.error_tv);
        mErrorPb = (ProgressBar) mErrorView.findViewById(R.id.error_pb);


        /*
         * ListView Setting
		 */

        ArrayList<Mission> missions = new ArrayList<Mission>();
        mLvAdapter = new LvAdapter(getActivity(), R.layout.place_fragment_lv,
                missions);
        mLv.setAdapter(mLvAdapter);

        /*

         */

        mPlaceDetailApiTask = new PlaceDetailApiTask();
        mPlaceDetailApiTask.execute();

        return rootView;
    }

    /**
     * ListView Apdater Setting
     */

    private class LvAdapter extends ArrayAdapter<Mission> {
        private static final String TAG = "XRecommendFragment LvAdapter";

        private ViewHolder viewHolder = null;
        public ArrayList<Mission> missions;
        private int textViewResourceId;

        public LvAdapter(Activity context, int textViewResourceId,
                         ArrayList<Mission> stores) {
            super(context, textViewResourceId, stores);

            this.textViewResourceId = textViewResourceId;
            this.missions = stores;

        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return missions.size();
        }

        @Override
        public Mission getItem(int position) {
            return missions.get(position);
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
                convertView = getActivity().getLayoutInflater()
                        .inflate(textViewResourceId, null);

                viewHolder = new ViewHolder();
                viewHolder.mScoreTv = (TextView) convertView.findViewById(R.id.score_tv);
                viewHolder.mTitleTv = (TextView) convertView.findViewById(R.id.title_tv);

                viewHolder.mDetailBtn = (Button) convertView.findViewById(R.id.detail_btn);
                viewHolder.mCheckinBtn = (Button) convertView.findViewById(R.id.checkin_btn);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Mission mission = this.getItem(position);

			/*
             * Data Import and export
			 */

            viewHolder.mScoreTv.setText(String.format("%d점", mission.getScore()));
            viewHolder.mTitleTv.setText(mission.getTitle());

            viewHolder.mCheckinBtn.setOnClickListener(getOnCheckinClickListener(mission.getId(), mission.getTitle()));
            viewHolder.mDetailBtn.setOnClickListener(getOnDetailClickListener(mission.getId(), mission.getTitle()));

            return convertView;
        }

        private class ViewHolder {
            TextView mScoreTv;
            TextView mTitleTv;

            Button mCheckinBtn;
            Button mDetailBtn;
        }

    }

    /**
     * @param id
     * @param title
     * @return
     */
    View.OnClickListener getOnCheckinClickListener(final int id, final String title) {

        return new View.OnClickListener() {
            public void onClick(View v) {

                PlaceActivity activity = (PlaceActivity) getActivity();
                activity.startCheckinActivity(id, title);

            }
        };

    }

    /**
     * @param id
     * @param title
     * @return
     */
    View.OnClickListener getOnDetailClickListener(final int id, final String title) {

        return new View.OnClickListener() {
            public void onClick(View v) {

                PlaceActivity activity = (PlaceActivity) getActivity();
                activity.startMissionActivity(id, title);


            }
        };

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class PlaceDetailApiTask extends AsyncTask<Void, Void, ArrayList<Mission>> {
        private int request_code = Argument.REQUEST_CODE_UNEXPECTED;

        @Override
        protected ArrayList<Mission> doInBackground(Void... param) {
            ArrayList<Mission> missions = null;

            try {
                PlaceDetailApi placeDetailApi = new PlaceDetailApi(getActivity().getApplication(), ((PlaceActivity) getActivity()).place_id);
                request_code = placeDetailApi.getRequestCode();
                if (request_code == Argument.REQUEST_CODE_SUCCESS)
                    missions = placeDetailApi.getResult();

            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

            if (missions == null)
                cancel(true);

            return missions;
        }

        @Override
        protected void onPostExecute(ArrayList<Mission> missions) {
            mPlaceDetailApiTask = null;
            ApiBase.showToastMsg(getActivity().getApplication(), request_code);

            if (request_code == Argument.REQUEST_CODE_SUCCESS) {
                if (missions.size() != 0) {
                    showErrorView(false, "");
                    mLvAdapter.missions.addAll(missions);
                    mLvAdapter.notifyDataSetChanged();

                } else {
                    showErrorView(true, "장소에 미션이 없습니다");
                }

            } else {
                showErrorView(true, "오류가 발생해 미션을 불러오지 못했습니다");
            }
        }

        @Override
        protected void onCancelled() {
            mPlaceDetailApiTask = null;
            ApiBase.showToastMsg(getActivity().getApplication(), request_code);
            showErrorView(true, "오류가 발생해 미션을 불러오지 못했습니다");

        }
    }

    /**
     * @param show
     */
    private void showErrorView(final boolean show, String msg) {

        if (show) {
            mLv.setVisibility(View.GONE);
            mErrorView.setVisibility(View.VISIBLE);
            mErrorTv.setText(msg);

            if (msg.equals("")) {
                mErrorPb.setVisibility(View.VISIBLE);
            } else {
                mErrorPb.setVisibility(View.GONE);
            }

        } else {
            mLv.setVisibility(View.VISIBLE);
            mErrorPb.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.GONE);
        }
    }

    /**
     *
     */
    public void onDestroy() {
        super.onDestroy();

        if (mPlaceDetailApiTask != null) {
            mPlaceDetailApiTask.cancel(true);
        }

    }

}
