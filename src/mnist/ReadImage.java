package mnist;

import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ReadImage {

	public int height;
	public int width;
	private int[] grayScaleImgData;

	public int[] readImage(String imgLocation) {
		try {
			BufferedImage image = ImageIO.read(new File(imgLocation));

			height = image.getHeight();
			width = image.getWidth();
			
			int[] pixel;
			grayScaleImgData = new int[height*width];
			// reading image and converting to gray scale
			for (int i = 0; i < height; i++) {
			    for (int j = 0; j < width; j++) {
			        pixel = image.getRaster().getPixel(i, j, new int[3]);
			        grayScaleImgData[i*height + j] = pixel[0];
			    }
			}
			
			return grayScaleImgData;
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return null;
		}
	}
		
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
}
