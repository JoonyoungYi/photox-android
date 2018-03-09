package kr.photox.android.api;

import java.util.ArrayList;

import kr.photox.android.model.Model;
import kr.photox.android.model.Protocol;

public interface Api {
	abstract public void execute();

	abstract public String getApiType();
	 
	abstract public ArrayList<Model> getModels();

	abstract public Protocol getProtocol();
	
	abstract public String getProtocolType();
	
	abstract public void setSecureKey(String session_key, String token, String enc_key,
			String enc_iv) ;

}
