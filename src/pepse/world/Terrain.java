package pepse.world;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import danogl.util.Vector2;

import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import danogl.gui.rendering.RectangleRenderable;

/**
 * Represents the terrain in the game, including ground height variations and the creation of terrain blocks.
 * Utilizes noise generation to create natural-looking landscapes.
 */
public class Terrain {
    private final float groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;

    /**
     * Constructor for Terrain.
     * Initializes the terrain with a given window size and seed for noise generation.
     * @param windowDimensions The size of the game window, used to determine the terrain's bounds.
     * @param seed A seed for noise generation, affecting the randomness of terrain features.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = windowDimensions.y() * 2/3;
        this.noiseGenerator = new NoiseGenerator(seed, (int)groundHeightAtX0);
    }

    /**
     * Calculates the height of the ground at a specific x-coordinate based on noise.
     * @param x The x-coordinate for which to calculate the ground height.
     * @return The calculated ground height at the specified x-coordinate.
     */
    public float groundHeightAt(float x) {
        // Use Perlin noise to generate ground height
        float noise = (float)noiseGenerator.noise(x, Block.BLOCK_SIZE * 7); // Adjust as needed
        return groundHeightAtX0 + noise;
    }

    /**
     * Creates terrain blocks within a specified range along the x-axis.
     * This method generates a list of ground blocks based on the calculated terrain height and depth.
     * @param minX The starting x-coordinate for terrain generation.
     * @param maxX The ending x-coordinate for terrain generation.
     * @return A list of {@link Block} objects representing the terrain within the specified range.
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();

        int endX = ((maxX + Block.BLOCK_SIZE - 1) / Block.BLOCK_SIZE) * Block.BLOCK_SIZE;
        // Loop over each X-coordinate within the specified range
        for (int x = minX; x <= endX; x += Block.BLOCK_SIZE) {
            float groundHeight = groundHeightAt(x);
            // Calculate the Y-coordinate for the ground
            int startY = (int) ((Math.floor(groundHeight / Block.BLOCK_SIZE) * Block.BLOCK_SIZE));
            // Create blocks within the terrain depth
            for (int i = 0; i < TERRAIN_DEPTH; i++){
                int y = startY + Block.BLOCK_SIZE * i;
                //Color baseColor = chooseRandomBaseColor();
                RectangleRenderable renderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                // Create a block at the current position
                Block groundBlock = new Block(new Vector2(x, y), renderable);
                groundBlock.setTag("ground");
                blocks.add(groundBlock);
            }
        }
        return blocks;
    }
}
