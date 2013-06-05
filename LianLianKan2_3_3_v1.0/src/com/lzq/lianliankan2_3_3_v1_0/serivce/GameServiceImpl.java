/**
 * 
 */
package com.lzq.lianliankan2_3_3_v1_0.serivce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.graphics.Point;
import android.util.Log;

import com.lzq.lianliankan2_3_3_v1_0.board.AbstractBoard;
import com.lzq.lianliankan2_3_3_v1_0.board.FullBoard;
import com.lzq.lianliankan2_3_3_v1_0.conf.GameConf;
import com.lzq.lianliankan2_3_3_v1_0.model.LinkInfo;
import com.lzq.lianliankan2_3_3_v1_0.model.Piece;

/**
 * @author Administrator
 * 
 */
public class GameServiceImpl implements GameService {

	private Piece[][] pieces;
	private Map<Integer, List<Point>> existImages = new HashMap<Integer, List<Point>>();
	private GameConf config;

	public GameServiceImpl(GameConf config) {
		this.config = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lzq.lianliankan2_3_3_v1_0.serivce.GameService#start()
	 */
	@Override
	public void start() {
		AbstractBoard board = null;
		Random random = new Random();
		int index = random.nextInt(4);
		switch (index) {
		case 1:
			board = new FullBoard();
			break;
		case 2:
			board = new FullBoard();
			break;
		default:
			board = new FullBoard();
			break;
		}
		this.pieces = board.create(config);
		createExistImages();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lzq.lianliankan2_3_3_v1_0.serivce.GameService#getPieces()
	 */
	@Override
	public Piece[][] getPieces() {
		return this.pieces;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lzq.lianliankan2_3_3_v1_0.serivce.GameService#hasPieces()
	 */
	@Override
	public boolean hasPieces() {
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {
				if (pieces[i][j] != null) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lzq.lianliankan2_3_3_v1_0.serivce.GameService#findPiece(float,
	 * float)
	 */
	@Override
	public Piece findPiece(float touchX, float touchY) {
		int relativeX = (int) touchX - this.config.getBeginImageX();
		int relativeY = (int) touchY - this.config.getBeginImageY();

		if (relativeX < 0 || relativeY < 0) {
			return null;
		}
		int indexX = getIndex(relativeX, GameConf.PIECE_WIDTH);
		int indexY = getIndex(relativeY, GameConf.PIECE_HEIGHT);

		if (indexX < 0 || indexY < 0) {
			return null;
		}

		if (indexX >= this.config.getxSize()
				|| indexY >= this.config.getySize()) {
			return null;
		}

		return this.pieces[indexX][indexY];

	}

	private int getIndex(int relative, int size) {
		int index = -1;
		if (relative % size == 0) {
			index = relative / size - 1;
		} else {
			index = relative / size;
		}
		return index;
	}

	public boolean checkCoupleExist() {
		for (Integer key : existImages.keySet()) {
			List<Point> points = existImages.get(key);
			for (int i = 0; i < points.size(); i++) {
				Point point1 = points.get(i);
				Piece piece1 = getPieceFromPoint(point1);
				for (int j = i + 1; j < points.size(); j++) {
					Point point2 = points.get(j);
					Piece piece2 = getPieceFromPoint(point2);
					if (null != piece1 && null != piece2) {
						LinkInfo info = link(piece1, piece2);
						if (null != info) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private Piece getPieceFromPoint(Point point) {
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {

				if (null != pieces[i][j] && pieces[i][j].getIndexX() == point.x
						&& pieces[i][j].getIndexY() == point.y) {
					return pieces[i][j];
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lzq.lianliankan2_3_3_v1_0.serivce.GameService#link(com.lzq.
	 * lianliankan2_3_3 .model.Piece, com.lzq.lianliankan2_3_3_v1_0.model.Piece)
	 */
	@Override
	public LinkInfo link(Piece p1, Piece p2) {
		if (p1.equals(p2)) {
			return null;
		}
		if (!p1.isSameImage(p2)) {
			return null;
		}

		if (p2.getIndexX() < p1.getIndexX()) {
			return link(p2, p1);
		}

		Point p1Point = p1.getCenter();
		Point p2Point = p2.getCenter();
		if (p1.getIndexY() == p2.getIndexY()) {
			if (!isXBlock(p1Point, p2Point, GameConf.PIECE_WIDTH)) {
				return new LinkInfo(p1Point, p2Point);
			}
		}

		if (p1.getIndexX() == p2.getIndexX()) {
			if (!isYBlock(p1Point, p2Point, GameConf.PIECE_HEIGHT)) {
				return new LinkInfo(p1Point, p2Point);
			}
		}

		Point cornerPoint = getCornerPoint(p1Point, p2Point,
				GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT);
		if (null != cornerPoint) {
			return new LinkInfo(p1Point, cornerPoint, p2Point);
		}

		Map<Point, Point> turns = getLinkPoints(p1Point, p2Point,
				GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT);
		if (0 != turns.size()) {
			return getShortcut(p1Point, p2Point, turns,
					getDistance(p1Point, p2Point));
		}

		return null;
	}

	private boolean isXBlock(Point p1, Point p2, int pieceWidth) {
		if (p2.x < p1.x) {
			return isXBlock(p2, p1, pieceWidth);
		}
		for (int i = p1.x + pieceWidth; i < p2.x; i = i + pieceWidth) {
			if (hasPiece(i, p1.y)) {
				return true;
			}
		}
		return false;
	}

	private boolean isYBlock(Point p1, Point p2, int pieceHeight) {
		if (p2.y < p1.y) {
			return isYBlock(p2, p1, pieceHeight);
		}
		for (int i = p1.y + pieceHeight; i < p2.y; i = i + pieceHeight) {
			if (hasPiece(p1.x, i)) {
				return true;
			}
		}
		return false;
	}

	private List<Point> getLeftChanel(Point p, int min, int pieceWidth) {
		List<Point> result = new ArrayList<Point>();
		for (int i = p.x - pieceWidth; i >= min; i -= pieceWidth) {
			if (hasPiece(i, p.y)) {
				return result;
			}
			result.add(new Point(i, p.y));
		}
		return result;
	}

	private List<Point> getRightChanel(Point p, int max, int pieceWidth) {
		List<Point> result = new ArrayList<Point>();
		for (int i = p.x + pieceWidth; i <= max; i += pieceWidth) {
			if (hasPiece(i, p.y)) {
				return result;
			}
			result.add(new Point(i, p.y));
		}
		return result;
	}

	private List<Point> getUpChanel(Point p, int min, int pieceHeight) {
		List<Point> result = new ArrayList<Point>();
		for (int i = p.y - pieceHeight; i >= min; i -= pieceHeight) {
			if (hasPiece(p.x, i)) {
				return result;
			}
			result.add(new Point(p.x, i));
		}
		return result;
	}

	private List<Point> getDownChanel(Point p, int max, int pieceHeight) {
		List<Point> result = new ArrayList<Point>();
		for (int i = p.y + pieceHeight; i <= max; i += pieceHeight) {
			if (hasPiece(p.x, i)) {
				return result;
			}
			result.add(new Point(p.x, i));
		}
		return result;
	}

	private Point getWrapPoint(List<Point> p1Chanel, List<Point> p2Chanel) {
		for (int i = 0; i < p1Chanel.size(); i++) {
			Point temp1 = p1Chanel.get(i);
			for (int j = 0; j < p2Chanel.size(); j++) {
				Point temp2 = p2Chanel.get(j);
				if (temp1.equals(temp2)) {
					return temp1;
				}
			}
		}
		return null;
	}

	private Point getCornerPoint(Point point1, Point point2, int pieceWidth,
			int pieceHeight) {
		if (isLeftUp(point1, point2) || isLeftDown(point1, point2)) {
			getCornerPoint(point2, point1, pieceWidth, pieceHeight);
		}
		List<Point> point1UpChanel = getUpChanel(point1, point2.y, pieceHeight);
		List<Point> point1DownChanel = getDownChanel(point1, point2.y,
				pieceHeight);
		List<Point> point1RightChanel = getRightChanel(point1, point2.x,
				pieceWidth);

		List<Point> point2UpChanel = getUpChanel(point2, point1.y, pieceHeight);
		List<Point> point2DownChanel = getDownChanel(point2, point1.y,
				pieceHeight);
		List<Point> point2LeftChanel = getLeftChanel(point2, point1.x,
				pieceWidth);

		if (isRightUp(point1, point2)) {
			Point linkPoint1 = getWrapPoint(point1RightChanel, point2DownChanel);
			Point linkPoint2 = getWrapPoint(point1UpChanel, point2LeftChanel);
			return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
		}

		if (isRightDown(point1, point2)) {
			Point linkPoint1 = getWrapPoint(point1RightChanel, point2UpChanel);
			Point linkPoint2 = getWrapPoint(point1DownChanel, point2LeftChanel);
			return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
		}
		return null;
	}

	private Map<Point, Point> getLinkPoints(Point p1, Point p2, int pieceWidth,
			int pieceHeight) {
		if (isLeftUp(p1, p2) || isLeftDown(p1, p2)) {
			getCornerPoint(p2, p1, pieceWidth, pieceHeight);
		}
		Map<Point, Point> result = new HashMap<Point, Point>();
		List<Point> p1UpChanel = getUpChanel(p1, p2.y, pieceHeight);
		List<Point> p1DownChanel = getDownChanel(p1, p2.y, pieceHeight);
		List<Point> p1RightChanel = getRightChanel(p1, p2.x, pieceWidth);

		List<Point> p2UpChanel = getUpChanel(p2, p1.y, pieceHeight);
		List<Point> p2DownChanel = getDownChanel(p2, p1.y, pieceHeight);
		List<Point> p2LeftChanel = getLeftChanel(p2, p1.x, pieceWidth);

		int widthMax = (this.config.getxSize() + 1) * pieceWidth
				+ this.config.getBeginImageX();
		int heightMax = (this.config.getySize() + 1) * pieceHeight
				+ this.config.getBeginImageY();

		if (p1.y == p2.y) {
			p1UpChanel = getUpChanel(p1, 0, pieceHeight);
			p2UpChanel = getUpChanel(p2, 0, pieceHeight);
			Map<Point, Point> upLinkPoints = getXLinkPoints(p1UpChanel,
					p2UpChanel, pieceWidth);

			p1DownChanel = getDownChanel(p1, heightMax, pieceHeight);
			p2DownChanel = getDownChanel(p2, heightMax, pieceHeight);
			Map<Point, Point> downLinkPoints = getXLinkPoints(p1DownChanel,
					p2DownChanel, pieceWidth);
			result.putAll(upLinkPoints);
			result.putAll(downLinkPoints);
		}

		if (p1.x == p2.x) {
			List<Point> p1LeftChanel = getLeftChanel(p1, 0, pieceWidth);
			p2LeftChanel = getLeftChanel(p2, 0, pieceWidth);
			Map<Point, Point> leftLinkPoints = getYLinkPoints(p1LeftChanel,
					p2LeftChanel, pieceHeight);

			p1RightChanel = getRightChanel(p1, widthMax, pieceWidth);
			List<Point> p2RightChanel = getRightChanel(p2, widthMax, pieceWidth);
			Map<Point, Point> rightLinkPoints = getYLinkPoints(p1RightChanel,
					p2RightChanel, pieceHeight);
			result.putAll(leftLinkPoints);
			result.putAll(rightLinkPoints);
		}

		if (isRightUp(p1, p2)) {
			Map<Point, Point> upDownLinkPoints = getXLinkPoints(p1UpChanel,
					p2DownChanel, pieceWidth);
			Map<Point, Point> rightLeftLinkPoints = getYLinkPoints(
					p1RightChanel, p2LeftChanel, pieceHeight);

			p1UpChanel = getUpChanel(p1, 0, pieceHeight);
			p2UpChanel = getUpChanel(p2, 0, pieceHeight);
			Map<Point, Point> upUpLinkPoints = getXLinkPoints(p1UpChanel,
					p2UpChanel, pieceWidth);

			p1DownChanel = getDownChanel(p1, heightMax, pieceHeight);
			p2DownChanel = getDownChanel(p2, heightMax, pieceHeight);
			Map<Point, Point> downDownLinkPoints = getXLinkPoints(p1DownChanel,
					p2DownChanel, pieceWidth);

			List<Point> p1LeftChanel = getLeftChanel(p1, 0, pieceWidth);
			p2LeftChanel = getLeftChanel(p2, 0, pieceWidth);
			Map<Point, Point> leftLeftLinkPoints = getYLinkPoints(p1LeftChanel,
					p2LeftChanel, pieceHeight);

			p1RightChanel = getRightChanel(p1, widthMax, pieceWidth);
			List<Point> p2RightChanel = getRightChanel(p2, widthMax, pieceWidth);
			Map<Point, Point> rightRightLinkPoints = getYLinkPoints(
					p1RightChanel, p2RightChanel, pieceHeight);

			result.putAll(upDownLinkPoints);
			result.putAll(rightLeftLinkPoints);
			result.putAll(upUpLinkPoints);
			result.putAll(downDownLinkPoints);
			result.putAll(rightRightLinkPoints);
			result.putAll(leftLeftLinkPoints);
		}

		if (isRightDown(p1, p2)) {
			Map<Point, Point> downUpLinkPoints = getXLinkPoints(p1DownChanel,
					p2UpChanel, pieceWidth);
			Map<Point, Point> rightLeftLinkPoints = getYLinkPoints(
					p1RightChanel, p2LeftChanel, pieceHeight);

			p1UpChanel = getUpChanel(p1, 0, pieceHeight);
			p2UpChanel = getUpChanel(p2, 0, pieceHeight);
			Map<Point, Point> upUpLinkPoints = getXLinkPoints(p1UpChanel,
					p2UpChanel, pieceWidth);

			p1DownChanel = getDownChanel(p1, heightMax, pieceHeight);
			p2DownChanel = getDownChanel(p2, heightMax, pieceHeight);
			Map<Point, Point> downDownLinkPoints = getXLinkPoints(p1DownChanel,
					p2DownChanel, pieceWidth);

			List<Point> p1LeftChanel = getLeftChanel(p1, 0, pieceWidth);
			p2LeftChanel = getLeftChanel(p2, 0, pieceWidth);
			Map<Point, Point> leftLeftLinkPoints = getYLinkPoints(p1LeftChanel,
					p2LeftChanel, pieceHeight);

			p1RightChanel = getRightChanel(p1, widthMax, pieceWidth);
			List<Point> p2RightChanel = getRightChanel(p2, widthMax, pieceWidth);
			Map<Point, Point> rightRightLinkPoints = getYLinkPoints(
					p1RightChanel, p2RightChanel, pieceHeight);

			result.putAll(downUpLinkPoints);
			result.putAll(rightLeftLinkPoints);
			result.putAll(upUpLinkPoints);
			result.putAll(downDownLinkPoints);
			result.putAll(rightRightLinkPoints);
			result.putAll(leftLeftLinkPoints);
		}
		return result;
	}

	private Map<Point, Point> getYLinkPoints(List<Point> p1Chanel,
			List<Point> p2Chanel, int pieceHeight) {
		Map<Point, Point> result = new HashMap<Point, Point>();
		for (int i = 0; i < p1Chanel.size(); i++) {
			Point temp1 = p1Chanel.get(i);
			for (int j = 0; j < p2Chanel.size(); j++) {
				Point temp2 = p2Chanel.get(j);
				if (temp1.x == temp2.x) {
					if (!isYBlock(temp1, temp2, pieceHeight)) {
						result.put(temp1, temp2);
					}
				}
			}
		}
		return result;
	}

	private Map<Point, Point> getXLinkPoints(List<Point> p1Chanel,
			List<Point> p2Chanel, int pieceWidth) {
		Map<Point, Point> result = new HashMap<Point, Point>();
		for (int i = 0; i < p1Chanel.size(); i++) {
			Point temp1 = p1Chanel.get(i);
			for (int j = 0; j < p2Chanel.size(); j++) {
				Point temp2 = p2Chanel.get(j);
				if (temp1.y == temp2.y) {
					if (!isXBlock(temp1, temp2, pieceWidth)) {
						result.put(temp1, temp2);
					}
				}
			}
		}
		return result;
	}

	private LinkInfo getShortcut(Point p1, Point p2, Map<Point, Point> turns,
			int shortDistance) {
		List<LinkInfo> infos = new ArrayList<LinkInfo>();
		for (Point point1 : turns.keySet()) {
			Point point2 = turns.get(point1);
			infos.add(new LinkInfo(p1, point1, point2, p2));
		}
		return getShortcut(infos, shortDistance);
	}

	private LinkInfo getShortcut(List<LinkInfo> infos, int shortDistance) {
		int temp1 = 0;
		LinkInfo result = null;
		for (int i = 0; i < infos.size(); i++) {
			LinkInfo info = infos.get(i);
			int distance = countAll(info.getLinkPoints());
			if (0 == i) {
				temp1 = distance - shortDistance;
				result = info;
			} else if (distance - shortDistance < temp1) {
				temp1 = distance - shortDistance;
				result = info;
			}
		}
		return result;
	}

	private int countAll(List<Point> points) {
		int result = 0;
		for (int i = 0; i < points.size() - 1; i++) {
			Point point1 = points.get(i);
			Point point2 = points.get(i + 1);
			result += getDistance(point1, point2);
		}
		return result;
	}

	private int getDistance(Point p1, Point p2) {
		int xDistance = Math.abs(p1.x - p2.x);
		int yDistance = Math.abs(p1.y - p2.y);
		return xDistance + yDistance;
	}

	private boolean isLeftUp(Point p1, Point p2) {
		return (p2.x < p1.x && p2.y < p1.y);
	}

	private boolean isLeftDown(Point p1, Point p2) {
		return (p2.x < p1.x && p2.y > p1.y);
	}

	private boolean isRightUp(Point p1, Point p2) {
		return (p2.x > p1.x && p2.y < p1.y);
	}

	private boolean isRightDown(Point p1, Point p2) {
		return (p2.x > p1.x && p2.y > p1.y);
	}

	private boolean hasPiece(int x, int y) {
		if (null == findPiece(x, y)) {
			return false;
		}
		return true;
	}

	@Override
	public Map<Integer, List<Point>> getExistImages() {
		return this.existImages;
	}

	@Override
	public void setExistImages(Map<Integer, List<Point>> existImages) {
		this.existImages = existImages;
	}

	@Override
	public void shufflePieces() {
		List<Piece> pieceList = new ArrayList<Piece>();
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {
				if (null != pieces[i][j]) {
					pieceList.add(pieces[i][j]);
				}
			}
		}
		Collections.shuffle(pieceList);
		Piece[][] newPieces = new Piece[pieces.length][pieces[0].length];
		Iterator<Piece> itPiece = pieceList.iterator();
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {
				if (null != pieces[i][j]) {
					Piece oldPiece = itPiece.next();
					Piece piece = new Piece(i, j);
					piece.setImage(oldPiece.getImage());
					piece.setBeginX(piece.getIndexX()
							* oldPiece.getImage().getImage().getWidth()
							+ config.getBeginImageX());
					piece.setBeginY(piece.getIndexY()
							* oldPiece.getImage().getImage().getHeight()
							+ config.getBeginImageY());
					newPieces[i][j] = piece;
				}
			}
		}
		pieces = newPieces;
		createExistImages();
	}
	
	private void createExistImages(){
		existImages.clear();
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {
				if (null != pieces[i][j]) {
					if (null == existImages.get(pieces[i][j].getImage()
							.getImageId())) {
						List<Point> points = new ArrayList<Point>();
						points.add(new Point(i, j));
						existImages.put(pieces[i][j].getImage().getImageId(),
								points);

					} else {
						List<Point> points = existImages.get(pieces[i][j]
								.getImage().getImageId());
						points.add(new Point(i, j));
						existImages.put(pieces[i][j].getImage().getImageId(),
								points);
					}
				}
			}
		}
	}
}
