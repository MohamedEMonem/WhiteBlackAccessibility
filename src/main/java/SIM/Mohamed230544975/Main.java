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
        mainFrame.setAlwaysOnTop(true); // Make the main frame always on top
        mainFrame.setVisible(true);

        // Open file chooser for the first time
        openFileChooser(mainFrame);
    }

    private static void openFileChooser(JFrame mainFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image Files");
        fileChooser.setMultiSelectionEnabled(true);

        // Restrict file chooser to JPG and PNG files only
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Open file chooser dialog
        int returnValue = fileChooser.showOpenDialog(mainFrame);

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

            // Create a new JFrame to replace the dialog
            JFrame optionsFrame = new JFrame("Select Image Processing Modes");
            optionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            optionsFrame.setSize(300, 200);
            optionsFrame.setLocationRelativeTo(mainFrame); // Center the frame
            optionsFrame.setAlwaysOnTop(true); // Make the options frame always on top
            optionsFrame.setLayout(new BorderLayout());

            // Add checkbox panel to options frame
            optionsFrame.add(checkboxPanel, BorderLayout.CENTER);

            // Add OK and Cancel buttons
            JPanel buttonPanel = new JPanel();
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            okButton.addActionListener(e -> {
                optionsFrame.dispose(); // Close options frame
                processSelectedFiles(selectedFiles, grayscaleCheckbox, blackAndWhiteCheckbox, invertCheckbox, bwInvertCheckbox, mainFrame);
            });

            cancelButton.addActionListener(e -> {
                optionsFrame.dispose(); // Close options frame
            });

            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            optionsFrame.add(buttonPanel, BorderLayout.SOUTH);
            optionsFrame.setVisible(true); // Show the options frame
        }
    }

    private static void processSelectedFiles(File[] selectedFiles, JCheckBox grayscaleCheckbox, JCheckBox blackAndWhiteCheckbox,
                                             JCheckBox invertCheckbox, JCheckBox bwInvertCheckbox, JFrame mainFrame) {
        // Ask for a folder to save the processed images
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select Folder to Save Processed Images");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int folderReturnValue = folderChooser.showSaveDialog(mainFrame);

        if (folderReturnValue == JFileChooser.APPROVE_OPTION) {
            File saveFolder = folderChooser.getSelectedFile();

            // Create a new JFrame for progress indication
            JFrame progressFrame = new JFrame("Processing");
            progressFrame.setAlwaysOnTop(true);
            progressFrame.setSize(300, 100);
            progressFrame.setLocationRelativeTo(mainFrame);
            progressFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JLabel progressLabel = new JLabel("Processing images, please wait...");
            progressFrame.add(progressLabel);
            progressFrame.setVisible(true); // Show the progress frame

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
                        // Close the progress frame
                        progressFrame.dispose();

                        // Show a success message
                        JOptionPane.showMessageDialog(mainFrame, "Image processing completed successfully!");

                        // Ask user if they want to process more images
                        int response = JOptionPane.showConfirmDialog(mainFrame,
                                "Do you want to process more images?",
                                "Continue Processing",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);

                        if (response == JOptionPane.YES_OPTION) {
                            // If user wants to process more images, reopen file chooser
                            openFileChooser(mainFrame);  // Call a method to handle file selection again
                        } else {
                            // If user chooses no, exit the application
                            System.exit(0);
                        }
                    });
                }
            };
            worker.execute(); // Start the worker
        }
    }

    private static void processAndSaveImage(String imagePath, int mode, File saveFolder) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));

            // Process the image according to the selected mode
            BufferedImage processedImage = null;
            String optionName = ""; // This will hold the option name for the output file

            switch (mode) {
                case 0: // Grayscale
                    processedImage = convertToGrayscale(originalImage);
                    optionName = "Grayscale";
                    break;
                case 1: // Black and White
                    processedImage = convertToBlackAndWhite(originalImage);
                    optionName = "BlackAndWhite";
                    break;
                case 2: // Invert
                    processedImage = invertColors(originalImage);
                    optionName = "Invert";
                    break;
                case 3: // Black-and-White + Invert
                    processedImage = invertColors(convertToBlackAndWhite(originalImage));
                    optionName = "BW_Invert";
                    break;
            }

            // Save the processed image with the option name in the filename
            String fileName = new File(imagePath).getName();
            String fileBaseName = fileName.substring(0, fileName.lastIndexOf('.')); // Get the name without extension
            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1); // Get the file extension
            String outputFileName = fileBaseName + "_" + optionName + "_Output." + fileExtension;

            // Save the file in the selected folder
            File outputFile = new File(saveFolder, outputFileName);
            if (processedImage != null) {
                ImageIO.write(processedImage, fileExtension, outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static BufferedImage convertToGrayscale(BufferedImage originalImage) {
        BufferedImage grayscaleImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                int rgb = originalImage.getRGB(x, y);
                grayscaleImage.setRGB(x, y, rgb);
            }
        }
        return grayscaleImage;
    }

    private static BufferedImage convertToBlackAndWhite(BufferedImage originalImage) {
        BufferedImage bwImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                int rgb = originalImage.getRGB(x, y);
                int grayValue = (rgb >> 16) & 0xff;
                if (grayValue > 127) {
                    bwImage.setRGB(x, y, 0xFFFFFFFF); // White
                } else {
                    bwImage.setRGB(x, y, 0xFF000000); // Black
                }
            }
        }
        return bwImage;
    }

    private static BufferedImage invertColors(BufferedImage originalImage) {
        BufferedImage invertedImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), originalImage.getType());
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                int rgb = originalImage.getRGB(x, y);
                int invertedRgb = (0xFFFFFF - rgb) | 0xFF000000; // Invert color
                invertedImage.setRGB(x, y, invertedRgb);
            }
        }
        return invertedImage;
    }
}
