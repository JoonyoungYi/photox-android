package kr.photox.android.api;

import org.json.JSONObject;

public class LoginApi {
    private final static String TAG = "Login Api";

    /**
     *
     */
    private String login_type = "";
    private String login_key = "";

	/**
	 * Init
	 */

	public LoginApi(String login_type, String login_key) {
        this.login_type = login_type;
        this.login_key = login_key;
	}

    /**
     *
     * @return
     */
	public boolean getResult() {

        //
        if (!login_type.equals("auto") && !login_type.equals("fb"))
            return false;
        if (login_key.equals(""))
            return false;

        RequestController rc = new RequestController("login", "POST", "http");
        rc.addBodyValue("login_type", this.login_type);
        rc.addBodyValue("login_key", this.login_key);
        rc.doRequest();

        return convertStr2RstBoolean(rc.getResponse_body());
	}

    /**
     *
     * @param rst
     * @return
     */
    private boolean convertStr2RstBoolean(String rst){
        try {
            String status = ((new JSONObject(rst)).getJSONObject("result")).getString("status");
            if (status.equals("ok"))
                return true;

        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
	
	
}
