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
import com.lzq.lianliankan2_3_3_v1_0.view.piecemove.IPieceMove;
import com.lzq.lianliankan2_3_3_v1_0.view.piecemove.PieceMoveFactory;

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
	private Piece firstPiece;
	private Piece secondPiece;
	private int stage;
	private Piece[] helpPieces;
	private IPieceMove pieceMove;

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

		if (null != pieces) {
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

		// if (null != firstPiece && null != secondPiece) {
		// moveLeft();
		// }

		if (null != helpPieces) {
			canvas.drawBitmap(this.selectImage, this.helpPieces[0].getBeginX(),
					this.helpPieces[0].getBeginY(), null);
			canvas.drawBitmap(this.selectImage, this.helpPieces[1].getBeginX(),
					this.helpPieces[1].getBeginY(), null);
		}
		if (null != this.selectedPiece) {
			canvas.drawBitmap(this.selectImage, this.selectedPiece.getBeginX(),
					this.selectedPiece.getBeginY(), null);
		}

		while (null == this.linkInfo && null != pieces
				&& gameService.hasPieces() && !gameService.checkCoupleExist()) {
			gameService.shufflePieces();
		}
		this.postInvalidate();
	}

	public void move() {
		pieceMove.move(gameService, firstPiece, secondPiece);
		// switch (stage) {
		// case 2:
		// moveLeft();
		// break;
		// case 3:
		// moveRight();
		// break;
		// default:
		// noMove();
		// }
	}

	public void helpCouples() {
		selectedPiece = null;
		helpPieces = gameService.getCouple();
		this.invalidate();
	}

	private void noMove() {
		Piece[][] pieces = gameService.getPieces();
		pieces[firstPiece.getIndexX()][firstPiece.getIndexY()] = null;
		pieces[secondPiece.getIndexX()][secondPiece.getIndexY()] = null;
		firstPiece = null;
		secondPiece = null;
		gameService.createExistImages();
	}

	private void moveLeft() {
		Piece[][] pieces = gameService.getPieces();
		int i, j;

		if (firstPiece.getIndexX() < secondPiece.getIndexX()) {
			Piece tmp = firstPiece;
			firstPiece = secondPiece;
			secondPiece = tmp;
		}
		for (i = firstPiece.getIndexX(), j = firstPiece.getIndexY(); i < pieces.length - 1; i++) {
			if (null != pieces[i + 1][j]) {
				pieces[i][j].setImage(pieces[i + 1][j].getImage());
			} else {
				break;
			}
		}
		pieces[i][j] = null;

		for (i = secondPiece.getIndexX(), j = secondPiece.getIndexY(); i < pieces.length - 1; i++) {
			if (null != pieces[i + 1][j]) {
				pieces[i][j].setImage(pieces[i + 1][j].getImage());
			} else {
				break;
			}
		}
		pieces[i][j] = null;
		firstPiece = null;
		secondPiece = null;
		gameService.createExistImages();

	}

	private void moveRight() {
		Piece[][] pieces = gameService.getPieces();
		int i, j;

		if (firstPiece.getIndexX() > secondPiece.getIndexX()) {
			Piece tmp = firstPiece;
			firstPiece = secondPiece;
			secondPiece = tmp;
		}
		for (i = firstPiece.getIndexX(), j = firstPiece.getIndexY(); i > 0; i--) {
			if (null != pieces[i - 1][j]) {
				pieces[i][j].setImage(pieces[i - 1][j].getImage());
			} else {
				break;
			}
		}
		pieces[i][j] = null;

		for (i = secondPiece.getIndexX(), j = secondPiece.getIndexY(); i > 0; i--) {
			if (null != pieces[i - 1][j]) {
				pieces[i][j].setImage(pieces[i - 1][j].getImage());
			} else {
				break;
			}
		}
		pieces[i][j] = null;
		firstPiece = null;
		secondPiece = null;
		gameService.createExistImages();

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
		helpPieces = null;
	}

	public void startGame() {
		this.gameService.start();
		this.postInvalidate();
	}

	public Piece getFirstPiece() {
		return firstPiece;
	}

	public void setFirstPiece(Piece firstPiece) {
		this.firstPiece = firstPiece;
	}

	public Piece getSecondPiece() {
		return secondPiece;
	}

	public void setSecondPiece(Piece secondPiece) {
		this.secondPiece = secondPiece;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
		pieceMove = null;
		pieceMove = PieceMoveFactory.createPieceMove(stage);
	}

}
