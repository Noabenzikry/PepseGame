package pepse.world;

/**
 * An interface for objects that need to be notified when the avatar jumps.
 * Implementing this interface allows an object to react to avatar jump events.
 */
public interface JumpAvatarObserver {

    /**
     * Notifies this object that the avatar has jumped.
     */
    void notifyObservers();
}
