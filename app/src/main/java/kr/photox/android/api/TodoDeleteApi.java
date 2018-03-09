package kr.photox.android.api;

import android.app.Application;
import android.util.Log;

public class TodoDeleteApi extends ApiBase {
    private final static String TAG = "Todo Add Api";

    /**
     * Init
     */
    public TodoDeleteApi(Application application, int mission_id) {

        /*

         */
        ApiRequestController rc = new ApiRequestController("todo/delete", "POST", "http");
        rc.addHeader("sessionkey", getSessionKeyInPrefs(application));
        rc.addBodyValue("mission_id", Integer.toString(mission_id));
        rc.doRequest();
        response = rc.getResponse_body();
        Log.d(TAG, response);
    }

    /**
     * @return
     */
    public void getResult() {

    }


}
