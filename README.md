# White and Black Accessibility

A software tool that converts JPG/PNG images into black and white or greyscale.

https://github.com/user-attachments/assets/23901c11-eb82-4920-9fa8-8cc63961256b

## Features

- Convert any JPG/PNG image to black and white or greyscale.
- Simple and easy-to-use interface.
- Fast and lightweight image processing.

## Author

- [**Mohamed Essam Abd El-Monem**](https://github.com/MohamedEMonem)

## Lessons Learned

While building this project, I gained valuable insights and overcame a variety of challenges:

1. **Reading Images**: Learned to read images using `BufferedImage` and `ImageIO` in Java.
2. **Pixel Manipulation**: Discovered how to iterate over each pixel and extract color values.
3. **Luminance Formula**: Applied the formula for converting RGB to greyscale: Value = int grayValue = (int) (red +
   green + blue) / 3
4. **Image Modification**: Gained experience in modifying pixel color data.
5. **Saving Images**: Used `ImageIO.write()` to save the edited image.
6. **Creating Executables**: Converted Java JAR files into executables using **Launch4j**.
7. **Setup Creation**: Learned to create a program setup file with **Inno Setup Compiler**.

## Installation

To install **White and Black Accessibility**, download and run the installer from the link below:

[Download WhiteBlackAccessibility Setup](https://github.com/MohamedEMonem/WhiteBlackAccessibility/releases/download/WhiteBlackAccessibility/WhiteBlackAccessibilitySetup.exe)

## Usage

1. Run the setup file to install.
2. Open the application, select an image, and choose whether to convert it to black and white or greyscale.

## Future Enhancements

- Adding batch processing for multiple images.
- Supporting additional file formats.
