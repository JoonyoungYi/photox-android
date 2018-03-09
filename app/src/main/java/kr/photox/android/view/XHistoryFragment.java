package kr.photox.android.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.ApiBase;
import kr.photox.android.api.CheckinListApi;
import kr.photox.android.model.Place;
import kr.photox.android.utils.Argument;

/**
 * A placeholder fragment containing a simple view.
 */
public class XHistoryFragment extends Fragment {
    private static final String TAG = "X History Fragment";

    /**
     *
     */

    private View mErrorView;
    private TextView mErrorTv;
    private ProgressBar mErrorPb;

    private ListView mLv;
    private LvAdapter mLvAdapter;

    /**
     *
     */

    private CheckinListApiTask mCheckinListApiTask = null;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */


    public XHistoryFragment() {
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
        View rootView = inflater.inflate(R.layout.x_recommend_fragment, container, false);

        /*

         */
        //int section_number = getArguments().getInt(ARG_SECTION_NUMBER);

        /**
         * ListView Default Setting
         */
        mLv = (ListView) rootView.findViewById(R.id.lv);
        mErrorView = rootView.findViewById(R.id.error_view);
        mErrorTv = (TextView) mErrorView.findViewById(R.id.error_tv);
        mErrorPb = (ProgressBar) mErrorView.findViewById(R.id.error_pb);

        /*
         * ListView Setting
		 */

        ArrayList<Place> places = new ArrayList<Place>();
        mLvAdapter = new LvAdapter(getActivity(), R.layout.x_recommend_fragment_lv,
                places);
        mLv.setAdapter(mLvAdapter);
        mLv.setOnItemClickListener(onItemClickListener);

        /*

         */
        mCheckinListApiTask = new CheckinListApiTask();
        mCheckinListApiTask.execute();

        return rootView;
    }

    /**
     * ListView Apdater Setting
     */

    private class LvAdapter extends ArrayAdapter<Place> {
        private static final String TAG = "XRecommendFragment LvAdapter";

        private ViewHolder viewHolder = null;
        public ArrayList<Place> places;
        private int textViewResourceId;

        public LvAdapter(Activity context, int textViewResourceId,
                         ArrayList<Place> stores) {
            super(context, textViewResourceId, stores);

            this.textViewResourceId = textViewResourceId;
            this.places = stores;

        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return places.size();
        }

        @Override
        public Place getItem(int position) {
            return places.get(position);
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

                viewHolder.mIconIv = (ImageView) convertView.findViewById(R.id.icon_iv);

                viewHolder.mCategoryTv = (TextView) convertView.findViewById(R.id.category_tv);
                viewHolder.mTitleTv = (TextView) convertView.findViewById(R.id.title_tv);
                viewHolder.mDescriptionTv = (TextView) convertView.findViewById(R.id.description_tv);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Place place = this.getItem(position);

			/*
             * Data Import and export
			 */

/*            viewHolder.mIconIv.setImageResource(Place.ARRAY_CATEGORY_DRAWABLE[place.getCategory()]);
            viewHolder.mCategoryTv.setText(Place.ARRAY_CATEGORY_NAME[place.getCategory()]);
            viewHolder.mTitleTv.setText(place.getTitle());
            viewHolder.mDescriptionTv.setText(String.format("%d개 미션", place.getTotal_mission_count()));
*/
            return convertView;
        }

        private class ViewHolder {
            ImageView mIconIv;

            TextView mCategoryTv;
            TextView mTitleTv;
            TextView mDescriptionTv;
        }

    }

    /**
     *
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            XActivity activity = (XActivity) getActivity();
            Place place = (Place) adapterView.getAdapter().getItem(i);
            activity.startPlaceActivity(place.getId(), place.getTitle());

        }
    };

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CheckinListApiTask extends AsyncTask<Void, Void, ArrayList<Place>> {
        private int request_code = Argument.REQUEST_CODE_UNEXPECTED;

        @Override
        protected ArrayList<Place> doInBackground(Void... params) {
            ArrayList<Place> stores = null;

            try {
                CheckinListApi checkinListApi = new CheckinListApi(getActivity().getApplication(), 0, 10);
                request_code = checkinListApi.getRequestCode();
                if (request_code == Argument.REQUEST_CODE_SUCCESS)
                    stores = checkinListApi.getResult();

            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

            if (stores == null)
                cancel(true);

            return stores;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {
            mCheckinListApiTask = null;
            ApiBase.showToastMsg(getActivity().getApplication(), request_code);

            if (request_code == Argument.REQUEST_CODE_SUCCESS) {
                if (places.size() != 0) {
                    showErrorView(false, "");
                    mLvAdapter.places.addAll(places);
                    mLvAdapter.notifyDataSetChanged();

                } else {
                    showErrorView(true, "데이터가 없습니다");
                }
            } else {
                showErrorView(true, "오류가 발생해 장소를 불러오지 못했습니다");
            }
        }

        @Override
        protected void onCancelled() {
            mCheckinListApiTask = null;
            ApiBase.showToastMsg(getActivity().getApplication(), request_code);
            showErrorView(true, "오류가 발생해 장소를 불러오지 못했습니다");

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
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((XActivity) activity).onSectionAttached(R.string.title_history);
    }

    /**
     *
     */
    public void onDestroy() {
        super.onDestroy();

        if (mCheckinListApiTask != null) {
            mCheckinListApiTask.cancel(true);
        }

    }

}

