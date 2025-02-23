package pepse.world.trees;

import java.awt.*;
import java.util.*;
import java.util.List;
import danogl.GameObject;
import danogl.util.Vector2;
import danogl.collisions.Collision;
import danogl.gui.rendering.OvalRenderable;

import pepse.world.Avatar;
import pepse.world.JumpAvatarObserver;

/**
 * Represents the fruits in the game, which can be eaten by the Avatar to gain energy.
 * These fruits are generated on trees and have various colors. They implement the JumpAvatarObserver
 * to change color upon the avatar's jump.
 */
public class Fruits extends GameObject implements JumpAvatarObserver {
    private static final float FRUIT_SIZE = 15;
    private static final Color FRUIT_DEF_COLOR = Color.RED;
    private boolean canBeEaten;
    private static final Color[] FRUIT_COLORS =
            {Color.ORANGE, Color.PINK, Color.YELLOW, Color.BLUE,Color.RED, Color.MAGENTA};

    /**
     * Constructor for creating a fruit object.
     * @param topLeftCorner The top-left position where the fruit will be placed.
     */
    public Fruits(Vector2 topLeftCorner) {
        super(topLeftCorner, new Vector2(FRUIT_SIZE, FRUIT_SIZE), new OvalRenderable(FRUIT_DEF_COLOR));
        canBeEaten=true;
    }

    /**
     * Static method to generate a list of fruit objects around the top position of a tree.
     * @param treeTopPos The position near which fruits are to be generated.
     * @param avatar The avatar object, to register as an observer for the fruits.
     * @return A list of created Fruits objects.
     */
    public static List<Fruits> createFruits(Vector2 treeTopPos, Avatar avatar) {
        List<Fruits> fruits = new ArrayList<>();
        Random random = new Random();
        int numberOfFruits = 3 + random.nextInt(2); // create 3 or 4 fruits
        for (int i = 0; i < numberOfFruits; i++) {
            float xOffset = random.nextFloat() * FRUIT_SIZE * 5 - FRUIT_SIZE * 3.5f;
            float yOffset = random.nextFloat() * FRUIT_SIZE * 5 - FRUIT_SIZE * 3.5f;
                Vector2 fruitPosition = treeTopPos.add(new Vector2(xOffset, yOffset));
                Fruits fruit = new Fruits(fruitPosition);
                fruits.add(fruit);
                avatar.registerObserver(fruit);
        }
        return fruits;
    }


    /**
     * Handles the logic when an avatar collides with a fruit, allowing the Avatar to "eat" the fruit and
     * gain energy.
     * @param other The GameObject that collides with this fruit.
     * @param collision Information about the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(canBeEaten){
            enableFruitEaten(other);
            canBeEaten=false;
        }

    }

    /**
     * Enables the fruit to be eaten by the Avatar, adding energy and making the fruit temporarily invisible.
     * @param other The GameObject that "eats" the fruit, expected to be the Avatar.
     */
    private void enableFruitEaten(GameObject other){
        if(other.getTag().equals("Avatar")){
            Avatar avatar = (Avatar) other; // downcasting safetly!
            avatar.addEnergy(10);
            Fruits currentFruit = this;

            currentFruit.renderer().setOpaqueness(0);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    currentFruit.renderer().setOpaqueness(1);
                    canBeEaten=true;
                }
            }, 30000);
        }

    }

    /**
     * Notifies the fruit about the avatar's jump, changing its color randomly.
     */
    @Override
    public void notifyObservers() {
        Random random = new Random();
        int randIndex = random.nextInt(FRUIT_COLORS.length);
        Color currentFruitColor = FRUIT_COLORS[randIndex];
        this.renderer().setRenderable(new OvalRenderable(currentFruitColor));
    }
}



