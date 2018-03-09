package kr.photox.android.view;

import java.util.ArrayList;

import kr.photox.android.R;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SearchActivity extends SherlockFragmentActivity {
	private final String TAG = "Search Activity";

	/**
	 * Preference Auto Login
	 */

	private SharedPreferences user_prefs;
	private SharedPreferences.Editor prefs_editor;
	private ArrayList<String> mRecentKeywords = new ArrayList<String>();

	/*
	 * 
	 */
	ProgressDialog dialog = null;

	/*
	 * UI Reference
	 */
	private Fragment mContent;
	private View mActionbar;
	private EditText mSearchEt;

	private View mRecentView;
	private View[] mRecentViews = new View[5];
	private TextView[] mRecentViewTvs = new TextView[5];
	private View[] mRecentViewDividers = new View[5];
	private Button[] mRecentViewBtns = new Button[5];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);

		/*
		 * Preference 선 셋팅
		 */

		user_prefs = getSharedPreferences("user_info", Context.MODE_PRIVATE);

		for (int i = 0; i < 5; i++) {
			String keyword = user_prefs.getString(
					"recent_keyword_" + Integer.toString(i), "");
			if (!keyword.equals("")) {
				mRecentKeywords.add(keyword);
			}
		}

		/*
		 * UI Reference Setting
		 */
		mActionbar = (View) findViewById(R.id.actionbar);
		mSearchEt = (EditText) mActionbar.findViewById(R.id.search_et);

		mRecentView = findViewById(R.id.recent_view);
		mRecentViews[0] = findViewById(R.id.recent_view_0);
		mRecentViews[1] = findViewById(R.id.recent_view_1);
		mRecentViews[2] = findViewById(R.id.recent_view_2);
		mRecentViews[3] = findViewById(R.id.recent_view_3);
		mRecentViews[4] = findViewById(R.id.recent_view_4);

		for (int i = 0; i < 5; i++) {
			mRecentViewTvs[i] = (TextView) mRecentViews[i]
					.findViewById(R.id.tv);
			mRecentViewDividers[i] = mRecentViews[i]
					.findViewById(R.id.divider_view);
			mRecentViewBtns[i] = (Button) mRecentViews[i]
					.findViewById(R.id.btn);
		}

		/*
		 * UI Visibllity Setting
		 */

		updateRecentView();

		mSearchEt.setImeActionLabel("Search", EditorInfo.IME_ACTION_SEARCH);
		mSearchEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					String keyword = mSearchEt.getText().toString();

					if (keyword.length() > 0) {
						startSearch(keyword);
					}

				}

				return false;
			}
		});

		mSearchEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					updateRecentView();
					mSearchEt.clearComposingText();
					testObjectAnimator(true);

				} else {
					testObjectAnimator(false);

				}
			}
		});

		mSearchEt.requestFocus();

		/*
		 * Initialize the Content Fragment
		 */

		if (mContent == null) {
			mContent = new SearchFragment();

		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_frame, mContent).commit();

		/*
		 * Button Click Listener
		 */

		for (int i = 0; i < 5; i++) {
			mRecentViewBtns[i].setOnClickListener(onRecentItemClickListener(i));
		}

	}

	/**
	 * 
	 */

	@Override
	public void onResume() {
		super.onResume();

		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}

	}

	@Override
	public void onDestroy() {
		saveRecentKeywords();
		super.onDestroy();

	}
	
	/**
	 * 
	 */
	
	private void startSearch(String keyword){
		((SearchFragment) mContent).JsonLoad(keyword);
		if(mRecentKeywords.contains(keyword)){
			mRecentKeywords.remove(keyword);
		}
		mRecentKeywords.add(0, keyword);

		mSearchEt.clearFocus();

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchEt.getWindowToken(),
				0);
	}

	/**
	 * 
	 */

	private void testObjectAnimator(boolean visible) {
		float height = mRecentView.getHeight();
		if (mRecentView.getY() == 0) {
			ObjectAnimator translationRight = ObjectAnimator.ofFloat(
					mRecentView, "Y", -height);
			translationRight.setDuration(getResources().getInteger(
					android.R.integer.config_mediumAnimTime));
			translationRight.start();
		} else {
			ObjectAnimator translationLeft = ObjectAnimator.ofFloat(
					mRecentView, "Y", 0f);
			translationLeft.setDuration(getResources().getInteger(
					android.R.integer.config_mediumAnimTime));
			translationLeft.start();
		}
	}

	private void updateRecentView() {
		int size = mRecentKeywords.size();

		if (size == 0) {
			mRecentView.setVisibility(View.GONE);
			return;
		} else {
			mRecentView.setVisibility(View.VISIBLE);
		}

		for (int i = 0; i < 5; i++) {
			if (i < size) {
				mRecentViews[i].setVisibility(View.VISIBLE);
				mRecentViewTvs[i].setText(mRecentKeywords.get(i));
				if (i == size - 1) {
					mRecentViewDividers[i].setVisibility(View.GONE);
				} else {
					mRecentViewDividers[i].setVisibility(View.VISIBLE);
				}
			} else {
				mRecentViews[i].setVisibility(View.GONE);
			}

		}

	}

	private void saveRecentKeywords() {
		prefs_editor = user_prefs.edit();

		for (int i = 0; i < 5; i++) {
			if (i < mRecentKeywords.size()) {
				prefs_editor.putString("recent_keyword_" + Integer.toString(i),
						mRecentKeywords.get(i).toString());
			}
		}
		prefs_editor.commit();
		mRecentKeywords = null;
	}

	/**
	 * Btn OnClickListener
	 */

	private OnClickListener onRecentItemClickListener(final int position) {
		OnClickListener onClickListener = new OnClickListener() {
			public void onClick(View v) {
				startSearch(mRecentKeywords.get(position));
				
			}
		};
		return onClickListener;
	}

	/**
	 * 미션을 넣었을 때 미션 셋 액티비티를 실행합니다.
	 */

	public void startMissionSetActivity(int mission_id) {
		dialog = ProgressDialog
				.show(SearchActivity.this, "", "로딩중입니다...", true, true);
		Log.d(TAG, "startMissionSetActivity Started!");

		/*
		 * 데이터를 넣어 새로운 액티비티를 실행합니다.
		 */

		Intent intent = new Intent(SearchActivity.this, MActivity.class);
		intent.putExtra("campaign_id", -mission_id);
		startActivity(intent);
	}

	/**
	 * 캠페인을 넣었을 때 캠페인 액티비티를 실행합니다.
	 */

	public void startCampaignActivity(int campaign_id, String campaign_title) {
		dialog = ProgressDialog
				.show(SearchActivity.this, "", "로딩중입니다...", true, true);
		Log.d(TAG, "startCampaignActivity Started!");

		/*
		 * 데이터를 넣어 새로운 액티비티를 실행합니다.
		 */

		Intent intent = new Intent(SearchActivity.this, NActivity.class);
		intent.putExtra("campaign_id", campaign_id);
		intent.putExtra("campaign_title", campaign_title);
		startActivity(intent);
	}

}
