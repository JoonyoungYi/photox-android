package kr.photox.android.model;


import kr.photox.android.R;

public class Place {

	private int id;
    private String title;
    private int category;
    private int total_mission_count;

    private float latitude;
    private float longitude;

    /**
	 * Init
	 */

	public Place() {

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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        if (category > 11 || category <= 0)
            this.category = 0;
        else
            this.category = category;
    }

    public int getTotal_mission_count() {
        return total_mission_count;
    }

    public void setTotal_mission_count(int total_mission_count) {
        this.total_mission_count = total_mission_count;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Static Data
     */

    public static final int[] ARRAY_CATEGORY_DRAWABLE = {
            R.drawable.category_00,
            R.drawable.category_01,
            R.drawable.category_02,
            R.drawable.category_03,
            R.drawable.category_04,
            R.drawable.category_05,
            R.drawable.category_06,
            R.drawable.category_07,
            R.drawable.category_08,
            R.drawable.category_09,
            R.drawable.category_10,
            R.drawable.category_11
    };

    public static final String[] ARRAY_CATEGORY_NAME = {
            "미분류",
            "바",
            "학교",
            "카페",
            "산책로",
            "시장",
            "자연경관(자전거)",
            "빌딩",
            "버스정류장",
            "박물관",
            "마을",
            "영화관"
    };

}
