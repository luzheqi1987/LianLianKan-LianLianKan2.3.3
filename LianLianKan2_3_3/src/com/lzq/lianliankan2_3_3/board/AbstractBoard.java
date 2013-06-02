/**
 * 
 */
package com.lzq.lianliankan2_3_3.board;

import java.util.List;

import com.lzq.lianliankan2_3_3.conf.GameConf;
import com.lzq.lianliankan2_3_3.model.Piece;
import com.lzq.lianliankan2_3_3.model.PieceImage;
import com.lzq.lianliankan2_3_3.utils.ImageUtil;

/**
 * @author Administrator
 * 
 */
public abstract class AbstractBoard {
	protected abstract List<Piece> createPieces(GameConf config,
			Piece[][] pieces);

	public Piece[][] create(GameConf config) {
		Piece[][] pieces = new Piece[config.getxSize()][config.getySize()];
		List<Piece> notNullPieces = createPieces(config, pieces);
		List<PieceImage> playImages = ImageUtil.getPlayImages(
				config.getContext(), notNullPieces.size());
		int imageWidth = playImages.get(0).getImage().getWidth();
		int imageHeight = playImages.get(0).getImage().getHeight();
		for (int i = 0; i < notNullPieces.size(); i++) {
			Piece piece = notNullPieces.get(i);
			piece.setImage(playImages.get(i));
			piece.setBeginX(piece.getIndexX() * imageWidth
					+ config.getBeginImageX());
			piece.setBeginY(piece.getIndexY() * imageHeight
					+ config.getBeginImageY());
			pieces[piece.getIndexX()][piece.getIndexY()] = piece;
		}
		return pieces;
	}
}
