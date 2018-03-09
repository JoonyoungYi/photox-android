package kr.photox.android.view;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.CampaignDetailApi;
import kr.photox.android.deprecated.SettingActivity;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Campaign;
import kr.photox.android.model.Model;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class XActivity extends FragmentActivity {

	private static final String TAG = "Main Activity";

	/**
	 * 
	 */
	private PullToRefreshAttacher mPullToRefreshAttacher;

	/**
	 * 
	 */

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mPhotoxTitles;
	private int[] mPhotoxIcons = { R.drawable.x_activity_lv_main,
			R.drawable.x_activity_lv_todo, R.drawable.x_activity_lv_album,
			R.drawable.x_activity_lv_campaign,
			R.drawable.x_activity_lv_statistics,
			R.drawable.x_activity_lv_setting };

	/**
	 * 
	 */

	boolean doubleBackToExitPressedOnce = false;

	/**
	 * 
	 */

	private ProgressDialog dialog = null;

	/**
	 * UI Reference
	 */
	private Fragment mContent;

	/**
	 * On Create
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//dalvik.system.VMRuntime.getRuntime().setTargetHeapUtilization(0.9f);
		setContentView(R.layout.x_activity);
		Log.d(TAG, "onCreated!");

		/**
		 * // The attacher should always be created in the Activity's onCreate
		 */

		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		DefaultHeaderTransformer ht = (DefaultHeaderTransformer) mPullToRefreshAttacher
				.getHeaderTransformer();
		ht.setProgressBarColor(0xFF0095D7);

		/**
		 * 
		 */

		mTitle = mDrawerTitle = getTitle();
		mPhotoxTitles = getResources().getStringArray(R.array.photox_array);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new LvAdapter(this, R.layout.x_activity_lv));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		/**
		 * 
		 */

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		/*
		 * Visibllity Setting
		 */

		// mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);

		/**
		 * Initialize the Content Fragment
		 */

		// if (mContent == null) {
		// mContent = new MainFragment();

		// }

		// getFragmentManager().beginTransaction()
		// .replace(R.id.main_frame, mContent).commit();

		/**
		 * 
		 */

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}

	}

	/**
	 * ListView Apdater Setting
	 */

	private class LvAdapter extends ArrayAdapter<String> {

		private ViewHolder viewHolder = null;
		private int textViewResourceId;

		public LvAdapter(Activity context, int textViewResourceId) {
			super(context, textViewResourceId, mPhotoxTitles);
			this.textViewResourceId = textViewResourceId;
		}

		@Override
		public int getCount() {
			return mPhotoxTitles.length;
		}

		@Override
		public String getItem(int position) {
			return mPhotoxTitles[position];
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
				convertView = getLayoutInflater().inflate(textViewResourceId,
						null);
				viewHolder = new ViewHolder();

				viewHolder.mIv = (ImageView) convertView.findViewById(R.id.iv);
				viewHolder.mTv = (TextView) convertView.findViewById(R.id.tv);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			/*
			 * Data Import and export
			 */

			viewHolder.mTv.setText(mPhotoxTitles[position]);
			viewHolder.mIv.setImageResource(mPhotoxIcons[position]);

			return convertView;
		}

		private class ViewHolder {
			ImageView mIv;
			TextView mTv;

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
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);

			}

			return true;
		}

		return super.onKeyDown(keycode, e);
	}

	/**
	 * Switch Content
	 * 
	 * @param fragment
	 */

	public void switchContent(final Fragment fragment) {
		mContent = fragment;
		// mContent_id = fragment_id;

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.main_frame, mContent)
				.commit();

		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.main_frame, fragment).commit();

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
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.actionbar_right_btn:

			break;
		default:
			break;
		}
	};

	/**
	 * Shampaign Fragment Start
	 */

	public void onShampaignFragmentCreated() {
		// Intent intent = new Intent("AsyncTaskMananger");
		// intent.putExtra("async_task_assinged_num", 0);
		// startService(intent);
	}

	/**
	 * 미션셋 액티비티를 실행합니다. 캠페인을 넣었을 때
	 */

	public void startMissionSetActivity(Campaign campaign) {
		dialog = ProgressDialog.show(XActivity.this, "", "로딩중입니다...", true,
				true);
		Log.d(TAG, "startMissionSetActivity Started!");

		loadCampaign(campaign.getId());

	}

	private void loadCampaign(int id) {
		ApplicationManager am = (ApplicationManager) getApplicationContext();
		am.setOnJsonLoadingCompletionListener(onJsonLoadingCompletionListener);
		CampaignDetailApi api = new CampaignDetailApi();
		api.setInput(id);
		am.addJsonLoadingTask(api);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJsonLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {

		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {

				Log.d(TAG, "Models size : " + Integer.toString(models.size()));

				Campaign campaign = (Campaign) models.get(0);

				/*
				 * 데이터를 넣어 새로운 액티비티를 실행합니다.
				 */

				Intent intent = new Intent(XActivity.this, MActivity.class);
				// intent.putExtra("campaign", campaign);

				startActivity(intent);
			} else {
				Toast.makeText(getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	/**
	 * 
	 */

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);

		}
	}

	private void selectItem(int position) {

		Fragment fragment = new XMainFragment();
		if (position == 0) {
			fragment = new XMainFragment();
		} else if (position == 1) {
			fragment = new XTodoFragment();
		} else if (position == 2) {
			fragment = new XAlbumFragment();
		} else if (position == 3) {
			fragment = new XCampaignFragment();
		} else if (position == 4) {
			fragment = new XStatisticFragment();
		} else if (position == 5) {
			fragment = new XSettingFragment();
		}

		switchContent(fragment);
		mDrawerList.setItemChecked(position, true);
		setTitle(mPhotoxTitles[position]);

		mDrawerLayout.closeDrawer(mDrawerList);
	}

	/**
	 * Action Item Setting
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getSupportMenuInflater();
		// inflater.inflate(R.menu.jinro_details_activity, menu);
		if ((mContent instanceof XMainFragment)
				|| (mContent instanceof XCampaignFragment)
				|| (mContent instanceof XTodoFragment)) {
			menu.add("Search")
					.setOnMenuItemClickListener(
							(android.view.MenuItem.OnMenuItemClickListener) menuItemClickListener)
					.setIcon(R.drawable.base_search)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {

		}

		return super.onCreateOptionsMenu(menu);
	}

	public OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			Intent intent = new Intent(XActivity.this, SearchActivity.class);
			startActivity(intent);

			return false;
		}
	};

	/**
	 * 
	 */

	public void startShampaignActivity(int campaign_id, String campaign_title) {
		dialog = ProgressDialog.show(XActivity.this, "", "로딩중입니다...", true,
				true);
		Log.d(TAG, "shampaign Activity Start");

		/*
		 * 데이터를 넣어 새로운 액티비티를 실행합니다. 투두 리스트의 경우 캠페인 아이디가 -1입니다. 따라서 미션 아이디를 넣어주어야
		 * 합니다.
		 */

		Intent intent = new Intent(XActivity.this, MActivity.class);
		intent.putExtra("campaign_id", campaign_id);
		intent.putExtra("campaign_title", campaign_title);

		startActivity(intent);
	}

	/**
	 * 
	 */

	public void startShotActivity(int shot_id, String mission_title,
			String img_url) {
		dialog = ProgressDialog.show(XActivity.this, "", "로딩중입니다...", true,
				true);
		Log.d(TAG, "shot Activity Start");

		/*
		 * 데이터를 넣어 새로운 액티비티를 실행합니다. 투두 리스트의 경우 캠페인 아이디가 -1입니다. 따라서 미션 아이디를 넣어주어야
		 * 합니다.
		 */

		Intent intent = new Intent(XActivity.this, ShotActivity.class);
		intent.putExtra("shot_id", shot_id);
		intent.putExtra("mission_title", mission_title);
		intent.putExtra("img_url", img_url);
		startActivity(intent);

	}

	/**
	 * 캠페인을 넣었을 때 캠페인 액티비티를 실행합니다.
	 */

	public void startCampaignActivity(int campaign_id, String campaign_title) {
		dialog = ProgressDialog.show(XActivity.this, "", "로딩중입니다...", true,
				true);
		Log.d(TAG, "startCampaignActivity Started!");

		/*
		 * 데이터를 넣어 새로운 액티비티를 실행합니다.
		 */

		Intent intent = new Intent(XActivity.this, NActivity.class);
		intent.putExtra("campaign_id", campaign_id);
		intent.putExtra("campaign_title", campaign_title);
		startActivity(intent);
	}

	/**
	 * 개발자에게 이메일을 보냅니다.
	 */

	public void startEmailIntet(String email) {
		Uri emailUri = Uri.parse("mailto:" + email);
		Intent intent = new Intent(Intent.ACTION_SENDTO, emailUri);
		startActivity(intent);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
	}

	/**
     * 
     */

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 뒤로가기 두번
	 */
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "한 번 더 누르시면, 포토엑스에서 빠져나갑니다.", Toast.LENGTH_SHORT)
				.show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;

			}
		}, 2000);
	}

	/*
	 * 
	 */

	public void showDialog(String message) {
		dialog = ProgressDialog.show(XActivity.this, "", message, true, true);
	}

	public void hideDialog() {
		if (dialog != null) {
			dialog.cancel();
			dialog = null;
		}
	}

	/**
	 * 
	 */

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

}
