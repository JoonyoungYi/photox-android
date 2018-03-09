package kr.photox.android.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import kr.photox.android.R;
import kr.photox.android.api.ApiBase;
import kr.photox.android.api.LoginApi;
import kr.photox.android.utils.Argument;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    /**
     *
     */
    private UiLifecycleHelper uiHelper;
    private LoginApiTask mLoginApiTask = null;

    /**
     *
     */
    public LoginFragment() {
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);

        LoginButton authButton = (LoginButton) rootView.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("basic_info", "user_about_me", "user_checkins"));

        rootView.findViewById(R.id.temp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogin();
            }
        });

        return rootView;
    }

    /**
     *
     */

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);

        }


        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
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

        if (mLoginApiTask != null) {
            mLoginApiTask.cancel(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {

        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    /**
     *
     */
    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this)
                    .setPermissions(Arrays.asList("user_checkins", "user_about_me", "basic_info"))
                    .setCallback(statusCallback));
        } else {
            Session.openActiveSession(getActivity(), this, true, statusCallback);
        }
    }

    private Session.StatusCallback statusCallback =
            new SessionStatusCallback();

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {

            if (session.getState() == SessionState.OPENED) {
                //Toast.makeText(getActivity(), session.getAccessToken(), Toast.LENGTH_SHORT).show();

                mLoginApiTask = new LoginApiTask();
                mLoginApiTask.execute(session.getAccessToken());

            }


        }
    }


    /**
     * Login Api Task
     */
    public class LoginApiTask extends AsyncTask<String, Void, Void> {
        int request_code = Argument.REQUEST_CODE_UNEXPECTED;

        /**
         *
         * @param access_token
         * @return
         */
        @Override
        protected Void doInBackground(String... access_token) {

            /*

             */
            try {
                LoginApi loginApi = new LoginApi(getActivity().getApplication(), "fb", access_token[0]);
                request_code = loginApi.getRequestCode();
                if (request_code == Argument.REQUEST_CODE_SUCCESS)
                    loginApi.getResult();

            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            mLoginApiTask = null;
            ApiBase.showToastMsg(getActivity().getApplication(), request_code);

            /*

             */
            if (request_code == Argument.REQUEST_CODE_SUCCESS) {
                LoginActivity loginActivity = (LoginActivity) getActivity();
                loginActivity.startXActivity();

            } else if (request_code == Argument.REQUEST_CODE_FAIL) {
                //mAuthKeyEt.setError(getString(R.string.error_invalid_auth_key));
                //mAuthKeyEt.requestFocus();
            }

        }

        @Override
        protected void onCancelled() {
            mLoginApiTask = null;
            ApiBase.showToastMsg(getActivity().getApplication(), request_code);
            //showProgress(false);
        }
    }


}

