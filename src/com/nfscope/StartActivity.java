package com.nfscope;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import name.nanek.andutil.Api8AccountManagerUtil;
import name.nanek.andutil.imageupload.ImageUpload;
import name.nanek.andutil.imageupload.ImageUpload.OnImageUploadListener;

import org.ndeftools.Message;
import org.ndeftools.MimeRecord;
import org.ndeftools.Record;
import org.ndeftools.externaltype.ExternalTypeRecord;
import org.ndeftools.util.activity.NfcReaderActivity;
import org.ndeftools.wellknown.TextRecord;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class StartActivity extends NfcReaderActivity implements
		OnImageUploadListener {

	public static final String LOG_TAG = "ScanScope";

	private static final String TAG = NfcReaderActivity.class.getName();

	protected Message message;
	
	private String mBoard;
	
	private String mEmail;
	
	private Uri mData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.reader);
		mEmail = Api8AccountManagerUtil.getAccountManagerEmail(this);
		
		Log.i(LOG_TAG, "email: " + mEmail);
		Log.i(LOG_TAG, "Started with intent data: " + getIntent().getData());
		Log.i(LOG_TAG, "Started with intent action: " + getIntent().getAction());
		
		// TODO remember last board to send to?
		// TODO use any tag ID, not just our own board format?
		mData = getIntent().getData();
		if ( null != mData && !"".equals(mData.toString().trim() ) ) {
			mBoard = getIntent().getData().getQueryParameter("board");
			takePicture();
		} else {
			toast("Scan an NFScope collage to add your picture!");
			//setDetecting(true);			
			
		}
		setDetecting(false);			
		
	}

	private void takePicture() {
		Intent intent = new Intent(this, CaptureImageActivity.class);
		startActivity(intent);
	}

	@Override
	public void readNdefMessage(Message message) {
		Log.i(LOG_TAG, "readNdefMessage: " + message);

		takePicture();

		if (message.size() > 1) {
			toast(getString(R.string.readMultipleRecordNDEFMessage));
		} else {
			toast(getString(R.string.readSingleRecordNDEFMessage));
		}

		this.message = message;

		// process message

		// show in log
		if (message != null) {
			// iterate through all records in message
			Log.d(TAG, "Found " + message.size() + " NDEF records");

			for (int k = 0; k < message.size(); k++) {
				Record record = message.get(k);

				Log.d(TAG, "Record " + k + " type "
						+ record.getClass().getSimpleName());

				// your own code here, for example:
				if (record instanceof MimeRecord) {
					// ..
				} else if (record instanceof ExternalTypeRecord) {
					// ..
				} else if (record instanceof TextRecord) {
					// ..
				} else { // more else
					// ..
				}
			}
		}
	}

	@Override
	protected void readEmptyNdefMessage() {
		toast(getString(R.string.readEmptyMessage));
	}

	@Override
	protected void readNonNdefMessage() {
		toast(getString(R.string.readNonNDEFMessage));
	}

	@Override
	protected void onNfcStateEnabled() {
		toast(getString(R.string.nfcAvailableEnabled));
	}

	@Override
	protected void onNfcStateDisabled() {
		toast(getString(R.string.nfcAvailableDisabled));
	}

	@Override
	protected void onNfcStateChange(boolean enabled) {
		if (enabled) {
			toast(getString(R.string.nfcAvailableEnabled));
		} else {
			toast(getString(R.string.nfcAvailableDisabled));
		}
	}

	@Override
	protected void onNfcFeatureNotFound() {
		toast(getString(R.string.noNfcMessage));
	}

	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
				0, 0);
		toast.show();
	}

	@Override
	protected void onResume() {
		super.onResume();

		ImageView imageView = (ImageView) findViewById(R.id.imageView);

		File photo = new File(getFilesDir(), "photo.jpg");

		if (photo.exists()) {
			
			// TODO read the data from the detected tag
			
			setDetecting(false);
			
			Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath());

			imageView.setImageBitmap(bitmap);

			//photo.delete();
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("board", mBoard);
			params.put("email", mEmail);

			new ImageUpload(this, this, photo, params).execute();

		}
	}

	@Override
	public void onImageUploaded(Uri uploadLocation) {
		File photo = new File(getFilesDir(), "photo.jpg");
		if (photo.exists()) {
			photo.delete();
		}
		
		Log.i(LOG_TAG, "photo uploaded = " + uploadLocation);		
		//if ( null != mData && !"".equals(mData.toString().trim() ) ) {
			
			Intent intent = new Intent(Intent.ACTION_VIEW);
			//intent.setData(mData);
			intent.setData(uploadLocation);
			startActivity(intent);
			finish();
		//}
	}

}
