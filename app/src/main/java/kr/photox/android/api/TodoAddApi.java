package kr.photox.android.api;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.photox.android.model.Mission;
import kr.photox.android.model.Place;

public class TodoAddApi extends ApiBase {
    private final static String TAG = "Todo Add Api";

    /**
     * Init
     */
    public TodoAddApi(Application application, int mission_id) {

        /*

         */
        ApiRequestController rc = new ApiRequestController("todo/add", "POST", "http");
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
