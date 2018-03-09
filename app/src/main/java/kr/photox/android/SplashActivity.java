package kr.photox.android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SplashActivity extends FragmentActivity {

    /**
     *
     */
    private static final String ARG_PREFS = "user_info";
    private static final String ARG_PREFS_AUTO_KEY = "auto_key";

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        /*
            일단, 오토키가 있는지 확인한다. 오토키가 없으면 LoginActivity에 IntroFragment의 시작점으로 넘어간다.
            오토키가 있으면, 로그인을 시도한다. 로그인에 성공하면 기본페이지로 넘어가고, 로그인에 실패하면 LoginActivity에 LoginFragment로 넘어간다.
         */
        String auto_key = getSharedPreferences(ARG_PREFS, Context.MODE_PRIVATE).getString(ARG_PREFS_AUTO_KEY, "");
        if (auto_key.equals("")) {
            new LoginAutoApiTask().execute(auto_key);

        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.fade_out);
        }

    }

    /**
     *
     */
    public class LoginAutoApiTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... auto_key) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }

            if (Math.random() > 0.5)
                return true;

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Intent intent = new Intent(SplashActivity.this, XActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);

            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }
            finish();
        }

        @Override
        protected void onCancelled() {
        }
    }

}
