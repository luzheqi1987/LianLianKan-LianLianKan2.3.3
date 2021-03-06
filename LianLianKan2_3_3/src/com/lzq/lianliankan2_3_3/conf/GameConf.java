/**
 * 
 */
package com.lzq.lianliankan2_3_3.conf;

import android.content.Context;

/**
 * @author Administrator
 * 
 */
public class GameConf {
	public static final int PIECE_WIDTH = 60;
	public static final int PIECE_HEIGHT = 60;
	public static int DEFAULT_TIME = 1000;
	private int beginImageX;
	private int beginImageY;
	private int xSize;
	private int ySize;
	private long gameTime;
	private Context context;

	public GameConf(int xSize, int ySize, int beginImageX, int beginImageY,
			long gameTime, Context context) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.beginImageX = beginImageX;
		this.beginImageY = beginImageY;
		this.gameTime = gameTime;
		this.context = context;
	}

	public long getGameTime() {
		return gameTime;
	}

	public int getBeginImageX() {
		return beginImageX;
	}

	public void setBeginImageX(int beginImageX) {
		this.beginImageX = beginImageX;
	}

	public int getBeginImageY() {
		return beginImageY;
	}

	public void setBeginImageY(int beginImageY) {
		this.beginImageY = beginImageY;
	}

	public int getxSize() {
		return xSize;
	}

	public void setxSize(int xSize) {
		this.xSize = xSize;
	}

	public int getySize() {
		return ySize;
	}

	public void setySize(int ySize) {
		this.ySize = ySize;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
