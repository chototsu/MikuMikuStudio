package com.jme.light;

import com.jme.scene.Node;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * Started Date: Jul 21, 2004<br><br>
 *
 * <code>SimpleLightNode</code> defines a scene node that contains and maintains a
 * light object. A light node contains a single light, and positions the light
 * based on it's translation vector. If the contained light is a spot light, the
 * rotation of the node determines it's direction. If the contained light is a
 * Directional light rotation determines it's direction. It has no concept of
 * location.
 *
 * @author Mark Powell
 * @author Jack Lindamood
 */
public class SimpleLightNode extends Node{
    private static final long serialVersionUID = 1L;
	private Light light;
    private Quaternion lightRotate;
    
    /**
     * Constructor creates a new <code>LightState</code> object. The light
     * state the node controls is required at construction time.
     *
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param light The light to be moved around by this Node
     */
    public SimpleLightNode(String name,Light light) {
        super(name);
        this.light=light;
    }

    /**
     * <code>updateWorldData</code> modifies the light data based on any
     * change the light node has made.
     *
     * @param time
     *            the time between frames.
     */
    public void updateWorldData(float time) {
        super.updateWorldData(time);
        lightRotate = worldRotation.mult(localRotation, lightRotate);

        switch (light.getType()) {
        case Light.LT_DIRECTIONAL:
            {
                DirectionalLight dLight = (DirectionalLight) light;
                dLight.setDirection(lightRotate.getRotationColumn(2,
                        dLight.getDirection()));
                break;
            }

        case Light.LT_POINT:
            {
                PointLight pLight = (PointLight) light;
                pLight.setLocation(worldTranslation);
                break;
            }

        case Light.LT_SPOT:
            {
                SpotLight sLight = (SpotLight) light;
                sLight.setLocation(worldTranslation);
                sLight.setDirection(lightRotate.getRotationColumn(2, sLight
                        .getDirection()));
                break;
            }

        default:
            break;
        }

    }
}