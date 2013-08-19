/**
 * 
 */
package com.lzq.lianliankan2_3_3_v1_0.board;

import java.util.ArrayList;
import java.util.List;

import com.lzq.lianliankan2_3_3_v1_0.conf.GameConf;
import com.lzq.lianliankan2_3_3_v1_0.model.Piece;

/**
 * @author Administrator
 * 
 */
public class VerticalBoard extends AbstractBoard {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lzq.lianliankan2_3_3_v1_0.board.AbstractBoard#createPieces(com.lzq.
	 * lianliankan2_3_3.conf.GameConf,
	 * com.lzq.lianliankan2_3_3_v1_0.model.Piece[][])
	 */
	@Override
	protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {
		List<Piece> notNullPieces = new ArrayList<Piece>();
		for (int i = 1; i < pieces.length; i++) {
			for (int j = 1; j < pieces[i].length; j++) {
				if (i % 2 == 0) {
					Piece piece = new Piece(i, j);
					notNullPieces.add(piece);
				}
			}
		}
		return notNullPieces;
	}
}
