package jmetest.flagrushtut.lesson8;

import jmetest.effects.cloth.TestCloth;

import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.math.spring.SpringPoint;
import com.jme.math.spring.SpringPointForce;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.cloth.ClothPatch;
import com.jmex.effects.cloth.ClothUtils;
import com.jmex.terrain.TerrainBlock;

public class Flag extends Node{
    private static final int LIFE_TIME = 10;
    float countdown = LIFE_TIME;
    TerrainBlock tb;
    private ClothPatch cloth;
    private float windStrength = 15f;
    private Vector3f windDirection = new Vector3f(0.8f, 0, 0.2f);
    private SpringPointForce gravity, drag, wind;
    public Flag(TerrainBlock tb) {
        super("flag");
        this.tb = tb;
        cloth = new ClothPatch("cloth", 25, 25, 1f, 10); // name, nodesX, nodesY, springSize, nodeMass
        // Add a simple breeze with mild random eddies:
        wind = new RandomFlagWindForce(windStrength, windDirection);
        cloth.addForce(wind);
        // Add a simple gravitational force:
        gravity = ClothUtils.createBasicGravity();
        cloth.addForce(gravity);
        // Add a simple drag force.
        drag = ClothUtils.createBasicDrag(10f);
        cloth.addForce(drag);
        
        Cylinder c = new Cylinder("pole", 10, 10, 0.5f, 50 );
        this.attachChild(c);
        Quaternion q = new Quaternion();
        //rotate the cylinder to be vertical
        q.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
        c.setLocalRotation(q);
        c.setLocalTranslation(new Vector3f(-12.5f,-12.5f,0));

        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        ts.setTexture(
            TextureManager.loadTexture(
            TestCloth.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
            Texture.MM_LINEAR_LINEAR,
            Texture.FM_LINEAR));
        
        PointLight dr = new PointLight();
        dr.setEnabled( true );
        dr.setDiffuse( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
        dr.setAmbient( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
        dr.setLocation( new Vector3f( 0.5f, -0.5f, 0 ) );

        LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);
        lightState.setTwoSidedLighting( true );
        
        LightNode lightNode = new LightNode( "light", lightState );
        lightNode.setLight( dr );
        lightNode.setLocalTranslation(new Vector3f(15,10,0));

        lightNode.setTarget( this );
        
        this.attachChild(lightNode);
        
        cloth.setRenderState(ts);
        CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        cs.setCullMode(CullState.CS_NONE);
        cloth.setRenderState(cs);
        this.attachChild(cloth);
        for (int i = 0; i < 5; i++) {
            cloth.getSystem().getNode(i*25).position.y *= .8f;
            cloth.getSystem().getNode(i*25).setMass(Float.POSITIVE_INFINITY);
            
        }
        
        for (int i = 24; i > 19; i--) {
            cloth.getSystem().getNode(i*25).position.y *= .8f;
            cloth.getSystem().getNode(i*25).setMass(Float.POSITIVE_INFINITY);
            
        }
        this.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        this.setLocalScale(0.25f);
        
    }
    
    public void update(float time) {
        countdown -= time;
        
        if(countdown <= 0) {
            reset();
        }
    }
    
    public void reset() {
        countdown = LIFE_TIME;
        placeFlag();
    }
    
    public void placeFlag() {
        float x = 45 + FastMath.nextRandomFloat() * 130;
        float z = 45 + FastMath.nextRandomFloat() * 130;
        float y = tb.getHeight(x,z) + 7.5f;
        localTranslation.x = x;
        localTranslation.y = y;
        localTranslation.z = z;
        
    }
    
    private class RandomFlagWindForce extends SpringPointForce{
        
        private final float strength;
        private final Vector3f windDirection;

        public RandomFlagWindForce(float strength, Vector3f direction) {
            this.strength = strength;
            this.windDirection = direction;
        }
        
        public void apply(float dt, SpringPoint node) {
            windDirection.x += dt * (FastMath.nextRandomFloat() - 0.5f);
            windDirection.z += dt * (FastMath.nextRandomFloat() - 0.5f);
            windDirection.normalize();
            float tStr = FastMath.nextRandomFloat() * strength;
            node.acceleration.addLocal(windDirection.x * tStr,
                                                                         windDirection.y * tStr,
                                                                         windDirection.z * tStr);
        }
    };

}
