package kr.photox.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Locale;

public class LoginFacebookFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     *
     */


    private LoginButton loginButton;


    /**
     *
     */

    private GraphUser user;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LoginFacebookFragment newInstance(int sectionNumber) {
        LoginFacebookFragment fragment = new LoginFacebookFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     */
    public LoginFacebookFragment() {

    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);

        /**
         * FB Login It's possible that we were waiting for this.user to // be
         * populated in order to post a // status update.
         */

        loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton
                .setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
                    @Override
                    public void onUserInfoFetched(GraphUser user) {
                        LoginFacebookFragment.this.user = user;
                        // updateUI();
                        // handlePendingAction();

                        if (user != null) {
                            // showProgress(true);
                            Session session = Session.getActiveSession();
                            requestFacebookLogin(session.getAccessToken(),
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


        /**
         *
         */
        return rootView;
    }

    /**
     *
     * @param access_token
     * @param user_id
     */
    private void requestFacebookLogin(String access_token, String user_id) {

        Toast.makeText(getActivity(), access_token, Toast.LENGTH_SHORT).show();

		/*
		 *
		 */
        /*
        prefs_editor = user_prefs.edit();
        prefs_editor.putString("user_name", user.getName());
        prefs_editor.putString("user_id", user.getId());
        prefs_editor.putString("user_email", user.getLink());
        prefs_editor.commit();

		/*
		 *
		 */
/*
        ApplicationManager am = (ApplicationManager) this
                .getApplicationContext();
        am.setOnJsonLoadingCompletionListener(onJoinRequestLoadingCompletionListener);
        LoginApi loginApi = new LoginApi();

		/*
		 * Set Input in APi Manager.
		 */

        //loginApi.setInput("fb", mLogin_nonce, null, access_token,
        //        SHA256_s(mLogin_nonce + user_id));


    }

}
