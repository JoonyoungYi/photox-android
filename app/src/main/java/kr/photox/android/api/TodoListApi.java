package kr.photox.android.api;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.photox.android.model.Mission;
import kr.photox.android.model.Place;

public class TodoListApi extends ApiBase {
    private final static String TAG = "Main List Api";

    /**
     * Init
     */
    public TodoListApi(Application application) {

        /*

         */
        ApiRequestController rc = new ApiRequestController("todo/list", "GET", "http");
        rc.addHeader("sessionkey", getSessionKeyInPrefs(application));
        rc.doRequest();
        response = rc.getResponse_body();
        Log.d(TAG, response);
    }

    /**
     * @return
     */
    public ArrayList<Mission> getResult() {
        ArrayList<Mission> missions = new ArrayList<Mission>();

        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jsonArray = null;

            jsonArray = jsonObj.getJSONArray("todos");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                obj = obj.getJSONObject("mission");

                Mission mission = new Mission();

                if (!obj.isNull("id")) {
                    int id = obj.getInt("id");
                    mission.setId(id);
                }

                if (!obj.isNull("title")) {
                    String title = obj.getString("title");
                    mission.setTitle(title);
                }

                if (!obj.isNull("place")) {
                    JSONObject placeObj = obj.getJSONObject("place");
                    Place place = new Place();
                    place.setTitle(placeObj.getString("title"));
                    place.setCategory(placeObj.getInt("category"));
                    mission.setPlace(place);
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
