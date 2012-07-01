package in.partake.base;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageUtil {

    public static BufferedImage createThumbnail(BufferedImage image, int maxWidth, int maxHeight) {
        double scaleW = image.getWidth() <= maxWidth ? 1.0 : (double) maxWidth / image.getWidth();
        double scaleH = image.getHeight() <= maxHeight ? 1.0 : (double) maxHeight / image.getHeight();
        double scale = Math.min(scaleW, scaleH);

        int width = (int) (scale * image.getWidth());
        int height = (int) (scale * image.getHeight());

        BufferedImage shrinkImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = shrinkImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2d.drawImage(image, 0, 0, width, height, null);

        return shrinkImage;
    }
}
