package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.components.GameObjectPhysics;

import danogl.util.Vector2;

/**
 * Represents a static block in the game world. These blocks do not move upon collision and are treated
 * as immovable objects.
 */
public class Block extends GameObject {

    /** The uniform size (width and height) of each block in pixels. */
    public static final int BLOCK_SIZE = 30;

    /**
     * Constructor for creating a block instance.
     * @param topLeftCorner The top-left corner position of the block in the game world.
     * @param renderable    The visual representation of the block.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(BLOCK_SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}