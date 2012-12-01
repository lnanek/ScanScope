package name.nanek.andutil.imageupload;

public class HttpResult {
	
	public final int mStatusCode;
	
	public final String mResponse;

	public HttpResult(int aStatusCode, String aResponse) {
		mStatusCode = aStatusCode;
		mResponse = aResponse;
	}
	
}
