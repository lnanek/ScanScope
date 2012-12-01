package name.nanek.andutil.imageupload;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import name.nanek.andutil.imageupload.SentCountingMultiPartEntity.SentCountListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class ImageUpload extends AsyncTask<Void, Integer, HttpResult> {
	
	public interface OnImageUploadListener {
		void sendBackgroundImage(Uri uploadLocation);
	}
	
	private static final String LOG_TAG = ImageUpload.class.getSimpleName();

	private static final String FILE_FORM_FIELD_NAME = "uploadedfile";

	private static final String USER_AGENT = "ScanScope";

	private static final Uri SERVER_URL = Uri.parse("http://nfscope.com/uploader.php");

	private final File mFile;

	private final Context mContext;

	private ProgressDialog mProgressDialog;

	private long mTotalSize;
	
	private OnImageUploadListener mListener;
	
	private Map<String, String> mParams;

	public ImageUpload(final Context aContext, 
			final OnImageUploadListener aListener, final File aFile, final Map<String, String> aParams) {
		mListener = aListener;
		mFile = aFile;
		mContext = aContext;
		mParams = aParams;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage("Uploading Picture...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface aDialog) {
				cancel(true);
			}
		});
		mProgressDialog.show();
	}

	private ContentBody getUploadImageBytes() throws IOException {

		FileBody bin = new FileBody(mFile);
		return bin;

	}
	
	public static String encode(String input) {
		if ( null == input ) {
			return "";
		}
		
		try {
			return URLEncoder.encode(input.toString(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		} 
	}

	@Override
	protected HttpResult doInBackground(Void... unused) {
		
		final HttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT, mContext);
		
		final String url = SERVER_URL.toString() 
				+ "?board=" + encode(mParams.get("board")) 
				+ "&email=" + encode(mParams.get("email"));

		Log.i("ScanScope", "using url = " + url);
		
		final HttpPost httpPost = new HttpPost(url);

		try {
			SentCountingMultiPartEntity multipartContent = new SentCountingMultiPartEntity(new SentCountListener() {
				@Override
				public void onSentCountUpdated(long aBytesSend) {
					publishProgress((int) ((aBytesSend / (float) mTotalSize) * 100));
				}
			});

			multipartContent.addPart(FILE_FORM_FIELD_NAME, getUploadImageBytes());

			mTotalSize = multipartContent.getContentLength();

			// Send it
			httpPost.setEntity(multipartContent);
			final HttpResponse response = httpClient.execute(httpPost);
			final String serverResponse = EntityUtils.toString(response.getEntity());
			final int responseCode = response.getStatusLine().getStatusCode();

			return new HttpResult(responseCode, serverResponse);
		} catch (Exception e) {
			Log.i(LOG_TAG, "Error uploading image: ", e);
			return null;
		}		
	}

	@Override
	protected void onProgressUpdate(final Integer... aProgressAmounts) {
		mProgressDialog.setProgress((int) (aProgressAmounts[0]));
	}

	@Override
	protected void onPostExecute(final HttpResult aResult) {
		mProgressDialog.dismiss();

		if ( null != aResult && null != aResult.mResponse && 200 == aResult.mStatusCode ) {
			try {
				final Uri uploadLocation = Uri.parse(aResult.mResponse);
				if ( null != uploadLocation ) {
					mListener.sendBackgroundImage(uploadLocation);
					return;
				}
			} catch (Exception e) {
			}			
		}
		
		new AlertDialog.Builder(mContext)
			.setTitle("Error uploading image. Please try again later.")
			.setPositiveButton("OK", null)
			.show();
	}
}