/**
 * 
 */
package com.lzq.lianliankan2_3_3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lzq.lianliankan2_3_3.model.LinkInfo;
import com.lzq.lianliankan2_3_3.serivce.GameService;

/**
 * @author Administrator
 *
 */
public class GameView extends View {
	private GameService gameService;
	private LinkInfo linkInfo;
	private Paint paint;
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
	}
	
}
