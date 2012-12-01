package name.nanek.andutil;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

/**
 * Gets possible user emails from profile. Requires these permissions:
 * <uses-permission android:name="android.permission.READ_PROFILE" />
 * <uses-permission android:name="android.permission.READ_CONTACTS" />
 * 
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Api14ProfileUtil implements LoaderManager.LoaderCallbacks<Cursor> {
	
	public interface OnResultListener {
		void onResult(List<String> emails);
	}

	private Activity mActivity;
	
	private OnResultListener mListener;
	
	public Api14ProfileUtil(final Activity activity, final OnResultListener aListener) {
		
		mActivity = activity;
		
		mListener = aListener;
		
		activity.getLoaderManager().initLoader(0, null, (LoaderCallbacks) this);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
		return new CursorLoader(mActivity,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(
						ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
				ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE + " = ?",
				new String[] { ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		List<String> emails = new ArrayList<String>();
		
		List<String> primaryEmails = new ArrayList<String>();
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			final String email = cursor.getString(ProfileQuery.ADDRESS);
			
			final int isPrimary = cursor.getInt(ProfileQuery.IS_PRIMARY);
			
			emails.add(email);
			// Potentially filter on ProfileQuery.IS_PRIMARY
			
			if ( isPrimary > 0 ) {
				primaryEmails.add(email);
			}
			
			cursor.moveToNext();

		}
		
		if ( !primaryEmails.isEmpty() ) {
			mListener.onResult(primaryEmails);
			return;
		}
		
		mListener.onResult(emails);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
	}

	private interface ProfileQuery {
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}
}
