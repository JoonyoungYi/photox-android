package kr.photox.android.model;


import java.util.ArrayList;

import kr.photox.android.R;

public class Mission {

    private int id;
    private String title;
    private String comment;
    private int score;
    private String img_url;

    private String description;
    private int[] ratings = new int[5];
    private int rating_my = 0;

    private ArrayList<String> img_urls = new ArrayList<String>();
    private boolean is_todo;

    private Place place = null;

    private ArrayList<Campaign> campaigns = new ArrayList<Campaign>();


    /**
     * Init
     */

    public Mission() {

    }

    /**
     * Getter and Setter
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int[] getRatings() {
        return ratings;
    }

    public void setRatings(int[] ratings) {
        this.ratings = ratings;
    }

    public int getRating_my() {
        return rating_my;
    }

    public void setRating_my(int rating_my) {
        this.rating_my = rating_my;
    }

    public ArrayList<String> getImg_urls() {
        return img_urls;
    }

    public void setImg_urls(ArrayList<String> img_urls) {
        this.img_urls = img_urls;
    }

    public boolean isIs_todo() {
        return is_todo;
    }

    public void setIs_todo(boolean is_todo) {
        this.is_todo = is_todo;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public ArrayList<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(ArrayList<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

}
