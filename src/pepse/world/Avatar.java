package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player's avatar in the game, handling animations, movements, and energy levels.
 */
public class Avatar extends GameObject {
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final float GRAVITY = 600;

    /** Vector of Avatar size. passing it to gameManager */
    public static Vector2 AVATAR_SIZE = new Vector2(50, 50);
    private final UserInputListener inputListener;
    private static final double MAX_ENERGY = 100;
    private double curEnergy = MAX_ENERGY;
    private AnimationRenderable idleAnimation;
    private AnimationRenderable runAnimation;
    private AnimationRenderable jumpAnimation;
    private final List<JumpAvatarObserver> observersList;


    /**
     * Constructor initializing the avatar with its position, animations, and input listener.
     * @param pos The initial position of the avatar.
     * @param inputListener A listener for user inputs to control the avatar.
     * @param imageReader Utility to read animation frames from images.
     */
    public Avatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader) {
        super(pos, AVATAR_SIZE, imageReader.readImage("assets/idle_0.png", true));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        setupAnimations(imageReader);
        observersList = new ArrayList<>();
    }


    /**
     * Initializes animations for the avatar using image paths for each animation frame.
     * @param imageReader Utility to read animation frames from files.
     */
    private void setupAnimations(ImageReader imageReader) {
        idleAnimation = createAnimation(new String[]{"assets/idle_0.png", "assets/idle_1.png",
                "assets/idle_2.png", "assets/idle_3.png"}, imageReader, 0.5);
        runAnimation = createAnimation(new String[]{"assets/run_0.png","assets/run_1.png","assets/run_2.png",
                "assets/run_3.png", "assets/run_4.png", "assets/run_5.png"}, imageReader, 0.2);
        jumpAnimation = createAnimation(new String[]{"assets/jump_0.png", "assets/jump_1.png",
                "assets/jump_2.png", "assets/jump_3.png"}, imageReader, 0.2);

        renderer().setRenderable(idleAnimation);
    }

    /**
     * Creates an animation from an array of image paths.
     * @param imagePaths Array of paths to animation frame images.
     * @param imageReader Utility to read animation frames from files.
     * @param frameDuration Duration of each frame in the animation.
     * @return An AnimationRenderable object for the animation.
     */
    private AnimationRenderable createAnimation(String[] imagePaths,
                                                ImageReader imageReader, double frameDuration) {
        return new AnimationRenderable(imagePaths, imageReader, true, frameDuration);
    }

    /**
     * Provides a tag for the avatar object. Used for identification and
     * interaction logic in the game engine.
     * @return A string representing the tag of this GameObject, specifically "Avatar" for this class.
     */
    @Override
    public String getTag() {
        return "Avatar";
    }

    /**
     * Updates the avatar's state each frame. Handles movement based on user input and applies the resulting
     * horizontal velocity. This method also triggers animation changes and energy consumption
     * based on actions.
     * @param deltaTime The time elapsed since the last frame update, used for frame-independent movement.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // Handle avatar movement and energy consumption
        float xVel = handleMovement();
        transform().setVelocityX(xVel);
    }

    /**
     * Handles keyboard input to control the avatar's movement, jumping, and energy consumption.
     * @return The calculated horizontal velocity based on input.
     */
    private float handleMovement() {
        float xVel = 0;
        if(curEnergy < 0.5){
            transform().setVelocityX(0);
        }
        // if avatar is standing and not moving
        if (inputListener.pressedKeys().isEmpty()) {
            handleIdle();
            transform().setVelocityX(xVel);
        } else if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) && this.curEnergy >= 0.5) { // moved left
            xVel -= VELOCITY_X;
            moveLeft(xVel);
        } else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && this.curEnergy >= 0.5) { // moved right
            xVel += VELOCITY_X;
            moveRight(xVel);
        } else if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0
                && this.curEnergy >= 10) { //  is jumping
            notifyObservers();
            jump();
        }
        return xVel;
    }

    /**
     * Handles the avatar's idle state, replenishing energy over time and setting the idle animation.
     */
    private void handleIdle(){
        if (curEnergy < MAX_ENERGY && getVelocity().y() == 0) {
            curEnergy++;
            if (curEnergy > MAX_ENERGY){
                curEnergy = MAX_ENERGY;
            }
            renderer().setRenderable(idleAnimation);
        }
    }

    /**
     * Handles movement to the left, updating energy and animation.
     * @param xVel The calculated velocity for the movement.
     */
    private void moveLeft(float xVel) {
        curEnergy -= 0.5;
        transform().setVelocityX(xVel);
        this.renderer().setRenderable(runAnimation);
        this.renderer().setIsFlippedHorizontally(true);
    }

    /**
     * Handles movement to the right, updating energy and animation.
     * @param xVel The calculated velocity for the movement.
     */
    private void moveRight(float xVel) {
        curEnergy -= 0.5;
        transform().setVelocityX(xVel);
        this.renderer().setRenderable(runAnimation);
        this.renderer().setIsFlippedHorizontally(false);
    }


    /**
     * Initiates the avatar's jump, updating energy and setting the jump animation.
     */
    private void jump() {
        transform().setVelocityY(VELOCITY_Y);
        curEnergy -= 10;
        renderer().setRenderable(jumpAnimation);
    }

    /**
     * Gets the current energy level of the avatar.
     * @return The current energy level.
     */
    public double getCurEnergy() {
        return curEnergy;
    }

    /**
     * Adds energy to the avatar, ensuring it does not exceed the maximum energy level.
     * @param numEnergy The amount of energy to add.
     */
    public void addEnergy(int numEnergy){
        curEnergy += numEnergy;
        if (curEnergy > MAX_ENERGY){
            curEnergy = MAX_ENERGY;
        }
    }

/**
     * Registers an observer to be notified when the avatar jumps.
     * This allows other game objects to react to the avatar's jump action.
     * @param observer The observer to register.
     */
    public void registerObserver(JumpAvatarObserver observer) {
        observersList.add(observer);
    }

    /**
     * Notifies all registered observers when the avatar jumps.
     * This method is typically called during the avatar's jump action to trigger reactions
     * from observing objects.
     */
    private void notifyObservers() {
        for (JumpAvatarObserver observer : observersList) {
            observer.notifyObservers();
        }
    }

}