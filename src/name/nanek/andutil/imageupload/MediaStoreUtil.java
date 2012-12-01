
package name.nanek.andutil.imageupload;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;

public class MediaStoreUtil {

    private static final String[] FILE_PATH_COLUMN = {
            MediaColumns.DATA
    };

    private static final String[] ID_COLUMN = {
            BaseColumns._ID
    };

    public static boolean checkImageHasFile(final Context aContext, final Uri aImageUri) {
        final String imagePath = getImageFilePath(aContext, aImageUri);
        if (null == imagePath) {
            return false;
        }

        final File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            return true;
        }

        return false;
    }

    public static Bitmap waitForImageThumbnail(final Context aContext, final int imageId) {
        final Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                aContext.getContentResolver(),
                imageId,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null);
        return thumbnail;
    }

    public static int getImageId(final Context aContext, final Uri uri) {

        if (null == uri) {
            return -1;
        }

        Cursor cursor = null;
        try {

            cursor = Images.Media.query(
                    aContext.getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    ID_COLUMN,
                    null,
                    null,
                    null);

            if (null == cursor) {
                return -1;
            }

            final int columnIndex = cursor.getColumnIndex(BaseColumns._ID);
            if (-1 == columnIndex) {
                return -1;
            }

            if (!cursor.moveToFirst()) {
                return -1;
            }

            return cursor.getInt(columnIndex);

        } catch (final NullPointerException e) {
            // This seems to get thrown internally If the image URI is no longer
            // in the DB.
            return -1;
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

    public static String getImageFilePath(final Context aContext, final Uri uri) {

        Cursor cursor = null;
        try {
            cursor = Images.Media.query(
                    aContext.getContentResolver(),
                    uri,
                    FILE_PATH_COLUMN);

            if (null == cursor) {
                return null;
            }

            final int columnIndex = cursor.getColumnIndex(MediaColumns.DATA);
            if (-1 == columnIndex) {
                return null;
            }

            if (!cursor.moveToFirst()) {
                return null;
            }

            final String path = cursor.getString(columnIndex);
            return path;

        } catch (final NullPointerException e) {
            // This seems to get thrown internally If the image URI is no longer
            // in the DB.
            return null;
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

    public static void removeImage(final Context aContext, final Uri uri) {

        aContext.getContentResolver().delete(uri, null, null);
    }
    
    public static class AlbumListing {
    	public final String name;
    	public final Integer id;
		public AlbumListing(final String aName, final Integer aId) {
			name = aName;
			id = aId;
		}
		
		@Override
		public String toString() {
			if ( null == name ) {
				return "Choose Album...";
			}
			
			return name.toString();
		}
    }
    
    public static List<AlbumListing> getAlbumTitles(final Context aContext) {
        	
        final Cursor cursor = aContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
               	new String[] {
               		MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
               		MediaStore.Images.ImageColumns.BUCKET_ID
               	},
                null,
                null,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
                );

    	final List<AlbumListing> titles = new LinkedList<AlbumListing>();        
        if ( null == cursor ) {
        	return titles;
        }
        
        AlbumListing previous = null;
        try {
	        while ( cursor.moveToNext() ) {
	        	final String name = cursor.getString(0);
	        	final int id = cursor.getInt(1);
	        	
	        	if ( null != previous && previous.name.equals(name) ) {
	        		continue;
	        	}
	        	
	        	final AlbumListing album = new AlbumListing(name, id);
	        	titles.add(album);
	        	previous = album;
	        }
        } finally {
        	cursor.close();
        }
        
        return titles;
    }

    public static String getImageFilePath(final Context aContext, final int aId) {

        final Cursor cursor = aContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, FILE_PATH_COLUMN,
                BaseColumns._ID + " = ? ",
                new String[] {
                    Integer.toString(aId)
                },
                BaseColumns._ID
                );
        if (null == cursor) {
            return null;
        }

        try {
            final int columnIndex = cursor.getColumnIndex(MediaColumns.DATA);
            if (-1 == columnIndex) {
                return null;
            }

            if (!cursor.moveToFirst()) {
                return null;
            }

            final String path = cursor.getString(columnIndex);
            return path;

        } finally {
            cursor.close();
        }
    }
}
