package org.room325.yzm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import javax.imageio.ImageIO;

public class YZM {

	public static String matches(int[] target) {
		for (int i = 1; i < TZ.tzs.length; i++) {
			if (isSame(target, TZ.tzs[i])) {
				return i + "";
			}
		}
		return "X";
	}

	// 新算法
	public static String matches2(int[] target) {
		for (int i = 1; i < TZ.tzs.length; i++) {
			if (isSame2(target, TZ.tzs[i])) {
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

		if (diff > 5) {
			return false;
		}
		return true;
	}

	// 新算法
	private static boolean isSame2(int[] target, int[] tz) {
		int tc = 0;
		int miss = 0;
		int confuse = 0;
		for (int i = 0; i < tz.length; i++) {

			if (tz[i] == 1 && target[i] == 1) {
				tc++;
			} else if (tz[i] == 1 && target[i] == 0) {
				miss++;
			} else if (tz[i] == 0 && target[i] == 1) {
				// 随机点的颜色恰好和数字的颜色一样
				confuse++;
			}

		}
		// 这个if的判断条件很重要
		if (miss < 10 && tc > 20 && confuse < 5) {
			return true;
		} else {
			return false;
		}

	}

	public static String getTextFromYZM(BufferedImage yzm) {

		StringBuilder sb = new StringBuilder();
		for (int count = 0; count < 4; count++) {
			BufferedImage img = yzm.getSubimage(2 + 10 * count, 4, 8, 10);
			int[] target = new int[80];
			int tc = 0;

			for (int i = 0; i < img.getHeight(); i++) {
				for (int j = 0; j < img.getWidth(); j++) {
					if (img.getRGB(j, i) == -657931) {
						target[tc++] = 0;
					} else {
						target[tc++] = 1;
					}
				}
			}

			String str = matches(target);

			if (!str.equals("X")) {// 识别出来了
				sb.append(str);
			} else {// 常规判断方式没有识别出来
				tc = 0;
				for (int i = 0; i < img.getHeight(); i++) {
					for (int j = 0; j < img.getWidth(); j++) {
						if (img.getRGB(j, i) == -657931) {
							// do nothing
							// target[tc++]=0;
						} else {
							target[tc++] = img.getRGB(j, i);
						}
					}
				}

				List<ColorNum> list = new ArrayList<ColorNum>();

				for (int i = 0; i < target.length; i++) {
					if (target[i] == 0) {
						break;
					}
					ColorNum colorNum = new ColorNum(target[i]);
					if (list.contains(colorNum)) {
						list.get(list.indexOf(colorNum)).add();
					} else {
						list.add(colorNum);
					}
				}

				// 得到数量最多的颜色的RGB值
				ColorNum maxValue = new ColorNum(-1);
				for (ColorNum colorNum : list) {
					if (colorNum.count > maxValue.count) {
						maxValue = colorNum;
					}
				}

				// 得到新算法的特征值数组
				tc = 0;

				lab: for (int i = 0; i < img.getHeight(); i++) {
					for (int j = 0; j < img.getWidth(); j++) {
						if (img.getRGB(j, i) == 0) {
							break lab;
						}
						if (img.getRGB(j, i) == maxValue.value) {
							target[tc++] = 1;
						} else {
							target[tc++] = 0;
						}
					}
				}

				str = matches2(target);
				sb.append(str);

			}

		}
		return sb.toString();
	}

	
	public static void main(String[] args) throws IOException {
		// String path = "C:\\Users\\user\\Desktop\\yz1.png";
		// BufferedImage yz1 = ImageIO.read(new File(path));
		long l1=System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			String path = "G:\\yzm1\\" + i + ".png";
			BufferedImage yz1 = ImageIO.read(new File(path));
			System.out.println(getTextFromYZM(yz1));
		}
		long l2=System.currentTimeMillis();
		System.out.println("time="+(l2-l1));
	}
}
