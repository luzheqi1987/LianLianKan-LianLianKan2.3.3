/**
 * 
 */
package com.lzq.lianliankan2_3_3_v1_0.model;

import android.graphics.Bitmap;

/**
 * @author Administrator
 * 
 */
public class PieceImage {
	private Bitmap image;
	private int imageId;

	public PieceImage(Bitmap image, int imageId) {
		this.image = image;
		this.imageId = imageId;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
}
