package kr.photox.android.deprecated;

import kr.photox.android.R;
import kr.photox.android.manager.ProtocolController;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class TempFragment extends SherlockFragment {

	JSONObject content_json;

	/**
	 * On Create View
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.x_todo_fragment, container, false);

		content_json = new JSONObject();
		try {
			content_json.put("os_type", 1);
			content_json.put("os_version", "ios6.0.1.1993");
			content_json.put("app_version", "1.0.0.1");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		/**
		 * Http Request Test
		 */

		String url_x = "pache.clude.kr/test/http/get";
		new HttpRequestTask().execute(url_x);

		/**
		 * Https Request Test
		 */

		String url_s = "pache.clude.kr/test/https/get";
		new HttpsRequestTask().execute(url_s);

		/**
		 * Httpa Request Test
		 */

		String url_a = "pache.clude.kr/test/httpa/get";
		new HttpaRequestTask().execute(url_a);

		/**
		 * 
		 * Temp
		 */

		// String session_key = "asdfasdfasdfasdf";
		// String token = "asdasdasdasd";

		// String json = "json { session_key: " + session_key + ", token: "
		// + token + ",  body: " + content + ". location : null }";

		// try {
		// String json_hash = SHA256_s(json);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		return v;
	}

	/**
	 * Http Request Test
	 * 
	 * @author Helena
	 * 
	 */

	public class HttpRequestTask extends AsyncTask<String, String, String> {

		protected String doInBackground(String... url) {
			String line = null;

			ProtocolController mGetStringFromUrl = new ProtocolController(
					url[0], "GET", "http");

			mGetStringFromUrl.setContent(content_json);
			mGetStringFromUrl.doRequest();

			line = mGetStringFromUrl.getResult();

			return line;
		}

		protected void onPostExecute(String line) {
			Log.d("http result", line);
		}
	}

	/**
	 * HttpS Request Test
	 * 
	 * @author Helena
	 * 
	 */

	public class HttpsRequestTask extends AsyncTask<String, String, String> {

		protected String doInBackground(String... url) {
			String line = null;

			ProtocolController mGetStringFromUrl = new ProtocolController(
					url[0], "GET", "https");
			mGetStringFromUrl.setContent(content_json);
			mGetStringFromUrl.doRequest();

			line = mGetStringFromUrl.getResult();

			return line;
		}

		protected void onPostExecute(String line) {
			Log.d("https result", line);
		}
	}

	/**
	 * Httpa Request Test
	 * 
	 * @author Helena
	 * 
	 */

	public class HttpaRequestTask extends AsyncTask<String, String, String> {

		protected String doInBackground(String... url) {
			String line = null;

			ProtocolController mGetStringFromUrl = new ProtocolController(
					url[0], "GET", "httpa");
			mGetStringFromUrl.setSecureKey("asdfasdfasdfasdf", "asdasdasdasd",
					"0123456789012345", "0123456789012345");
			try {
				mGetStringFromUrl.setContent(content_json);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mGetStringFromUrl.doRequest();

			line = mGetStringFromUrl.getResult();

			return line;
		}

		protected void onPostExecute(String line) {
			Log.d("httpa result", line);

		}
	}

}
