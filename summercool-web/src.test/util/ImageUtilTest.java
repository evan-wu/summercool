package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.util.FileCopyUtils;
import org.summercool.util.ImageUtil;

public class ImageUtilTest {

	public static void main(String[] args) throws IOException {
		FileInputStream in = new FileInputStream(new File("D:/gif/0.gif"));
		FileOutputStream out = new FileOutputStream(new File("D:/gif/g-bk_b.png"));
		try {

			// resizeGif(in, out, 600, 600, 1f, null, null, null);

			// resizeJpg(in, out, 640, 640, 0.85f, new String[] { "@王少-_-",
			// "weibo.com/dragonsoar" }, FONT, FONT_COLOR);

			// makePng(new String(FileCopyUtils.copyToByteArray(new
			// File("D:/gif/g.txt")), "UTF-8"), out, 598, 16,
			// new Font("微软雅黑", Font.PLAIN, 16), new Color(0, 0, 0, 200));

			// makePng(new String(FileCopyUtils.copyToByteArray(new
			// File("D:/gif/g.txt")), "UTF-8"), out, 598, -1,
			// new Font("微软雅黑", Font.PLAIN, 14), new Color(0, 0, 0, 200));

			byte[] bytes = FileCopyUtils.copyToByteArray(new File("D:/gif/gsyh.txt"));
			Font pngFont = new Font("微软雅黑", Font.PLAIN, 14);

			Color pngFontColor = new Color(0, 0, 0, 200);
			Font watermarkFont = new Font("微软雅黑", Font.BOLD, 12);
			Color watermarkFontColor = new Color(0, 0, 0, 150);
			//
			long begin = System.currentTimeMillis();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageUtil.makePng(new String(bytes), baos, 598, -1, pngFont, pngFontColor);
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
			ImageUtil.makeWatermark(new String[] { "@王少-_-", "weibo.com/dragonsoar" }, image, watermarkFont, watermarkFontColor);
			ImageIO.write(image, "png", out);
			System.out.println(System.currentTimeMillis() - begin);

			// long begin = System.currentTimeMillis();
			// for (int i = 0; i < 10; i++) {
			// baos = new ByteArrayOutputStream();
			// makePng(new String(bytes), baos, 598, -1, pngFont, pngFontColor);
			// image = ImageIO.read(new
			// ByteArrayInputStream(baos.toByteArray()));
			// makeWatermark(new String[] { "@王少-_-", "weibo.com/dragonsoar" },
			// image, watermarkFont, watermarkFontColor);
			// ImageIO.write(image, "png", out);
			// }
			// System.out.println(System.currentTimeMillis() - begin);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			in.close();
			out.close();
		}
	}

}
