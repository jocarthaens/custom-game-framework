package gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

// Helper class that contains static methods for loading, creating, and modifying images
// including fonts and rendering text outlines.


public class GraphicsUtils {
	
	
	public static BufferedImage loadImage(String filePath) {
		try {
			return ImageIO.read(GraphicsUtils.class.getResourceAsStream(filePath));
		} catch (IOException | IllegalArgumentException e) {
			System.out.println("Could not load image from path: " + filePath);
			e.printStackTrace();
		}
		return null;
	}
	
	public static BufferedImage createCompatibleImage(int width, int height, int transparency) {
        GraphicsConfiguration graphicsConfig = GraphicsEnvironment.getLocalGraphicsEnvironment()
        		.getDefaultScreenDevice().getDefaultConfiguration();
        return graphicsConfig.createCompatibleImage(width, height, transparency);
    }
	
	public static BufferedImage scaleImage(BufferedImage original, int width, int height) {	
		BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
		Graphics2D g2 = scaledImage.createGraphics();
		g2.drawImage(original, 0, 0, width, height, null);
		g2.dispose();
		
		return scaledImage;
	}
	
	public static BufferedImage ninePatchImage(BufferedImage original, int width, int height, int left, int right, int top, int bottom) {
		
		BufferedImage patchedImage = new BufferedImage(width, height, original.getType());
		Graphics2D g2 = patchedImage.createGraphics();
		
		int origWidth = original.getWidth();
		int origHeight = original.getHeight();
		
		// Corners
		g2.drawImage(original, 0, 0, left, top, 0, 0, left, top, null); // top-left
		g2.drawImage(original, width - right, 0, width, top, origWidth - right, 0, origWidth, top, null); // top-right
		g2.drawImage(original, 0, height - bottom, left, height, 0, origHeight - bottom, left, origHeight, null); // bot-left
		g2.drawImage(original, width - right, height - bottom, width, height, origWidth - right, origHeight - bottom, origWidth, origHeight, null); // bot-right
		
		// Edges
		g2.drawImage(original, left, 0, width - right, top, left, 0, origWidth - right, top, null); // top
		g2.drawImage(original, left, height - bottom, width - right, height, left, origHeight - bottom, origWidth - right, origHeight, null); // bottom
		g2.drawImage(original, 0, top, left, height - bottom, 0, top, left, origHeight - bottom, null); // left
		g2.drawImage(original, width - right, top, width, height - bottom, origWidth - right, top, origWidth, origHeight - bottom, null); // left
		
		// Center
		g2.drawImage(original, left, top, width - right, height - bottom, left, top, origWidth - right, origHeight - bottom, null); // center
		
        g2.dispose();
        
		return patchedImage;
	}
	
	public static BufferedImage getImageRegion(BufferedImage image, int regionX, int regionY, int regionWidth, int regionHeight) {
		BufferedImage region = image.getSubimage(regionX, regionY, regionWidth, regionHeight);
		return region;
	}
	
	public static List<BufferedImage> splitImage(BufferedImage image, List<BufferedImage> outSplitImages,
    		int width, int height) {
		return splitImage(image, outSplitImages, width, height, 0, 0, -1, true, true, false);
	}
	
	public static List<BufferedImage> splitImage(BufferedImage image, List<BufferedImage> outSplitImages,
    		int width, int height, int xIndex, int yIndex) {
		return splitImage(image, outSplitImages, width, height, xIndex, yIndex, -1, true, true, false);
	}
	
	public static List<BufferedImage> splitImage(BufferedImage image, List<BufferedImage> outSplitImages,
    		int width, int height, int xIndex, int yIndex, int count) {
		return splitImage(image, outSplitImages, width, height, xIndex, yIndex, count, true, true, false);
	}
	
	public static List<BufferedImage> splitImage(BufferedImage image, List<BufferedImage> outSplitImages,
    		int width, int height, int xIndex, int yIndex, int count,
    		boolean leftToRight, boolean topToBottom, boolean verticalSequence) {
    	
    	if (width <= 0 || height <= 0) {
    		throw new IllegalArgumentException(
    				"Width and height should be greater than 0. "
    				+ "\n width = "+width+", height = "+height+".");
    	}
    	
    	
    	if (image.getWidth() % width != 0 || image.getHeight() % height != 0) {
			throw new IllegalArgumentException(
					"Specified dimensions doesn't allow"
					+ " splitting images equally.");
		}
    	
    	if (width > image.getWidth() || height > image.getHeight()) {
			throw new IllegalArgumentException("Specified dimensions "
					+ "exceeds image's dimensions. "
					+ "\n width = "+width+", height = "+height+"."
					+ "\n imageWidth = "+image.getWidth()+", imageHeight = "+image.getHeight()+".");
		}
    	
    	int h = image.getWidth() / width;
    	int v = image.getHeight() / height;
    	
    	int x = xIndex >= 0 ? xIndex * width : (h - 1) * width;
    	int y = yIndex >= 0 ? yIndex * height : (v - 1) * height;
    	
    	int c = count > 0 ? count : h * v;
    	
    	if (!verticalSequence) {    		
    		for (int i=0; i < c; i++) {
        		if ((leftToRight && x >= image.getWidth()) || (!leftToRight && x <= 0)) {
        			x = leftToRight ? 0 : image.getWidth() - width;
        			y = topToBottom ? y + height : y - height;
        			if ((topToBottom && y >= image.getHeight()) || (!topToBottom && y <= 0)) {
            			break;
            		}
        		}
        		outSplitImages.add(image.getSubimage(x, y, width, height));
        		x = leftToRight ? x + width : x - width;
        	}
    	}
    	else {
    		for (int i=0; i < c; i++) {
        		if ((topToBottom && y >= image.getHeight()) || (!topToBottom && y <= 0)) {
        			y = topToBottom ? 0 : image.getHeight() - height;
        			x = leftToRight ? x + width : x - width;
        			if ((leftToRight && x >= image.getWidth()) || (!leftToRight && x <= 0)) {
            			break;
            		}
        		}
        		outSplitImages.add(image.getSubimage(x, y, width, height));
        		y = topToBottom ? y + height : y - height;
        		
        	}
    	}
    	return outSplitImages;
    }
	
