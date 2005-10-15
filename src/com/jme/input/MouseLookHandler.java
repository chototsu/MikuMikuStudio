package com.jme.input;

import com.jme.input.action.MouseLook;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>MouseLookHandler</code> defines an InputHandler that allows to rotate the camera via the mouse.
 */
public class MouseLookHandler extends InputHandler {

    public MouseLookHandler( Camera cam, float speed ) {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        setMouse(mouse);

        MouseLook mouseLook = new MouseLook(mouse, cam, speed );
        mouseLook.setLockAxis(new Vector3f(cam.getUp().x, cam.getUp().y,
                cam.getUp().z));
        addAction(mouseLook);
    }
}
