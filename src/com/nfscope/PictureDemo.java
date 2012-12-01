/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
 */

package com.nfscope;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class PictureDemo extends Activity {

	private static final int MAX_PICTURE_DIMENSIONS = 1024;

	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.take_picture);

		preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			Camera.CameraInfo info = new Camera.CameraInfo();

			for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
				Camera.getCameraInfo(i, info);

				if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					camera = Camera.open(i);
				}
			}
		}

		if (camera == null) {
			camera = Camera.open();
		}

		startPreview();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		inPreview = false;

		super.onPause();
	}

	private void takePicture() {
		if (inPreview) {
			camera.takePicture(null, null, photoCallback);
			inPreview = false;
		}
	}

	private Camera.Size getBestPreviewSize(int maxWidth, int maxHeight,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= maxWidth && size.height <= maxHeight) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}

	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (result == null) {
				result = size;
			} else {
				int resultArea = result.width * result.height;
				int newArea = size.width * size.height;

				if (newArea < resultArea) {
					result = size;
				}
			}
		}

		return (result);
	}

	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {

				// Fix sideways preview on some phones
				final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
						.getDefaultDisplay();
				if (display.getRotation() == Surface.ROTATION_0) {
					camera.setDisplayOrientation(90);
				} else if (display.getRotation() == Surface.ROTATION_90) {
					// Do nothing.
				} else if (display.getRotation() == Surface.ROTATION_180) {
					// Surface.ROTATION_270
				} else {
					camera.setDisplayOrientation(180);
				}

				camera.setPreviewDisplay(previewHolder);

			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(PictureDemo.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters = camera.getParameters();
				Camera.Size size = getBestPreviewSize(width, height, parameters);
				// Camera.Size pictureSize = getSmallestPictureSize(parameters);
				Camera.Size pictureSize = getBestPreviewSize(
						MAX_PICTURE_DIMENSIONS, MAX_PICTURE_DIMENSIONS,
						parameters);

				if (size != null && pictureSize != null) {
					parameters.setPreviewSize(size.width, size.height);
					parameters.setPictureSize(pictureSize.width,
							pictureSize.height);
					parameters.setPictureFormat(ImageFormat.JPEG);
					camera.setParameters(parameters);
					cameraConfigured = true;
				}
			}
		}
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview = true;

			// Take picture as soon as ready.
			takePicture();
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			// no-op -- wait until surfaceChanged()
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
		}
	};

	Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			new SavePhotoTask().execute(data);
			camera.startPreview();
			inPreview = true;
		}
	};
	
    public static Bitmap flipBitmap270(final Bitmap input) {
        final Bitmap destBitmap = Bitmap.createBitmap(
                input.getHeight(), input.getWidth(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(destBitmap);

        final Matrix m = new Matrix();
        m.setRotate(270);
        m.postTranslate(0, input.getWidth());
        //m.postTranslate(input.getHeight(), 0);
        final Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(input, m, paint);
        input.recycle();

        return destBitmap;
    }    
    
    public static Bitmap flipBitmap90(final Bitmap input) {
        final Bitmap destBitmap = Bitmap.createBitmap(
                input.getHeight(), input.getWidth(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(destBitmap);

        final Matrix m = new Matrix();
        m.setRotate(90);
        //m.postTranslate(0, input.getWidth());
        m.postTranslate(input.getHeight(), 0);
        final Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(input, m, paint);
        input.recycle();

        return destBitmap;
    }

	class SavePhotoTask extends AsyncTask<byte[], String, String> {
		@Override
		protected String doInBackground(byte[]... jpeg) {

			Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg[0], 0,
					jpeg[0].length);

			
			// Fix sideways preview on some phones
			final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			if (display.getRotation() == Surface.ROTATION_0) {

				bitmap = flipBitmap270(bitmap);
			
			} else if (display.getRotation() == Surface.ROTATION_90) {
				// Do nothing.
			} else if (display.getRotation() == Surface.ROTATION_180) {
				// Surface.ROTATION_270
			} else {

				bitmap = flipBitmap90(bitmap);
			}
			
			
			File photo = new File(Environment.getExternalStorageDirectory(), "photo.jpg");

			if (photo.exists()) {
				photo.delete();
			}

			try {
				FileOutputStream fos = new FileOutputStream(photo.getPath());

				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
				fos.close();

				//fos.write(jpeg[0]);
				fos.close();
				bitmap.recycle();
			} catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			}

			return (null);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			finish();
		}

	}
}