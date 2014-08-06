package org.room325.yzm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class YZM2 {

	private static final int COLOR_RED = -261628;

	public static String matches(int[] target) {
		for (int i = 0; i < TZ.tzs.length; i++) {
			if (isSame(target, TZ.tzs[i])) {
				return i + "";
			}
		}
		return "X";
	}

	private static boolean isSame(int[] target, int[] tz) {
		int diff = 0;
		for (int i = 0; i < tz.length; i++) {
			diff += Math.abs(target[i] - tz[i]);
		}
		if (diff > 2) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param img
	 *            整个的验证码图片
	 * @return 正确的验证码字符串
	 */
	public static String getCode(BufferedImage img) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {
			BufferedImage sub = img.getSubimage(10 + i * 12, 0, 8, 40);

			boolean hasRed = hasColor(sub, COLOR_RED);
			if (hasRed) {
				String subcode = getOneCode(sub);
				if (subcode.equals("X")) {
					System.out.println("BUG!!!!!!!!!!!!");
				} else {
					sb.append(subcode);
				}
			}

		}
		return sb.toString();
	}

	private static String getOneCode(BufferedImage sub) {
		BufferedImage realSub;
		for (int i = 0; i < sub.getHeight(); i++) {
			for (int j = 0; j < sub.getWidth(); j++) {
				if (sub.getRGB(j, i) == COLOR_RED) {
					realSub = sub.getSubimage(0, i, 8, 10);
					return getCodeFromRealSub(realSub);
				}
			}
		}
		System.out.println("BUG::can not found sub");
		return "X";
	}

	private static String getCodeFromRealSub(BufferedImage realSub) {
		int[] target = new int[80];
		int count = 0;
		for (int i = 0; i < realSub.getHeight(); i++) {
			for (int j = 0; j < realSub.getWidth(); j++) {
				if (realSub.getRGB(j, i) == COLOR_RED) {
					target[count++] = 1;
				} else {
					target[count++] = 0;
				}
			}
		}
		return matches(target);

	}

	private static boolean hasColor(BufferedImage img, int rgb) {
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				if (img.getRGB(i, j) == rgb) {
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String[] args) {
		long l1 = System.currentTimeMillis();

		String path = "G:\\yzm2\\1748.gif";
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(new File(path));
			String result = getCode(bufferedImage);
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}

		long l2 = System.currentTimeMillis();
		System.out.println(l2);
		System.out.println("time=" + (l2 - l1));
	}

}
