package kr.photox.android.view;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.photox.android.R;
import kr.photox.android.api.AppInitApi;
import kr.photox.android.api.LoginApi;
import kr.photox.android.manager.ApplicationManager;
import kr.photox.android.model.Login;
import kr.photox.android.model.Model;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnStartLoadingListener;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;
import com.facebook.widget.ProfilePictureView;

public class SInActivity extends SherlockFragmentActivity {
	public static final String TAG = "Sign In Activity";

	/**
	 * Preference Auto Login
	 */

	SharedPreferences user_prefs;
	SharedPreferences.Editor prefs_editor;

	/**
	 * Values for email and password at the time of the login attempt.
	 */
	private String mEmail;
	private String mPassword;
	private String mLogin_nonce;

	/**
	 * 
	 */

	private ProgressDialog dialog = null;

	/**
	 * UI references.
	 */
	private View mLoadingView;

	private View mLoginView;
	private EditText mEmailEt;
	private EditText mPasswordEt;

	/**
	 * Related to FB Login
	 */

	private static final String PERMISSION = "publish_actions";
	private static final Location SEATTLE_LOCATION = new Location("") {
		{
			setLatitude(47.6097);
			setLongitude(-122.3331);
		}
	};

	private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";

	private Button postStatusUpdateButton;
	private Button postPhotoButton;
	private Button pickFriendsButton;
	private Button pickPlaceButton;
	private LoginButton loginButton;
	private ProfilePictureView profilePictureView;
	private TextView greeting;
	private PendingAction pendingAction = PendingAction.NONE;
	private ViewGroup controlsContainer;
	private GraphUser user;
	private GraphPlace place;
	private List<GraphUser> tags;
	private boolean canPresentShareDialog;

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d("HelloFacebook", "Success!");
		}
	};

	/**
	 * onCreate Method
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * Ready to Fb Login
		 */

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			String name = savedInstanceState
					.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}

		setContentView(R.layout.s_in_activity);

		/**
		 * 
		 */

		user_prefs = getSharedPreferences("user_info", Context.MODE_PRIVATE);

		/**
		 * UI Reference Init
		 */

		mLoadingView = findViewById(R.id.loading_view);

		mLoginView = findViewById(R.id.login_view);
		mEmailEt = (EditText) findViewById(R.id.email_et);
		mPasswordEt = (EditText) findViewById(R.id.password_et);

		/**
		 * Set up the login form.
		 */

		// mEmailEt.setText(mEmail);

		mPasswordEt
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		/**
		 * FB Login It's possible that we were waiting for this.user to // be
		 * populated in order to post a // status update.
		 */

		loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						SInActivity.this.user = user;
						// updateUI();
						// handlePendingAction();

						if (user != null) {
							// showProgress(true);
							Session session = Session.getActiveSession();
							doRequestFbLogin(session.getAccessToken(),
									user.getId());

							// Log.d(TAG, "user_location : " +
							// user.getLocation());

							// Log.d(TAG, "user_id : " + user.getId());
							// Log.d(TAG, "user_name : " + user.getName());
							// Log.d(TAG, "user_link : " + user.getLink());
							// Log.d(TAG, "user_birthday : " +
							// user.getBirthday());

						}

					}
				});

		loginButton.setOnStartLoadingListener(new OnStartLoadingListener() {
			@Override
			public void onStartLoading() {
				// showProgress(true);
				dialog = ProgressDialog.show(SInActivity.this, "",
						"로그인 중입니다...", true, true);

			}
		});

		/**
		 * 
		 */

		profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
		greeting = (TextView) findViewById(R.id.greeting);

		postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
		postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onClickPostStatusUpdate();
			}
		});

		postPhotoButton = (Button) findViewById(R.id.postPhotoButton);
		postPhotoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onClickPostPhoto();
			}
		});

		pickFriendsButton = (Button) findViewById(R.id.pickFriendsButton);
		pickFriendsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onClickPickFriends();
			}
		});

		pickPlaceButton = (Button) findViewById(R.id.pickPlaceButton);
		pickPlaceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onClickPickPlace();
			}
		});

		controlsContainer = (ViewGroup) findViewById(R.id.main_ui_container);

		final FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_container);
		if (fragment != null) {
			// If we're being re-created and have a fragment, we need to a) hide
			// the main UI controls and
			// b) hook up its listeners again.
			controlsContainer.setVisibility(View.GONE);
			if (fragment instanceof FriendPickerFragment) {
				setFriendPickerListeners((FriendPickerFragment) fragment);
			} else if (fragment instanceof PlacePickerFragment) {
				setPlacePickerListeners((PlacePickerFragment) fragment);
			}
		}

		// Listen for changes in the back stack so we know if a fragment got
		// popped off because the user
		// clicked the back button.
		fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				if (fm.getBackStackEntryCount() == 0) {
					// We need to re-show our UI.
					controlsContainer.setVisibility(View.VISIBLE);
				}
			}
		});

		canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG);

		/**
		 * 클릭 리스너를 지정합니다.
		 */

		findViewById(R.id.login_btn).setOnClickListener(onClickListener);
		findViewById(R.id.join_btn).setOnClickListener(onClickListener);

		/**
		 * App Init Api 를 호출합니다.
		 */

		DoRequestAppInit();

	}

	/**
	 * Json Load
	 */

	private void DoRequestAppInit() {

		ApplicationManager am = (ApplicationManager) this
				.getApplicationContext();
		am.setOnJsonLoadingCompletionListener(onJsonLoadingCompletionListener);

		AppInitApi appInitApi = new AppInitApi();

		/*
		 * OS_version �� �����ɴϴ�.
		 */

		String os_version = android.os.Build.VERSION.RELEASE;

		/*
		 * �� ���� ������ �����ɴϴ�.
		 */
		String app_version = "";

		try {
			PackageInfo i = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			app_version = i.versionName;
		} catch (NameNotFoundException e) {
		}

		Log.d("app_version", app_version);

		/*
		 * UUID�� �����ɴϴ�.
		 */

		String uuid = Settings.Secure.getString(getContentResolver(),
				"android_id");

		uuid = SHA256_s(uuid);

		/*
		 * App init API �� ��ǲ���� �ֽ��ϴ�.
		 */

		appInitApi.setInput(os_version, app_version, uuid);

		am.addJsonLoadingTask(appInitApi);

	}

	private ApplicationManager.OnJsonLoadingCompletionListener onJsonLoadingCompletionListener = new ApplicationManager.OnJsonLoadingCompletionListener() {
		@Override
		public void onJsonLoadingCompletion(ArrayList<Model> models,
				boolean isCompleted) {
			if (isCompleted) {
				Login login = (Login) models.get(0);
				mLogin_nonce = login.getLogin_nonce();
			} else {
				Toast.makeText(getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

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
		case R.id.join_btn: {
			Intent intent = new Intent(this, SUpActivity.class);
			intent.putExtra("login_nonce", mLogin_nonce);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			break;
		}
		case R.id.login_btn:
			attemptLogin();
			break;
		default:
			break;
		}
	};

	/**
	 * Related to FB Login
	 */

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

		updateUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

		outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (pendingAction != PendingAction.NONE
				&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(SInActivity.this)
					.setTitle(R.string.cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(R.string.ok, null).show();
			pendingAction = PendingAction.NONE;
		} else if (state == SessionState.OPENED_TOKEN_UPDATED) {
			handlePendingAction();
		}
		updateUI();
	}

	private void updateUI() {
		Session session = Session.getActiveSession();

		boolean enableButtons = (session != null && session.isOpened());

		postStatusUpdateButton.setEnabled(enableButtons
				|| canPresentShareDialog);
		postPhotoButton.setEnabled(enableButtons);
		pickFriendsButton.setEnabled(enableButtons);
		pickPlaceButton.setEnabled(enableButtons);

		if (enableButtons && user != null) {

			profilePictureView.setProfileId(user.getId());
			greeting.setText(getString(R.string.hello_user, user.getFirstName()));
		} else {
			profilePictureView.setProfileId(null);
			greeting.setText(null);
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but
		// we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
		case POST_PHOTO:
			postPhoto();
			break;
		case POST_STATUS_UPDATE:
			postStatusUpdate();
			break;
		}
	}

	private interface GraphObjectWithId extends GraphObject {
		String getId();
	}

	private void showPublishResult(String message, GraphObject result,
			FacebookRequestError error) {
		String title = null;
		String alertMessage = null;
		if (error == null) {
			title = getString(R.string.success);
			String id = result.cast(GraphObjectWithId.class).getId();
			alertMessage = getString(R.string.successfully_posted_post,
					message, id);
		} else {
			title = getString(R.string.error);
			alertMessage = error.getErrorMessage();
		}

		new AlertDialog.Builder(this).setTitle(title).setMessage(alertMessage)
				.setPositiveButton(R.string.ok, null).show();
	}

	private void onClickPostStatusUpdate() {
		performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
	}

	private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
		return new FacebookDialog.ShareDialogBuilder(this)
				.setName("Hello Facebook")
				.setDescription(
						"The 'Hello Facebook' sample application showcases simple Facebook integration")
				.setLink("http://developers.facebook.com/android");
	}

	private void postStatusUpdate() {
		if (canPresentShareDialog) {
			FacebookDialog shareDialog = createShareDialogBuilder().build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
		} else if (user != null && hasPublishPermission()) {
			final String message = getString(R.string.status_update,
					user.getFirstName(), (new Date().toString()));
			Request request = Request.newStatusUpdateRequest(
					Session.getActiveSession(), message, place, tags,
					new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							showPublishResult(message,
									response.getGraphObject(),
									response.getError());
						}
					});
			request.executeAsync();
		} else {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}

	private void onClickPostPhoto() {
		performPublish(PendingAction.POST_PHOTO, false);
	}

	private void postPhoto() {
		if (hasPublishPermission()) {
			Bitmap image = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.icon);
			Request request = Request.newUploadPhotoRequest(
					Session.getActiveSession(), image, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							showPublishResult(getString(R.string.photo_post),
									response.getGraphObject(),
									response.getError());
						}
					});
			request.executeAsync();
		} else {
			pendingAction = PendingAction.POST_PHOTO;
		}
	}

	private void showPickerFragment(PickerFragment<?> fragment) {
		fragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
			@Override
			public void onError(PickerFragment<?> pickerFragment,
					FacebookException error) {
				String text = getString(R.string.exception, error.getMessage());
				Toast toast = Toast.makeText(SInActivity.this, text,
						Toast.LENGTH_SHORT);
				toast.show();
			}
		});

		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().replace(R.id.fragment_container, fragment)
				.addToBackStack(null).commit();

		controlsContainer.setVisibility(View.GONE);

		// We want the fragment fully created so we can use it immediately.
		fm.executePendingTransactions();

		fragment.loadData(false);
	}

	private void onClickPickFriends() {
		final FriendPickerFragment fragment = new FriendPickerFragment();

		setFriendPickerListeners(fragment);

		showPickerFragment(fragment);
	}

	private void setFriendPickerListeners(final FriendPickerFragment fragment) {
		fragment.setOnDoneButtonClickedListener(new FriendPickerFragment.OnDoneButtonClickedListener() {
			@Override
			public void onDoneButtonClicked(PickerFragment<?> pickerFragment) {
				onFriendPickerDone(fragment);
			}
		});
	}

	private void onFriendPickerDone(FriendPickerFragment fragment) {
		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack();

		String results = "";

		List<GraphUser> selection = fragment.getSelection();
		tags = selection;
		if (selection != null && selection.size() > 0) {
			ArrayList<String> names = new ArrayList<String>();
			for (GraphUser user : selection) {
				names.add(user.getName());
			}
			results = TextUtils.join(", ", names);
		} else {
			results = getString(R.string.no_friends_selected);
		}

		showAlert(getString(R.string.you_picked), results);
	}

	private void onPlacePickerDone(PlacePickerFragment fragment) {
		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack();

		String result = "";

		GraphPlace selection = fragment.getSelection();
		if (selection != null) {
			result = selection.getName();
		} else {
			result = getString(R.string.no_place_selected);
		}

		place = selection;

		showAlert(getString(R.string.you_picked), result);
	}

	private void onClickPickPlace() {
		final PlacePickerFragment fragment = new PlacePickerFragment();
		fragment.setLocation(SEATTLE_LOCATION);
		fragment.setTitleText(getString(R.string.pick_seattle_place));

		setPlacePickerListeners(fragment);

		showPickerFragment(fragment);
	}

	private void setPlacePickerListeners(final PlacePickerFragment fragment) {
		fragment.setOnDoneButtonClickedListener(new PlacePickerFragment.OnDoneButtonClickedListener() {
			@Override
			public void onDoneButtonClicked(PickerFragment<?> pickerFragment) {
				onPlacePickerDone(fragment);
			}
		});
		fragment.setOnSelectionChangedListener(new PlacePickerFragment.OnSelectionChangedListener() {
			@Override
			public void onSelectionChanged(PickerFragment<?> pickerFragment) {
				if (fragment.getSelection() != null) {
					onPlacePickerDone(fragment);
				}
			}
		});
	}

	private void showAlert(String title, String message) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setPositiveButton(R.string.ok, null).show();
	}

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null
				&& session.getPermissions().contains("publish_actions");
	}

	private void performPublish(PendingAction action, boolean allowNoSession) {
		Session session = Session.getActiveSession();
		if (session != null) {
			pendingAction = action;
			if (hasPublishPermission()) {
				// We can do the action right away.
				handlePendingAction();
				return;
			} else if (session.isOpened()) {
				// We need to get new permissions, then complete the action when
				// we get called back.
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
						this, PERMISSION));
				return;
			}
		}

		if (allowNoSession) {
			pendingAction = action;
			handlePendingAction();
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		/*
		 * Reset errors.
		 */
		mEmailEt.setError(null);
		mPasswordEt.setError(null);

		/*
		 * Store values at the time of the login attempt.
		 */
		mEmail = mEmailEt.getText().toString();
		mPassword = mPasswordEt.getText().toString();

		boolean cancel = false;
		View focusView = null;

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

			/**
			 * Show a progress spinner, and kick off a background task to
			 * perform the user login attempt.
			 */
			showProgress(true);

			doRequestLogin();

		}
	}

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

		prefs_editor = user_prefs.edit();
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

		mPassword = SHA256_s(mLogin_nonce + SHA256_s(mPassword + mEmail));

		loginApi.setInput("email", mLogin_nonce, null, mEmail, mPassword);

		/*
		 * 
		 */

		am.addJsonLoadingTask(loginApi);

	}

	private void doRequestFbLogin(String access_token, String user_id) {

		/*
		 * 
		 */

		prefs_editor = user_prefs.edit();
		prefs_editor.putString("user_name", user.getName());
		prefs_editor.putString("user_id", user.getId());
		prefs_editor.putString("user_email", user.getLink());
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

		loginApi.setInput("fb", mLogin_nonce, null, access_token,
				SHA256_s(mLogin_nonce + user_id));

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

				showProgress(true);

				/*
				 * 성공하면 로딩창을 제거하고 메인 액티비티로 들어갑니다.
				 */

				Intent MainIntent = new Intent(getApplicationContext(),
						XActivity.class);
				startActivity(MainIntent);

				Toast.makeText(getApplication(), "로그인에 성공하셨습니다.",
						Toast.LENGTH_SHORT).show();

				/*
				 * 
				 */
				Session session = Session.getActiveSession();
				boolean enable = (session != null && session.isOpened());

				if (enable)

					loginButton.requestFbLogout();

				/*
				 * 
				 */

				if (dialog != null) {
					dialog.cancel();
					dialog = null;
				}

				finish();

			} else {

				showProgress(false);

				Toast.makeText(getApplication(), "로그인에 실패하셨습니다.",
						Toast.LENGTH_SHORT).show();
				Toast.makeText(getApplicationContext(),
						"인터넷 연결이 불안정 합니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

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

}
