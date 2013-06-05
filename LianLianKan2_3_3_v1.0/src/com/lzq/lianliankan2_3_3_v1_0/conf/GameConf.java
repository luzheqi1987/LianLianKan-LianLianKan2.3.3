/**
 * 
 */
package com.lzq.lianliankan2_3_3_v1_0.conf;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author Administrator
 * 
 */
public class GameConf {
	public static int PIECE_WIDTH = 40;
	public static int PIECE_HEIGHT = 40;
	public static int DEFAULT_TIME = 1000;
	private int beginImageX;
	private int beginImageY;
	private int xSize;
	private int ySize;
	private long gameTime;
	private Context context;

	public GameConf(int xSize, int ySize, int beginImageX, int beginImageY,
			long gameTime, int dpi, Context context) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.beginImageX = beginImageX;
		this.beginImageY = beginImageY;
		this.gameTime = gameTime;
		if (160 == dpi) {
			PIECE_WIDTH = 40;
			PIECE_HEIGHT = 40;
		} else if (240 == dpi) {
			PIECE_WIDTH = 60;
			PIECE_HEIGHT = 60;
		}
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
