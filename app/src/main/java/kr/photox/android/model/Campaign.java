package kr.photox.android.model;


import kr.photox.android.R;

public class Campaign {

    private String title;
    private String icon_url;


    /**
     * Init
     */

    public Campaign() {

    }

    /**
     * Getter and Setter
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }


}
