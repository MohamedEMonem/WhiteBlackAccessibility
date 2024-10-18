package SIM.Mohamed230544975;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

    }

    public static void convertImage(String path, int mode) {
        File file = new File(path);
        try {
            // Read the input image
            BufferedImage inputImage = ImageIO.read(file);
            BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            // Get the file extension (either jpg or png)
            String fileExtension = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
            if (!fileExtension.equals("jpg") && !fileExtension.equals("png")) {
                throw new IllegalArgumentException("Unsupported file format. Use either JPG or PNG.");
            }

            // Process the image based on the mode
            if (mode >= 0 && mode <= 3) {
                processImage(inputImage, outputImage, mode);
            } else {
                throw new IllegalArgumentException("Invalid mode. Use: 0 for Grayscale, 1 for Black-and-White, 2 for Invert, 3 for Black-and-White + Invert.");
            }

            // Define output file path with the same extension as the original image
            String outputFilePath;
            switch (mode) {
                case 0:
                    outputFilePath = path.replace("." + fileExtension, "_grayscale_output." + fileExtension);
                    break;
                case 1:
                    outputFilePath = path.replace("." + fileExtension, "_black_white_output." + fileExtension);
                    break;
                case 2:
                    outputFilePath = path.replace("." + fileExtension, "_inverted_output." + fileExtension);
                    break;
                case 3:
                    outputFilePath = path.replace("." + fileExtension, "_bw_inverted_output." + fileExtension);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + mode); // Should never reach here
            }

            // Write the processed image to the output file
            ImageIO.write(outputImage, fileExtension, new File(outputFilePath));
            JOptionPane.showMessageDialog(null, "Image processed and saved as: " + outputFilePath);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading or writing the image file: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
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
                int grayValue = (red + green + blue) / 3;

                switch (mode) {
                    case 0:
                        // Set the pixel to grayscale
                        Color grayColor = new Color(grayValue, grayValue, grayValue);
                        outputImage.setRGB(x, y, grayColor.getRGB());
                        break;
                    case 1:
                        // Set pixel color to Black & White
                        if (grayValue >= threshold) {
                            outputImage.setRGB(x, y, Color.WHITE.getRGB()); // White pixel
                        } else {
                            outputImage.setRGB(x, y, Color.BLACK.getRGB()); // Black pixel
                        }
                        break;
                    case 2:
                        // Invert Pixel color
                        Color invertColor = new Color(255 - red, 255 - green, 255 - blue);
                        outputImage.setRGB(x, y, invertColor.getRGB());
                        break;
                    case 3:
                        // Set image to Black and White then invert color
                        if (grayValue >= threshold) {
                            // White in Black & White -> Black in inverted
                            outputImage.setRGB(x, y, Color.BLACK.getRGB());
                        } else {
                            // Black in Black & White -> White in inverted
                            outputImage.setRGB(x, y, Color.WHITE.getRGB());
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid mode: " + mode);
                }
            }
        }
    }
}

