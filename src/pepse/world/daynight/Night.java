package pepse.world.daynight;

import java.awt.*;
import danogl.GameObject;
import danogl.util.Vector2;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;


/**
 * Represents the night cycle in the game world.
 * This class creates a night object that fills the entire window with a black rectangle,
 * representing the night sky.
 * The opacity of the night object gradually increases to a predefined value, making the
 * scene darker as the night progresses.

 */
public class Night{
    private static final Float MIDNIGHT_OPACITY = 0.5f;


    /**
     * Create a night object filling the entire window with a black rectangle.
     *
     * @param windowDimensions The dimensions of the window.
     * @param cycleLength number of seconds
     * @return The created night object.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        RectangleRenderable renderable = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO ,windowDimensions, renderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag("night");

        // Creating the transition
        new Transition<Float>(
                night, // The game object to perform the change on
                night.renderer()::setOpaqueness, // Callback that performs the change
                0f, // Initial value (night fully transparent)
                MIDNIGHT_OPACITY, // Final value (half darkness)
                Transition.CUBIC_INTERPOLATOR_FLOAT, // Cubic interpolator
                cycleLength/2, // Transition time (half a day)
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Looping transition
                null);  // No action needed after reaching the final value

        return night;
    }
}