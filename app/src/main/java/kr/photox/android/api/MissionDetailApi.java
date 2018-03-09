package kr.photox.android.api;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.photox.android.model.Campaign;
import kr.photox.android.model.Mission;
import kr.photox.android.model.Place;

public class MissionDetailApi extends ApiBase {
    private final static String TAG = "Mission Detail Api";

    /**
     * Init
     */
    public MissionDetailApi(Application application, int id) {

        /*

         */
        ApiRequestController rc = new ApiRequestController("mission/detail", "GET", "http");
        rc.addHeader("sessionkey", getSessionKeyInPrefs(application));
        rc.addBodyValue("id", Integer.toString(id));
        rc.doRequest();

        response = rc.getResponse_body();
    }

    /**
     * @return
     */
    public Mission getResult() {
        Mission mission = new Mission();

        try {
            JSONObject jsonObj = new JSONObject(response);

            if (!jsonObj.isNull("title")) {
                String title = jsonObj.getString("title");
                mission.setTitle(title);
            }

            if (!jsonObj.isNull("description")) {
                String description = jsonObj.getString("description");
                mission.setDescription(description);
            }

            if (!jsonObj.isNull("img_urls")) {
                ArrayList<String> img_urls = new ArrayList<String>();
                JSONArray imgArr = jsonObj.getJSONArray("img_urls");
                for (int i = 0; i < imgArr.length(); i++) {
                    JSONObject imgObj = imgArr.getJSONObject(i);
                    String url = imgObj.getString("url");
                    img_urls.add(url);
                }
                mission.setImg_urls(img_urls);
            }

            if (!jsonObj.isNull("score")) {
                int score = jsonObj.getInt("score");
                mission.setScore(score);
            }

            if (!jsonObj.isNull("my_rating")) {
                int my_rating = jsonObj.getInt("my_rating");
                mission.setRating_my(my_rating);
            }

            if (!jsonObj.isNull("ratings")) {
                int[] ratings = new int[5];
                JSONObject ratingsObj = jsonObj.getJSONObject("ratings");
                for (int i = 0; i < 5; i++) {
                    ratings[i] = ratingsObj.getInt(Integer.toString(i + 1));
                }
                mission.setRatings(ratings);
            }

            if (!jsonObj.isNull("is_todo")) {
                boolean is_todo = jsonObj.getBoolean("is_todo");
                mission.setIs_todo(is_todo);
            }

            if (!jsonObj.isNull("location")) {
                JSONObject locationObj = jsonObj.getJSONObject("location");
                Log.d(TAG, "location is NOT null");

                if (!locationObj.isNull("latitude") && !locationObj.isNull("longitude")) {
                    Log.d(TAG, "latitude and longitude is not null");
                    Place place = new Place();
                    place.setLatitude(locationObj.getDouble("latitude"));
                    place.setLongitude(locationObj.getDouble("longitude"));
                    mission.setPlace(place);
                }

            }

            if (!jsonObj.isNull("campaigns")) {
                JSONArray campaignArr = jsonObj.getJSONArray("campaigns");
                ArrayList<Campaign> campaigns = new ArrayList<Campaign>();

                for (int i = 0; i < campaignArr.length(); i++) {
                    JSONObject campaingObj = campaignArr.getJSONObject(i);
                    Campaign campaign = new Campaign();
                    campaign.setTitle(campaingObj.getString("title"));
                    campaign.setIcon_url(campaingObj.getString("icon_url"));
                }

                mission.setCampaigns(campaigns);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            mission = null;
        }

        return mission;


    }


}
