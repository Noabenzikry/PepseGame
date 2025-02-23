package pepse;

import java.util.List;
import danogl.GameObject;
import danogl.GameManager;
import java.util.ArrayList;
import danogl.util.Vector2;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.collisions.Layer;
import danogl.gui.WindowController;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.TextRenderable;

import pepse.world.Sky;
import pepse.world.Block;
import pepse.world.Avatar;
import pepse.world.Terrain;
import pepse.world.trees.Trunk;
import pepse.world.trees.Flora;
import pepse.world.trees.Fruits;
import pepse.world.trees.Leaves;
import pepse.world.daynight.Sun;
import pepse.world.daynight.Night;
import pepse.world.daynight.SunHalo;


/**
 * Manages the game environment for Pepse, setting up the world including the sky, terrain,
 * flora, and game characters like the avatar and the sun. It handles the creation and addition
 * of these elements to the game at the start.
 */
public class PepseGameManager extends GameManager{
    private Terrain terrain;
    private Flora flora; // Declare Flora as a class-level variable

    /**
     * Initializes the game by setting up the game environment including the sky, terrain, day-night cycle,
     * avatar, and flora within the game world.
     * @param imageReader Allows reading of images from files, used for avatar and flora images.
     * @param soundReader Allows reading of sound files.
     * @param inputListener Listens for user input to control the avatar.
     * @param windowController Controls the game window properties.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {

        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(60);

        // add sky
        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);

        // add ground
        terrain = new Terrain(windowController.getWindowDimensions(), 120);
        List<Block> ground = terrain.createInRange(0, (int) windowController.getWindowDimensions().x());
        for (Block block: ground) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }

        // add night
        GameObject night = Night.create(windowController.getWindowDimensions(), 30);
        gameObjects().addGameObject(night, Layer.BACKGROUND);

        // add sun
        GameObject sun = Sun.create(windowController.getWindowDimensions(),30);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);

        // add sun halo
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);

        // add avatar
        Vector2 avatarInitialLocation = new Vector2(0,
                terrain.groundHeightAt(0)-Avatar.AVATAR_SIZE.x());
        Avatar avatar = new Avatar(avatarInitialLocation,
                inputListener, imageReader);
        gameObjects().addGameObject(avatar);

        // energy display
        GameObject energyCounter = new EnergyGraphicIndicator(new Vector2(20,20),
                new Vector2(20, 20), new TextRenderable(""), avatar::getCurEnergy);
        gameObjects().addGameObject(energyCounter, Layer.UI);

        //add flora, all plants related elements
        flora = new Flora(avatar);
        SetupAndAddFlora(windowController);
    }

    /**
     * Separates and adds flora objects into the game, organizing them into different layers
     * based on their type
     * (trunks, leaves, fruits) to ensure correct rendering order.
     * @param windowController Controls the game window properties.
     */
    private void SetupAndAddFlora(WindowController windowController) {
        flora.setGroundHeightProvider(x -> (int) terrain.groundHeightAt(x));
        int minX = Block.BLOCK_SIZE;
        int maxX = (int) windowController.getWindowDimensions().x();
        List<GameObject> floraObjects = flora.createInRange(minX, maxX);

        // Separate the flora objects by type
        List<GameObject> trunks = new ArrayList<>();
        List<GameObject> leaves = new ArrayList<>();
        List<GameObject> fruits = new ArrayList<>();

        for (GameObject floraObject : floraObjects) {
            if (floraObject instanceof Trunk) {
                trunks.add(floraObject);
            } else if (floraObject instanceof Leaves) {
                leaves.add(floraObject);
            } else if (floraObject instanceof Fruits) {
                fruits.add(floraObject);
            }
        }
        // Add trunks first, then leaves, then fruits
        trunks.forEach(trunk -> gameObjects().addGameObject(trunk, Layer.STATIC_OBJECTS));
        leaves.forEach(leaf -> gameObjects().addGameObject(leaf, Layer.STATIC_OBJECTS));
        fruits.forEach(fruit -> gameObjects().addGameObject(fruit, Layer.STATIC_OBJECTS));
    }

    /**
     * The entry point of the Pepse game application.
     * It creates an instance of PepseGameManager and starts the game.
     * @param args Command line arguments passed to the application (not used).
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
