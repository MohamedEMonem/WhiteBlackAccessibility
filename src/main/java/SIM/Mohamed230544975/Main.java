package SIM.Mohamed230544975;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String imagePath = "C:\\Users\\moham\\IdeaProjects\\white_black_accessibility\\src\\main\\java\\SIM\\Mohamed230544975\\image2.jpg";
        convertImage(imagePath, 0);
        convertImage(imagePath, 1);
    }
    public static void convertImage(String path, int mode) {
        File file = new File(path);
        try {
            BufferedImage inputImage = ImageIO.read(file);
            BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            if (mode == 0) {
                processImage(inputImage, outputImage, 0); // Black and White
            } else if (mode == 1) {
                processImage(inputImage, outputImage, 1); // Grayscale
            } else {
                throw new IllegalArgumentException("Invalid mode. Use 0 for Black-and-White and 1 for Grayscale.");
            }

            String outputFilePath = path + (mode == 0 ? "_black_white_output.jpg" : "_grayscale_output.jpg");
            ImageIO.write(outputImage, "jpg", new File(outputFilePath));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
            throw new RuntimeException(e);
        }
    }
    private static void processImage(BufferedImage inputImage, BufferedImage outputImage, int mode) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        int threshold = 128;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelColor = inputImage.getRGB(x, y);

                // Extract color components
                Color color = new Color(pixelColor);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Calculate the grayscale value (luminance)
                int grayValue = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

                if (mode == 1) {
                    // Set the pixel to grayscale
                    Color grayColor = new Color(grayValue, grayValue, grayValue);
                    outputImage.setRGB(x, y, grayColor.getRGB());
                }
                if (mode == 0) {
                    if (grayValue >= threshold) {
                        outputImage.setRGB(x, y, Color.WHITE.getRGB()); // White pixel
                    } else {
                        outputImage.setRGB(x, y, Color.BLACK.getRGB()); // Black pixel
                    }
                }
            }
        }
    }
}
