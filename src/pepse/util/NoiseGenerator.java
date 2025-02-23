package pepse.util;

/**
 * This class is responsible for generating pseudo-random noise, useful for terrain generation
 * and other randomized visual effects within a game environment. It utilizes Perlin noise or
 * a similar algorithm to produce smooth, continuous noise patterns based on a given seed.
 * The noise can be generated for any x-coordinate, allowing for dynamic and varied terrain
 * features or other elements that require randomness.
 * The generated noise is deterministic and repeatable for the same seed and startPoint values,
 * ensuring consistent game worlds across different sessions.

 */
public class NoiseGenerator {
    private final double seed;
    private int[] p;
    private final double startPoint;

    /**
     * The constructor of the NoiseGenerator class.
     *
     * @param seed can be anything you want (even 1234 or new Random().nextGaussian()).
     *             This seed is the basis of the random generator, which
     *             will draw upon it to generate pseudo-random noise.
     *
     * @param startPoint is a relative point that the noise will be generated from.
     *                   In our case it should be your ground height at X0 (specified in
     *                   ex4 when we talk about the terrain: 2.2.1).
     *
     */
    public NoiseGenerator(double seed, int startPoint) {
        this.seed = seed;
        this.startPoint = startPoint;
        init();
    }


    /**
     * Initializes the permutation array required for noise generation.
     * This method populates the array with a predefined set of values and duplicates them
     * to ensure seamless noise pattern generation over a wide range of inputs.
     */
    private void init() {
        // Initialize the permutation array.
        this.p = new int[512];
        int[] permutation = new int[]{151, 160, 137, 91, 90, 15, 131, 13, 201,
                95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99,
                37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26,
                197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88,
                237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74,
                165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111,
                229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40,
                244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76,
                132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159,
                86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
                124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207,
                206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170,
                213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155,
                167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113,
                224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242,
                193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235,
                249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184,
                84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236,
                205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66,
                215, 61, 156, 180};
        long default_size = 35;

        // Populate it
        for (int i = 0; i < 256; i++) {
            p[256 + i] = p[i] = permutation[i];
        }

    }

    /**
     * Noise is responsible to generate pseudo random noise according to
     * the seed given upon constructing the object.
     * @param x the wanted x to receive noise for (in our case,
     *          the x coordinate of the terrain you'd want to create).
     * @param factor describes how large the noise should be (play with it,
     *               but BLOCK_SIZE *7 should be enough).
     * @return returns a noise you should *add* to the groundHeightAtX0 you have.
     *
     * example:
     * public float groundHeightAt(float x) {
     *           float noise = (float) noiseGenerator.noise(x, BLOCK_SIZE *7);
     *           return groundHeightAtX0 + noise;
     *       }
     *
     */
    public double noise(double x, double factor) {
        double value = 0.0;
        double currentPoint = startPoint;

        while (currentPoint >= 1) {
            value += smoothNoise((x / currentPoint), 0, 0) * currentPoint;
            currentPoint /= 2.0;
        }

        return value * factor / startPoint;
    }


    /**
     * Generates a smoothed noise value based on x, y, and z coordinates.
     * This helper method is used internally to calculate noise values with a smoothing function
     * to ensure continuity and natural transitions between noise values.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate (unused in current implementation, included for potential future use).
     * @param z The z-coordinate (unused in current implementation, included for potential future use).
     * @return A smoothed noise value based on the provided coordinates.
     */
    private double smoothNoise(double x, double y, double z) {
        // Offset each coordinate by the seed value
        x += this.seed;
        y += this.seed;
        x += this.seed;

        int X = (int) Math.floor(x) & 255; // FIND UNIT CUBE THAT
        int Y = (int) Math.floor(y) & 255; // CONTAINS POINT.
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x); // FIND RELATIVE X,Y,Z
        y -= Math.floor(y); // OF POINT IN CUBE.
        z -= Math.floor(z);

        double u = fade(x); // COMPUTE FADE CURVES
        double v = fade(y); // FOR EACH OF X,Y,Z.
        double w = fade(z);

        int A = p[X] + Y;
        int AA = p[A] + Z;
        int AB = p[A + 1] + Z; // HASH COORDINATES OF
        int B = p[X + 1] + Y;
        int BA = p[B] + Z;
        int BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

        return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z),    // AND ADD
                                grad(p[BA], x - 1, y, z)), // BLENDED
                        lerp(u, grad(p[AB], x, y - 1, z),    // RESULTS
                                grad(p[BB], x - 1, y - 1, z))),// FROM 8
                lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1),    // CORNERS
                                grad(p[BA + 1], x - 1, y, z - 1)), // OF CUBE
                        lerp(u, grad(p[AB + 1], x, y - 1, z - 1),
                                grad(p[BB + 1], x - 1, y - 1, z - 1))));
    }

    /**
     * Applies a fade curve to t, improving the smoothness of the interpolation.
     *
     * @param t The input value.
     * @return The faded value of t.
     */
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    /**
     * Linearly interpolates between two points a and b by a factor of t.
     *
     * @param t The interpolation factor.
     * @param a The start value.
     * @param b The end value.
     * @return The interpolated value.
     */
    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    /**
     * Calculates gradient vector and dot product for 3D noise.
     *
     * @param hash A unique identifier for the gradient direction.
     * @param x X-coordinate in space.
     * @param y Y-coordinate in space.
     * @param z Z-coordinate in space.
     * @return The dot product of the gradient vector and the vector (x, y, z).
     */
    private double grad(int hash, double x, double y, double z) {
        int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
        double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
                v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
