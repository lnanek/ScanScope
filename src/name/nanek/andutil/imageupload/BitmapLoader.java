
package name.nanek.andutil.imageupload;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapLoader {

    public static class DimensionF {
        public final float x, y;

        public DimensionF(final float aX, final float aY) {
            x = aX;
            y = aY;
        }
    }

    /**
     * Decodes a bitmap to a given size, using the minimum sampling size of data
     * to read in and still be able to scale the image down.
     */
    public static Bitmap fitInsideDestinationPreservingAspect(
            final Resources r, final int resource,
            final int destWidth, final int destHeight) {

        final BitmapFactory.Options readMetaDataOnly = new BitmapFactory.Options();
        readMetaDataOnly.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(r, resource, readMetaDataOnly);
        final int originalWidth = readMetaDataOnly.outWidth;
        final int originalHeight = readMetaDataOnly.outHeight;

        final BitmapFactory.Options minSamples = new BitmapFactory.Options();
        minSamples.inSampleSize = Math.max(originalWidth / destWidth, originalHeight / destHeight);
        final Bitmap sampledBitmap = BitmapFactory.decodeResource(r, resource, minSamples);

        final RectF sampledSize = new RectF(0, 0, sampledBitmap.getWidth(),
                sampledBitmap.getHeight());
        final RectF destSize = new RectF(0, 0, destWidth, destHeight);
        final float scalingRatio = getAspectRatioPreservingScaleToFit(sampledSize, destSize);

        final Bitmap destBitmap = Bitmap.createScaledBitmap(
                sampledBitmap,
                (int)(sampledBitmap.getWidth() * scalingRatio),
                (int)(sampledBitmap.getHeight() * scalingRatio),
                true);
        sampledBitmap.recycle();

        return destBitmap;
    }

    public static Bitmap flipBitmap90(final Bitmap input) {
        final Bitmap destBitmap = Bitmap.createBitmap(
                input.getHeight(), input.getWidth(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(destBitmap);

        final Matrix m = new Matrix();
        m.setRotate(90);
        m.postTranslate(input.getHeight(), 0);
        final Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(input, m, paint);
        input.recycle();

        return destBitmap;
    }

    public static Bitmap flipBitmap180(final Bitmap input) {
        final Bitmap destBitmap = Bitmap.createBitmap(
                input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(destBitmap);

        final Matrix m = new Matrix();
        m.setRotate(180);
        m.postTranslate(input.getWidth(), input.getHeight());
        final Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(input, m, paint);
        input.recycle();

        return destBitmap;
    }

    public static Bitmap fillDestinationLosingAspect(final Resources r, final int resource,
            final int destWidth, final int destHeight) {

        final BitmapFactory.Options readMetaDataOnly = new BitmapFactory.Options();
        readMetaDataOnly.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(r, resource, readMetaDataOnly);
        final int originalWidth = readMetaDataOnly.outWidth;
        final int originalHeight = readMetaDataOnly.outHeight;

        final BitmapFactory.Options minSamples = new BitmapFactory.Options();
        minSamples.inSampleSize = Math.max(originalWidth / destWidth, originalHeight / destHeight);
        final Bitmap sampledBitmap = BitmapFactory.decodeResource(r, resource, minSamples);

        final Bitmap destBitmap = Bitmap.createScaledBitmap(
                sampledBitmap,
                destWidth,
                destHeight,
                true);
        sampledBitmap.recycle();

        return destBitmap;
    }

    public static Bitmap fillDestinationPreservingAspectCroppingEdges(final String uri,
            final int destWidth, final int destHeight) {

        final BitmapFactory.Options readMetaDataOnly = new BitmapFactory.Options();
        readMetaDataOnly.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, readMetaDataOnly);
        final int originalWidth = readMetaDataOnly.outWidth;
        final int originalHeight = readMetaDataOnly.outHeight;

        final BitmapFactory.Options minSamples = new BitmapFactory.Options();
        minSamples.inSampleSize = Math.max(originalWidth / destWidth, originalHeight / destHeight);
        final Bitmap sampledBitmap = BitmapFactory.decodeFile(uri, minSamples);

        final Rect sampledSize = new Rect(0, 0, sampledBitmap.getWidth(),
                sampledBitmap.getHeight());
        final Rect destSize = new Rect(0, 0, destWidth, destHeight);

        final Bitmap destBitmap = Bitmap.createBitmap(
                destWidth, destHeight, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(destBitmap);

        final float dstRatio = destSize.width() / (float)destSize.height();
        final float srcRatio = sampledSize.width() / (float)sampledSize.height();
        if (dstRatio > srcRatio) {
            // Destination is wider than the source.
            final float newSourceHeight = sampledBitmap.getWidth() / dstRatio;
            final float extraHeight = sampledBitmap.getHeight() - newSourceHeight;
            final float topPadding = extraHeight / 2;
            final float bottomPadding = extraHeight - topPadding;
            destSize.top -= topPadding;
            destSize.bottom += bottomPadding;
        } else if (dstRatio < srcRatio) {
            // Destination is narrower than the source.
            final float newSourceWidth = sampledBitmap.getHeight() * dstRatio;
            final float extraWidth = sampledBitmap.getWidth() - newSourceWidth;
            final float leftPadding = extraWidth / 2;
            final float rightPadding = extraWidth - leftPadding;
            destSize.left -= leftPadding;
            destSize.right += rightPadding;
        }

        final Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(sampledBitmap, sampledSize, destSize, paint);

        sampledBitmap.recycle();

        return destBitmap;
    }

    public static float getAspectRatioPreservingScaleToFit(final RectF source, final RectF target) {
        final float[] scalingMatrix = new float[9];
        {
            final Matrix m = new Matrix();
            m.setRectToRect(source, target, Matrix.ScaleToFit.CENTER);
            m.getValues(scalingMatrix);
        }
        return scalingMatrix[0];
    }

    /**
     * Decodes a bitmap to a given size or bigger, using the minimum sampling
     * size of data to read in and still be able to scale the image down.
     */
    public static Bitmap sampleToAtLeastSize(final String uri,
            final Integer destWidth, final Integer destHeight) {

        final BitmapFactory.Options readMetaDataOnly = new BitmapFactory.Options();
        readMetaDataOnly.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, readMetaDataOnly);
        final int originalWidth = readMetaDataOnly.outWidth;
        final int originalHeight = readMetaDataOnly.outHeight;

        final BitmapFactory.Options minSamples = new BitmapFactory.Options();
        if ( null == destWidth && null == destHeight ) {
        	minSamples.inSampleSize = 1;
        } else if ( null == destWidth ) {
        	minSamples.inSampleSize = originalHeight / destHeight;
        } else if ( null == destHeight ) {
        	minSamples.inSampleSize = originalWidth / destWidth;
        } else {
            minSamples.inSampleSize = Math.max(originalWidth / destWidth, originalHeight / destHeight);
        }
        
        if ( 0 == minSamples.inSampleSize ) {
        	minSamples.inSampleSize = 1;
        }
        
        final Bitmap sampledBitmap = BitmapFactory.decodeFile(uri, minSamples);

        return sampledBitmap;
    }

    /**
     * Decodes a bitmap to a given size or bigger, using the minimum sampling
     * size of data to read in and still be able to scale the image down.
     */
    public static Bitmap sampleToAtLeastSize(
            final Resources r, final int resource,
            final int destWidth, final int destHeight) {

        final BitmapFactory.Options readMetaDataOnly = new BitmapFactory.Options();
        readMetaDataOnly.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(r, resource, readMetaDataOnly);
        final int originalWidth = readMetaDataOnly.outWidth;
        final int originalHeight = readMetaDataOnly.outHeight;

        final BitmapFactory.Options minSamples = new BitmapFactory.Options();
        minSamples.inSampleSize = Math.max(originalWidth / destWidth, originalHeight / destHeight);
        final Bitmap sampledBitmap = BitmapFactory.decodeResource(r, resource, minSamples);

        return sampledBitmap;
    }
}
