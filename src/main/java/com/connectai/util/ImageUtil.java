package com.connectai.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

public class ImageUtil {
    public static BufferedImage makeCircularImage(BufferedImage source, int targetDiameter) {
        if (source == null) return null;
        
        BufferedImage scaled = scaleImage(source, targetDiameter, targetDiameter);
        BufferedImage output = new BufferedImage(targetDiameter, targetDiameter, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillOval(0, 0, targetDiameter, targetDiameter);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();
        
        return output;
    }

    public static BufferedImage scaleImage(BufferedImage source, int targetWidth, int targetHeight) {
        if (source == null) return null;
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        g2.dispose();
        return resized;
    }

    public static byte[] imageToBytes(BufferedImage image, String format) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static BufferedImage bytesToImage(byte[] bytes) {
        try {
            if (bytes == null || bytes.length == 0) return null;
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            return null;
        }
    }
}
