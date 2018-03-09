package kr.photox.android.view;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import kr.photox.android.R;
import kr.photox.android.api.ApiBase;
import kr.photox.android.api.MissionDetailApi;
import kr.photox.android.api.TodoAddApi;
import kr.photox.android.api.TodoDeleteApi;
import kr.photox.android.model.Mission;
import kr.photox.android.utils.Argument;

public class MissionActivity extends ActionBarActivity {
    private static final String TAG = "Mission Activity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    //
    private TextView mDescriptionTv;
    private Button mTodoBtn;
    private Button mLocationBtn;
    private Button mCheckinBtn;
    //
    private TextView mRatingTitleTv;
    private TextView[] mRatingTv;
    private TextView[] mRatingIndicator;

    /**
     *
     */
    private int id = -1;
    boolean is_todo = false;
    Location location = null;
    private String title = null;
    /**
     *
     */
    private MissionDetailApiTask mMissionDetailApiTask = null;
    private TodoAddDeleteApiTask mTodoAddDeleteApiTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mission_activity);

        /*

         */
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        title = bundle.getString("title");

        /*

         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

        /*

         */
        mViewPager = (ViewPager) findViewById(R.id.pager);
        //
        mDescriptionTv = (TextView) findViewById(R.id.description_tv);
        mTodoBtn = (Button) findViewById(R.id.todo_btn);
        mLocationBtn = (Button) findViewById(R.id.location_btn);
        mCheckinBtn = (Button) findViewById(R.id.checkin_btn);
        //
        mRatingTitleTv = (TextView) findViewById(R.id.rating_title_tv);
        mRatingTv = new TextView[]{
                (TextView) findViewById(R.id.rating_tv_1),
                (TextView) findViewById(R.id.rating_tv_2),
                (TextView) findViewById(R.id.rating_tv_3),
                (TextView) findViewById(R.id.rating_tv_4),
                (TextView) findViewById(R.id.rating_tv_5)
        };
        mRatingIndicator = new TextView[]{
                (TextView) findViewById(R.id.rating_indicator_1),
                (TextView) findViewById(R.id.rating_indicator_2),
                (TextView) findViewById(R.id.rating_indicator_3),
                (TextView) findViewById(R.id.rating_indicator_4),
                (TextView) findViewById(R.id.rating_indicator_5)
        };

        /*
            Create the adapter that will return a fragment for each of the three primary sections of the activity.
         */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /*

         */
        mTodoBtn.setOnClickListener(onClickListener);
        mLocationBtn.setOnClickListener(onClickListener);
        mCheckinBtn.setOnClickListener(onClickListener);

        /*

         */
        mMissionDetailApiTask = new MissionDetailApiTask();
        mMissionDetailApiTask.execute(id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Btn OnClickListener
     */

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            clickListenerHandler(v.getId());
        }
    };

    private void clickListenerHandler(int id) {
        if (id == R.id.todo_btn) {
            mTodoAddDeleteApiTask = new TodoAddDeleteApiTask();
            mTodoAddDeleteApiTask.execute();

        } else if (id == R.id.location_btn) {

            if (location != null) {
                Uri uri = Uri.parse("http://maps.google.com/maps?q=loc:" + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude()));
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                Toast.makeText(MissionActivity.this, "위치가 등록되지 않은 미션입니다", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.checkin_btn) {

        }
    }

    ;

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.mission_fragment, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class MissionDetailApiTask extends AsyncTask<Integer, Void, Mission> {
        private int request_code = Argument.REQUEST_CODE_UNEXPECTED;

        @Override
        protected Mission doInBackground(Integer... id) {
            Mission mission = null;

            try {
                MissionDetailApi missionDetailApi = new MissionDetailApi(getApplication(), id[0]);
                request_code = missionDetailApi.getRequestCode();
                if (request_code == Argument.REQUEST_CODE_SUCCESS)
                    mission = missionDetailApi.getResult();

            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

            if (mission == null)
                cancel(true);

            return mission;
        }

        @Override
        protected void onPostExecute(Mission mission) {
            mMissionDetailApiTask = null;
            ApiBase.showToastMsg(getApplication(), request_code);

            if (request_code == Argument.REQUEST_CODE_SUCCESS) {

                updateView(mission);

            } else {
                showErrorView(true, "오류가 발생해 미션을 불러오지 못했습니다");
            }
        }

        @Override
        protected void onCancelled() {
            mMissionDetailApiTask = null;
            ApiBase.showToastMsg(getApplication(), request_code);
            showErrorView(true, "오류가 발생해 미션을 불러오지 못했습니다");

        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class TodoAddDeleteApiTask extends AsyncTask<Void, Void, Void> {
        private int request_code = Argument.REQUEST_CODE_UNEXPECTED;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (!is_todo) {
                    TodoAddApi todoAddApi = new TodoAddApi(getApplication(), id);
                    request_code = todoAddApi.getRequestCode();
                    if (request_code == Argument.REQUEST_CODE_SUCCESS)
                        todoAddApi.getResult();
                } else {
                    TodoDeleteApi todoDeleteApi = new TodoDeleteApi(getApplication(), id);
                    request_code = todoDeleteApi.getRequestCode();
                    if (request_code == Argument.REQUEST_CODE_SUCCESS)
                        todoDeleteApi.getResult();
                }

            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            mTodoAddDeleteApiTask = null;
            ApiBase.showToastMsg(getApplication(), request_code);

            if (request_code == Argument.REQUEST_CODE_SUCCESS) {
                is_todo = !is_todo;
                if (is_todo) {
                    Toast.makeText(MissionActivity.this, "할일에 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MissionActivity.this, "성공적으로 할일에서 해제되었습니다.", Toast.LENGTH_SHORT).show();
                }

            } else {
                showErrorView(true, "오류가 발생해 미션을 불러오지 못했습니다");
            }
        }

        @Override
        protected void onCancelled() {
            mTodoAddDeleteApiTask = null;
            showErrorView(true, "오류가 발생해 미션을 불러오지 못했습니다");

        }
    }

    /**
     * @param mission
     */
    private void updateView(Mission mission) {

        /*

         */
        mDescriptionTv.setText(mission.getDescription() + "(" + Integer.toString(mission.getScore()) + "점)");
        is_todo = mission.isIs_todo();
        if (is_todo) {
            mTodoBtn.setText("해제하기");
        } else {
            mTodoBtn.setText("저장하기");
        }

        if (mission.getPlace() != null) {
            location = new Location("reverseGeocoded");
            location.setLatitude(mission.getPlace().getLatitude());
            location.setLongitude(mission.getPlace().getLongitude());
        }

        /*

         */
        int max_offset_height = getResources().getDimensionPixelSize(R.dimen.mission_activity_rating_indicator_max_offset_height);
        int default_height = getResources().getDimensionPixelSize(R.dimen.mission_activity_rating_indicator_default_height);
        int[] ratings = mission.getRatings();
        int rating_sum = 0;
        int rating_average_x10 = 0;
        for (int i = 0; i < 5; i++) {
            mRatingTv[i].setText(String.format("%d명", ratings[i]));
            rating_sum += ratings[i];
            rating_average_x10 += (i + 1) * ratings[i];
        }
        //
        if (rating_sum == 0) {
            mRatingTitleTv.setText("평균 별점 0.0 / 5.0 (0명 참여)");
            rating_sum = 1;
        } else {
            mRatingTitleTv.setText(String.format("평균 별점 %.1f / 5.0 (%d명 참여)", ((float) rating_average_x10 / (float) rating_sum), rating_sum));
        }
        //
        for (int i = 0; i < 5; i++) {
            int height = max_offset_height * ratings[i] / rating_sum + default_height;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    height);
            mRatingIndicator[i].setLayoutParams(params);
        }
    }

    /**
     * @param show
     */
    private void showErrorView(final boolean show, String msg) {
        /*
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
        }*/
    }

    /**
     *
     */
    public void onDestroy() {
        super.onDestroy();

        if (mMissionDetailApiTask != null) {
            mMissionDetailApiTask.cancel(true);
        }

    }

}
