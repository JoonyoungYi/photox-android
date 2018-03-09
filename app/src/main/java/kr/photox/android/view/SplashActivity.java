package kr.photox.android.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import kr.photox.android.R;
import kr.photox.android.api.ApiBase;
import kr.photox.android.api.LoginApi;
import kr.photox.android.utils.Argument;

public class SplashActivity extends FragmentActivity {
    private static final String TAG = "Splash Activity";

    /**
     *
     */


    LoginApiTask mLoginApiTask = null;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        mLoginApiTask = new LoginApiTask();
        mLoginApiTask.execute();

    }

    /**
     *
     */
    public class LoginApiTask extends AsyncTask<Void, Void, Void> {
        private int request_code = Argument.REQUEST_CODE_UNEXPECTED;

        /**
         *
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Void... params) {

            try {
                LoginApi loginApi = new LoginApi(getApplication(), "auto", null);
                request_code = loginApi.getRequestCode();
                if (request_code  == Argument.REQUEST_CODE_SUCCESS){
                    loginApi.getResult();
                }

            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            mLoginApiTask = null;
            ApiBase.showToastMsg(getApplication(), request_code);

            if (request_code == Argument.REQUEST_CODE_SUCCESS) {
                Intent intent = new Intent(SplashActivity.this, XActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);

            } else if (request_code == Argument.REQUEST_CODE_FAIL){
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }

            finish();
        }

        @Override
        protected void onCancelled() {
            mLoginApiTask = null;
            //ApiBase.showToastMsg(getApplication(), request_code);
        }
    }

    @Override
    public void onDestroy() {
        if (mLoginApiTask != null) {
            mLoginApiTask.cancel(true);
        }

        super.onDestroy();
    }
}
