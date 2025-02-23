package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Avatar;
import pepse.world.JumpAvatarObserver;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents leaves on trees, capable of dynamic movement and reacting to avatar jumps.
 */
public class Leaves extends GameObject implements JumpAvatarObserver {
    private static final Color LEAF_COLOR = new Color(50, 200, 30);

    private static final int LEAF_SIZE = 25;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Leaves(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        Random random = new Random();
        new ScheduledTask(this,
                random.nextFloat(), false, this::startLeafTransitions);
    }

    /**
     * Generates a cluster of leaves around a specified position.
     * @param treeTopPos The position around which leaves are to be generated.
     * @param avatar     The avatar object to register as an observer for leaf movement.
     * @return A list of Leaves objects created around the given position.
     */
    public static List<Leaves> createLeavesCluster(Vector2 treeTopPos, Avatar avatar) {
        List<Leaves> leaves = new ArrayList<>();
        Random random = new Random();
        int range = LEAF_SIZE*3;

        for (int i = -range; i < range; i += LEAF_SIZE) {
            for (int j = -range; j < range; j += LEAF_SIZE) {
//                int xOffset =  random.nextInt(LEAF_SIZE) - LEAF_SIZE / 2;
//                int yOffset =  random.nextInt(LEAF_SIZE) - LEAF_SIZE / 2;
                if (random.nextFloat() <= 0.4) {
                    Vector2 position = treeTopPos.add(new Vector2(i , j));
                    Leaves leaf =  new Leaves(position, new Vector2(LEAF_SIZE, LEAF_SIZE),
                            new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
                    avatar.registerObserver(leaf);
                    leaves.add(leaf);
                }
            }
        }
        return leaves;
    }

    /**
     * Initiates transitions for leaf movement and size change to simulate natural behavior.
     */
    private void startLeafTransitions(){
        Transition<Float> getMovingTransition = new Transition<>(
                this,
                angle -> renderer().setRenderableAngle(angle),
                0f, // Start angle
                15f, // End angle
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                1, // Duration in seconds
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );

        Transition<Float> getSizeTransition = new Transition<>(
                this,
                (dimensions) -> setDimensions(new Vector2(dimensions,dimensions)),
                (float)LEAF_SIZE, // Start size
                (float)LEAF_SIZE-3, // End width
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                1, // Duration in seconds
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    /**
     * Responds to notifications from the avatar (e.g., on jump) by initiating a leaf rotation effect.
     */
    @Override
    public void notifyObservers() {
        new Transition<Float>(
                this,
                angle -> renderer().setRenderableAngle(angle),
                0f, 90f,
                Transition.CUBIC_INTERPOLATOR_FLOAT, 1,
                Transition.TransitionType.TRANSITION_ONCE,
                null);
    }

}

