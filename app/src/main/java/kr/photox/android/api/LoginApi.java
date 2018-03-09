package kr.photox.android.api;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import kr.photox.android.utils.Argument;

public class LoginApi extends ApiBase {
    private final static String TAG = "Login Api";

    /**
     * @param application
     * @param login_type
     * @param login_key
     */
    public LoginApi(Application application, String login_type, String login_key) {
        this.application = application;

        //
        if (login_type.equals("auto")) {
            login_key = getStringInPrefs(application, Argument.PREFS_AUTO_KEY, null);
            Log.d(TAG, "login_key: " + login_key);
        }

        //
        if ((!login_type.equals("auto") && !login_type.equals("fb")) || ( login_type.equals("fb") && login_key == null)) {
            Log.d(TAG, "error");
            Log.d(TAG, "login_key: " + login_key);
            return;
        }

        //
        ApiRequestController rc = new ApiRequestController("login", "POST", "http");
        rc.addBodyValue("login_type", login_type);
        rc.addBodyValue("login_key", login_key);
        rc.doRequest();

        //
        response = rc.getResponse_body();
        Log.d(TAG, response);

    }

    /**
     * 결과를 반환합니다.
     */

    public void getResult() {

        try {
            JSONObject jsonObj = new JSONObject(response);

            if (!jsonObj.isNull("session_key")) {
                String session_key = jsonObj.getString("session_key");
                setString2Prefs(application, Argument.PREFS_SESSION_KEY, session_key);
            }

            if (!jsonObj.isNull("auto_key")) {
                String auto_key = jsonObj.getString("auto_key");
                setString2Prefs(application, Argument.PREFS_AUTO_KEY, auto_key);
                Log.d(TAG, "login_key: " + auto_key);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
