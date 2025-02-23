package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import danogl.components.Transition;


import java.awt.*;

/**
 * Represents the sun in the day-night cycle, creating a visual representation
 * and managing its movement across the sky based on the cycle length.
 */
public class Sun {

    private static final int SUN_SIZE = 80;

    /**
     * Creates and returns a GameObject representing the sun.
     * The sun follows a circular path in the sky, simulating day-night cycle.
     *
     * @param windowDimensions The dimensions of the game window, used to position the sun.
     * @param cycleLength The duration of one complete day-night cycle.
     * @return A GameObject representing the sun.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        // Create a static yellow circle in the center of the sky
        OvalRenderable renderable = new OvalRenderable(Color.YELLOW);
        Vector2 sunPosition = new Vector2(windowDimensions.x() / 2, windowDimensions.y() / 2);
        Vector2 sunSize = new Vector2(SUN_SIZE,SUN_SIZE);

        GameObject sun = new GameObject(Vector2.ZERO, sunSize, renderable);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setCenter(sunPosition);
        sun.setTag("sun");

        Vector2 initialSunCenter = new Vector2((windowDimensions.x() / 2) - SUN_SIZE,
                windowDimensions.y() / 2-SUN_SIZE);

        Vector2 cycleCenter = new Vector2(windowDimensions.x() / 2, windowDimensions.y() * 2/3);

        // Create a transition to move the sun in a circular path
        Transition<Float> sunTransition = new Transition<>(
                sun, // Game object being changed
                (Float angle) -> sun.setCenter (initialSunCenter.subtract(cycleCenter).rotated(angle)
                        .add(cycleCenter)), // Callback to set sun position
                0f, // Initial value (start angle)
                360f, // Final value (end angle)
                Transition.LINEAR_INTERPOLATOR_FLOAT, // Linear interpolator
                cycleLength, // Transition time (cycle length)
                Transition.TransitionType.TRANSITION_LOOP, // Looping transition
                null // No action needed after reaching the final value
        );
        return sun;
    }
}
