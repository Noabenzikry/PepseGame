package pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;


/**
 * Responsible for generating the flora within the game, including trees with their trunks,
 * leaves, and fruits. It doesn't directly create terrain objects but relies on either callbacks
 * or a pre-defined list of tree positions to place flora appropriately within the game world.
 */
public class Flora{
    private final Random random = new Random();
    private final Avatar avatar;
    //callback
    private IntFunction<Integer> groundHeightProvider;

    /**
     * Initializes the flora generator with a reference to the avatar.
     * @param avatar The game's avatar, used for interactions with the flora.
     */
    public Flora(Avatar avatar){
        this.avatar = avatar;
    }

    /**
     * Sets the provider that determines ground height at any given x-coordinate.
     * @param provider A function that returns the ground height given an x-coordinate.
     */
    public void setGroundHeightProvider(IntFunction<Integer> provider) {
        this.groundHeightProvider = provider;
    }

    /**
     * Generates trees within a specified range along the x-axis.
     * @param minX The minimum x-coordinate to start generating trees from.
     * @param maxX The maximum x-coordinate to stop generating trees at.
     * @return A list of all game objects (trees, leaves, fruits) created within the range.
     */
    public List<GameObject> createInRange(int minX, int maxX) {
        List<GameObject> trees = new ArrayList<>();
        for (int i = minX; i < maxX; i += Block.BLOCK_SIZE) {
            if (random.nextDouble() < 0.1) {
                int randomHeight = random.nextInt(100) + 100;
                int groundHeight = groundHeightProvider.apply(i);
                Vector2 treePosition = new Vector2(i, groundHeight - randomHeight);

                //create the trunk
                Trunk trunk = new Trunk(treePosition, randomHeight);
                trees.add(trunk);
                avatar.registerObserver(trunk);
                //create the moving leaves
                List<Leaves> leaves = Leaves.createLeavesCluster(trunk.getTopLeftCorner(),avatar);
                trees.addAll((leaves));
                //create the fruits
                List<Fruits> fruits = Fruits.createFruits(trunk.getTopLeftCorner(), avatar);
                trees.addAll(fruits);
            }
        }
        return trees;
    }

}
