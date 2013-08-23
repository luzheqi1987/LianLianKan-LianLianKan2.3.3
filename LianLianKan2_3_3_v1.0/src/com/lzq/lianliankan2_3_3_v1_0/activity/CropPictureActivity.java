package com.lzq.lianliankan2_3_3_v1_0.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lzq.lianliankan2_3_3_v1_0.R;
import com.lzq.lianliankan2_3_3_v1_0.conf.GameConf;
import com.lzq.lianliankan2_3_3_v1_0.crop.CropImageView;
import com.lzq.lianliankan2_3_3_v1_0.crop.HighlightView;
import com.lzq.lianliankan2_3_3_v1_0.utils.Util;

public class CropPictureActivity extends MonitoredActivity {
	private static final String TAG = "CropPictureActivity";
	private CropImageView corpPicture = null;
	private int mAspectX, mAspectY; // CR: two definitions per line == sad //
									// panda.
	boolean mWaitingToPick; // Whether we are wait the user to pick a face.
	private boolean mDoFaceDetection = true;
	private final Handler mHandler = new Handler();
	private boolean mCircleCrop = false;
	private Bitmap mBitmap;
	HighlightView mCrop;
	// These options specifiy the output image size and whether we should
	// scale the output to fit it (or just crop it).
	private int mOutputX, mOutputY;
	private boolean mScale; // 是否 规定剪切图片的大小
	private boolean mScaleUp = true;
	private Uri mSaveUri = null;
	private ContentResolver mContentResolver; // These are various options can
												// be specified in the intent.
	private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.PNG; // only
																				// used
																				// with
																				// mSaveUri
	private String baseFileName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_picture);
		corpPicture = (CropImageView) findViewById(R.id.croppicture);

		Intent srcIntent = getIntent(); // 获取来源的intent
		Bundle extras = srcIntent.getExtras();
		baseFileName = extras.getString(getString(R.string.base_file_name_key));
		mOutputX = extras.getInt(getString(R.string.m_output_x_key),
				GameConf.PIECE_WIDTH);
		mOutputY = extras.getInt(getString(R.string.m_output_y_key),
				GameConf.PIECE_HEIGHT);
		mScale = extras.getBoolean(getString(R.string.m_scale_key), true);

		Intent intent = new Intent(); // 从系统图库中选择图片
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 1);
		
		findViewById(R.id.discard).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						setResult(RESULT_CANCELED);
						finish();
					}
				});

		findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onSaveClicked();
			}
		});
		mContentResolver = getContentResolver();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr
						.openInputStream(uri));
				mBitmap = bitmap;
				corpPicture.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			startFaceDetection();
		}
	}

	private void onSaveClicked() {
		// CR: TODO!
		// TODO this code needs to change to use the decode/crop/encode single
		// step api so that we don't require that the whole (possibly large)
		// bitmap doesn't have to be read into memory
		// if (mSaving)
		// return;

		if (mCrop == null) {
			return;
		}

		Rect r = mCrop.getCropRect();

		int width = r.width(); // CR: final == happy panda!
		int height = r.height();

		// If we are circle cropping, we want alpha channel, which is the
		// third param here.
		Bitmap croppedImage = Bitmap.createBitmap(width, height,
				mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		{
			Canvas canvas = new Canvas(croppedImage);
			Rect dstRect = new Rect(0, 0, width, height);
			canvas.drawBitmap(mBitmap, r, dstRect, null);
		}

		if (mCircleCrop) {
			// OK, so what's all this about?
			// Bitmaps are inherently rectangular but we want to return
			// something that's basically a circle. So we fill in the
			// area around the circle with alpha. Note the all important
			// PortDuff.Mode.CLEARes.
			Canvas c = new Canvas(croppedImage);
			Path p = new Path();
			p.addCircle(width / 2F, height / 2F, width / 2F, Path.Direction.CW);
			c.clipPath(p, Region.Op.DIFFERENCE);
			c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
		}

		// If the output is required to a specific size then scale or fill.
		if (mOutputX != 0 && mOutputY != 0) {
			if (mScale) {
				// Scale the image to the required dimensions.
				Bitmap old = croppedImage;
				croppedImage = Util.transform(new Matrix(), croppedImage,
						mOutputX, mOutputY, mScaleUp);
				if (old != croppedImage) {
					old.recycle();
				}
			} else {

				/*
				 * Don't scale the image crop it to the size requested. Create
				 * an new image with the cropped image in the center and the
				 * extra space filled.
				 */

				// Don't scale the image but instead fill it so it's the
				// required dimension
				Bitmap b = Bitmap.createBitmap(mOutputX, mOutputY,
						Bitmap.Config.RGB_565);
				Canvas canvas = new Canvas(b);

				Rect srcRect = mCrop.getCropRect();
				Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

				int dx = (srcRect.width() - dstRect.width()) / 2;
				int dy = (srcRect.height() - dstRect.height()) / 2;

				// If the srcRect is too big, use the center part of it.
				srcRect.inset(Math.max(0, dx), Math.max(0, dy));

				// If the dstRect is too big, use the center part of it.
				dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

				// Draw the cropped bitmap in the center.
				canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

				// Set the cropped bitmap as the new bitmap.
				croppedImage.recycle();
				croppedImage = b;
			}
		}

		// Return the cropped image directly or save it to the specified URI.
		final Bitmap b = croppedImage;
		final Runnable save = new Runnable() {
			public void run() {
				saveOutput(b);
			}
		};
		Util.startBackgroundJob(this, null,
				getResources().getString(R.string.saving_image), save, mHandler);

	}

	@SuppressLint("SdCardPath")
	private void saveOutput(Bitmap croppedImage) {
		if (mSaveUri != null) {
			OutputStream outputStream = null;
			try {
				outputStream = mContentResolver.openOutputStream(mSaveUri);
				if (outputStream != null) {
					croppedImage.compress(mOutputFormat, 75, outputStream);
				}
				// TODO ExifInterface write
			} catch (IOException ex) {
				Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
			} finally {
				Util.closeSilently(outputStream);
			}
			Bundle extras = new Bundle();
			setResult(RESULT_OK,
					new Intent(mSaveUri.toString()).putExtras(extras));
		} else {
			int x = 0;
			// Try file-1.jpg, file-2.jpg, ... until we find a filename
			// which
			// does not exist yet.
			while (true) {
				x += 1;
				String candidate = getFilesDir() + "/" + baseFileName + "-" + x
						+ ".png";
				boolean exists = (new File(candidate)).exists();
				if (!exists) { // CR: inline the expression for exists
								// here--it's clear enough.
					break;
				}
			}

			String title = baseFileName + "-" + x;
			String finalFileName = title + ".png";

			OutputStream outputStream = null;
			try {
				File dir = new File(getFilesDir().toString());
				if (!dir.exists())
					dir.mkdirs();
				File file = new File(getFilesDir().toString(), finalFileName);
				outputStream = new FileOutputStream(file);
				// outputStream = super.openFileOutput(finalFileName,
				// Activity.MODE_PRIVATE);
				if (croppedImage != null) {
					croppedImage.compress(CompressFormat.PNG, 75, outputStream);
				}
			} catch (Exception ex) {
				Log.w(TAG, ex);
				return;
			} finally {
				Util.closeSilently(outputStream);
			}
			setResult(RESULT_OK, new Intent(CropPictureActivity.this,
					MenuActivity.class));
		}

		croppedImage.recycle();
		finish();
	}

	private void startFaceDetection() {
		if (isFinishing()) {
			return;
		}

		corpPicture.setImageBitmapResetBase(mBitmap, true);

		Util.startBackgroundJob(this, null,
				getResources().getString(R.string.running_face_detection),
				new Runnable() {
					public void run() {
						final CountDownLatch latch = new CountDownLatch(1);
						final Bitmap b = mBitmap;
						mHandler.post(new Runnable() {
							public void run() {
								if (b != mBitmap && b != null) {
									corpPicture
											.setImageBitmapResetBase(b, true);
									mBitmap.recycle();
									mBitmap = b;
								}
								if (corpPicture.getScale() == 1.0f) {
									corpPicture.center(true, true);
								}
								latch.countDown();
							}
						});
						try {
							latch.await();
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
						mRunFaceDetection.run();
					}
				}, mHandler);
	}

	Runnable mRunFaceDetection = new Runnable() {
		float mScale = 1F;
		Matrix mImageMatrix;
		FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
		int mNumFaces;

		// For each face, we create a HightlightView for it.
		private void handleFace(FaceDetector.Face f) {
			PointF midPoint = new PointF();

			int r = ((int) (f.eyesDistance() * mScale)) * 2;
			f.getMidPoint(midPoint);
			midPoint.x *= mScale;
			midPoint.y *= mScale;

			int midX = (int) midPoint.x;
			int midY = (int) midPoint.y;

			HighlightView hv = new HighlightView(corpPicture);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			RectF faceRect = new RectF(midX, midY, midX, midY);
			faceRect.inset(-r, -r);
			if (faceRect.left < 0) {
				faceRect.inset(-faceRect.left, -faceRect.left);
			}

			if (faceRect.top < 0) {
				faceRect.inset(-faceRect.top, -faceRect.top);
			}

			if (faceRect.right > imageRect.right) {
				faceRect.inset(faceRect.right - imageRect.right, faceRect.right
						- imageRect.right);
			}

			if (faceRect.bottom > imageRect.bottom) {
				faceRect.inset(faceRect.bottom - imageRect.bottom,
						faceRect.bottom - imageRect.bottom);
			}

			hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop,
					mAspectX != 0 && mAspectY != 0);

			corpPicture.add(hv);
		}

		// Create a default HightlightView if we found no face in the picture.
		private void makeDefault() {
			HighlightView hv = new HighlightView(corpPicture);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			// CR: sentences!
			// make the default size about 4/5 of the width or height
			int cropWidth = Math.min(width, height) * 4 / 5;
			int cropHeight = cropWidth;

			if (mAspectX != 0 && mAspectY != 0) {
				if (mAspectX > mAspectY) {
					cropHeight = cropWidth * mAspectY / mAspectX;
				} else {
					cropWidth = cropHeight * mAspectX / mAspectY;
				}
			}

			int x = (width - cropWidth) / 2;
			int y = (height - cropHeight) / 2;

			RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);

			hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop,
					mAspectX != 0 && mAspectY != 0);
			corpPicture.add(hv);
		}

		// Scale the image down for faster face detection.
		private Bitmap prepareBitmap() {
			if (mBitmap == null) {
				return null;
			}

			// 256 pixels wide is enough.
			if (mBitmap.getWidth() > 256) {
				mScale = 256.0F / mBitmap.getWidth(); // CR: F => f (or change
														// all f to F).
			}
			Matrix matrix = new Matrix();
			matrix.setScale(mScale, mScale);
			Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
					mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
			return faceBitmap;
		}

		public void run() {
			mImageMatrix = corpPicture.getImageMatrix();
			Bitmap faceBitmap = prepareBitmap();

			mScale = 1.0F / mScale;
			if (faceBitmap != null && mDoFaceDetection) {
				FaceDetector detector = new FaceDetector(faceBitmap.getWidth(),
						faceBitmap.getHeight(), mFaces.length);
				mNumFaces = detector.findFaces(faceBitmap, mFaces);
			}

			if (faceBitmap != null && faceBitmap != mBitmap) {
				faceBitmap.recycle();
			}

			mHandler.post(new Runnable() {
				public void run() {
					mWaitingToPick = mNumFaces > 1;
					if (mNumFaces > 0) {
						for (int i = 0; i < mNumFaces; i++) {
							handleFace(mFaces[i]);
						}
					} else {
						makeDefault();
					}
					corpPicture.invalidate();
					if (corpPicture.getmHighlightViews().size() == 1) {
						mCrop = corpPicture.getmHighlightViews().get(0);
						mCrop.setFocus(true);
					}

					if (mNumFaces > 1) {
						// CR: no need for the variable t. just do
						// Toast.makeText(...).show().
						Toast t = Toast.makeText(CropPictureActivity.this,
								R.string.multiface_crop_help,
								Toast.LENGTH_SHORT);
						t.show();
					}
				}
			});
		}
	};
}
