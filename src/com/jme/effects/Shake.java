/*
 * Created on Jun 1, 2004
 */
package com.jme.effects;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;

/**
 * <code>Shake</code> is a controller that allows a user to "Shake" a spatial's
 * localTranslation.  The two flags signal if you want to either:<br>
 * 1) Shake randomly every update, wandering in random directions.<br>
 * 2) Always return back to the first location after a shake, so the Spatial
 * doesn't wander in random directions.
 * @author Ahmed
 * @author Jack Lindamood (javadoc only)
 */
public class Shake extends Controller {

    /** Returns the Spatial to the origin after a shake. */
    public static final int RETURN_TO_ORIGIN = 0;

    /** Shakes the spatial to random locations every update. */
    public static final int RANDOMISE = 1;

    private int currentType;

    private float amount;

    private float random;

    private boolean goingBackToActualLock;

    private Spatial child;

    /**
     * Creates a Shake controller.  The amount flag signals how much
     * of a shake to create each update.  The Shake controller is disabled
     * by default.  The default shake type is RETURN_TO_ORIGIN.
     * @param spat The spatial to shake.
     * @param amount The amount to posibly shake by each update.
     */
    public Shake(Spatial spat, float amount) {
        this.amount = amount;
        this.setActive(false);
        goingBackToActualLock = true;
        this.child = spat;
        currentType=RETURN_TO_ORIGIN;
    }

    /**
     * Sets the type of shake.  One of either RETURN_TO_ORIGIN or
     * RANDOMISE.  If an invalid type is passed, nothing changes.
     * @param type The type of shake to have.
     */
    public void setType(int type) {
        if (type!= RETURN_TO_ORIGIN && type!=RANDOMISE) return;
        currentType = type;
    }

    /**
     * Updates the shake.
     * @param time Ignored
     */
    public void update(float time) {
        if (isActive()) {

            Vector3f loc = child.getLocalTranslation();
            if (currentType == RETURN_TO_ORIGIN) {
                if (goingBackToActualLock) {
                    loc.x -= ((random * amount) - (amount / 2));
                    loc.y -= ((random * amount) - (amount / 2));
                    loc.z -= ((random * amount) - (amount / 2));
                    goingBackToActualLock = false;
                } else {
                    random = (float) Math.random();
                    loc.x += ((random * amount) - (amount / 2));
                    loc.y += ((random * amount) - (amount / 2));
                    loc.z += ((random * amount) - (amount / 2));

                    child.setLocalTranslation(loc);
                    goingBackToActualLock = true;
                }
            } else if (currentType == RANDOMISE) {
                random = (float) Math.random();
                loc.x += ((random * amount) - (amount / 2));
                loc.y += ((random * amount) - (amount / 2));
                loc.z += ((random * amount) - (amount / 2));

                child.setLocalTranslation(loc);
            }
        }
    }

}