package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Provides functionality to create a halo effect around the sun.
 */
public class SunHalo {
    private static final float HALO_SIZE = 120;

    /**
     * Creates a halo effect for the sun, giving it a glowing appearance.
     *
     * @param sun The GameObject representing the sun, to which the halo is added.
     * @return A new GameObject representing the sun's halo.
     */
    public static GameObject create(GameObject sun){
        Vector2 sunInitialPosition = sun.getCenter();
        float haloSize = HALO_SIZE;
        OvalRenderable renderable = new OvalRenderable(new Color(255, 255, 0, 20));

        GameObject sunHalo = new GameObject(sunInitialPosition, new Vector2(haloSize, haloSize),
                renderable);

        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag("sunHalo");

        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));

        return sunHalo;
    }
}
