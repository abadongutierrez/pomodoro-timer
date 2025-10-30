package com.jabaddon.pomodorotimer.infrastructure.animation;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SpriteSheet {
    private final Image spriteSheet;
    private final int tileWidth;
    private final int tileHeight;
    private final Map<String, WritableImage> frameCache;

    public SpriteSheet(String resourcePath, int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.frameCache = new HashMap<>();

        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new RuntimeException("Could not find sprite sheet: " + resourcePath);
        }
        this.spriteSheet = new Image(inputStream);
    }

    public SpriteSheet(Image image, int tileWidth, int tileHeight) {
        this.spriteSheet = image;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.frameCache = new HashMap<>();
    }

    /**
     * Extracts a single frame from the sprite sheet
     * @param col Column index (0-based)
     * @param row Row index (0-based)
     * @return The extracted frame as a WritableImage
     */
    public WritableImage getFrame(int col, int row) {
        String key = col + "," + row;

        if (frameCache.containsKey(key)) {
            return frameCache.get(key);
        }

        int x = col * tileWidth;
        int y = row * tileHeight;

        if (x + tileWidth > spriteSheet.getWidth() || y + tileHeight > spriteSheet.getHeight()) {
            throw new IllegalArgumentException(
                String.format("Frame at col=%d, row=%d exceeds sprite sheet bounds", col, row)
            );
        }

        PixelReader reader = spriteSheet.getPixelReader();
        WritableImage frame = new WritableImage(reader, x, y, tileWidth, tileHeight);

        frameCache.put(key, frame);
        return frame;
    }

    /**
     * Extracts multiple frames from a specific row
     * @param row Row index (0-based)
     * @param startCol Starting column (0-based)
     * @param count Number of frames to extract
     * @return Array of frames
     */
    public WritableImage[] getFramesFromRow(int row, int startCol, int count) {
        WritableImage[] frames = new WritableImage[count];
        for (int i = 0; i < count; i++) {
            frames[i] = getFrame(startCol + i, row);
        }
        return frames;
    }

    /**
     * Extracts multiple frames from a specific column
     * @param col Column index (0-based)
     * @param startRow Starting row (0-based)
     * @param count Number of frames to extract
     * @return Array of frames
     */
    public WritableImage[] getFramesFromColumn(int col, int startRow, int count) {
        WritableImage[] frames = new WritableImage[count];
        for (int i = 0; i < count; i++) {
            frames[i] = getFrame(col, startRow + i);
        }
        return frames;
    }

    /**
     * Extracts frames from specific coordinates
     * @param coordinates Array of {col, row} pairs
     * @return Array of frames
     */
    public WritableImage[] getFramesFromCoordinates(int[][] coordinates) {
        WritableImage[] frames = new WritableImage[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            frames[i] = getFrame(coordinates[i][0], coordinates[i][1]);
        }
        return frames;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getColumns() {
        return (int) (spriteSheet.getWidth() / tileWidth);
    }

    public int getRows() {
        return (int) (spriteSheet.getHeight() / tileHeight);
    }
}
