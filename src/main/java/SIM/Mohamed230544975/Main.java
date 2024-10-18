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
        // Create a Swing UI to choose the image file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image Files");
        fileChooser.setMultiSelectionEnabled(true);  // Enable multiple selection

        // Restrict file chooser to JPG and PNG files only
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Open file chooser dialog
        int returnValue = fileChooser.showOpenDialog(null); // Pass null to use the default parent window

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();  // Get multiple files

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
            int result = JOptionPane.showConfirmDialog(null, checkboxPanel, "Select Image Processing Modes",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                // Ask for a folder to save the processed images
                JFileChooser folderChooser = new JFileChooser();
                folderChooser.setDialogTitle("Select Folder to Save Processed Images");
                folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int folderReturnValue = folderChooser.showSaveDialog(null);

                if (folderReturnValue == JFileChooser.APPROVE_OPTION) {
                    File saveFolder = folderChooser.getSelectedFile();

                    // Show a progress dialog during processing
                    JDialog progressDialog = new JDialog();
                    JLabel progressLabel = new JLabel("Processing images, please wait...");
                    progressDialog.add(progressLabel);
                    progressDialog.setSize(300, 100);
                    progressDialog.setLocationRelativeTo(null); // Center the dialog
                    progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Prevent closing

                    // Create and start the SwingWorker
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            for (File file : selectedFiles) {
                                String imagePath = file.getAbsolutePath();

                                if (grayscaleCheckbox.isSelected()) {
                                    processAndSaveImage(imagePath, 0, saveFolder); // Grayscale
                                }
                                if (blackAndWhiteCheckbox.isSelected()) {
                                    processAndSaveImage(imagePath, 1, saveFolder); // Black and White
                                }
                                if (invertCheckbox.isSelected()) {
                                    processAndSaveImage(imagePath, 2, saveFolder); // Invert
                                }
                                if (bwInvertCheckbox.isSelected()) {
                                    processAndSaveImage(imagePath, 3, saveFolder); // Black-and-White + Invert
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            SwingUtilities.invokeLater(() -> {
                                // Close the progress dialog
                                progressDialog.dispose();

                                // Show a success message
                                JOptionPane.showMessageDialog(null, "Image processing completed successfully!");
                            });
                        }
                    };

                    // Show the progress dialog before starting the worker
                    SwingUtilities.invokeLater(() -> progressDialog.setVisible(true));

                    worker.execute(); // Start the SwingWorker
                }
            }
        }
    }

    public static void processAndSaveImage(String imagePath, int mode, File saveFolder) {
        File file = new File(imagePath);
        try {
            BufferedImage inputImage = ImageIO.read(file);
            BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            processImage(inputImage, outputImage, mode);

            // Generate the output file path
            String suffix;
            switch (mode) {
                case 0:
                    suffix = "_Grayscale";
                    break;
                case 1:
                    suffix = "_BK&W";
                    break;
                case 2:
                    suffix = "_Inverted";
                    break;
                case 3:
                    suffix = "_BKW_Inverted";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid mode: " + mode);
            }

            String fileName = file.getName();
            String newFileName = fileName.substring(0, fileName.lastIndexOf('.')) + suffix + ".jpg";
            File outputFile = new File(saveFolder, newFileName);

            // Save the image
            ImageIO.write(outputImage, "jpg", outputFile);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error processing the image: " + e.getMessage());
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
}
