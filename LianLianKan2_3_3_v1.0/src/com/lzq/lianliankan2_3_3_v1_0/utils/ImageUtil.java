/**
 * 
 */
package com.lzq.lianliankan2_3_3_v1_0.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lzq.lianliankan2_3_3_v1_0.R;
import com.lzq.lianliankan2_3_3_v1_0.conf.GameConf;
import com.lzq.lianliankan2_3_3_v1_0.model.PieceImage;

/**
 * @author Administrator
 * 
 */
public class ImageUtil {
	private static List<Integer> imageValues = getImageValues();

	public static void refreshImageValues() {
		imageValues = getImageValues();
	}

	/**
	 * 
	 * @return
	 */
	public static List<Integer> getImageValues() {
		try {
			// Field[] drawableFields = R.drawable.class.getFields();
			List<Integer> resourceValues = new ArrayList<Integer>();
			// for (Field field : drawableFields) {
			// if (field.getName().startsWith("p_")) {
			// resourceValues.add(field.getInt(R.drawable.class));
			// }
			// }

			File file = new File(GameConf.getBaseFileName());
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.getName().startsWith("ci")) {
					Integer resourceValue = 0xFF000000 + Integer.valueOf(f
							.getName().split("[-|.]")[1]);
					resourceValues.add(resourceValue);
				}
			}
			return resourceValues;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isEmpty() {
		File file = new File(GameConf.getBaseFileName());
		File[] files = file.listFiles();
		if (files.length > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @param sourceValues
	 * @param size
	 * @return
	 */
	public static List<Integer> getRandomValues(List<Integer> sourceValues,
			int size) {
		Random random = new Random();
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			try {
				int index = random.nextInt(sourceValues.size());
				Integer image = sourceValues.get(index);
				result.add(image);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return result;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	public static List<Integer> getPlayValues(int size) {
		if (size % 2 != 0) {
			size += 1;
		}
		List<Integer> playImageValues = getRandomValues(imageValues, size / 2);
		playImageValues.addAll(playImageValues);
		Collections.shuffle(playImageValues);
		return playImageValues;
	}

	/**
	 * 
	 * @param context
	 * @param size
	 * @return
	 */
	public static List<PieceImage> getPlayImages(Context context, int size) {
		List<Integer> resourceValues = getPlayValues(size);
		List<PieceImage> result = new ArrayList<PieceImage>();
		for (Integer value : resourceValues) {
			if (Integer.toHexString(value).startsWith("FF")
					|| Integer.toHexString(value).startsWith("ff")) {
				int number = 0x00FFFFFF & value;
				Bitmap bm = BitmapFactory.decodeFile(GameConf.getBaseFileName()
						+ "/ci-" + number + ".png");
				PieceImage pieceImage = new PieceImage(bm, value);
				result.add(pieceImage);
			} else {
				Bitmap bm = BitmapFactory.decodeResource(
						context.getResources(), value);
				PieceImage pieceImage = new PieceImage(bm, value);
				result.add(pieceImage);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static Bitmap getSelectImage(Context context) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.selected);
		return bm;
	}
}
