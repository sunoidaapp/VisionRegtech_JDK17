package com.vision.authentication;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import javax.imageio.ImageIO;

import com.vision.util.ValidationUtil;

public class capctha {

    private static final SecureRandom random = new SecureRandom();
    public static String generateCaptchaImageBase64(String text) {
    	String base64Img = "";
		int width = 150;
		int height = 50;
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		// Background
		Color bgColor = Color.white;
		g2d.setColor(bgColor);
		g2d.fillRect(0, 0, width, height);

		// Fonts
		Font[] fonts = { new Font("Arial", Font.PLAIN, 40), new Font("Courier", Font.ITALIC, 40), };

		// Draw characters with random color, rotation, and font
		for (int i = 0; i < text.length(); i++) {
			g2d.setFont(fonts[random.nextInt(fonts.length)]);
//	            g2d.setColor(getRandomColor(30, 150));
			g2d.setColor(Color.BLACK);

			AffineTransform original = g2d.getTransform();
			AffineTransform rotate = new AffineTransform();

			int x = 20 + i * 20;
			int y = 35 + random.nextInt(5); // vertical jitter

			rotate.rotate((random.nextDouble() - 0.5) * 0.5, x, y);
			g2d.setTransform(rotate);

			g2d.drawString(String.valueOf(text.charAt(i)), x, y);
			g2d.setTransform(original);
		}

		// Add noise lines
		g2d.setStroke(new BasicStroke(1));
		for (int i = 0; i < 5; i++) {
			g2d.setColor(getRandomColor(160, 200));
			int x1 = random.nextInt(width);
			int y1 = random.nextInt(height);
			int x2 = random.nextInt(width);
			int y2 = random.nextInt(height);
			g2d.drawLine(x1, y1, x2, y2);
		}

		g2d.dispose();

        // Encode to base64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            byte[] bytes = baos.toByteArray();
            return  Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("CAPTCHA generation failed", e);
        }
    }

    private static Color getRandomColor(int min, int max) {
        int r = min + random.nextInt(max - min);
        int g = min + random.nextInt(max - min);
        int b = min + random.nextInt(max - min);
        return new Color(r, g, b);
    }

	public static String generateCaptchaImageBase64o(String text) {
		String base64Img = "";

		Font font = new Font("Arial", Font.PLAIN, 32);
		BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.setFont(font);
		FontMetrics fm = g2.getFontMetrics();

		int paddingLeft = 10;
		int paddingRight = 10;
		int spacing = 5;  // space between chars

		// Calculate width of entire text with spacing between chars
		int totalWidth = paddingLeft + paddingRight;
		for (int i = 0; i < text.length(); i++) {
		    totalWidth += fm.charWidth(text.charAt(i));
		    if (i < text.length() - 1) {
		        totalWidth += spacing; // spacing between chars
		    }
		}

		int height = 60;
		g2.dispose();

		BufferedImage image = new BufferedImage(totalWidth, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		// Transparent background
		g2d.setComposite(AlphaComposite.Clear);
		g2d.fillRect(0, 0, totalWidth, height);
		g2d.setComposite(AlphaComposite.SrcOver);

		g2d.setFont(font);
		g2d.setColor(Color.BLACK);
		fm = g2d.getFontMetrics();

		// Draw each character manually with spacing
		int x = paddingLeft;
		int y = (height + fm.getAscent()) / 2 - 5;

		for (char c : text.toCharArray()) {
		    g2d.drawString(String.valueOf(c), x, y);
		    x += fm.charWidth(c) + spacing;
		}

		g2d.dispose();

		// Encode to Base64
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ImageIO.write(bufferedImage, "png", baos);
			byte[] imageBytes = baos.toByteArray();
			return Base64.getEncoder().encodeToString(imageBytes);
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate CAPTCHA image", e);
		}
	}

	public static void main(String[] args) {
		String PWD= ValidationUtil.generateOTP("C");
		System.out.println(PWD);
		System.out.println(generateCaptchaImageBase64(PWD));
	}
}
