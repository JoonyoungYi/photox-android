package kr.photox.android;

import android.app.Activity;
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

import kr.photox.android.model.Place;

/**
 * A placeholder fragment containing a simple view.
 */
public class XRecommendFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     *
     */
    private String portal_id = "";
    private int time_offset = 0;

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

   // private ListApiTask mListApiTask = null;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    public static XRecommendFragment newInstance(int sectionNumber) {
        XRecommendFragment fragment = new XRecommendFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public XRecommendFragment() {
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
        int section_number = getArguments().getInt(ARG_SECTION_NUMBER);

        /**
         * ListView Default Setting
         */
        mLv = (ListView) rootView.findViewById(R.id.lv);
        //mErrorView = rootView.findViewById(R.id.error_view);
        //mErrorTv = (TextView) mErrorView.findViewById(R.id.error_tv);
        //mErrorPb = (ProgressBar) mErrorView.findViewById(R.id.error_pb);

        /*

         */
        ListView.LayoutParams params = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        View v = new View(getActivity());
        v.setLayoutParams(params);
        mLv.addFooterView(v);
        mLv.addHeaderView(v);

        /*
         * ListView Setting
		 */

        ArrayList<Place> places = new ArrayList<Place>();

        for (int i = 1; i < 12 ; i ++ ){
            Place place = new Place();
            place.setCategory(i);
            place.setTitle(String.format("장소이름 %d", i));
            place.setId(0);
            place.setLatitude(0);
            place.setLongitude(0);
            place.setTotal_mission_count(i*10);
            places.add(place);
        }

        mLvAdapter = new LvAdapter(getActivity(), R.layout.x_recommend_fragment_lv,
                places);
        mLv.setAdapter(mLvAdapter);
        mLv.setOnItemClickListener(onItemClickListener);

        /*

         */
        /*mListApiTask = new ListApiTask();
        if (section_number == 0)
            mListApiTask.execute("favorite");
        else if (section_number == 1)
            mListApiTask.execute("campus");
        else if (section_number == 2)
            mListApiTask.execute("delivery");
        else if (section_number == 3)
            mListApiTask.execute("external");

        /*

         */
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
                         ArrayList<Place> places) {
            super(context, textViewResourceId, places);

            this.textViewResourceId = textViewResourceId;
            this.places = places;
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

            //
            viewHolder.mIconIv.setImageResource(Place.ARRAY_CATEGORY_DRAWABLE[place.getCategory()]);
            viewHolder.mCategoryTv.setText(Place.ARRAY_CATEGORY_NAME[place.getCategory()]);
            viewHolder.mTitleTv.setText(place.getTitle());
            viewHolder.mDescriptionTv.setText(String.format("%.02fkm / %d개 미션", 0.1, place.getTotal_mission_count()));

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

           /* BabActivity activity = (BabActivity) getActivity();
            Store store = (Store) adapterView.getAdapter().getItem(i);
            activity.startDetailActivity(store.getTitle(), store.getId(), store.getCode());
    */
        }
    };

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     *//*
    public class ListApiTask extends AsyncTask<String, Void, ArrayList<Store>> {
        private String api_sort = "";

        @Override
        protected ArrayList<Store> doInBackground(String... sort) {
            ArrayList<Store> stores = null;
            api_sort = sort[0];

            try {
                ListApi listApi = new ListApi(api_sort, XRecommendFragment.this.mAutoKey, XRecommendFragment.this.time_offset);
                stores = listApi.getResult();

            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

            if (stores == null)
                cancel(true);

            return stores;
        }

        @Override
        protected void onPostExecute(ArrayList<Store> stores) {

            if (stores.size() != 0) {
                showErrorView(false, "");
                mLvAdapter.places.addAll(stores);
                mLvAdapter.notifyDataSetChanged();

            } else if (api_sort.equals("favorite")) {
                showErrorView(true, "등록된 즐겨찾기가 없습니다.");

            } else {
                showErrorView(true, "데이터가 없습니다");
            }

            mListApiTask = null;
        }

        @Override
        protected void onCancelled() {

            showErrorView(true, "오류가 발생해 식당을 불러오지 못했습니다");
            mListApiTask = null;
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

            if (msg.equals("")){
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((XActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /**
     *
     */
    public void onDestroy() {
        super.onDestroy();

        //if (mListApiTask != null) {
        //    mListApiTask.cancel(true);
       // }

    }

}

