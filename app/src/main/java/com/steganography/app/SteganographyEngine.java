package com.steganography.app;

import android.graphics.Bitmap;
import android.graphics.Color;

public class SteganographyEngine {

    private static final String DELIMITER = "##END##";

    /**
     * Encodes a secret message into a bitmap using LSB (Least Significant Bit) technique.
     * Each pixel's RGB channels store 1 bit each = 3 bits per pixel.
     */
    public static Bitmap encodeMessage(Bitmap original, String message) throws Exception {
        String fullMessage = message + DELIMITER;
        byte[] messageBytes = fullMessage.getBytes("UTF-8");
        int messageLength = messageBytes.length;

        // Check capacity: 3 bits per pixel, need messageLength * 8 bits
        int totalBitsNeeded = messageLength * 8;
        int availablePixels = original.getWidth() * original.getHeight();
        int availableBits = availablePixels * 3;

        if (totalBitsNeeded > availableBits) {
            throw new Exception("Message too large for this image!\nMax characters: " + (availableBits / 8 - DELIMITER.length()));
        }

        Bitmap stegoBitmap = original.copy(Bitmap.Config.ARGB_8888, true);
        int[] pixels = new int[original.getWidth() * original.getHeight()];
        stegoBitmap.getPixels(pixels, 0, original.getWidth(), 0, 0, original.getWidth(), original.getHeight());

        int bitIndex = 0;

        for (int i = 0; i < pixels.length && bitIndex < totalBitsNeeded; i++) {
            int pixel = pixels[i];
            int r = Color.red(pixel);
            int g = Color.green(pixel);
            int b = Color.blue(pixel);
            int a = Color.alpha(pixel);

            // Embed in Red channel
            if (bitIndex < totalBitsNeeded) {
                int bit = getBit(messageBytes, bitIndex++);
                r = (r & 0xFE) | bit;
            }
            // Embed in Green channel
            if (bitIndex < totalBitsNeeded) {
                int bit = getBit(messageBytes, bitIndex++);
                g = (g & 0xFE) | bit;
            }
            // Embed in Blue channel
            if (bitIndex < totalBitsNeeded) {
                int bit = getBit(messageBytes, bitIndex++);
                b = (b & 0xFE) | bit;
            }

            pixels[i] = Color.argb(a, r, g, b);
        }

        stegoBitmap.setPixels(pixels, 0, original.getWidth(), 0, 0, original.getWidth(), original.getHeight());
        return stegoBitmap;
    }

    /**
     * Decodes a hidden message from a stego bitmap.
     */
    public static String decodeMessage(Bitmap stegoBitmap) throws Exception {
        int[] pixels = new int[stegoBitmap.getWidth() * stegoBitmap.getHeight()];
        stegoBitmap.getPixels(pixels, 0, stegoBitmap.getWidth(), 0, 0, stegoBitmap.getWidth(), stegoBitmap.getHeight());

        StringBuilder binaryData = new StringBuilder();

        for (int pixel : pixels) {
            binaryData.append(Color.red(pixel) & 1);
            binaryData.append(Color.green(pixel) & 1);
            binaryData.append(Color.blue(pixel) & 1);
        }

        // Convert binary string to bytes
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 0; i + 7 < binaryData.length(); i += 8) {
            String byteStr = binaryData.substring(i, i + 8);
            int charCode = Integer.parseInt(byteStr, 2);
            if (charCode == 0) break;
            messageBuilder.append((char) charCode);

            // Check for delimiter
            if (messageBuilder.toString().endsWith(DELIMITER)) {
                String result = messageBuilder.toString();
                result = result.substring(0, result.length() - DELIMITER.length());
                if (result.isEmpty()) {
                    throw new Exception("No hidden message found in this image.");
                }
                return result;
            }
        }

        throw new Exception("No hidden message found in this image.\nMake sure you're using a stego image encoded by this app.");
    }

    /**
     * Returns the maximum characters that can be hidden in the given bitmap.
     */
    public static int getMaxCapacity(Bitmap bitmap) {
        int totalPixels = bitmap.getWidth() * bitmap.getHeight();
        int totalBits = totalPixels * 3;
        return (totalBits / 8) - DELIMITER.length();
    }

    private static int getBit(byte[] data, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitPos = 7 - (bitIndex % 8);
        return (data[byteIndex] >> bitPos) & 1;
    }
}
