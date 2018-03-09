package kr.photox.android.api;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.photox.android.model.Place;

public class MainListApi extends ApiBase {
    private final static String TAG = "Main List Api";

    /**
     * Init
     *
     */
    public MainListApi(Application application, int offset, int count, String ftag) {

        /*

         */
        ApiRequestController rc = new ApiRequestController("main/list", "GET", "http");
        rc.addHeader("sessionkey", getSessionKeyInPrefs(application));
        rc.addBodyValue("offset", Integer.toString(offset));
        rc.addBodyValue("count", Integer.toString(count));
        rc.addBodyValue("ftag", ftag);
        rc.doRequest();

        response = rc.getResponse_body();
    }

    /**
     * @return
     */
    public ArrayList<Place> getResult() {
        ArrayList<Place> places = new ArrayList<Place>();

        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jsonArray = null;

            jsonArray = jsonObj.getJSONArray("places");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Place place = new Place();

                if (!obj.isNull("id")) {
                    int id = obj.getInt("id");
                    place.setId(id);
                }

                if (!obj.isNull("title")) {
                    String title = obj.getString("title");
                    place.setTitle(title);
                }

                if (!obj.isNull("category")) {
                    int category = obj.getInt("category");
                    place.setCategory(category);
                }

                if (!obj.isNull("total_mission_count")) {
                    int total_mission_count = obj.getInt("total_mission_count");
                    place.setTotal_mission_count(total_mission_count);
                }

                if (!obj.isNull("location")) {
                    JSONObject location = obj.getJSONObject("location");

                    place.setLatitude(location.getDouble("latitude"));
                    place.setLongitude(location.getDouble("longitude"));

                }

                places.add(place);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            places = null;
        }

        return places;


    }


}
