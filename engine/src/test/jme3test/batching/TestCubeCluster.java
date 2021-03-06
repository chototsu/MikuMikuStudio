/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package jme3test.batching;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.input.controls.KeyTrigger;
import java.util.ArrayList;
import java.util.Random;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.BatchedGeometry;
import com.jme3.scene.GeometryBatch;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import java.util.List;

public class TestCubeCluster extends SimpleApplication {

    public static void main(String[] args) {
        TestCubeCluster app = new TestCubeCluster();
        settingst = new AppSettings(true);
        //settingst.setFrameRate(75);
        settingst.setResolution(640, 480);
        settingst.setVSync(false);
        settingst.setFullscreen(false);
        app.setSettings(settingst);
        app.setShowSettings(false);
        app.start();
    }
    private ActionListener al = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Start Game")) {
//              randomGenerator();
            }
        }
    };
    protected Random rand = new Random();
    protected int maxCubes = 2000;
    protected int startAt = 0;
    protected static int xPositions = 0, yPositions = 0, zPositions = 0;
    protected int returner = 0;
    protected ArrayList<Integer> xPosition = new ArrayList<Integer>();
    protected ArrayList<Integer> yPosition = new ArrayList<Integer>();
    protected ArrayList<Integer> zPosition = new ArrayList<Integer>();
    protected int xLimitf = 60, xLimits = -60, yLimitf = 60, yLimits = -20, zLimitf = 60, zLimits = -60;
    protected int circ = 8;//increases by 8 every time.
    protected int dynamic = 4;
    protected static AppSettings settingst;
    protected boolean isTrue = true;
    private BitmapText helloText2;
    private int lineLength = 50;
    protected GeometryBatch blue;
    protected GeometryBatch brown;
    protected GeometryBatch pink;
    protected GeometryBatch orange;
    protected List<Geometry> blueList = new ArrayList<Geometry>();
    protected List<Geometry> brownList = new ArrayList<Geometry>();
    protected List<Geometry> pinkList = new ArrayList<Geometry>();
    protected List<Geometry> orangeList = new ArrayList<Geometry>();
    Material mat1;
    Material mat2;
    Material mat3;
    Material mat4;
    Node terrain;
    //protected
