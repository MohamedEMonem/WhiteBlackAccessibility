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
        JFrame mainFrame = new JFrame("Image Processing Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the app closes properly
        mainFrame.setSize(400, 200); // Set a reasonable frame size to accommodate both labels
        mainFrame.setLocationRelativeTo(null); // Center the frame
        mainFrame.setAlwaysOnTop(true); // Make the main frame always on top

        // Create a panel for title and author
        JPanel titlePanel = getTitlePanel();

        // Add Start, Compression, and Exit buttons
        JPanel buttonPanel = getPanel(mainFrame);

        // Add title panel and button panel to the main frame
        mainFrame.setLayout(new BorderLayout()); // Use BorderLayout for the main frame
        mainFrame.add(titlePanel, BorderLayout.NORTH); // Add title panel at the top
        mainFrame.add(buttonPanel, BorderLayout.CENTER); // Add button panel in the center

        mainFrame.setVisible(true); // Show the main frame
    }

    private static JPanel getTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new GridLayout(2, 1)); // Use GridLayout for two rows

        // Create labels for title and author
        JLabel titleLabel = new JLabel("WhiteBlackAccessibility", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Set title font size and style
        JLabel authorLabel = new JLabel("Author: Mohamed Essam Abd El Monem", SwingConstants.CENTER);
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Set author font size and style

        // Add labels to the title panel
        titlePanel.add(titleLabel);
        titlePanel.add(authorLabel);
        return titlePanel;
    }

    private static JPanel getPanel(JFrame mainFrame) {
        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton compressionButton = new JButton("Compress");
        JButton exitButton = new JButton("Exit");

        startButton.addActionListener(e -> openFileChooser(mainFrame, false));
        compressionButton.addActionListener(e -> openFileChooser(mainFrame, true));
        exitButton.addActionListener(e -> System.exit(0)); // Exit the entire program

        buttonPanel.add(startButton);
        buttonPanel.add(compressionButton);
        buttonPanel.add(exitButton);
        return buttonPanel;
    }


    private static void openFileChooser(JFrame mainFrame, boolean isCompression) {
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
                if (isCompression) {
                    compressSelectedFiles(selectedFiles, mainFrame);
                } else {
                    processSelectedFiles(selectedFiles, grayscaleCheckbox, blackAndWhiteCheckbox, invertCheckbox, bwInvertCheckbox, mainFrame);
                }
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

    private static void compressSelectedFiles(File[] selectedFiles, JFrame mainFrame) {
        // Ask for a folder to save the compressed images
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select Folder to Save Compressed Images");
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

            JLabel progressLabel = new JLabel("Compressing images, please wait...");
            progressFrame.add(progressLabel);
            progressFrame.setVisible(true); // Show the progress frame

            // Create and start the SwingWorker
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    for (File file : selectedFiles) {
                        compressImage(file, saveFolder);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    SwingUtilities.invokeLater(() -> {
                        // Close the progress frame
                        progressFrame.dispose();

                        // Show a success message and return to the main frame
                        JOptionPane.showMessageDialog(mainFrame, "Image compression completed successfully!");
                    });
                }
            };
            worker.execute(); // Start the worker
        }
    }

    private static void compressImage(File imageFile, File saveFolder) {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            String fileName = imageFile.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1); // Get the file extension

            // Save a compressed version of the image (e.g., reduce quality)
            File outputFile = new File(saveFolder, "compressed_" + fileName);
            ImageIO.write(originalImage, fileExtension, outputFile);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
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

                        // Show a success message and return to the main frame
                        JOptionPane.showMessageDialog(mainFrame, "Image processing completed successfully!");
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
            BufferedImage processedImage;
            String optionName; // This will hold the option name for the output file

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
                default:
                    throw new IllegalArgumentException("Invalid mode: " + mode);
            }

            // Save the processed image to the specified folder
            String fileName = new File(imagePath).getName();
            String outputFilePath = saveFolder + File.separator + optionName + "_" + fileName;
            ImageIO.write(processedImage, "png", new File(outputFilePath));

        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static BufferedImage convertToGrayscale(BufferedImage originalImage) {
        BufferedImage grayscaleImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscaleImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        return grayscaleImage;
    }

    private static BufferedImage convertToBlackAndWhite(BufferedImage originalImage) {
        BufferedImage bwImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                Color c = new Color(originalImage.getRGB(x, y));
                int gray = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                bwImage.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }
        return bwImage;
    }

    private static BufferedImage invertColors(BufferedImage originalImage) {
        BufferedImage invertedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                Color c = new Color(originalImage.getRGB(x, y));
                Color invertedColor = new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
                invertedImage.setRGB(x, y, invertedColor.getRGB());
            }
        }
        return invertedImage;
    }
}
