package SIM.Mohamed230544975;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        // Create the main application window (JFrame)
        JFrame mainFrame = new JFrame("Image Processing Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the app closes properly
        mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null); // Center the frame
        mainFrame.setVisible(true);

        // Create a Swing UI to choose the image file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image File");

        // Restrict file chooser to JPG and PNG files only
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Open file chooser dialog
        int returnValue = fileChooser.showOpenDialog(mainFrame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();

            if (imagePath.toLowerCase().endsWith(".jpg") || imagePath.toLowerCase().endsWith(".png")) {

                // Replace the single-choice option dialog with checkboxes
                JCheckBox grayscaleCheckbox = new JCheckBox("Grayscale");
                JCheckBox blackAndWhiteCheckbox = new JCheckBox("Black and White");
                JCheckBox invertCheckbox = new JCheckBox("Invert");
                JCheckBox bwInvertCheckbox = new JCheckBox("Black-and-White + Invert");

                JPanel checkboxPanel = new JPanel(new GridLayout(0, 1));
                checkboxPanel.add(grayscaleCheckbox);
                checkboxPanel.add(blackAndWhiteCheckbox);
                checkboxPanel.add(invertCheckbox);
                checkboxPanel.add(bwInvertCheckbox);

                // Show the checkbox panel in a dialog
                int result = JOptionPane.showConfirmDialog(mainFrame, checkboxPanel, "Select Image Processing Modes",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    new Thread(() -> {
                        JDialog processingDialog = createProcessingDialog(mainFrame);
                        processingDialog.setVisible(true);
                    }).start();

                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            // Process each selected mode
                            if (grayscaleCheckbox.isSelected()) {
                                convertImage(imagePath, 0); // Grayscale
                            }
                            if (blackAndWhiteCheckbox.isSelected()) {
                                convertImage(imagePath, 1); // Black and White
                            }
                            if (invertCheckbox.isSelected()) {
                                convertImage(imagePath, 2); // Invert
                            }
                            if (bwInvertCheckbox.isSelected()) {
                                convertImage(imagePath, 3); // Black-and-White + Invert
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            SwingUtilities.invokeLater(() -> {
                                for (Window window : Window.getWindows()) {
                                    if (window instanceof JDialog && window.isVisible()) {
                                        window.dispose(); // Close processing dialog
                                        mainFrame.dispose();
                                    }
                                }
                            });
                        }
                    }.execute(); // Start the SwingWorker
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please select a JPG or PNG file.", "Invalid File", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void convertImage(String path, int mode) {
        File file = new File(path);
        try {
            BufferedImage inputImage = ImageIO.read(file);
            BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            if (mode >= 0 && mode <= 3) {
                processImage(inputImage, outputImage, mode);
            } else {
                throw new IllegalArgumentException("Invalid mode. Use 0 for Grayscale, 1 for Black-and-White, 2 for Invert, 3 for Black-and-White + Invert.");
            }

            // Define output file path with user selection for saving
            JFileChooser saveFileChooser = new JFileChooser();
            saveFileChooser.setDialogTitle("Save Processed Image");
            switch (mode) {
                case 0:
                    saveFileChooser.setSelectedFile(new File(path + "_Grayscale_output." + path.substring(path.lastIndexOf('.') + 1).toLowerCase()));
                    break;
                case 1:
                    saveFileChooser.setSelectedFile(new File(path + "_BK&W_output." + path.substring(path.lastIndexOf('.') + 1).toLowerCase()));
                    break;
                case 2:
                    saveFileChooser.setSelectedFile(new File(path + "_Inverted-colors_output." + path.substring(path.lastIndexOf('.') + 1).toLowerCase()));
                    break;
                case 3:
                    saveFileChooser.setSelectedFile(new File(path + "_BKW&inverted_output." + path.substring(path.lastIndexOf('.') + 1).toLowerCase()));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid mode: " + mode);

            }

            int saveReturnValue = saveFileChooser.showSaveDialog(null);
            if (saveReturnValue == JFileChooser.APPROVE_OPTION) {
                File outputFile = saveFileChooser.getSelectedFile();
                ImageIO.write(outputImage, "jpg", outputFile);
                JOptionPane.showMessageDialog(null, "Image saved successfully: " + outputFile.getAbsolutePath());
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading or writing the image file: " + e.getMessage());
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

                // Calculate grayscale value (luminance)
                int grayValue = (red + green + blue) / 3;

                switch (mode) {
                    case 0:
                        // Grayscale
                        Color grayColor = new Color(grayValue, grayValue, grayValue);
                        outputImage.setRGB(x, y, grayColor.getRGB());
                        break;
                    case 1:
                        // Black and White
                        outputImage.setRGB(x, y, grayValue >= threshold ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
                        break;
                    case 2:
                        // Invert colors
                        Color invertedColor = new Color(255 - red, 255 - green, 255 - blue);
                        outputImage.setRGB(x, y, invertedColor.getRGB());
                        break;
                    case 3:
                        // Black and White + Invert
                        outputImage.setRGB(x, y, grayValue >= threshold ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid mode: " + mode);
                }
            }
        }
    }

    private static JDialog createProcessingDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Processing Image...", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(parentFrame);

        JLabel label = new JLabel("Processing... Please wait.", SwingConstants.CENTER);
        dialog.add(label);
        return dialog;
    }
}
