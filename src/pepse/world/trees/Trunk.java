package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.JumpAvatarObserver;

import java.awt.*;

/**
 * Represents the trunk of a tree in the game, providing visual and physical presence within the game world.
 * Implements JumpAvatarObserver to react to specific avatar actions.
 */
public class Trunk extends GameObject implements JumpAvatarObserver {
    private static final Color TRUNK_DEF_COLOR = new Color(100, 50, 20);
    private static final int TRUNK_WIDTH = 20;

    private static final Color[] BROWN_TONES = {
            new Color(129, 69, 19), // Brown
            new Color(105, 62, 12), // Brown1
            new Color(60, 40, 10), // Chocolate
            new Color(60, 49, 19), // SaddleBrown
            new Color(110,40,10),// maroon
            new Color(100, 50, 20) // regular
    };

    /**
     * Constructs a trunk object with a specified position and height.
     * @param topLeftCorner The position of the trunk in the game world.
     * @param randomHeight The height of the trunk, typically determined randomly for variety.
     */
    public Trunk(Vector2 topLeftCorner, float randomHeight) {
        super(topLeftCorner,new Vector2(TRUNK_WIDTH, randomHeight),new RectangleRenderable(TRUNK_DEF_COLOR));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    /**
     * Changes the trunk's color randomly upon receiving a notification (e.g., avatar jump).
     * This method is intended to visually indicate the interaction or to add visual variety.
     */
    @Override
    public void notifyObservers() {
        int randomIndex = (int) (Math.random() * BROWN_TONES.length);
        Color trunkColor = BROWN_TONES[randomIndex];
        this.renderer().setRenderable(new RectangleRenderable(trunkColor));
    }
}

