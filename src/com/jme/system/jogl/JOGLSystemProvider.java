package com.jme.system.jogl;

import com.jme.system.DisplaySystem;
import com.jme.system.SystemProvider;
import com.jme.util.NanoTimer;
import com.jme.util.Timer;

public class JOGLSystemProvider implements SystemProvider {

    public static final String SYSTEM_IDENTIFIER = "JOGL";

    private DisplaySystem displaySystem;

    private Timer timer;

    public String getProviderIdentifier() {
        return SYSTEM_IDENTIFIER;
    }

    public DisplaySystem getDisplaySystem() {
        if (displaySystem == null) {
            displaySystem = new JOGLDisplaySystem();
        }

        return displaySystem;
    }

    public Timer getTimer() {
        if (timer == null) {
            timer = new NanoTimer();
        }

        return timer;
    }

    public void installLibs() {
        // TODO Auto-generated method stub

    }

    public void disposeDisplaySystem() {
        displaySystem = null;
    }

}
