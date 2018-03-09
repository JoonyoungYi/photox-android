package kr.photox.android.api;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.photox.android.model.Mission;
import kr.photox.android.model.Place;

public class PlaceDetailApi extends ApiBase {
    private final static String TAG = "Place List Api";

    /**
     * Init
     */
    public PlaceDetailApi(Application application, int id) {

        /*

         */
        ApiRequestController rc = new ApiRequestController("place/detail", "GET", "http");
        rc.addHeader("sessionkey", getSessionKeyInPrefs(application));
        rc.addBodyValue("id", Integer.toString(id));
        rc.doRequest();

        response = rc.getResponse_body();
    }

    /**
     * @return
     */
    public ArrayList<Mission> getResult() {
        ArrayList<Mission> missions = new ArrayList<Mission>();

        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jsonArray = null;

            jsonArray = jsonObj.getJSONArray("missions");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Mission mission = new Mission();

                if (!obj.isNull("id")) {
                    int id = obj.getInt("id");
                    mission.setId(id);
                }

                if (!obj.isNull("title")) {
                    String title = obj.getString("title");
                    mission.setTitle(title);
                }

                if (!obj.isNull("score")) {
                    int score = obj.getInt("score");
                    mission.setScore(score);
                }

                missions.add(mission);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            missions = null;
        }

        return missions;


    }


}
