package kr.photox.android.api;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.photox.android.model.Mission;
import kr.photox.android.model.Place;

public class CheckinListApi extends ApiBase {
    private final static String TAG = "Main List Api";

    /**
     * Init
     */
    public CheckinListApi(Application application, int offset, int count) {

        /*

         */
        ApiRequestController rc = new ApiRequestController("checkin/list", "GET", "http");
        rc.addHeader("sessionkey", getSessionKeyInPrefs(application));
        rc.addBodyValue("offset", Integer.toString(offset));
        rc.addBodyValue("count", Integer.toString(count));
        rc.doRequest();
        response = rc.getResponse_body();
        Log.d(TAG, response);
    }

    /**
     * @return
     */
    public ArrayList<Place> getResult() {
        ArrayList<Place> places = new ArrayList<Place>();

        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jsonArray = null;

            jsonArray = jsonObj.getJSONArray("checkins");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Place place = new Place();
                Mission mission = new Mission();

                if (!obj.isNull("img_url")) {
                    String img_url = obj.getString("img_url");
                    mission.setImg_url(img_url);
                }

                if (!obj.isNull("mission_title")) {
                    String title = obj.getString("mission_title");
                    mission.setTitle(title);
                }

                if (!obj.isNull("comment")) {
                    String comment = obj.getString("comment");
                    mission.setComment(comment);
                }

                if (!obj.isNull("place")) {
                    JSONObject placeObj = obj.getJSONObject("place");
                    place.setId(placeObj.getInt("id"));
                    place.setTitle(placeObj.getString("title"));
                    place.setCategory(placeObj.getInt("category"));
                }

                if (!obj.isNull("score")) {
                    int score = obj.getInt("score");
                    mission.setScore(score);
                }
                place.setMission(mission);
                places.add(place);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            places = null;
        }

        return places;


    }


}