//    protected Geometry player;

    @Override
    public void simpleInitApp() {


        blue = new GeometryBatch("blue");
        brown = new GeometryBatch("brown");
        pink = new GeometryBatch("pink");
        orange = new GeometryBatch("orange");
        mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.White);
        mat1.setColor("GlowColor", ColorRGBA.Blue.mult(10));
        blue.setMaterial(mat1);
        mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.White);
        mat2.setColor("GlowColor", ColorRGBA.Red.mult(10));
        brown.setMaterial(mat2);
        mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", ColorRGBA.White);
        mat3.setColor("GlowColor", ColorRGBA.Yellow.mult(10));
        pink.setMaterial(mat3);
        mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat4.setColor("Color", ColorRGBA.White);
        mat4.setColor("GlowColor", ColorRGBA.Orange.mult(10));
        orange.setMaterial(mat4);
        //rootNode.attachChild(SkyFactory.createSky(
        //  assetManager, "Textures/SKY02.zip", false));
        inputManager.addMapping("Start Game", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addListener(al, new String[]{"Start Game"});


        cam.setLocation(new Vector3f(-34.403286f, 126.65158f, 434.791f));
        cam.setRotation(new Quaternion(0.022630932f, 0.9749435f, -0.18736298f, 0.11776358f));


        //ChaseCamera c = new ChaseCamera();
        //flyCam.setEnabled(false);
        xPosition.add(0);
        yPosition.add(0);
        zPosition.add(0);


        randomGenerator();


        List<BatchedGeometry> l = blue.batch(blueList);
//        for (Iterator<BatchedGeometry> it = l.iterator(); it.hasNext();) {
//            BatchedGeometry batchedGeometry = it.next();
//            batchedGeometry.addControl(new MyControl());
//        }
        l = brown.batch(brownList);
//        for (Iterator<BatchedGeometry> it = l.iterator(); it.hasNext();) {
//            BatchedGeometry batchedGeometry = it.next();
//            batchedGeometry.addControl(new MyControl());
//        }
        l = pink.batch(pinkList);
//        for (Iterator<BatchedGeometry> it = l.iterator(); it.hasNext();) {
//            BatchedGeometry batchedGeometry = it.next();
//            batchedGeometry.addControl(new MyControl());
//        }

        l = orange.batch(orangeList);
//        for (Iterator<BatchedGeometry> it = l.iterator(); it.hasNext();) {
//            BatchedGeometry batchedGeometry = it.next();
//            batchedGeometry.addControl(new MyControl());
//            
//        }
        //    GeometryBatchFactory.optimize(terrain);
        terrain = new Node("terrain");
        terrain.setLocalTranslation(50, 0, 50);
        terrain.attachChild(blue);
        terrain.attachChild(brown);
        terrain.attachChild(pink);
        terrain.attachChild(orange);
        flyCam.setMoveSpeed(100);
        rootNode.attachChild(terrain);
        Vector3f pos = new Vector3f(-40, 0, -40);
        blue.setLocalTranslation(pos);
        brown.setLocalTranslation(pos);
        pink.setLocalTranslation(pos);
        orange.setLocalTranslation(pos);


        Arrow a = new Arrow(new Vector3f(0, 50, 0));
        Geometry g = new Geometry("a", a);
        g.setLocalTranslation(terrain.getLocalTranslation());
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", ColorRGBA.Blue);
        g.setMaterial(m);
        //    rootNode.attachChild(g);


        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(new BloomFilter(BloomFilter.GlowMode.Objects));
//        SSAOFilter ssao = new SSAOFilter(8.630104f,22.970434f,2.9299977f,0.2999997f);    
//        fpp.addFilter(ssao);
        viewPort.addProcessor(fpp);
        //   viewPort.setBackgroundColor(ColorRGBA.DarkGray);
    }

    public void randomGenerator() {
        for (int i = startAt; i < maxCubes - 1; i++) {
            randomize();
            Geometry box = new Geometry("Box" + i, new Box(Vector3f.ZERO, 1, 1, 1));
            box.setLocalTranslation(new Vector3f(xPosition.get(xPosition.size() - 1),
                    yPosition.get(yPosition.size() - 1),
                    zPosition.get(zPosition.size() - 1)));

            if (i < 500) {
                blueList.add(box);
            } else if (i < 1000) {
                brownList.add(box);
            } else if (i < 1500) {
                pinkList.add(box);
            } else {
                orangeList.add(box);
            }

        }
    }

    public GeometryBatch randomBatch() {

        int randomn = rand.nextInt(4);
        if (randomn == 0) {
            return blue;
        } else if (randomn == 1) {
            return brown;
        } else if (randomn == 2) {
            return pink;
        } else if (randomn == 3) {
            return orange;
        }
        return null;
    }

    public ColorRGBA randomColor() {
        ColorRGBA color = ColorRGBA.Black;
        int randomn = rand.nextInt(4);
        if (randomn == 0) {
            color = ColorRGBA.Orange;
        } else if (randomn == 1) {
            color = ColorRGBA.Blue;
        } else if (randomn == 2) {
            color = ColorRGBA.Brown;
        } else if (randomn == 3) {
            color = ColorRGBA.Magenta;
        }
        return color;
    }

    public void randomize() {
        int xpos = xPosition.get(xPosition.size() - 1);
        int ypos = yPosition.get(yPosition.size() - 1);
        int zpos = zPosition.get(zPosition.size() - 1);
        int x = 0;
        int y = 0;
        int z = 0;
        boolean unTrue = true;
        while (unTrue) {
            unTrue = false;
            boolean xChanged = false;
            x = 0;
            y = 0;
            z = 0;
            if (xpos >= lineLength * 2) {
                x = 2;
                xChanged = true;
            } else {
                x = xPosition.get(xPosition.size() - 1) + 2;
            }
            if (xChanged) {
                //y = yPosition.get(yPosition.size() - lineLength) + 2;
            } else {
                y = rand.nextInt(3);
                if (yPosition.size() > lineLength) {
                    if (yPosition.size() > 51) {
                        if (y == 0 && ypos < yLimitf && getym(lineLength) > ypos - 2) {
                            y = ypos + 2;
                        } else if (y == 1 && ypos > yLimits && getym(lineLength) < ypos + 2) {
                            y = ypos - 2;
                        } else if (y == 2 && getym(lineLength) > ypos - 2 && getym(lineLength) < ypos + 2) {
                            y = ypos;
                        } else {
                            if (ypos >= yLimitf) {
                                y = ypos - 2;
                            } else if (ypos <= yLimits) {
                                y = ypos + 2;
                            } else if (y == 0 && getym(lineLength) >= ypos - 4) {
                                y = ypos - 2;
                            } else if (y == 0 && getym(lineLength) >= ypos - 2) {
                                y = ypos;
                            } else if (y == 1 && getym(lineLength) >= ypos + 4) {
                                y = ypos + 2;
                            } else if (y == 1 && getym(lineLength) >= ypos + 2) {
                                y = ypos;
                            } else if (y == 2 && getym(lineLength) <= ypos - 2) {
                                y = ypos - 2;
                            } else if (y == 2 && getym(lineLength) >= ypos + 2) {
                                y = ypos + 2;
                            } else {
                                System.out.println("wtf");
                            }
                        }
                    } else if (yPosition.size() == lineLength) {
                        if (y == 0 && ypos < yLimitf) {
                            y = getym(lineLength) + 2;
                        } else if (y == 1 && ypos > yLimits) {
                            y = getym(lineLength) - 2;
                        }
                    }
                } else {
                    if (y == 0 && ypos < yLimitf) {
                        y = ypos + 2;
                    } else if (y == 1 && ypos > yLimits) {
                        y = ypos - 2;
                    } else if (y == 2) {
                        y = ypos;
                    } else if (y == 0 && ypos >= yLimitf) {
                        y = ypos - 2;
                    } else if (y == 1 && ypos <= yLimits) {
                        y = ypos + 2;
                    }
                }
            }
            if (xChanged) {
                z = zpos + 2;
            } else {
                z = zpos;
            }
//          for (int i = 0; i < xPosition.size(); i++)
//          {
//              if (x - xPosition.get(i) <= 1 && x - xPosition.get(i) >= -1 &&
//                      y - yPosition.get(i) <= 1 && y - yPosition.get(i) >= -1
//                      &&z - zPosition.get(i) <= 1 && z - zPosition.get(i) >=
//                      -1)
//              {
//                  unTrue = true;
//              }
//          }
        }
        xPosition.add(x);
        yPosition.add(y);
        zPosition.add(z);
    }

    public int getxm(int i) {
        return xPosition.get(xPosition.size() - i);
    }

    public int getym(int i) {
        return yPosition.get(yPosition.size() - i);
    }

    public int getzm(int i) {
        return zPosition.get(zPosition.size() - i);
    }

    public int getx(int i) {
        return xPosition.get(i);
    }

    public int gety(int i) {
        return yPosition.get(i);
    }

    public int getz(int i) {
        return zPosition.get(i);
    }
    long nbFrames = 0;
    long cullTime = 0;
    float time = 0;
    Vector3f lookAtPos = new Vector3f(0, 0, 0);
    float xpos = 0;
    Spatial box;

    @Override
    public void simpleUpdate(float tpf) {
        time += tpf;
        int random = rand.nextInt(2000);
        float mult1 = 1.0f;
        float mult2 = 1.0f;
        GeometryBatch b = null;
        if (random < 500) {
            b = blue;
            mult1 = 1.0f;
            mult2 = 1.0f;
        } else if (random < 1000) {
            b = brown;
            mult1 = -1.0f;
            mult2 = 1.0f;
        } else if (random < 1500) {
            b = pink;
            mult1 = 1.0f;
            mult2 = -1.0f;
        } else if (random <= 2000) {
            b = orange;
            mult1 = -1.0f;
            mult2 = -1.0f;
        }
        box = b.getChild("Box" + random);
        if (box != null) {
            Vector3f v = box.getLocalTranslation();
            box.setLocalTranslation(v.x + FastMath.sin(time * mult1) * 20, v.y +( FastMath.sin(time * mult1)* FastMath.cos(time * mult1)* 20), v.z + FastMath.cos(time * mult2) * 20);
        }
        terrain.setLocalRotation(new Quaternion().fromAngleAxis(time, Vector3f.UNIT_Y));


    }
}
