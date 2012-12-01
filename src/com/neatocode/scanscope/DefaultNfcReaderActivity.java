package com.neatocode.scanscope;

import java.io.File;

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

public class DefaultNfcReaderActivity extends NfcReaderActivity implements
		OnImageUploadListener {

	private static final String TAG = NfcReaderActivity.class.getName();

	protected Message message;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.reader);

		setDetecting(true);

		Log.i("ScanScope", "Started with intent data: " + getIntent().getData());

		Log.i("ScanScope", "Started with intent action: "
				+ getIntent().getAction());

		takePicture();

	}

	private void takePicture() {
		Intent intent = new Intent(this, PictureDemo.class);
		startActivity(intent);
	}

	@Override
	public void readNdefMessage(Message message) {

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

		// show in gui
		showList();
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

	private void showList() {
		if (message != null && !message.isEmpty()) {

			// display the message
			// show in gui
			ArrayAdapter<? extends Object> adapter = new NdefRecordAdapter(
					this, message);
			ListView listView = (ListView) findViewById(R.id.recordListView);
			listView.setAdapter(adapter);
		} else {
			clearList();
		}
	}

	private void clearList() {
		ListView listView = (ListView) findViewById(R.id.recordListView);
		listView.setAdapter(null);
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

		File photo = new File(Environment.getExternalStorageDirectory(),
				"photo.jpg");

		if (photo.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath());

			imageView.setImageBitmap(bitmap);

			//photo.delete();

			new ImageUpload(this, this, photo).execute();

		}
	}

	@Override
	public void sendBackgroundImage(Uri uploadLocation) {
		Log.i("ScanScope", "photo uploaded = " + uploadLocation);
	}

}
