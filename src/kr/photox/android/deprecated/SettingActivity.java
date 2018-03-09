package kr.photox.android.deprecated;

import kr.photox.android.R;
import kr.photox.android.view.XSettingFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SettingActivity extends SherlockFragmentActivity {

	/*
	 * UI Reference
	 */
	private Fragment mContent;

	private View mActionbar;
	private TextView mTitleTv;
	private ImageButton mBackBtn;
	private ImageButton mRightBtn;

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
		 * Visibillity Setting
		 */
		mTitleTv.setText("설정하기");
		mBackBtn.setPadding(0, 0, 0, 0);
		mBackBtn.setImageResource(R.drawable.base_actionbar_dir_bottom);
		mRightBtn.setVisibility(View.INVISIBLE);

		/*
		 * Initialize the Content Fragment
		 */

		if (mContent == null) {
			mContent = new XSettingFragment();

		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_frame, mContent).commit();

		/*
		 * Button Click Listner
		 */

		mBackBtn.setOnClickListener(onClickListener);

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
