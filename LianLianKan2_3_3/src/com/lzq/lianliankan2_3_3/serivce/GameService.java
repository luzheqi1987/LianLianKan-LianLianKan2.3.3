/**
 * 
 */
package com.lzq.lianliankan2_3_3.serivce;

import com.lzq.lianliankan2_3_3.model.LinkInfo;
import com.lzq.lianliankan2_3_3.model.Piece;

/**
 * @author Administrator
 * 
 */
public interface GameService {
	void start();

	Piece[][] getPiece();

	boolean hasPieces();

	Piece findPiece(float touchX, float touchY);

	LinkInfo link(Piece p1, Piece p2);
}
