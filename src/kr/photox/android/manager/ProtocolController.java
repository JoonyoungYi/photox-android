package kr.photox.android.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Base64;

/**
 * 프로토콜 컨트롤러는 철저히 스트링만 아웃풋으로 보내줍니다.
 * 
 * @author yearnning
 * 
 */

public class ProtocolController {
	private final String TAG = "Protocol Controller";

	/**
	 * 
	 */

	private String url;
	private String url_protocol = "http://";
	private String url_base = "xapi.photox.kr/";

	private String result;
	private String type;

	private String method;

	private List<NameValuePair> params = new LinkedList<NameValuePair>();

	/**
	 * 
	 */

	private String session_key;
	private String token;
	private String enc_key;
	private String enc_iv;
	private Location current_location = null;

	/**
	 * Protocol Controller 초기화
	 * 
	 */

	public ProtocolController(String url, String method, String type) {

		if (type == "http") {
			this.params.add(new BasicNameValuePair("type", "http"));

		} else if (type == "https") {
			this.params.add(new BasicNameValuePair("type", "https"));
			this.url_protocol = "https://";

			// Log.d(TAG, "type" + "https");

		} else if (type == "httpa") {
			this.params.add(new BasicNameValuePair("type", "httpa"));

		}

		if (method == "GET") {
			if (!url.endsWith("?")) {
				url += "?";
			}
		}

		this.type = type;
		this.url = url_protocol + url_base + url;
		this.method = method;

	}

	/**
	 * Set Additional Data
	 * 
	 */

	public void setAdditionalData(String name, String value) {
		this.params.add(new BasicNameValuePair(name, value));

	}

	/**
	 * Set Location
	 * 
	 */

	public void setLocation(Location current_location) {
		this.current_location = current_location;
	}

	/**
	 * Set Content
	 * 
	 * @throws Exception
	 * 
	 */

	public void setContent(JSONObject object) {

		if (type == "httpa") {

			/**
			 * content ��ȣȭ�ؼ� ���
			 */

			String value_AES;
			try {
				value_AES = AES_s(MakeJsonToString(object));
				// Log.d(TAG, "object : " + object.toString());

				// Log.d(TAG, "content : " + value_AES);
				this.params.add(new BasicNameValuePair("content", value_AES));

			} catch (Exception e) {
				e.printStackTrace();
			}

			/**
			 * hash �� �߰� ������ �����.
			 */

			String value_SHA;
			try {
				value_SHA = SHA256_s(MakeJsonToString(object));
				this.params.add(new BasicNameValuePair("hash", value_SHA));

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

			this.params
					.add(new BasicNameValuePair("content", object.toString()));
			// Log.d(TAG, "object.toString : " + object.toString());
		}
	}

	/**
	 * Make Content to Json
	 */

	private String MakeJsonToString(JSONObject body_json) {

		JSONObject content_json = new JSONObject();
		try {
			content_json.put("session_key", this.session_key);
			content_json.put("token", this.token);
			content_json.put("body", body_json.toString());
			if (this.current_location == null) {
				content_json.put("location", null);
			} else {
				JSONObject locationObject = new JSONObject();
				locationObject.put("latitude",
						this.current_location.getLatitude());
				locationObject.put("longitude",
						this.current_location.getLongitude());
				content_json.put("location", locationObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return content_json.toString();
	}

	/**
	 * AES encoding
	 */

	public String AES_s(String json) {
		// byte[] json_bytes = json.getBytes("UTF-8");
		// byte[] enc_key_bytes = enc_key.getBytes("UTF-8");
		// byte[] enc_iv_bytes = enc_iv.getBytes("UTF-8");

		String result = "";

		try {
			SecretKeySpec key_spec = new SecretKeySpec(
					enc_key.getBytes("UTF-8"), "AES");
			IvParameterSpec iv_spec = new IvParameterSpec(
					enc_iv.getBytes("UTF-8"));
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key_spec, iv_spec);

			byte[] encryptedData = cipher.doFinal(json.getBytes("UTF-8"));
			result = Base64.encodeToString(encryptedData, 0).replace("\n", "");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * 
	 * SHA256 encoding (This is hash)
	 */

	public String SHA256_s(String json) {
		String txtClipher = "";

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");

			md.update(json.getBytes("UTF-8"));

			byte byteData[] = md.digest();
			byte[] byteData_new = new byte[byteData.length];

			for (int i = 0; i < byteData.length; i++) {
				byteData_new[i] = (byte) (byteData[i] & 0xff);
			}

			txtClipher = Base64.encodeToString(byteData_new, 0).replace("\n",
					"");

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return txtClipher;
	}

	/**
	 * Set Secure Key
	 */

	public void setSecureKey(String session_key, String token, String enc_key,
			String enc_iv) {
		this.session_key = session_key;
		this.token = token;
		this.enc_key = enc_key;
		this.enc_iv = enc_iv;

		this.params
				.add(new BasicNameValuePair("session_key", this.session_key));
	}

	/**
	 * DoRequest
	 * 
	 * @param url
	 * @return
	 */

	public void doRequest() {

		// Log.d(TAG, "doRequest isStarted");

		BufferedReader br;
		StringBuffer sb = new StringBuffer();

		try {
			br = new BufferedReader(new InputStreamReader(
					getInputStreamFromUrl(this.url), "utf-8"));

			String line = null;

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.result = sb.toString().trim();
	}

	public InputStream getInputStreamFromUrl(String url) {
		InputStream contentStream = null;

		HttpResponse response = null;

		try {

			HttpClient httpclient = new DefaultHttpClient();

			/*
			 * https �϶� �������� �����ϴ� http client �� �ٲپ� �ݴϴ�.
			 */

			if (type == "https") {
				httpclient = sslClient(httpclient);
			}

			/*
			 * http GET POST method�� �����մϴ�.
			 */

			if (method == "GET") {

				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += paramString;

				// Log.d(TAG, "Get url : " + url);
				HttpGet httpGet = new HttpGet(url);
				response = httpclient.execute(httpGet);

			} else if (method == "POST") {
				HttpPost httpPost = new HttpPost(url);

				// Log.d(TAG, "POST url : " + url);
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				httpPost.setEntity(ent);
				response = httpclient.execute(httpPost);

				// Log.d(TAG, "params : " + params.get(0).toString());

			}

			// List<NameValuePair> nameValuePairs = new
			// ArrayList<NameValuePair>(2);

			// httpGet
			// setEntity(new UrlEncodedFormEntity(nameValuePairs));

			contentStream = response.getEntity().getContent();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return contentStream;
	}

	/**
	 * HTTPS�� ��� �� �Լ��� ���ؼ� �������� �����մϴ�.
	 * 
	 */

	private static HttpClient sslClient(HttpClient client) {
		try {
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, client.getParams());
		} catch (Exception ex) {
			return null;
		}
	}

	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		public MySSLSocketFactory(SSLContext context)
				throws KeyManagementException, NoSuchAlgorithmException,
				KeyStoreException, UnrecoverableKeyException {
			super(null);
			sslContext = context;
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	/**
	 * get Result
	 */

	public String getResult() {

		return this.result;

	}

}
