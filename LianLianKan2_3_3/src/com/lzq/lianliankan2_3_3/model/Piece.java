/**
 * 
 */
package com.lzq.lianliankan2_3_3.model;

/**
 * @author Administrator
 * 
 */
public class Piece {
	private PieceImage image;
	private int beginX;
	private int beginY;
	private int indexX;
	private int indexY;

	public Piece(int indexX, int indexY) {
		this.indexX = indexX;
		this.indexY = indexY;
	}

	public boolean inSameImage(Piece other) {
		if (null == image) {
			if (null != other.getImage()) {
				return false;
			}
		}
		return image.getImageId() == other.getImage().getImageId();
	}

	public PieceImage getImage() {
		return image;
	}

	public void setImage(PieceImage image) {
		this.image = image;
	}

	public int getBeginX() {
		return beginX;
	}

	public void setBeginX(int beginX) {
		this.beginX = beginX;
	}

	public int getBeginY() {
		return beginY;
	}

	public void setBeginY(int beginY) {
		this.beginY = beginY;
	}

	public int getIndexX() {
		return indexX;
	}

	public void setIndexX(int indexX) {
		this.indexX = indexX;
	}

	public int getIndexY() {
		return indexY;
	}

	public void setIndexY(int indexY) {
		this.indexY = indexY;
	}
}
