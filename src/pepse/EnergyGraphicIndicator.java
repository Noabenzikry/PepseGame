package pepse;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.function.Supplier;

/**
 * A graphical representation of the avatar's energy level in the game.
 * Displays energy as a percentage on the screen.
 */
public class EnergyGraphicIndicator extends GameObject {
    private final Supplier<Double> callback;

    /**
     * Constructor for creating an energy level indicator.
     * @param position The position on the screen where the energy level will be displayed.
     * @param dimensions The size of the text renderable area.
     * @param renderable The text renderable used to display the energy level.
     * @param callback A supplier callback to retrieve the current energy level.
     */
    public EnergyGraphicIndicator(Vector2 position, Vector2 dimensions,
                                  TextRenderable renderable, Supplier<Double> callback) {
        super(position, dimensions, renderable);
        this.callback = callback;
        updateText();
    }

    /**
     * Updates the display text with the current energy level on each frame.
     * @param deltaTime The time elapsed since the last frame, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateText();
    }

    /**
     * Retrieves the current energy level using the callback and updates the text renderable
     * to display this value.
     */
    private void updateText() {
        double currentEnergy = callback.get();
        TextRenderable renderedText = new TextRenderable(currentEnergy + "%");
        this.renderer().setRenderable(renderedText);
    }
}
