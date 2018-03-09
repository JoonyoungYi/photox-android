package kr.photox.android.view;

import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.LogoutApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Model;
import kr.photox.android.model.Protocol;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class XSettingFragment extends Fragment {
	private static final String TAG = "X Setting Fragment";
	
	/**
	 * Preference Auto Login
	 */

	private SharedPreferences user_prefs;
	private SharedPreferences.Editor prefs_editor;

	/**
	 * UI Reference
	 */

	private ListView mLv;

	/**
	 * ListView init
	 */

	private LvAdapter mLvAdapter;

	/**
	 * ListView에 표시될 데이터들입니다.
	 */

	private final int[][] DATA = {
			{ 0, R.string.x_setting_fragment_account },
			{ 1, R.string.x_setting_fragment_account_logout, -1 },
			{ 1, R.string.x_setting_fragment_account_connect_fb,
					R.string.x_setting_fragment_ready },
			{ 1, R.string.x_setting_fragment_account_connect_gp,
					R.string.x_setting_fragment_ready },
			{ 1, R.string.x_setting_fragment_account_convert,
					R.string.x_setting_fragment_ready },
			{ 0, R.string.x_setting_fragment_general },
			{
					1,
					R.string.x_setting_fragment_general_remove_search_record,
					R.string.x_setting_fragment_general_remove_search_record_summary },
			{ 1, R.string.x_setting_fragment_general_push_todo,
					R.string.x_setting_fragment_ready },
			{ 1, R.string.x_setting_fragment_general_push_confirm,
					R.string.x_setting_fragment_ready },
			{ 0, R.string.x_setting_fragment_info },
			{ 1, R.string.x_setting_fragment_info_version, -1 },
			{ 1, R.string.x_setting_fragment_info_terms,
					R.string.x_setting_fragment_ready },
			{ 1, R.string.x_setting_fragment_info_license,
					R.string.x_setting_fragment_info_license_summary },
			{ 1, R.string.x_setting_fragment_info_faq,
					R.string.x_setting_fragment_ready },
			{ 1, R.string.x_setting_fragment_info_ask,
					R.string.x_setting_fragment_info_ask_summary } };

	/**
	 * 
	 */

	private String mEmail;
	private String mBuildVersion;

	/**
	 * On Create View
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.x_setting_fragment, container, false);

		/*
		 * UI Init
		 */

		mLv = (ListView) v.findViewById(R.id.lv);

		/*
		 * ListView setting
		 */

		mLvAdapter = new LvAdapter(getActivity());
		mLv.setAdapter(mLvAdapter);
		mLv.setOnItemClickListener(onItemClickListener);

		

		/*
		 * Preference Setting
		 */

		user_prefs = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		mEmail = user_prefs.getString("user_email", "");

		/*
		 * 앱 버전을 얻어옵니다.
		 */

		try {
			PackageInfo i = getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(), 0);
			mBuildVersion = i.versionName;
		} catch (NameNotFoundException e) {
		}

		return v;
	}

	/**
	 * Lv Adapter
	 * 
	 * @author JoonYoungYi
	 * 
	 */

	private class LvAdapter extends ArrayAdapter {
		private Activity context;
		private ViewHolder viewHolder = null;

		public LvAdapter(Activity context) {
			super(context, R.layout.x_setting_fragment_lv, DATA);

			this.context = context;
		}

		/*
		 * visible session setting, DATA Setting
		 */

		public View getView(int position, View convertView, ViewGroup parent) {

			/*
			 * UI Initiailizing
			 */

			if (convertView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.x_setting_fragment_lv,
						null);

				viewHolder = new ViewHolder();

				viewHolder.mParentView = convertView
						.findViewById(R.id.parent_view);
				viewHolder.mSelectorTv = (TextView) convertView
						.findViewById(R.id.selector_tv);

				viewHolder.mChildView = convertView
						.findViewById(R.id.child_view);
				viewHolder.mTitleTv = (TextView) convertView
						.findViewById(R.id.title_tv);
				viewHolder.mSubtitleTv = (TextView) convertView
						.findViewById(R.id.subtitle_tv);
				viewHolder.mDivider = convertView.findViewById(R.id.divider);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}

			/*
			 * UI Visibillity Setting
			 */

			if (DATA[position][0] == 0) {

				viewHolder.mParentView.setVisibility(View.VISIBLE);
				viewHolder.mChildView.setVisibility(View.GONE);

				viewHolder.mSelectorTv.setText(DATA[position][1]);

			} else {

				viewHolder.mParentView.setVisibility(View.GONE);
				viewHolder.mChildView.setVisibility(View.VISIBLE);

				if (position == DATA.length - 1) {
					viewHolder.mDivider.setVisibility(View.GONE);
				} else if (DATA[position + 1][0] == 0) {
					viewHolder.mDivider.setVisibility(View.GONE);
				} else {
					viewHolder.mDivider.setVisibility(View.VISIBLE);
				}

				/*
				 * 
				 */

				if (DATA[position][2] == R.string.x_setting_fragment_ready) {
					viewHolder.mTitleTv.setTextColor(0xFF9f9f9f);
					viewHolder.mSubtitleTv.setTextColor(0xFFb0b0b0);
				} else {
					viewHolder.mTitleTv.setTextColor(0xFF444444);
					viewHolder.mSubtitleTv.setTextColor(0xFF777777);
				}

				/*
				 * 
				 */

				viewHolder.mTitleTv.setText(DATA[position][1]);

				if (DATA[position][2] != -1) {
					viewHolder.mSubtitleTv.setText(DATA[position][2]);
				} else if (DATA[position][1] == R.string.x_setting_fragment_account_logout) {
					viewHolder.mSubtitleTv.setText(mEmail);
				} else if (DATA[position][1] == R.string.x_setting_fragment_info_version) {
					viewHolder.mSubtitleTv.setText(mBuildVersion);
				}

			}

			return convertView;
		}

		/**
		 * Category Click event Disabled
		 */

		public boolean isEnabled(int position) {

			if (DATA[position][0] == 0) {
				return false;
			} else if (DATA[position][2] == R.string.x_setting_fragment_ready) {
				return false;
			} else {
				return true;
			}
		}

		public class ViewHolder {
			View mParentView;
			TextView mSelectorTv;

			View mChildView;
			TextView mTitleTv;
			TextView mSubtitleTv;
			View mDivider;
		}

	}

	/**
	 * click List Item.
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if (DATA[position][1] == R.string.x_setting_fragment_info_ask) {
				startEmailIntent();

			} else if (DATA[position][1] == R.string.x_setting_fragment_account_logout) {
				requestLogout();

			} else if (DATA[position][1] == R.string.x_setting_fragment_general_remove_search_record) {
				requestRemoveRecentKeywords();

			} else if (DATA[position][1] == R.string.x_setting_fragment_info_version) {
				requestCheckingUpdate();

			} else if (DATA[position][1] == R.string.x_setting_fragment_info_license) {
				startLicenseIntent();

			}

		}
	};

	/**
	 * 
	 */

	private void startEmailIntent() {
		XActivity xActivity = (XActivity) getActivity();
		xActivity.startEmailIntet("photox.android@gmail.com");
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 * 
	 * 
	 */

	private void requestLogout() {
		AlertDialog.Builder alert_confirm = new AlertDialog.Builder(
				getActivity());
		alert_confirm.setMessage("로그아웃 하시겠습니까?").setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestLogoutApi();
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 'No'
						return;
					}
				});

		final AlertDialog alert = alert_confirm.create();

		alert.setOnKeyListener(new Dialog.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					alert.dismiss();
				}
				return true;
			}
		});

		alert.show();
	}

	private void requestLogoutApi() {

		/*
		 * Api Controller를 통해 Session_Key 를 얻어옵니다.
		 */
		ApplicationManager am = ((ApplicationManager) getActivity()
				.getApplicationContext());

		/*
		 * 콜백을 지정해서 로그아웃이 끝났을때 어떻게 할지를 정합니다.
		 */

		am.setOnJsonLoadingCompletionListener(onJoinRequestLoadingCompletionListener);

		/*
		 * 로그아웃 Api를 만들고 세션키를 대입합니다.
		 */

		LogoutApi logoutApi = new LogoutApi();
		// logoutApi.setInput(session_key);

		/*
		 * Api컨트롤러에 로그아웃 에이피아이를 추가합니다.
		 */

		am.addJsonLoadingTask(logoutApi);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJoinRequestLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {
				/*
				 * 상태와 메세지에 관한 정보를 추출하기 위해 프로토콜 모델을 추출합니다.
				 */

				Protocol protocol = (Protocol) models.get(models.size() - 1);
				String status = protocol.getResult_status();
				Log.d("result_status", status);

				if (status.equals("ok")) {
					Toast.makeText(getActivity(), "로그아웃에 성공하셨습니다.",
							Toast.LENGTH_SHORT).show();
					prefs_editor = user_prefs.edit();
					prefs_editor.putString("auto_key", "");
					prefs_editor.putString("user_id", "");
					prefs_editor.putString("user_name", "");
					prefs_editor.putString("user_email", "");
					prefs_editor.commit();

					removeRecentKeywords();

					Intent splashIntent = new Intent(getActivity(),
							SActivity.class);
					splashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					splashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					getActivity().startActivity(splashIntent);
					getActivity().finish();

				} else if (status.equals("fail")) {
					String message = protocol.getResult_message();
					Log.d("message", message);

					Toast.makeText(getActivity(), "알수 없는 오류로 로그아웃에 실패하셨습니다.",
							Toast.LENGTH_SHORT).show();

				}
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

	/*
	 * 
	 */

	private void requestRemoveRecentKeywords() {
		AlertDialog.Builder alert_confirm = new AlertDialog.Builder(
				getActivity());
		alert_confirm.setMessage("최근 검색어를 모두 삭제 하시겠습니까?").setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeRecentKeywords();
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 'No'
						return;
					}
				});

		final AlertDialog alert = alert_confirm.create();

		alert.setOnKeyListener(new Dialog.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					alert.dismiss();
				}
				return true;
			}
		});

		alert.show();
	}

	private void removeRecentKeywords() {
		prefs_editor = user_prefs.edit();
		for (int i = 0; i < 5; i++) {
			prefs_editor.putString("recent_keyword_" + Integer.toString(i), "");
		}
		prefs_editor.commit();

		//
		Toast.makeText(getActivity().getApplicationContext(),
				"최근 검색어를 모두 삭제했습니다.", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 */

	private void requestCheckingUpdate() {
		Toast.makeText(getActivity().getApplicationContext(),
				"업데이트 확인 기능을 준비중입니다.", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 */

	private void startLicenseIntent() {
		Toast.makeText(getActivity().getApplicationContext(),
				"라이센스 페이지를 준비중입니다.", Toast.LENGTH_SHORT).show();
	}

}
