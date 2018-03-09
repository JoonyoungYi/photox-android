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
import kr.photox.android.api.TodoListApi;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Place;
import kr.photox.android.utils.Argument;

/**
 * A placeholder fragment containing a simple view.
 */
public class XTodoFragment extends Fragment {
    private static final String TAG = "X Todo Fragment";

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

    private TodoListApiTask mTodoListApiTask = null;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */


    public XTodoFragment() {
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

        ArrayList<Mission> missions = new ArrayList<Mission>();
        mLvAdapter = new LvAdapter(getActivity(), R.layout.x_recommend_fragment_lv,
                missions);
        mLv.setAdapter(mLvAdapter);
        mLv.setOnItemClickListener(onItemClickListener);

        /*

         */
        mTodoListApiTask = new TodoListApiTask();
        mTodoListApiTask.execute();

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
                         ArrayList<Mission> missions) {
            super(context, textViewResourceId, missions);

            this.textViewResourceId = textViewResourceId;
            this.missions = missions;

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

                viewHolder.mIconIv = (ImageView) convertView.findViewById(R.id.icon_iv);

                viewHolder.mCategoryTv = (TextView) convertView.findViewById(R.id.category_tv);
                viewHolder.mTitleTv = (TextView) convertView.findViewById(R.id.title_tv);
                viewHolder.mDescriptionTv = (TextView) convertView.findViewById(R.id.description_tv);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Mission mission = this.getItem(position);

			/*
             * Data Import and export
			 */

            viewHolder.mIconIv.setImageResource(Place.ARRAY_CATEGORY_DRAWABLE[mission.getPlace().getCategory()]);
            viewHolder.mCategoryTv.setText(Place.ARRAY_CATEGORY_NAME[mission.getPlace().getCategory()]);
            viewHolder.mTitleTv.setText(mission.getPlace().getTitle());
            viewHolder.mDescriptionTv.setText(mission.getTitle());

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
            Mission mission = (Mission) adapterView.getAdapter().getItem(i);
            activity.startMissionActivity(mission.getId(), mission.getTitle());

        }
    };

    /**
     *
     */
    public class TodoListApiTask extends AsyncTask<Void, Void, ArrayList<Mission>> {
        private int request_code = Argument.REQUEST_CODE_UNEXPECTED;

        @Override
        protected ArrayList<Mission> doInBackground(Void... params) {
            ArrayList<Mission> missions = null;

            try {
                TodoListApi todoListApi = new TodoListApi(getActivity().getApplication());
                request_code = todoListApi.getRequestCode();
                if (request_code == Argument.REQUEST_CODE_SUCCESS)
                    missions = todoListApi.getResult();

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
            mTodoListApiTask = null;
            ApiBase.showToastMsg(getActivity().getApplication(), request_code);

            if (request_code == Argument.REQUEST_CODE_SUCCESS) {
                if (missions.size() != 0) {
                    showErrorView(false, "");
                    mLvAdapter.missions.addAll(missions);
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
            mTodoListApiTask = null;
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
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((XActivity) activity).onSectionAttached(R.string.title_todo);
    }

    /**
     *
     */
    public void onDestroy() {
        super.onDestroy();

        if (mTodoListApiTask != null) {
            mTodoListApiTask.cancel(true);
        }

    }

}

