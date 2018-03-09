package kr.photox.android.view;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import kr.photox.android.R;
import kr.photox.android.api.JoinApi;
import kr.photox.android.api.LoginApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Model;
import kr.photox.android.model.Protocol;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SUpActivity extends Activity {
	private static final String TAG = "Join Activity";

	/**
	 * Preference Auto Login
	 */

	SharedPreferences prefs;
	SharedPreferences.Editor prefs_editor;

	/**
	 * Values for email and password at the time of the login attempt.
	 */

	private String mEmail;
	private String mPassword;
	private String mConfirm;
	private String mLogin_nonce;

	/**
	 * UI references
	 */

	private View mLoadingView;

	private View mLoginView;
	private EditText mEmailEt;
	private EditText mPasswordEt;
	private EditText mConfirmEt;

	/**
	 * onCreate Method
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.s_up_activity);

		/**
		 * Auto Key 사용을 위한 Pref Setting
		 */

		prefs = getSharedPreferences("user_info", Context.MODE_PRIVATE);

		/**
		 * 
		 */

		mLogin_nonce = getIntent().getExtras().getString("login_nonce");

		/**
		 * UI Reference Init
		 */

		mLoadingView = findViewById(R.id.loading_view);

		mLoginView = findViewById(R.id.login_view);
		mEmailEt = (EditText) findViewById(R.id.email_et);
		mPasswordEt = (EditText) findViewById(R.id.password_et);
		mConfirmEt = (EditText) findViewById(R.id.confirm_et);

		AccountManager accountManager = AccountManager.get(this);
		Account[] accounts = accountManager.getAccountsByType("com.google");
		String username = accounts[0].name;
		mEmailEt.setText(username);

		/**
		 * Set up the login form.
		 */

		// mEmailEt.setText(mEmail);

		mPasswordEt
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.btn || id == EditorInfo.IME_NULL) {
							attemptJoin();
							return true;
						}
						return false;
					}
				});

		/**
		 * 클릭 리스너를 지정합니다.
		 */

		findViewById(R.id.btn).setOnClickListener(onClickListener);

	}

	/**
	 * Button onClick Listener
	 */

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			clickListenerHandler(v.getId());
		}
	};

	private void clickListenerHandler(int id) {
		switch (id) {
		case R.id.btn:
			attemptJoin();
			break;
		default:
			break;
		}
	};

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptJoin() {

		// if (mAuthTask != null) {
		// return;
		// }

		/*
		 * Reset errors.
		 */
		mEmailEt.setError(null);
		mPasswordEt.setError(null);
		mConfirmEt.setError(null);

		/*
		 * Store values at the time of the login attempt.
		 */
		mEmail = mEmailEt.getText().toString();
		mPassword = mPasswordEt.getText().toString();
		mConfirm = mConfirmEt.getText().toString();

		boolean cancel = false;
		View focusView = null;

		/*
		 * Check for a valid password confirm.
		 */

		if (TextUtils.isEmpty(mConfirm)) {
			mConfirmEt.setError(getString(R.string.error_field_required));
			focusView = mConfirmEt;
			cancel = true;
		} else if (!mPassword.equals(mConfirm)) {
			mConfirmEt.setError("입력해주신 비밀번호와 일치하지 않습니다");
			focusView = mConfirmEt;
			cancel = true;
		}

		/*
		 * Check for a valid password.
		 */
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordEt.setError(getString(R.string.error_field_required));
			focusView = mPasswordEt;
			cancel = true;
		} else if (mPassword.length() < 8) {
			mPasswordEt.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordEt;
			cancel = true;
		}

		/*
		 * Check for a valid email address.
		 */
		if (TextUtils.isEmpty(mEmail)) {
			mEmailEt.setError(getString(R.string.error_field_required));
			focusView = mEmailEt;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailEt.setError(getString(R.string.error_invalid_email));
			focusView = mEmailEt;
			cancel = true;
		}

		if (cancel) {
			/*
			 * There was an error; don't attempt login and focus the first form
			 * field with an error.
			 */
			focusView.requestFocus();
		} else {

			/*
			 * Show a progress spinner, and kick off a background task to
			 * perform the user login attempt.
			 */
			showProgress(true);

			// LgDatum mLgDatum = new LgDatum();
			// mLgDatum.setEmail(mEmail);
			// mLgDatum.setPassword(mPassword);

			// mAuthTask = new UserLoginTask();
			// mAuthTask.execute(mLgDatum);

			doRequestJoin();
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 * 
	 * 
	 */

	private void doRequestJoin() {

		ApplicationManager am = (ApplicationManager) this
				.getApplicationContext();
		am.setOnJsonLoadingCompletionListener(onJsonLoadingCompletionListener);
		JoinApi joinApi = new JoinApi();

		/*
		 * Set Input in APi Manager.
		 */
		Log.d("mEmail", mEmail);
		Log.d("mPassword", mPassword);
		joinApi.setInput(mEmail, mPassword);

		/*
		 * 
		 */

		am.addJsonLoadingTask(joinApi);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJsonLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
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
					Toast.makeText(getApplication(), "회원가입에 성공하셨습니다.",
							Toast.LENGTH_LONG).show();

					doRequestLogin();

				} else if (status.equals("fail")) {
					String message = protocol.getResult_message();
					if (message.contains("Email")) {
						mEmailEt.setError("이미 가입된 이메일 입니다");
						mEmailEt.requestFocus();
					}

					mPasswordEt.setError(message);
					mPasswordEt.requestFocus();
					Log.d("message", message);

				}
			} else {
				Toast.makeText(getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}

			showProgress(false);
		}
	};

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 * 
	 * 
	 */

	private void doRequestLogin() {

		/*
		 * 이메일로 로그인 하는 유저의 경우 이메일 계정의 아이디를 이름으로 취급합니다.
		 */

		String username = mEmail.substring(0, mEmail.indexOf("@"))
				.toUpperCase();

		prefs_editor = prefs.edit();
		prefs_editor.putString("user_name", username);
		prefs_editor.putString("user_email", mEmail);
		prefs_editor.commit();

		/*
		 * 
		 */

		ApplicationManager am = (ApplicationManager) this
				.getApplicationContext();
		am.setOnJsonLoadingCompletionListener(onJoinRequestLoadingCompletionListener);
		LoginApi loginApi = new LoginApi();

		/*
		 * Set Input in APi Manager.
		 */
		Log.d(TAG, "mEmail : " + mEmail);
		Log.d(TAG, "mPassword : " + mPassword);

		mPassword = SHA256_s(mLogin_nonce + SHA256_s(mPassword + mEmail));

		loginApi.setInput("email", mLogin_nonce, null, mEmail, mPassword);

		/*
		 * 
		 */

		am.addJsonLoadingTask(loginApi);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJoinRequestLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {

			if (isCompleted) {

				/*
				 * 로딩창을 제거하고 메인 액티비티로 들어갑니다.
				 */
				showProgress(false);

				Intent intent = new Intent(getApplicationContext(),
						XActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);

				/**
			 * 
			 */

				Toast.makeText(getApplication(), "로그인에 성공하셨습니다.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	/**
	 * 
	 * SHA256 encoding (This is hash)
	 */

	public String SHA256_s(String json) {
		String txtClipher = "";

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");

			md.update(json.getBytes("UTF-8"));

			byte byteData[] = md.digest();
			byte[] byteData_new = new byte[byteData.length];

			for (int i = 0; i < byteData.length; i++) {
				byteData_new[i] = (byte) (byteData[i] & 0xff);
			}

			txtClipher = Base64.encodeToString(byteData_new, 0).replace("\n",
					"");

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return txtClipher;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		/*
		 * On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		 * for very easy animations. If available, use these APIs to fade-in the
		 * progress spinner.
		 */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoadingView.setVisibility(View.VISIBLE);
			mLoadingView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoadingView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginView.setVisibility(View.VISIBLE);
			mLoginView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});

		} else {
			/*
			 * The ViewPropertyAnimator APIs are not available, so simply show
			 * and hide the relevant UI components.
			 */

			mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * 
	 */

	/**
	 * 뒤로가기 두번
	 */
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}
}
