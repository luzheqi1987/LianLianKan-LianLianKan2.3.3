/**
 * 
 */
package com.lzq.lianliankan2_3_3_v1_0.serivce;

import java.util.List;
import java.util.Map;

import android.graphics.Point;

import com.lzq.lianliankan2_3_3_v1_0.model.LinkInfo;
import com.lzq.lianliankan2_3_3_v1_0.model.Piece;

/**
 * @author Administrator
 * 
 */
public interface GameService {
	void start();

	Piece[][] getPieces();
	
	Map<Integer, List<Point>> getExistImages();

	boolean hasPieces();

	Piece findPiece(float touchX, float touchY);

	LinkInfo link(Piece p1, Piece p2);
}
