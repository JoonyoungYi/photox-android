package kr.photox.android.view;

import kr.photox.android.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class NActivity extends SherlockFragmentActivity {
	private static final String TAG = "Campaign Activity";
	ProgressDialog dialog = null;
	/*
	 * UI Reference
	 */
	private Fragment mContent;

	private View mActionbar;
	private TextView mTitleTv;
	private ImageButton mBackBtn;
	private ImageButton mRightBtn;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campaign_activity);

		/*
		 * UI Setting
		 */
		mActionbar = (View) findViewById(R.id.actionbar);
		mTitleTv = (TextView) mActionbar.findViewById(R.id.title_tv);
		mBackBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_left_btn);
		mRightBtn = (ImageButton) mActionbar
				.findViewById(R.id.actionbar_right_btn);

		/*
		 * Initialize the Content Fragment
		 */

		if (mContent == null) {
			mContent = new NFragment();

			Bundle args = new Bundle();
			args.putInt("campaign_id",
					getIntent().getExtras().getInt("campaign_id", -1));

			mContent.setArguments(args);
		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_frame, mContent).commit();

		/*
		 * Visibillity Setting
		 */
		mTitleTv.setText(getIntent().getExtras().getString("campaign_title",
				"캠페인"));
		mBackBtn.setPadding(0, 0, 0, 0);
		mBackBtn.setImageResource(R.drawable.base_actionbar_dir_bottom);
		mRightBtn.setVisibility(View.INVISIBLE);

		/*
		 * Button Click Listner
		 */

		mBackBtn.setOnClickListener(onClickListener);

	}

	@Override
	public void onResume() {
		super.onResume();

		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}

	}

	/**
	 * 미션을 넣었을 때
	 */

	public void startMissionSetActivity(int mission_id) {
		dialog = ProgressDialog.show(NActivity.this, "", "로딩중입니다...",
				true, true);
		Log.d(TAG, "startMissionSetActivity Started!");
		/*
		 * 데이터를 넣어 새로운 액티비티를 실행합니다.
		 */

		Intent intent = new Intent(NActivity.this,
				MActivity.class);
		intent.putExtra("campaign_id", mission_id);
		startActivity(intent);
	}

	/**
	 * Btn OnClickListener
	 */

	OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			clickListenerHandler(v.getId());
		}
	};

	private void clickListenerHandler(int id) {

		switch (id) {
		case R.id.actionbar_left_btn:
			onBackPressed();
			break;
		default:
			break;
		}
	};
}
