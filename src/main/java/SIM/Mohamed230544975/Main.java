package SIM.Mohamed230544975;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Create a Swing UI to choose the image file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image File");

        // Open file chooser dialog
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();

            // Create an option pane for user to choose the processing mode
            String[] options = {"Black and White", "Grayscale"};
            int mode = JOptionPane.showOptionDialog(null, "Choose the image processing mode:",
                    "Image Processing Mode", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (mode != JOptionPane.CLOSED_OPTION) {
                // Start a new thread for the processing dialog
                new Thread(() -> {
                    JDialog processingDialog = createProcessingDialog();
                    processingDialog.setVisible(true); // Show the dialog before processing
                }).start();

                // Run the processing in a background thread
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        convertImage(imagePath, mode); // Call the conversion method
                        return null;
                    }

                    @Override
                    protected void done() {
                        // Close the processing dialog in the Event Dispatch Thread
                        SwingUtilities.invokeLater(() -> {
                            for (Window window : Window.getWindows()) {
                                if (window instanceof JDialog && window.isVisible()) {
                                    window.dispose(); // Close any visible dialog
                                }
                            }
                        });
                    }
                }.execute(); // Start the SwingWorker
            }
        }
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

            // Allow user to choose where to save the output image
            JFileChooser saveFileChooser = new JFileChooser();
            saveFileChooser.setDialogTitle("Save Processed Image");
            saveFileChooser.setSelectedFile(new File(path + (mode == 0 ? "_black_white_output.jpg" : "_grayscale_output.jpg")));

            int saveReturnValue = saveFileChooser.showSaveDialog(null);
            if (saveReturnValue == JFileChooser.APPROVE_OPTION) {
                File outputFile = saveFileChooser.getSelectedFile();
                ImageIO.write(outputImage, "jpg", outputFile);
                JOptionPane.showMessageDialog(null, "Image saved successfully: " + outputFile.getAbsolutePath());
            }

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

    private static JDialog createProcessingDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Processing Image...");
        dialog.setModal(true); // Prevent interaction with other windows
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(null); // Center the dialog

        JLabel label = new JLabel("Processing... Please wait.", SwingConstants.CENTER);
        dialog.add(label);
        return dialog; // Return the dialog without showing it yet
    }
}
