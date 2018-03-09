package kr.photox.android.api;

import org.json.JSONObject;

public class DecalReaderApi extends ApiBase{

	/**
	 * �ʱ�ȭ
	 */
	private JSONObject object;
	private int id;
	
	public DecalReaderApi() {
		String url = "decal_reader/api.json";
		this.object = getLine(url);

	}
	
	/**
	 * Input ���ڸ� �޾ƿɴϴ�.
	 */
	
	public void setInput(int id){
		this.id = id;
	}
}