	//add transparency adjustment, color blending, and rotation;
	
	public static Font loadFont(String filePath) {
		try {
			InputStream is = GraphicsUtils.class.getResourceAsStream(filePath);
			Font.createFont(Font.TRUETYPE_FONT, is);
			return Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * If maxWidth is any negative number or Float.MAX_VALUE, 
	 * this method will return a list containing 
	 * the whole text as 1 entry.
	 */
	public static List<String> splitString(String text, float maxWidth, FontMetrics fontMetric) {
		List<String> result = new ArrayList<String>();
		StringBuilder line = new StringBuilder();
		String[] words = text.split(" ");
		
		if (maxWidth < 0 || maxWidth == Float.MAX_VALUE) {
			result.add(text);
			return result;}
		
		for (String word: words) {
			if (fontMetric.stringWidth(line.toString() + word) <= maxWidth) {
				line.append(word).append(" ");
			}
			else {
				if (line.length() > 0) {
					result.add(line.toString().trim());
					line.setLength(0);}
				if (fontMetric.stringWidth(word) <= maxWidth) {
					line.append(word).append(" ");
				} 
				else {
					String newWord = word + " ";
					for (char c: newWord.toCharArray()) {
						if (fontMetric.stringWidth(line.toString() + c) <= maxWidth) {
							line.append(c);
						}
						else {
							result.add(line.toString());
							line.setLength(0);
							line.append(c);
						}
					}
					if (line.length() > 0) {
						result.add(line.toString());
						line.setLength(0);}
				}
			}
		}
		
		if (line.length() > 0) {
			result.add(line.toString().trim());
			line.setLength(0);}
		
		return result;
	}
	
	
	
	public static void drawTextOutline(BufferedImage canvas, String text, Font font, int startX, int startY, int thickness, Color boundaryColor) {
        // Render the string onto a temporary image
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        BufferedImage textImage = new BufferedImage(width, height, canvas.getType());
        Graphics2D g2d = textImage.createGraphics();
        g2d.setFont(font);
        g2d.setColor(Color.WHITE); // Render in white to easily identify text pixels
        g2d.drawString(text, startX, startY);
        g2d.dispose();
        
        int newThickness = thickness > 0 ? Math.max(thickness / 2 , 1) : 0;
        
        // Identify and draw the boundary pixels outward
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isBoundaryPixel(textImage, x, y)) {
                    // Draw the boundary outward
                	drawOutward(textImage, canvas, x, y, newThickness, boundaryColor);
                }
            }
        }
    }
    // Check if a pixel is on the boundary of the text (pixel must be opaque and at least one neighboring pixel is transparent)
    private static boolean isBoundaryPixel(BufferedImage textImg, int x, int y) {
        int pixel = textImg.getRGB(x, y);
        // Checks if this pixel is part of the rendering text (must be opaque)
        if ((pixel & 0xFF000000) == 0xFF000000) { 
            // Check neighboring pixels (left, right, up, and down) to see if any are not part of the rendering text (must be transparent)
            return (x > 0 					 && (textImg.getRGB(x - 1, y) & 0xFF000000) == 0) ||
                   (x < textImg.getWidth() - 1  && (textImg.getRGB(x + 1, y) & 0xFF000000) == 0) ||
                   (y > 0 					 && (textImg.getRGB(x, y - 1) & 0xFF000000) == 0) ||
                   (y < textImg.getHeight() - 1 && (textImg.getRGB(x, y + 1) & 0xFF000000) == 0);
        }
        return false;
    }
    // Draw the boundary pixels outward from the given (x, y) position
    private static void drawOutward(BufferedImage textImg, BufferedImage canvas, int x, int y, int thickness, Color boundaryColor) {
    	
    	// loops through each pixel within thickness distance, forming a square with length 2 * thickness + 1, centered at (x,y)
        for (int dx = -thickness; dx <= thickness; dx++) {
            for (int dy = -thickness; dy <= thickness; dy++) {
            	
            	// solves for distance to check if they're within thickness (both are square distance for performance)
            	int dist2 = dx * dx + dy * dy; 
                if (dist2 <= thickness * thickness) {
                	
                    int nx = x + dx; // dx is added to x to find nx, the x-component of the pixel to be rendered as outline
                    int ny = y + dy; // dy is added to y to find nx, the y-component of the pixel to be rendered as outline
                    
                    // checks if nx and ny is within the image boundaries
                    if (nx >= 0 && nx < canvas.getWidth() && ny >= 0 && ny < canvas.getHeight()) {
                    	
                    	// Ensures the pixel to be rendered is not part of the rendered text
                        int origPixel = textImg.getRGB(nx, ny);
                        if ((origPixel & 0xFF000000) == 0) { 
                        	// If all things a true, the pixel is drawn to the image with the boundary color
                        	canvas.setRGB(nx, ny, boundaryColor.getRGB());
                        }
                    }
                }
            }
        }
    }
	
}
