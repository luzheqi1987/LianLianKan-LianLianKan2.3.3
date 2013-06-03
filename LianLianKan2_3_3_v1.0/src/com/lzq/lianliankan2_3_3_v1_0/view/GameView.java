/**
 * 
 */
package com.lzq.lianliankan2_3_3_v1_0.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.lzq.lianliankan2_3_3_v1_0.R;
import com.lzq.lianliankan2_3_3_v1_0.model.LinkInfo;
import com.lzq.lianliankan2_3_3_v1_0.model.Piece;
import com.lzq.lianliankan2_3_3_v1_0.serivce.GameService;
import com.lzq.lianliankan2_3_3_v1_0.utils.ImageUtil;

/**
 * @author Administrator
 * 
 */
public class GameView extends View {
	private GameService gameService;
	private Piece selectedPiece;
	private LinkInfo linkInfo;
	private Paint paint;
	private Bitmap selectImage;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.paint = new Paint();
		this.paint.setShader(new BitmapShader(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.heart),
				Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
		this.paint.setStrokeWidth(9);
		this.selectImage = ImageUtil.getSelectImage(context);
	}

	public void setGameService(GameService gameService) {
		this.gameService = gameService;
	}

	public void setLinkInfo(LinkInfo linkInfo) {
		this.linkInfo = linkInfo;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (null == this.gameService) {
			return;
		}
		Piece[][] pieces = gameService.getPieces();

		if (pieces != null) {
			for (int i = 0; i < pieces.length; i++) {
				for (int j = 0; j < pieces[i].length; j++) {
					if (null != pieces[i][j]) {
						Piece piece = pieces[i][j];
						canvas.drawBitmap(piece.getImage().getImage(),
								piece.getBeginX(), piece.getBeginY(), null);
					}
				}
			}
		}
		if (null != this.linkInfo) {
			drawLine(this.linkInfo, canvas);
			this.linkInfo = null;
		}
		if (null != this.selectedPiece) {
			canvas.drawBitmap(this.selectImage, this.selectedPiece.getBeginX(),
					this.selectedPiece.getBeginY(), null);
		}
	}

	private void drawLine(LinkInfo linkInfo, Canvas canvas) {
		List<Point> points = linkInfo.getLinkPoints();
		for (int i = 0; i < points.size() - 1; i++) {
			Point currentPoint = points.get(i);
			Point nextPoint = points.get(i + 1);
			canvas.drawLine(currentPoint.x, currentPoint.y, nextPoint.x,
					nextPoint.y, this.paint);
		}
	}

	public void setSelectedPiece(Piece selectedPiece) {
		this.selectedPiece = selectedPiece;
	}

	public void startGame() {
		this.gameService.start();
		this.postInvalidate();
	}
}
