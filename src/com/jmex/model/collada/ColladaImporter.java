package com.jmex.model.collada;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.BoneTransform;
import com.jme.animation.SkinNode;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.ErrorManager;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.GeometryTool;
import com.jmex.model.collada.schema.COLLADASchemaDoc;
import com.jmex.model.collada.schema.COLLADAType;
import com.jmex.model.collada.schema.InstanceWithExtra;
import com.jmex.model.collada.schema.Name_arrayType;
import com.jmex.model.collada.schema.TargetableFloat3;
import com.jmex.model.collada.schema.accessorType;
import com.jmex.model.collada.schema.animationType;
import com.jmex.model.collada.schema.assetType;
import com.jmex.model.collada.schema.colorType;
import com.jmex.model.collada.schema.common_newparam_type;
import com.jmex.model.collada.schema.controllerType;
import com.jmex.model.collada.schema.effectType;
import com.jmex.model.collada.schema.float4x4;
import com.jmex.model.collada.schema.float_arrayType;
import com.jmex.model.collada.schema.fx_sampler2D_common;
import com.jmex.model.collada.schema.fx_surface_common;
import com.jmex.model.collada.schema.geometryType;
import com.jmex.model.collada.schema.imageType;
import com.jmex.model.collada.schema.instance_controllerType;
import com.jmex.model.collada.schema.lambertType;
import com.jmex.model.collada.schema.library_animationsType;
import com.jmex.model.collada.schema.library_controllersType;
import com.jmex.model.collada.schema.library_effectsType;
import com.jmex.model.collada.schema.library_geometriesType;
import com.jmex.model.collada.schema.library_imagesType;
import com.jmex.model.collada.schema.library_lightsType;
import com.jmex.model.collada.schema.library_materialsType;
import com.jmex.model.collada.schema.library_visual_scenesType;
import com.jmex.model.collada.schema.lightType;
import com.jmex.model.collada.schema.materialType;
import com.jmex.model.collada.schema.meshType;
import com.jmex.model.collada.schema.nodeType2;
import com.jmex.model.collada.schema.paramType3;
import com.jmex.model.collada.schema.phongType;
import com.jmex.model.collada.schema.sceneType;
import com.jmex.model.collada.schema.skinType;
import com.jmex.model.collada.schema.sourceType;
import com.jmex.model.collada.schema.techniqueType2;
import com.jmex.model.collada.schema.technique_commonType4;
import com.jmex.model.collada.schema.textureType;
import com.jmex.model.collada.schema.trianglesType;
import com.jmex.model.collada.schema.vertex_weightsType;
import com.jmex.model.collada.schema.visual_sceneType;

/**
 * <code>ColladaNode</code> provides a mechanism to parse and load a COLLADA
 * (COLLAborative Design Activity) model. Making use of a DOM parse contained in
 * com.nccore.collada, the XML formatted COLLADA file is parsed into Java Type
 * classes and then processed by jME. This processing is currently aimed at the
 * 1.4 release of the COLLADA Specification, and will, in most likelyhood,
 * require updating with a new release of COLLADA.
 * 
 * @author Mark Powell
 */
public class ColladaImporter {
    // asset information
    private String modelAuthor;
    private String tool;
    private String revision;
    private String unitName;
    private float unitMeter;
    private String upAxis;
    private static ColladaImporter instance;
    private String name;

    private String[] boneIds;

    private static boolean squelch;

    // location of texture assets (requires trailing '/')
    URL textureDirectory;

    private Map<String, Object> resourceLibrary;

    private ArrayList<String> controllerNames;
    private ArrayList<String> skinNodeNames;
    private ArrayList<String> geometryNames;
    private ArrayList<String> skeletonNames;

    private Node model;

    /**
     * Unique Serial ID for ColladaNode
     */
    private static final long serialVersionUID = -4024091270314000507L;

    /**
     * Default constructor creates the ColladaNode. A basic Node structure is
     * built and no data is loaded until the <code>load</code> method is
     * called.
     * 
     * @param name
     *            the name of the node.
     */
    private ColladaImporter(String name) {
        this.name = name;
    }

    /**
     * load takes the model path as a string object and uses the
     * COLLADASchemaDoc object to load it. This is then stored as a heirarchy of
     * data objects. This heirarchy is passed to the processCollada method to
     * build the jME data structures necessary to view the model.
     * 
     * @param model
     *            the complete path to the model to be loaded.
     */
    public static void load(InputStream source, URL textureDirectory,
            String name) {
        if (instance == null) {
            instance = new ColladaImporter(name);
        }

        instance.load(source, textureDirectory);
    }

    /**
     * load is called by the static load method, creating an instance of the
     * model to be returned.
     * 
     * @param source
     *            the source to import.
     * @param textureDirectory
     *            the location of the textures.
     * @return the model.
     */
    private void load(InputStream source, URL textureDirectory) {
        model = new Node(name);
        resourceLibrary = new HashMap<String, Object>();
        this.textureDirectory = textureDirectory;
        COLLADASchemaDoc doc = new COLLADASchemaDoc();
        try {
            COLLADAType root = new COLLADAType(doc.load(source));
            System.err.println("Version: " + root.getversion().getValue());
            processCollada(root);
        } catch (Exception ex) {
            ErrorManager.getInstance().addError(Level.WARNING,
                    "Unable to load Collada file. " + ex.getMessage());
            return;
        }

    }

    public static ArrayList<String> getControllerNames() {
        if (instance == null) {
            return null;
        }
        return instance.controllerNames;
    }

    public static ArrayList<String> getSkinNodeNames() {
        if (instance == null) {
            return null;
        }
        return instance.skinNodeNames;
    }

    public static ArrayList<String> getSkeletonNames() {
        if (instance == null) {
            return null;
        }
        return instance.skeletonNames;
    }

    public static ArrayList<String> getGeometryNames() {
        if (instance == null) {
            return null;
        }
        return instance.geometryNames;
    }

    public static Node getModel() {
        if (instance == null) {
            return null;
        }
        return instance.model;
    }

    public static SkinNode getSkinNode(String id) {
        if (instance == null) {
            return null;
        }
        return (SkinNode) instance.resourceLibrary.get(id);
    }

    public static BoneAnimation getAnimationController(String id) {
        if (instance == null) {
            return null;
        }
        return (BoneAnimation) instance.resourceLibrary.get(id);
    }

    public static Bone getSkeleton(String id) {
        if (instance == null) {
            return null;
        }
        return (Bone) instance.resourceLibrary.get(id);
    }

    public static Geometry getGeometry(String id) {
        if (instance == null) {
            return null;
        }
        return (Geometry) instance.resourceLibrary.get(id);
    }

    public static void cleanUp() {
        if (instance != null) {
            instance.shutdown();
        }
    }

    public void shutdown() {

        // if (vertMap != null) {
        // vertMap.clear();
        // vertMap = null;
        // }

        if (skinNodeNames != null) {
            skinNodeNames.clear();
            skinNodeNames = null;
        }

        // clean up
        if (resourceLibrary != null) {
            resourceLibrary.clear();
            resourceLibrary = null;
        }

        if (controllerNames != null) {
            controllerNames.clear();
            controllerNames = null;
        }

        boneIds = null;

        if (skinNodeNames != null) {
            skinNodeNames.clear();
            skinNodeNames = null;
        }

        if (geometryNames != null) {
            geometryNames.clear();
            geometryNames = null;
        }

        if (skeletonNames != null) {
            skeletonNames.clear();
            skeletonNames = null;
        }

        instance = null;
    }

    /**
     * Author of the last loaded collada model.
     * 
     * @return the modelAuthor the author of the last loaded model.
     */
    public String getModelAuthor() {
        return modelAuthor;
    }

    /**
     * Revision number of the last loaded collada model.
     * 
     * @return the revision revision number of the last loaded collada model.
     */
    public String getRevision() {
        return revision;
    }

    /**
     * the tool used to build the last collada model.
     * 
     * @return the tool
     */
    public String getTool() {
        return tool;
    }

    /**
     * the unit scale of the last collada model.
     * 
     * @return the unitMeter
     */
    public float getUnitMeter() {
        return unitMeter;
    }

    /**
     * the unit name of the last collada model.
     * 
     * @return the unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * getAssetInformation returns a string of the collected asset information
     * of this COLLADA model. The format is such: <br>
     * AUTHOR REVISION<br>
     * TOOL<br>
     * UNITNAME UNITMETER<br>
     * UPAXIS<br>
     * 
     * @return the string representation of the asset information of this file.
     */
    public String getAssetInformation() {
        return modelAuthor + " " + revision + "\n" + tool + "\n" + unitName
                + " " + unitMeter + "\n" + upAxis;
    }

    /**
     * processCollada takes a COLLADAType object that contains the heirarchical
     * information obtained from the XML structure of a COLLADA model. This root
     * object is processed and sets the data structures for jME to render the
     * model to *this* object.
     * 
     * @param root
     *            the COLLADAType data structure that contains the COLLADA model
     *            information.
     */
    public void processCollada(COLLADAType root) {

        // build the asset information about this model. This can be used
        // for debugging information. Only a single asset tag is allowed per
        // model.
        if (root.hasasset()) {
            try {
                processAssetInformation(root.getasset());
            } catch (Exception e) {
                if (!squelch) {
                    e.printStackTrace();
                    ErrorManager.getInstance().addError(Level.WARNING,
                            "Error processing asset information - " + e, e);
                }
            }
        }

        // builds the animation keyframes and places the controllers into a
        // node.
        if (root.haslibrary_animations()) {
            try {
                processAnimationLibrary(root.getlibrary_animations());
            } catch (Exception e) {
                e.printStackTrace();
                if (!squelch) {
                    ErrorManager.getInstance().addError(Level.WARNING,
                            "Error processing animation information - " + e, e);
                }
            }
        }

        if (root.haslibrary_animation_clips()) {
            if (!squelch) {
                ErrorManager.getInstance().addError(Level.WARNING,
                        "Animation Clips not currently supported");
            }
        }

        if (root.haslibrary_cameras()) {
            if (!squelch) {
                ErrorManager.getInstance().addError(Level.WARNING,
                        "Cameras not currently supported");
            }
        }

        if (root.haslibrary_force_fields()) {
            if (!squelch) {
                ErrorManager.getInstance().addError(Level.WARNING,
                        "Forcefields not currently supported");
            }
        }

        if (root.haslibrary_lights()) {
            try {
                processLightLibrary(root.getlibrary_lights());
            } catch (Exception e) {
                e.printStackTrace();
                if (!squelch) {
                    ErrorManager.getInstance().addError(Level.WARNING,
                            "Error processing light information - " + e, e);
                }
            }
        }

        if (root.haslibrary_nodes()) {
            if (!squelch) {
                ErrorManager.getInstance().addError(Level.WARNING,
                        "Stand-alone nodes not currently supported");
            }
        }

        // build a map of images that the materials can use in the future.
        if (root.haslibrary_images()) {
            try {
                processImageLibrary(root.getlibrary_images());
            } catch (Exception e) {
                if (!squelch) {
                    ErrorManager.getInstance()
                            .addError(
                                    Level.WARNING,
                                    "Error processing image library information - "
                                            + e, e);
                }
            }
        }

        // build all the material states that can be used later
        if (root.haslibrary_materials()) {
            try {
                processMaterialLibrary(root.getlibrary_materials());
            } catch (Exception e) {
                if (!squelch) {
                    ErrorManager.getInstance().addError(
                            Level.WARNING,
                            "Error processing material library information - "
                                    + e, e);
                }
            }
        }

        // process the library of effects, filling in the appropriate
        // states.
        if (root.haslibrary_effects()) {
            try {
                processEffects(root.getlibrary_effects());
            } catch (Exception e) {
                if (!squelch) {
                    e.printStackTrace();
                    ErrorManager.getInstance().addError(
                            Level.WARNING,
                            "Error processing effects library information - "
                                    + e, e);
                }
            }
        }

        // process the geometry information, creating the appropriate Geometry
        // object from jME (TriMesh, lines or point).
        if (root.haslibrary_geometries()) {
            try {
                processGeometry(root.getlibrary_geometries());
            } catch (Exception e) {
                e.printStackTrace();
                if (!squelch) {
                    ErrorManager.getInstance().addError(
                            Level.WARNING,
                            "Error processing geometry library information - "
                                    + e, e);
                    e.printStackTrace();
                }
            }
        }

        // controllers will define the action of another object. For example,
        // there may be a controller with a skin tag, defining how a mesh
        // is skinning a skeleton.
        if (root.haslibrary_controllers()) {
            try {
                processControllerLibrary(root.getlibrary_controllers());
            } catch (Exception e) {
                e.printStackTrace();
                if (!squelch) {
                    ErrorManager.getInstance().addError(
                            Level.WARNING,
                            "Error processing controller library information - "
                                    + e, e);
                }
            }
        }

        // process the visual scene. This scene will define how the geometries
        // are structured in the world.
        if (root.haslibrary_visual_scenes()) {
            try {
                processVisualSceneLibrary(root.getlibrary_visual_scenes());
            } catch (Exception e) {
                if (!squelch) {
                    e.printStackTrace();
                    ErrorManager.getInstance().addError(
                            Level.WARNING,
                            "Error processing visual scene library information - "
                                    + e, e);
                }
            }
        }

        // the scene tag actually takes instances of the visual scene defined
        // above
        // and attaches them to the model that is returned.
        if (root.hasscene()) {
            try {
                processScene(root.getscene());
            } catch (Exception e) {
                if (!squelch) {
                    ErrorManager.getInstance().addError(Level.WARNING,
                            "Error processing scene information - " + e, e);
                }
            }
        }

        optimizeGeometry();
    }

    private void optimizeGeometry() {
        for (String key : resourceLibrary.keySet()) {
            Object val = resourceLibrary.get(key);
            if (val instanceof TriMesh) {
                TriMesh mesh = (TriMesh) val;
                int options = GeometryTool.MV_SAME_COLORS
                        | GeometryTool.MV_SAME_NORMALS
                        | GeometryTool.MV_SAME_TEXS;
                if (mesh.getParent() instanceof SkinNode) {
                    SkinNode pNode = ((SkinNode) mesh.getParent());
                    pNode.updateGeometricState(100, true); // do an update
                    // first to get
                    // things into the
                    // right state.
                    pNode.revertToBind();
                    pNode.remapInfluences(GeometryTool.minimizeVerts(mesh,
                            options));
                    pNode.regenInfluenceOffsets();
                    pNode.refreshSkeletons();
                    pNode.updateSkin();
                } else {
                    GeometryTool.minimizeVerts(mesh, options);
                }
            }
        }
    }

    private void processLightLibrary(library_lightsType libraryLights)
            throws Exception {
        if (libraryLights.haslight()) {
            for (int i = 0; i < libraryLights.getlightCount(); i++) {
                processLight(libraryLights.getlightAt(i));
            }
        }
    }

    private void processLight(lightType light) throws Exception {
        technique_commonType4 common = light.gettechnique_common();
        if (common.hasambient()) {
            return;
        }
        if (DisplaySystem.getDisplaySystem() == null) {
            return;
        }
        LightNode lightNode = new LightNode(light.getid().toString(),
                DisplaySystem.getDisplaySystem().getRenderer()
                        .createLightState());

        Light l = null;

        if (common.hasdirectional()) {
            l = new DirectionalLight();
            l.getAmbient().set(0, 0, 0, 1);
            l.getSpecular().set(0, 0, 0, 1);
            l.setDiffuse(getLightColor(common.getdirectional().getcolor()));
        } else if (common.haspoint()) {
            l = new PointLight();
            l.getAmbient().set(0, 0, 0, 1);
            l.getSpecular().set(0, 0, 0, 1);
            l.setDiffuse(getLightColor(common.getpoint().getcolor()));
            l.setAttenuate(true);
            l.setConstant(Float.parseFloat(common.getpoint()
                    .getconstant_attenuation().getValue().toString()));
            l.setLinear(Float.parseFloat(common.getpoint()
                    .getlinear_attenuation().getValue().toString()));
            l.setQuadratic(Float.parseFloat(common.getpoint()
                    .getquadratic_attenuation().getValue().toString()));
        } else if (common.hasspot()) {
            l = new SpotLight();
            l.getAmbient().set(0, 0, 0, 1);
            l.getSpecular().set(0, 0, 0, 1);
            l.setDiffuse(getLightColor(common.getspot().getcolor()));
            l.setAttenuate(true);
            l.setConstant(Float.parseFloat(common.getspot()
                    .getconstant_attenuation().getValue().toString()));
            l.setLinear(Float.parseFloat(common.getspot()
                    .getlinear_attenuation().getValue().toString()));
            l.setQuadratic(Float.parseFloat(common.getspot()
                    .getquadratic_attenuation().getValue().toString()));
            ((SpotLight) l).setAngle(Float.parseFloat(common.getspot()
                    .getfalloff_angle().getValue().toString()));
            ((SpotLight) l).setExponent(Float.parseFloat(common.getspot()
                    .getfalloff_exponent().getValue().toString()));
        }

        if (l != null) {
            l.setEnabled(true);
            lightNode.setLight(l);

            resourceLibrary.put(lightNode.getName(), lightNode);
        }
    }

    private ColorRGBA getLightColor(TargetableFloat3 color) {
        StringTokenizer st = new StringTokenizer(color.getValue().toString());
        ColorRGBA c = new ColorRGBA(Float.parseFloat(st.nextToken()), Float
                .parseFloat(st.nextToken()), Float.parseFloat(st.nextToken()),
                1);
        return c;
    }

    /**
     * processScene finalizes the model node to be returned as the COLLADA
     * model. This looks up visual scene instances that were placed in the
     * resource library previously.
     * 
     * @param scene
     *            the scene to process
     * @throws Exception
     *             thrown if there is an error processing the xml.
     */
    public void processScene(sceneType scene) throws Exception {
        if (scene.hasinstance_visual_scene()) {
            for (int i = 0; i < scene.getinstance_visual_sceneCount(); i++) {
                String key = scene.getinstance_visual_sceneAt(i).geturl()
                        .toString().substring(1);
                Node n = (Node) resourceLibrary.get(key);
                if (n != null) {
                    model.attachChild(n);
                }
            }
        }
    }

    /**
     * processSource builds resource objects TIME, TRANSFORM and Name array for
     * the interpolation type.
     * 
     * @param source
     *            the source to process
     * @throws Exception
     *             exception thrown if there is a problem with
     */
    private void processSource(sourceType source) throws Exception {
        if (source.hasfloat_array()) {
            float[] floatArray = processFloatArray(source.getfloat_array());
            if (source.hastechnique_common()) {
                paramType3 p = source.gettechnique_common().getaccessor()
                        .getparam();
                if ("TIME".equals(p.getname().toString())) {
                    resourceLibrary.put(source.getid().toString(), floatArray);
                } else if ("float4x4".equals(p.gettype().toString())) {
                    Matrix4f[] transforms = new Matrix4f[floatArray.length / 16];
                    for (int i = 0; i < transforms.length; i++) {
                        transforms[i] = new Matrix4f();
                        transforms[i].m00 = floatArray[(16 * i)];
                        transforms[i].m01 = floatArray[(16 * i) + 1];
                        transforms[i].m02 = floatArray[(16 * i) + 2];
                        transforms[i].m03 = floatArray[(16 * i) + 3];
                        transforms[i].m10 = floatArray[(16 * i) + 4];
                        transforms[i].m11 = floatArray[(16 * i) + 5];
                        transforms[i].m12 = floatArray[(16 * i) + 6];
                        transforms[i].m13 = floatArray[(16 * i) + 7];
                        transforms[i].m20 = floatArray[(16 * i) + 8];
                        transforms[i].m21 = floatArray[(16 * i) + 9];
                        transforms[i].m22 = floatArray[(16 * i) + 10];
                        transforms[i].m23 = floatArray[(16 * i) + 11];
                        transforms[i].m30 = floatArray[(16 * i) + 12];
                        transforms[i].m31 = floatArray[(16 * i) + 13];
                        transforms[i].m32 = floatArray[(16 * i) + 14];
                        transforms[i].m33 = floatArray[(16 * i) + 15];
                    }
                    resourceLibrary.put(source.getid().toString(), transforms);
                } else if ("ROTX.ANGLE".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] xRot = new float[floatArray.length];
                        for (int i = 0; i < xRot.length; i++) {
                            xRot[i] = floatArray[i];
                        }
                        resourceLibrary.put(source.getid().toString(), xRot);
                    } else {
                        if (!squelch) {
                            ErrorManager.getInstance().addError(
                                    Level.WARNING,
                                    p.gettype() + " not yet supported "
                                            + "for animation transforms.");
                        }
                    }
                } else if ("ROTY.ANGLE".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] yRot = new float[floatArray.length];
                        for (int i = 0; i < yRot.length; i++) {
                            yRot[i] = floatArray[i];
                        }
                        resourceLibrary.put(source.getid().toString(), yRot);
                    } else {
                        if (!squelch) {
                            ErrorManager.getInstance().addError(
                                    Level.WARNING,
                                    p.gettype() + " not yet supported "
                                            + "for animation transforms.");
                        }
                    }
                } else if ("ROTZ.ANGLE".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] zRot = new float[floatArray.length];
                        for (int i = 0; i < zRot.length; i++) {
                            zRot[i] = floatArray[i];
                        }
                        resourceLibrary.put(source.getid().toString(), zRot);
                    } else {
                        if (!squelch) {
                            ErrorManager.getInstance().addError(
                                    Level.WARNING,
                                    p.gettype() + " not yet supported "
                                            + "for animation transforms.");
                        }
                    }
                } else if ("TRANS.X".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] xTrans = new float[floatArray.length];
                        for (int i = 0; i < xTrans.length; i++) {
                            xTrans[i] = floatArray[i];
                        }
                        resourceLibrary.put(source.getid().toString(), xTrans);
                    } else {
                        if (!squelch) {
                            ErrorManager.getInstance().addError(
                                    Level.WARNING,
                                    p.gettype() + " not yet supported "
                                            + "for animation transforms.");
                        }
                    }
                } else if ("TRANS.Y".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] yTrans = new float[floatArray.length];
                        for (int i = 0; i < yTrans.length; i++) {
                            yTrans[i] = floatArray[i];
                        }
                        resourceLibrary.put(source.getid().toString(), yTrans);
                    } else {
                        if (!squelch) {
                            ErrorManager.getInstance().addError(
                                    Level.WARNING,
                                    p.gettype() + " not yet supported "
                                            + "for animation transforms.");
                        }
                    }
                } else if ("TRANS.Z".equals(p.getname().toString())) {
                    if ("float".equals(p.gettype().toString())) {
                        float[] zTrans = new float[floatArray.length];
                        for (int i = 0; i < zTrans.length; i++) {
                            zTrans[i] = floatArray[i];
                        }
                        resourceLibrary.put(source.getid().toString(), zTrans);
                    } else {
                        if (!squelch) {
                            ErrorManager.getInstance().addError(
                                    Level.WARNING,
                                    p.gettype() + " not yet supported "
                                            + "for animation transforms.");
                        }
                    }
                } else {
                    if (!squelch) {
                        ErrorManager.getInstance().addError(
                                Level.WARNING,
                                p.getname() + " not yet supported "
                                        + "for animation source.");
                    }
                }
            }
        } else if (source.hasName_array()) {
            int[] interpolation = processInterpolationArray(source
                    .getName_array());
            resourceLibrary.put(source.getid().toString(), interpolation);
        }
    }

    /**
     * processInterpolationArray builds a int array that corresponds to the
     * interpolation types defined in BoneAnimationController.
     * 
     * @param array
     *            the array to process.
     * @return the int array.
     * @throws Exception
     *             thrown if there is a problem processing this xml document.
     */
    private int[] processInterpolationArray(Name_arrayType array)
            throws Exception {
        StringTokenizer st = new StringTokenizer(array.getValue().toString());
        int[] out = new int[array.getcount().intValue()];
        String token = null;
        for (int i = 0; i < out.length; i++) {
            token = st.nextToken();
            if ("LINEAR".equals(token)) {
                out[i] = BoneAnimation.LINEAR;
            } else if ("BEZIER".equals(token)) {
                out[i] = BoneAnimation.BEZIER;
            }
        }
        return out;
    }

    /**
     * processes a float array object. The floats are represented as a String
     * with the values delimited by a space.
     * 
     * @param array
     *            the array to parse.
     * @return the float array to return.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private float[] processFloatArray(float_arrayType array) throws Exception {
        StringTokenizer st = new StringTokenizer(array.getValue().toString());
        float[] out = new float[array.getcount().intValue()];
        for (int i = 0; i < out.length; i++) {
            out[i] = Float.parseFloat(st.nextToken());
        }
        return out;
    }

    /**
     * processAssetInformation will store the information about the collada file
     * for future reference. This will include the author, the tool used, the
     * revision, the unit information, and the defined up axis.
     * 
     * @param asset
     *            the assetType for the root of the model.
     */
    private void processAssetInformation(assetType asset) throws Exception {
        modelAuthor = asset.getcontributor().getauthor().toString();
        tool = asset.getcontributor().getauthoring_tool().toString();
        if (asset.hasrevision()) {
            revision = asset.getrevision().toString();
        }
        unitName = asset.getunit().getname().toString();
        unitMeter = asset.getunit().getmeter().floatValue();
        upAxis = asset.getup_axis().getValue();
    }

    /**
     * processAnimationLibrary will store the individual
     * BoneAnimationControllers in the resource library for future use.
     * Animations at this level can be considered top level animations that
     * should be called from this level. These animations may contain children
     * animations the top level animation is responsible for calling.
     * 
     * @param animLib
     *            the library of animations to parse.
     */
    private void processAnimationLibrary(library_animationsType animLib)
            throws Exception {
        if (animLib.hasanimation()) {
            if (controllerNames == null) {
                controllerNames = new ArrayList<String>();
            }
            for (int i = 0; i < animLib.getanimationCount(); i++) {
                BoneAnimation bac = processAnimation(animLib.getanimationAt(i));
                bac.setInterpolate(false);
                bac.optimize(true);
                resourceLibrary.put(bac.getName(), bac);
                controllerNames.add(bac.getName());
            }

        }
    }

    /**
     * the animation element catgorizes an animation hierarchy with each
     * controller defining the animation's keyframe and sampler functions. These
     * interact on single bones, where a collection of controllers will build up
     * a complete animation.
     * 
     * @param animation
     *            the animation to parse.
     * @param bac
     *            the parent controller.
     */
    private BoneAnimation processAnimation(animationType animation)
            throws Exception {
        BoneAnimation out = new BoneAnimation(animation.getid().toString());
        BoneTransform bt = new BoneTransform();
        out.setInterpolate(true);

        if (animation.hassource()) {
            for (int i = 0; i < animation.getsourceCount(); i++) {
                processSource(animation.getsourceAt(i));
            }
        }

        float[] rotx = null;
        float[] roty = null;
        float[] rotz = null;
        float[] transx = null;
        float[] transy = null;
        float[] transz = null;
        boolean transformsSet = false;
        if (animation.hassampler()) {
            for (int j = 0; j < animation.getsamplerCount(); j++) {
                for (int i = 0; i < animation.getsamplerAt(j).getinputCount(); i++) {
                    if ("INPUT".equals(animation.getsamplerAt(j).getinputAt(i)
                            .getsemantic().toString())) {
                        float[] times = (float[]) resourceLibrary.get(animation
                                .getsamplerAt(j).getinputAt(i).getsource()
                                .toString().substring(1));
                        out.setTimes(times);
                        out.setStartFrame(0);
                        out.setEndFrame(times.length - 1);
                    } else if ("OUTPUT".equals(animation.getsamplerAt(j)
                            .getinputAt(i).getsemantic().toString())) {
                        Object object = resourceLibrary.get(animation
                                .getsamplerAt(j).getinputAt(i).getsource()
                                .toString().substring(1));
                        if (object instanceof Matrix4f[]) {
                            Matrix4f[] transforms = (Matrix4f[]) object;
                            bt.setTransforms(transforms);
                            transformsSet = true;
                        } else if (object instanceof float[]) {
                            // Another bit of a hack that should be improved:
                            // to put the float arrays into the BoneTransform,
                            // we need to know what angle it is changing,
                            // I see know way to determine other than looking
                            // at the source name.
                            if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Rotate-X-")) {
                                rotx = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Rotate-Y-")) {
                                roty = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Rotate-Z-")) {
                                rotz = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Translate-X-")) {
                                transx = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Translate-Y-")) {
                                transy = (float[]) object;
                            } else if (animation.getsamplerAt(j).getinputAt(i)
                                    .getsource().toString().contains(
                                            "Translate-Z-")) {
                                transz = (float[]) object;
                            } else {
                                if (!squelch) {
                                    ErrorManager.getInstance().addError(
                                            Level.WARNING,
                                            "Not sure what this sampler is.");
                                }
                            }
                        }
                    } else if ("INTERPOLATION".equals(animation.getsamplerAt(j)
                            .getinputAt(i).getsemantic().toString())) {
                        int[] interpolation = (int[]) resourceLibrary
                                .get(animation.getsamplerAt(j).getinputAt(i)
                                        .getsource().toString().substring(1));
                        out.setInterpolationTypes(interpolation);
                    }
                }
            }

            if (!transformsSet) {
                Matrix4f[] transforms = generateTransforms(rotx, roty, rotz,
                        transx, transy, transz);
                if (transforms != null) {
                    bt.setTransforms(transforms);
                }
            }
        }

        if (animation.haschannel()) {
            String key = animation.getchannel().gettarget().toString()
                    .substring(
                            0,
                            animation.getchannel().gettarget().toString()
                                    .indexOf('/'));
            bt.setBoneId(key);
            bt.getBoneId();
            Bone b = (Bone) resourceLibrary.get(key);
            if (b != null) {
                bt.setBone(b);
            }

            out.addBoneTransforms(bt);

        }

        // if the animation has children attach them
        if (animation.hasanimation()) {
            for (int i = 0; i < animation.getanimationCount(); i++) {
                out.addBoneAnimation(processAnimation(animation
                        .getanimationAt(i)));
            }
        }

        return out;
    }

    private Matrix4f[] generateTransforms(float[] rotx, float[] roty,
            float[] rotz, float[] transx, float[] transy, float[] transz) {

        Quaternion rot = new Quaternion();
        int index = 0;
        if (rotx != null) {
            index = rotx.length;
        } else if (transx != null) {
            index = transx.length;
        }

        Matrix4f[] transforms = new Matrix4f[index];
        float[] angles = new float[3];
        for (int i = 0; i < transforms.length; i++) {
            angles[0] = angles[1] = angles[2] = 0;
            if (rotx != null) {
                angles[0] = rotx[i];
            }

            if (roty != null) {
                angles[1] = roty[i];
            }

            if (rotz != null) {
                angles[2] = rotz[i];
            }
            rot.fromAngles(angles);
            transforms[i] = rot.toRotationMatrix(new Matrix4f());
            if (transx != null) {
                transforms[i].m03 = transx[i];
            }
            if (transx != null) {
                transforms[i].m13 = transy[i];
            }
            if (transx != null) {
                transforms[i].m23 = transz[i];
            }
        }

        return transforms;

    }

    /**
     * processImageLibrary will build a collection of image filenames. The image
     * tag contains the full directory path of the image from the artists
     * working directory. Therefore, the directory will be stripped off leaving
     * only the filename. This filename will be associated with a id key that
     * can be obtained by the material that wishes to make use of it.
     * 
     * @param libraryImg
     *            the library of images (name/image pair).
     */
    private void processImageLibrary(library_imagesType libraryImg)
            throws Exception {
        if (libraryImg.hasimage()) {
            for (int i = 0; i < libraryImg.getimageCount(); i++) {
                processImage(libraryImg.getimageAt(i));

            }
        }
    }

    /**
     * processImage takes an image type and places the necessary information in
     * the resource library.
     * 
     * @param image
     *            the image to process.
     * @throws Exception
     *             thrown if there is a problem with the imagetype.
     */
    private void processImage(imageType image) throws Exception {
        if (image.hasdata()) {
            if (!squelch) {
                ErrorManager.getInstance().addError(Level.WARNING,
                        "Raw data images not supported.");
            }
        }

        if (image.hasinit_from()) {
            resourceLibrary.put(image.getid().toString(), getFileName(image
                    .getinit_from().toString()));
        }
    }

    /**
     * getFileName takes a String object stripping off any directory information
     * returning only the substring that follows the last '/' or '\'.
     * 
     * @param fullName
     *            the fileName to strip.
     * @return the stripped file name.
     */
    private String getFileName(String fullName) {
        int a = fullName.lastIndexOf('/') + 1;
        int b = fullName.lastIndexOf('\\') + 1;

        int index = Math.max(a, b);

        return fullName.substring(index);
    }

    /**
     * processMaterialLibrary will build a collection (Map) of MaterialStates,
     * with the defined material id as the key in the Map. This map and
     * corresponding key will then be used to apply materials to the appropriate
     * node. The library only defines the id of the material and the url of the
     * instance effect that defines its qualities, it won't be until the
     * library_effects tag is processed that the material state information is
     * filled in.
     * 
     * @param libraryMat
     *            the material library type.
     */
    private void processMaterialLibrary(library_materialsType libraryMat)
            throws Exception {
        if (libraryMat.hasmaterial()) {
            for (int i = 0; i < libraryMat.getmaterialCount(); i++) {
                processMaterial(libraryMat.getmaterialAt(i));
            }
        }
    }

    /**
     * process Material which typically contains an id and a reference URL to an
     * effect.
     * 
     * @param mat
     */
    private void processMaterial(materialType mat) throws Exception {
        if (mat.hasinstance_effect()) {
            String url = mat.getinstance_effect().geturl().toString();
            if (url.startsWith("#")) {
                url = url.substring(1);
            }
            resourceLibrary.put(url, new ColladaMaterial());
            resourceLibrary.put(mat.getid().toString(), url);
        }
    }

    /**
     * processEffects will build effects as defined by the techinque. The
     * appropriate render state will be obtained from the materialMap hashmap
     * based on the the name of the effect. Currently, the id of the effect is
     * ignored as it is directly tied to the material id. However, in the future
     * this may require support.
     * 
     * @param libraryEffects
     *            the library of effects to build.
     */
    private void processEffects(library_effectsType libraryEffects)
            throws Exception {
        if (libraryEffects.haseffect()) {
            for (int i = 0; i < libraryEffects.geteffectCount(); i++) {
                String key = libraryEffects.geteffectAt(i).getid().toString();
                ColladaMaterial mat = (ColladaMaterial) resourceLibrary
                        .get(key);
                if (mat != null) {
                    fillMaterial(libraryEffects.geteffectAt(i), mat);
                }
            }
        }
    }

    /**
     * fillMaterial will use the provided effectType to generate the material
     * setting for the collada model. The effect can handle both programmable
     * pipelines and fixed pipelines. This is defined by what sort of profile it
     * is using (profile_COMMON, profile_GLSL, profile_CG). Currently,
     * profile_CG is ignored. There may be multiple profiles, describing a path
     * of fallbacks. Currently, only one profile will be supported at a time.<br>
     * <br>
     * There is a possibility that each profile may have multiple techniques,
     * defining different materials for different situations, i.e. LOD. This
     * version of the loader will assume a single technique.
     * 
     * @param effect
     *            the collada effect to process.
     * @param mat
     *            the ColladaMaterial that will hold the RenderStates needed to
     *            express this material.
     * @throws Exception
     *             thrown if there is a problem processing the file.
     */
    private void fillMaterial(effectType effect, ColladaMaterial mat)
            throws Exception {
        // process the fixed pipeline information
        if (effect.hasprofile_COMMON()) {
            for (int i = 0; i < effect.getprofile_COMMON().getnewparamCount(); i++) {
                processNewParam(effect.getprofile_COMMON().getnewparamAt(i),
                        mat);
            }

            for (int i = 0; i < effect.getprofile_COMMON().gettechniqueCount(); i++) {
                processTechnique(effect.getprofile_COMMON().gettechniqueAt(i),
                        mat);
            }
        }

        // process the programmable pipeline
        if (effect.hasprofile_GLSL()) {
            if (!squelch) {
                ErrorManager.getInstance().addError(Level.WARNING,
                        "GLSL loading not yet supported.");
            }
        }
    }

    /**
     * processNewParam sets specific properties of a material (surface properties,
     * sampler properties, etc).
     * @param param the xml element of the new parameter.
     * @param mat the material to store the parameters in.
     * @throws Exception thrown if there is a problem reading the xml.
     */
    private void processNewParam(common_newparam_type param, ColladaMaterial mat)
            throws Exception {
        if (param.hassampler2D()) {
            processSampler2D(param.getsid().toString(), param.getsampler2D(),
                    mat);
        }

        if (param.hassurface()) {
            processSurface(param.getsid().toString(), param.getsurface(), mat);
        }
    }

    /**
     * processes images information, defining the min and mag filter for
     * mipmapping.
     * @param id the id on the sampler
     * @param sampler the sampler xml element.
     * @param mat the material to store the values in.
     * @throws Exception thrown if there is a problem reading the file.
     */
    private void processSampler2D(String id, fx_sampler2D_common sampler,
            ColladaMaterial mat) throws Exception {
        if (sampler.hasmagfilter()) {
            mat.magFilter = sampler.getmagfilter().getValue().toString();
        }

        if (sampler.hasminfilter()) {
            mat.minFilter = sampler.getminfilter().getValue().toString();
        }

        resourceLibrary.put(id, sampler.getsource().getValue().toString());

    }

    private void processSurface(String id, fx_surface_common surface,
            ColladaMaterial mat) throws Exception {
        resourceLibrary.put(id, surface.getinit_from().getValue().toString());
    }

    /**
     * processTechnique process a technique of techniqueType2 which are defined
     * to be returned from a profile_COMMON object. This technique contains
     * images, lambert shading, phong shading and blinn shading.
     * 
     * @param technique
     *            the fixed pipeline technique.
     * @param mat
     *            the material to store the technique in.
     */
    private void processTechnique(techniqueType2 technique, ColladaMaterial mat)
            throws Exception {
        if (technique.haslambert()) {
            // lambert shading, create a FLAT shade state and material state
            // with
            // defined colors.
            ShadeState ss = DisplaySystem.getDisplaySystem().getRenderer()
                    .createShadeState();
            ss.setShade(ShadeState.SM_FLAT);
            mat.setState(ss);

            // obtain the colors for the material
            MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                    .createMaterialState();
            lambertType lt = technique.getlambert();
            // set the ambient color value of the material
            if (lt.hasambient()) {
                ms.setAmbient(getColor(lt.getambient().getcolor()));
            }
            // set the diffuse color value of the material
            if (lt.hasdiffuse()) {
                if (lt.getdiffuse().hascolor()) {
                    ms.setDiffuse(getColor(lt.getdiffuse().getcolor()));
                }
            }
            // set the emmission color value of the material
            if (lt.hasemission()) {
                ms.setEmissive(getColor(lt.getemission().getcolor()));
            }
            mat.setState(ms);

            if (lt.hastransparent()) {
                if (lt.gettransparent().hascolor()
                        && !lt.gettransparency().getfloat2().getValue()
                                .toString().equals("0")) {
                    AlphaState as = DisplaySystem.getDisplaySystem()
                            .getRenderer().createAlphaState();
                    as.setSrcFunction(AlphaState.SB_ONE_MINUS_DST_COLOR);
                    as.setDstFunction(AlphaState.DB_ONE);
                    as.setBlendEnabled(true);
                    mat.setState(as);
                } else if (lt.gettransparent().hastexture()) {
                    AlphaState as = DisplaySystem.getDisplaySystem()
                            .getRenderer().createAlphaState();
                    as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
                    as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
                    as.setBlendEnabled(true);
                    as.setReference(0.14f);
                    as.setTestEnabled(true);
                    as.setTestFunction(AlphaState.TF_GEQUAL);
                    mat.setState(as);
                }

            }

            // Ignored: reflective attributes, transparent attributes
        }

        // blinn shading and phong shading are virtually the same, and OpenGL
        // only has a single "smooth" attribute for this.
        if (technique.hasphong()) {
            // obtain the colors for the material
            MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                    .createMaterialState();
            phongType pt = technique.getphong();
            // set the ambient color value of the material
            if (pt.hasambient()) {
                ms.setAmbient(getColor(pt.getambient().getcolor()));
            }
            // set the diffuse color value of the material
            if (pt.hasdiffuse()) {
                if (pt.getdiffuse().hascolor()) {
                    ms.setDiffuse(getColor(pt.getdiffuse().getcolor()));
                }

                if (pt.getdiffuse().hastexture()) {
                    // create a texturestate, and we will need to make use of
                    // texcoord to put this texture in the correct "unit"
                    for (int i = 0; i < pt.getdiffuse().gettextureCount(); i++) {
                        mat.setState(processTexture(pt.getdiffuse()
                                .gettextureAt(i), mat));
                    }
                }
            }
            // set the emmission color value of the material
            if (pt.hasemission()) {
                ms.setEmissive(getColor(pt.getemission().getcolor()));
            }

            if (pt.hastransparent()) {
                if (pt.gettransparent().hascolor()
                        && !pt.gettransparency().getfloat2().getValue()
                                .toString().equals("0")) {
                    AlphaState as = DisplaySystem.getDisplaySystem()
                            .getRenderer().createAlphaState();
                    as.setSrcFunction(AlphaState.SB_ONE_MINUS_DST_COLOR);
                    as.setDstFunction(AlphaState.DB_ONE);
                    as.setBlendEnabled(true);
                    mat.setState(as);
                } else if (pt.gettransparent().hastexture()) {
                    AlphaState as = DisplaySystem.getDisplaySystem()
                            .getRenderer().createAlphaState();
                    as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
                    as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
                    as.setBlendEnabled(true);
                    as.setReference(0.14f);
                    as.setTestEnabled(true);
                    as.setTestFunction(AlphaState.TF_GEQUAL);
                    mat.setState(as);
                }

            }

            mat.setState(ms);
        }
    }

    /**
     * processTexture generates a texture state that contains the image and
     * texture coordinate unit information. This texture state is returned to be
     * placed in the Collada material.
     * 
     * @param texture
     *            the texture type to process.
     * @return the generated TextureState that handles this texture tag.
     */
    private TextureState processTexture(textureType texture, ColladaMaterial mat)
            throws Exception {
        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer()
                .createTextureState();
        URL textureURL = null;
        String key = texture.gettexture().toString();
        String filename = (String) resourceLibrary.get(key);

        if (filename == null) {
            return null;
        }

        while (!filename.contains(".")) {
            filename = (String) resourceLibrary.get(filename);
            if (filename == null) {
                return null;
            }
        }

        loadTexture(ts, textureURL, filename, mat);

        return ts;
    }

    /**
     * @param ts
     * @param textureURL
     * @param filename
     */
    private void loadTexture(TextureState ts, URL textureURL, String filename,
            ColladaMaterial mat) {
        if (textureDirectory != null) {
            try {
                textureURL = new URL(textureDirectory.toString() + filename);
            } catch (MalformedURLException e) {
                if (!squelch) {
                    ErrorManager.getInstance().addError(
                            Level.WARNING,
                            "Invalid texture location (texture not found): \""
                                    + (textureDirectory.toString() + filename)
                                    + "\"");
                }
            }
        }
        Texture t0 = TextureManager.loadTexture(textureURL, mat
                .getMinFilterConstant(), mat.getMagFilterConstant());
        if (t0 != null) {
            // Clamping for now, there is probably a section that defines how
            // wrapping should be handled.
            t0.setWrap(Texture.WM_ECLAMP_S_ECLAMP_T);
            ts.setTexture(t0);
        } else {
            if (!squelch) {
                ErrorManager.getInstance().addError(
                        Level.WARNING,
                        "Invalid texture: \""
                                + (textureDirectory.toString() + filename)
                                + "\"");
            }
        }
    }

    /**
     * Process Geometry will build a number of Geometry objects attaching them
     * to the supplied parent.
     * 
     * @param geometryLibrary
     *            the geometries to process individually.
     * @param parent
     *            the Node that the built geometries will be attached to.
     */
    private void processGeometry(library_geometriesType geometryLibrary)
            throws Exception {
        // go through each geometry one at a time
        for (int i = 0; i < geometryLibrary.getgeometryCount(); i++) {
            geometryType geom = geometryLibrary.getgeometryAt(i);
            if (geom.hasmesh()) {
                for (int j = 0; j < geom.getmeshCount(); j++) {
                    Geometry g = processMesh(geom.getmeshAt(j), geom);
                    resourceLibrary.put(geom.getid().toString(), g);
                    if (geometryNames == null) {
                        geometryNames = new ArrayList<String>();
                    }

                    geometryNames.add(geom.getid().toString());
                }
            }
            // splines are not currently supported.
            if (geom.hasspline()) {
                if (!squelch) {
                    ErrorManager.getInstance().addError(Level.WARNING,
                            "splines not yet supported.");
                }
            }
        }
    }

    /**
     * processControllerLibrary builds a controller for each controller tag in
     * the file.
     * 
     * @param controllerLibrary
     *            the controller library object to parse.
     * @throws Exception
     *             thrown if there is a problem with the loader.
     */
    private void processControllerLibrary(
            library_controllersType controllerLibrary) throws Exception {
        if (controllerLibrary.hascontroller()) {
            for (int i = 0; i < controllerLibrary.getcontrollerCount(); i++) {
                processController(controllerLibrary.getcontrollerAt(i));
            }
        }
    }

    /**
     * controllers define how one object interacts with another. Typically, this
     * is skinning and morph targets.
     * 
     * @param controller
     *            the controller to process
     */
    private void processController(controllerType controller) throws Exception {
        // skin and morph are mutually exclusive.
        if (controller.hasskin()) {
            // there can only be one skin per controller
            processSkin(controller.getid().toString(), controller.getskin());
        } else if (controller.hasmorph()) {
            //more not currently supported.
        }
    }

    /**
     * processSkin builds a SkinnedMesh object that defines the vertex
     * information of a model and the skeletal system that supports it.
     * 
     * @param skin
     *            the skin to process
     * @throws Exception
     *             thrown if there is a problem parsing the skin.
     */
    private void processSkin(String id, skinType skin) throws Exception {
        // Add this skin's associated mesh to the resource library
        // resourceLibrary.put(id, skin.getsource().toString());

        SkinNode skinNode = new SkinNode(id + "_node");
        if (skinNodeNames == null) {
            skinNodeNames = new ArrayList<String>();
        }
        skinNodeNames.add(id);
        resourceLibrary.put(id, skinNode);

        // create a new SkinnedMesh object that will act on a given geometry.
        // SkinnedMesh skinnedMesh = new
        // SkinnedMesh(source.getName()+"skinned",source);
        // the bind shape matrix defines the overall orientation of the mesh
        // before any skinning occurs.
        if (skin.hasbind_shape_matrix()) {
            String key = skin.getsource().toString();
            if(key.startsWith("#")) {
                key = key.substring(1);
            }
            Geometry mesh = (Geometry) resourceLibrary.get(key);
            if (mesh == null) {
                if (!squelch) {
                    ErrorManager.getInstance().addError(
                            Level.WARNING,
                            key
                                    + " mesh does NOT exist in COLLADA file.");
                }
                return;
            }
            processBindShapeMatrix(skinNode, skin.getbind_shape_matrix());
            skinNode.setSkin(mesh);
        }

        // There are a couple types of sources, those setting the joints,
        // the binding table, and the weights. The Collada exporter
        // automatically
        // names them something like skin-joint-*, skin-binding-table-*, etc.
        // we are going to check for the string to determine what it is.
        if (skin.hassource2()) {
            for (int i = 0; i < skin.getsource2Count(); i++) {
                processControllerSource(skin.getsource2At(i));
            }
        }

        // the vertex weights will be assigned to the appropriate bones
        if (skin.hasvertex_weights()) {
            processVertexWeights(skin.getvertex_weights(), skinNode);
        }

        if (skin.hasjoints()) {
            String[] boneIds = null;
            Matrix4f[] bindMatrices = null;
            // define the inverse bind matrix to the joint
            if (skin.getjoints().hasinput()) {
                for (int i = 0; i < skin.getjoints().getinputCount(); i++) {
                    if ("JOINT".equals(skin.getjoints().getinputAt(i)
                            .getsemantic().toString())) {
                        boneIds = (String[]) resourceLibrary.get(skin
                                .getjoints().getinputAt(i).getsource()
                                .toString().substring(1));
                    } else if ("INV_BIND_MATRIX".equals(skin.getjoints()
                            .getinputAt(i).getsemantic().toString())) {
                        bindMatrices = (Matrix4f[]) resourceLibrary.get(skin
                                .getjoints().getinputAt(i).getsource()
                                .toString().substring(1));
                    }
                }
            }

            for (int i = 0; i < boneIds.length; i++) {
                Bone b = (Bone) resourceLibrary.get(boneIds[i]);
                b.setBindMatrix(bindMatrices[i].invert());
            }
        }
    }

    /**
     * processVertexWeights defines a list of vertices and weights for a given
     * bone. These bones are defined by <v> as the first element to a group. The
     * bones were prebuilt in the priocessControllerSource method.
     * 
     * @param weights
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void processVertexWeights(vertex_weightsType weights,
            SkinNode skinNode) throws Exception {
        int[] boneCount = new int[weights.getcount().intValue()];
        StringTokenizer st = new StringTokenizer(weights.getvcount().getValue());
        for (int i = 0; i < boneCount.length; i++) {
            boneCount[i] = Integer.parseInt(st.nextToken());
        }

        st = new StringTokenizer(weights.getv().getValue());
        int count = 0;

        String[] boneIdArray = null;
        float[] weightArray = null;
        for (int i = 0; i < weights.getinputCount(); i++) {
            if ("JOINT".equals(weights.getinputAt(i).getsemantic().toString())) {
                String key = weights.getinputAt(i).getsource().toString();
                key = key.substring(1);
                boneIdArray = (String[]) resourceLibrary.get(key);
            } else if ("WEIGHT".equals(weights.getinputAt(i).getsemantic()
                    .toString())) {
                String key = weights.getinputAt(i).getsource().toString();
                key = key.substring(1);
                weightArray = (float[]) resourceLibrary.get(key);
            }
        }

        if (boneIdArray == null || weightArray == null) {
            if (!squelch) {
                ErrorManager.getInstance().addError(
                        Level.WARNING,
                        "Missing resource values for either bone "
                                + "weights or bone vertex ids.");
            }
            return;

        }

        Map<Integer, ArrayList<BatchVertPair>> vertMap = (Map) resourceLibrary
                .get(skinNode.getSkin().getName() + "VertMap");
        while (st.hasMoreTokens()) {
            // Get bone index
            for (int i = 0; i < boneCount[count]; i++) {
                int idIndex = Integer.parseInt(st.nextToken());
                int key = Integer.parseInt(st.nextToken());

                float weight = weightArray[key];
                ArrayList<BatchVertPair> target = vertMap.get(count);
                if (target != null) {
                    for (int j = 0, max = target.size(); j < max; j++) {
                        BatchVertPair bvp = target.get(j);
                        // Bone b = (Bone)resourceLibrary.get(boneIds[idIndex]);
                        skinNode.addBoneInfluence(bvp.batch, bvp.index,
                                boneIds[idIndex], weight);
                    }
                }
            }
            count++;
        }
    }

    /**
     * processControllerSource will process the source types that define how a
     * controller is built. This includes support for skin joints, bindings and
     * weights.
     * 
     * @param source
     *            the source to process.
     * @throws Exception
     *             thrown if there is a problem processing the XML.
     */
    private void processControllerSource(sourceType source) throws Exception {
        // check for the joint id list
        String key = source.gettechnique_common().getaccessor().getparam().gettype().getValue().toString();
        if (key.equalsIgnoreCase("IDREF")) {
            if (source.hasIDREF_array()) {
                Bone[] bones = new Bone[Integer.parseInt(source.getIDREF_array()
                        .getcount().toString())];
                boneIds = new String[bones.length];
                StringTokenizer st = new StringTokenizer(source.getIDREF_array()
                        .getValue().toString());
                for (int i = 0; i < bones.length; i++) {
                    // this skin has a number of bones assigned to it.
                    // Create a Bone for each entry.
                    bones[i] = new Bone(st.nextToken());
                    boneIds[i] = bones[i].getName();
                    resourceLibrary.put(boneIds[i], bones[i]);
                    resourceLibrary.put(source.getid().toString(), boneIds);
                }

            }
        } else if (key.equalsIgnoreCase("Name")) {
            if (source.hasName_array()) {
                Bone[] bones = new Bone[Integer.parseInt(source.getName_array()
                        .getcount().toString())];
                boneIds = new String[bones.length];
                StringTokenizer st = new StringTokenizer(source.getName_array()
                        .getValue().toString());
                for (int i = 0; i < bones.length; i++) {
                    // this skin has a number of bones assigned to it.
                    // Create a Bone for each entry.
                    bones[i] = new Bone(st.nextToken());
                    boneIds[i] = bones[i].getName();
                    resourceLibrary.put(boneIds[i], bones[i]);
                    resourceLibrary.put(source.getid().toString(), boneIds);
                }

            }
        } else if (key.equalsIgnoreCase("float4x4")) {

            StringTokenizer st = new StringTokenizer(source.getfloat_array()
                    .getValue().toString());
            int numOfTransforms = st.countTokens() / 16;
            // this creates a 4x4 matrix

            Matrix4f[] tm = new Matrix4f[numOfTransforms];
            for (int i = 0; i < tm.length; i++) {
                tm[i] = new Matrix4f();
                tm[i].m00 = Float.parseFloat(st.nextToken());
                tm[i].m01 = Float.parseFloat(st.nextToken());
                tm[i].m02 = Float.parseFloat(st.nextToken());
                tm[i].m03 = Float.parseFloat(st.nextToken());
                tm[i].m10 = Float.parseFloat(st.nextToken());
                tm[i].m11 = Float.parseFloat(st.nextToken());
                tm[i].m12 = Float.parseFloat(st.nextToken());
                tm[i].m13 = Float.parseFloat(st.nextToken());
                tm[i].m20 = Float.parseFloat(st.nextToken());
                tm[i].m21 = Float.parseFloat(st.nextToken());
                tm[i].m22 = Float.parseFloat(st.nextToken());
                tm[i].m23 = Float.parseFloat(st.nextToken());
                tm[i].m30 = Float.parseFloat(st.nextToken());
                tm[i].m31 = Float.parseFloat(st.nextToken());
                tm[i].m32 = Float.parseFloat(st.nextToken());
                tm[i].m33 = Float.parseFloat(st.nextToken());
            }

            resourceLibrary.put(source.getid().toString(), tm);
        } else if (key.equalsIgnoreCase("float")) {
            float[] weights = new float[Integer.parseInt(source
                    .getfloat_array().getcount().toString())];
            StringTokenizer st = new StringTokenizer(source.getfloat_array()
                    .getValue().toString());
            for (int i = 0; i < weights.length; i++) {
                weights[i] = Float.parseFloat(st.nextToken());
            }

            resourceLibrary.put(source.getid().toString(), weights);
        }
    }

    /**
     * processBindShapeMatrix sets the initial transform of the skinned mesh.
     * The 4x4 matrix is converted to a 3x3 matrix and a vector, then passed to
     * the skinned mesh for use.
     * 
     * @param matrix
     *            the matrix to parse.
     * @param mesh
     *            the mesh to apply the bind to.
     */
    private void processBindShapeMatrix(SkinNode skin, float4x4 matrix) {
        Matrix4f mat = new Matrix4f();
        StringTokenizer st = new StringTokenizer(matrix.getValue().toString());

        mat.m00 = Float.parseFloat(st.nextToken());
        mat.m01 = Float.parseFloat(st.nextToken());
        mat.m02 = Float.parseFloat(st.nextToken());
        mat.m03 = Float.parseFloat(st.nextToken());
        mat.m10 = Float.parseFloat(st.nextToken());
        mat.m11 = Float.parseFloat(st.nextToken());
        mat.m12 = Float.parseFloat(st.nextToken());
        mat.m13 = Float.parseFloat(st.nextToken());
        mat.m20 = Float.parseFloat(st.nextToken());
        mat.m21 = Float.parseFloat(st.nextToken());
        mat.m22 = Float.parseFloat(st.nextToken());
        mat.m23 = Float.parseFloat(st.nextToken());
        mat.m30 = Float.parseFloat(st.nextToken());
        mat.m31 = Float.parseFloat(st.nextToken());
        mat.m32 = Float.parseFloat(st.nextToken());
        mat.m33 = Float.parseFloat(st.nextToken());

        skin.setBindMatrix(mat);
    }

    /**
     * processMesh will create either lines or a TriMesh. This means that the
     * only supported child elements are: triangles and lines or linestrips.
     * Polygons, trifans and tristrips are ignored.
     * 
     * @param mesh
     *            the mesh to parse.
     * @param geom
     *            the geometryType of the Geometry to build.
     * @return the created Geometry built from the mesh data.
     */
    private Geometry processMesh(meshType mesh, geometryType geom)
            throws Exception {
        // we need to build all the source data objects.
        for (int i = 0; i < mesh.getsourceCount(); i++) {
            sourceType source = mesh.getsourceAt(i);
            if (source.hasfloat_array()) {
                float[] floats = new float[Integer.parseInt(source
                        .getfloat_array().getcount().toString())];
                StringTokenizer st = new StringTokenizer(source
                        .getfloat_array().getValue().toString());
                // build an array of data to use for the final vector list.
                for (int j = 0; j < Integer.parseInt(source.getfloat_array()
                        .getcount().toString()); j++) {
                    floats[j] = Float.parseFloat(st.nextToken());
                }
                // technique_common should have the accessor type
                if (source.hastechnique_common()) {
                    accessorType accessor = source.gettechnique_common()
                            .getaccessor();
                    // XXX a bit of a hack, but will check parameters values. If
                    // XXX the parameter contains an X, Y, Z, then we know we
                    // are
                    // XXX dealing with Vector3f, if it has S, T, R, then we
                    // will also
                    // XXX use Vector3f but ignore the R when using as tex
                    // coord.
                    if (accessor.getparam().getname().toString().equals("X")) {
                        // create an array of Vector3fs
                        Vector3f[] vecs = new Vector3f[Integer
                                .parseInt(accessor.getcount().toString())];
                        int stride = Integer.parseInt(accessor.getstride()
                                .toString());
                        for (int k = 0; k < vecs.length; k++) {
                            vecs[k] = new Vector3f(floats[(k * stride)],
                                    floats[(k * stride) + 1],
                                    floats[(k * stride) + 2]);
                            resourceLibrary
                                    .put(source.getid().toString(), vecs);
                        }
                    } else if (accessor.getparam().getname().toString().equals(
                            "S")) {
                        // create an array of Vector3fs
                        Vector3f[] vecs = new Vector3f[Integer
                                .parseInt(accessor.getcount().toString())];
                        int stride = Integer.parseInt(accessor.getstride()
                                .toString());
                        for (int k = 0; k < vecs.length; k++) {
                            vecs[k] = new Vector3f(floats[(k * stride)],
                                    floats[(k * stride) + 1],
                                    floats[(k * stride) + 2]);
                            resourceLibrary
                                    .put(source.getid().toString(), vecs);
                        }
                    }
                }
            }
        }

        // next we have to define what source defines the vertices positional
        // information
        if (mesh.hasvertices()) {
            if (mesh.getvertices().hasinput()) {
                resourceLibrary.put(mesh.getvertices().getid().toString(), mesh
                        .getvertices().getinput().getsource().toString());
            }
        }

        // determine what type of geometry this is, and use the
        // lists to build the object.
        if (mesh.hastriangles()) {
            return processTriMesh(mesh, geom);
        } else if (mesh.haslines()) {
            return processLines(mesh, geom);
        } else {
            return null;
        }
    }

    /**
     * processTriMesh will process the triangles tag from the mesh section of
     * the COLLADA file. A jME TriMesh is returned that defines the vertices,
     * indices, normals, texture coordinates and colors.
     * 
     * @param mesh
     *            the meshType to process for the trimesh.
     * @param geom
     *            the geometryType of the TriMesh to build.
     * @return the jME tri mesh representing the COLLADA mesh.
     */
    private TriMesh processTriMesh(meshType mesh, geometryType geom)
            throws Exception {
        HashMap<Integer, ArrayList<BatchVertPair>> vertMap = new HashMap<Integer, ArrayList<BatchVertPair>>();
        resourceLibrary.put(geom.getname().toString() + "VertMap", vertMap);
        TriMesh out = new TriMesh(geom.getname().toString());
        TriangleBatch[] batch = new TriangleBatch[mesh.gettrianglesCount()];
        for (int batchIndex = 0; batchIndex < batch.length; batchIndex++) {
            trianglesType tri = mesh.gettrianglesAt(batchIndex);

            if (batchIndex > 0) {
                batch[batchIndex] = new TriangleBatch();
                out.addBatch(batch[batchIndex]);
            }

            out.getBatch(batchIndex).setName(
                    tri.getmaterial().toString() + " Batch");

            // first set the appropriate materials to this mesh.
            String matKey = (String) resourceLibrary.get(tri.getmaterial()
                    .toString());
            ColladaMaterial cm = (ColladaMaterial) resourceLibrary.get(matKey);
            if (cm != null) {
                for (int i = 0; i < RenderState.RS_MAX_STATE; i++) {
                    if (cm.getState(i) != null) {
                        if (cm.getState(i).getType() == RenderState.RS_ALPHA) {
                            out.getBatch(batchIndex).setRenderQueueMode(
                                    Renderer.QUEUE_TRANSPARENT);
                        }
                        if (batch.length > 1) {
                            out.getBatch(batchIndex).setRenderState(
                                    cm.getState(i));
                        } else {
                            out.setRenderState(cm.getState(i));
                        }
                    }
                }
            }

            // build the index buffer, this is going to be easy as it's only
            // 0...N where N is the number of vertices in the model.
            IntBuffer indexBuffer = BufferUtils.createIntBuffer(Integer
                    .parseInt(tri.getcount().toString()) * 3);
            for (int i = 0; i < indexBuffer.capacity(); i++) {
                indexBuffer.put(i);
            }
            out.setIndexBuffer(batchIndex, indexBuffer);

            // find the maximum offset to understand the stride
            int maxOffset = -1;
            for (int i = 0; i < tri.getinputCount(); i++) {
                int temp = Integer.parseInt(tri.getinputAt(i).getoffset()
                        .toString());
                if (maxOffset < temp) {
                    maxOffset = temp;
                }
            }

            // next build the other buffers, based on the input semantic
            for (int i = 0; i < tri.getinputCount(); i++) {
                if ("VERTEX".equals(tri.getinputAt(i).getsemantic().toString())) {
                    // build the vertex buffer
                    String key = tri.getinputAt(i).getsource().getValue()
                            .toString();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }

                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }

                    if (data == null) {
                        ErrorManager.getInstance().addError(Level.WARNING,
                                "Invalid source: " + key);
                        continue;
                    }

                    Vector3f[] v = (Vector3f[]) data;

                    StringTokenizer st = new StringTokenizer(tri.getp()
                            .getValue());
                    int vertCount = Integer.parseInt(tri.getcount().toString()) * 3;
                    FloatBuffer vertBuffer = BufferUtils
                            .createVector3Buffer(vertCount);
                    out.getBatch(batchIndex).setVertexCount(vertCount);
                    for (int j = 0; j < vertCount; j++) {

                        String token = st.nextToken();
                        // need to store the index in p to what j is for later
                        // processing the index to the vert for bones
                        int vertKey = Integer.parseInt(token);
                        ArrayList<BatchVertPair> storage = vertMap.get(Integer
                                .valueOf(vertKey));
                        if (storage == null) {
                            storage = new ArrayList<BatchVertPair>();
                            storage.add(new BatchVertPair(batchIndex, j));
                            vertMap.put(Integer.valueOf(vertKey), storage);
                        } else {
                            storage.add(new BatchVertPair(batchIndex, j));
                        }

                        BufferUtils.setInBuffer(v[vertKey], vertBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            st.nextToken();
                        }
                    }
                    out.setVertexBuffer(batchIndex, vertBuffer);
                } else if ("NORMAL".equals(tri.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the normal buffer
                    String key = tri.getinputAt(i).getsource().getValue()
                            .toString();
                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }

                    Object data = resourceLibrary.get(key);

                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }

                    if (data == null) {
                        ErrorManager.getInstance().addError(Level.WARNING,
                                "Invalid source: " + key);
                        continue;
                    }

                    Vector3f[] v = (Vector3f[]) data;

                    StringTokenizer st = new StringTokenizer(tri.getp()
                            .getValue());
                    int normCount = Integer.parseInt(tri.getcount().toString()) * 3;
                    FloatBuffer normBuffer = BufferUtils
                            .createVector3Buffer(normCount);

                    int offset = Integer.parseInt(tri.getinputAt(i).getoffset()
                            .toString());
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }
                    String token;
                    for (int j = 0; j < normCount; j++) {
                        token = st.nextToken();
                        int index = Integer.parseInt(token);
                        if (index < v.length)
                            BufferUtils.setInBuffer(v[index], normBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }

                    out.setNormalBuffer(batchIndex, normBuffer);
                } else if ("TEXCOORD".equals(tri.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the texture buffer
                    String key = tri.getinputAt(i).getsource().getValue()
                            .toString();

                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }

                    if (data == null) {
                        ErrorManager.getInstance().addError(Level.WARNING,
                                "Invalid source: " + key);
                        continue;
                    }

                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(tri.getp()
                            .getValue());
                    int texCount = Integer.parseInt(tri.getcount().toString()) * 3;
                    FloatBuffer texBuffer = BufferUtils
                            .createVector2Buffer(texCount);
                    int offset = Integer.parseInt(tri.getinputAt(i).getoffset()
                            .toString());
                    int set = Integer.parseInt(tri.getinputAt(i).getset()
                            .toString());
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }

                    // Keep a max to set the wrap mode (if it's 1, clamp, if
                    // it's > 1 wrap it)
                    float maxX = -1;
                    float maxY = -1;

                    Vector2f tempTexCoord = new Vector2f();
                    for (int j = 0; j < texCount; j++) {

                        int index = Integer.parseInt(st.nextToken());
                        Vector3f value = v[index];
                        if (value.x > maxX) {
                            maxX = value.x;
                        }

                        if (value.y > maxY) {
                            maxY = value.y;
                        }

                        tempTexCoord.set(value.x, value.y);
                        BufferUtils.setInBuffer(tempTexCoord, texBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }

                    int unit;
                    if (set == 0) {
                        unit = 0;
                    } else {
                        unit = set - 1;
                    }
                    out.setTextureBuffer(batchIndex, texBuffer, unit);

                    // Set the wrap mode, check if the batch has a texture
                    // first, if not
                    // check the geometry.
                    // Then, based on the texture coordinates, we may need to
                    // change it from the
                    // default.
                    TextureState ts = (TextureState) out.getBatch(batchIndex)
                            .getRenderState(RenderState.RS_TEXTURE);
                    if (ts == null) {
                        ts = (TextureState) out
                                .getRenderState(RenderState.RS_TEXTURE);
                    }

                    if (ts != null) {
                        Texture t = ts.getTexture(unit);
                        if (t != null) {
                            if (maxX > 1) {
                                if (maxY > 1) {
                                    t.setWrap(Texture.WM_WRAP_S_WRAP_T);
                                } else {
                                    t.setWrap(Texture.WM_WRAP_S_CLAMP_T);
                                }
                            } else if (maxY > 1) {
                                t.setWrap(Texture.WM_CLAMP_S_WRAP_T);
                            }
                        }
                    }
                } else if ("COLOR".equals(tri.getinputAt(i).getsemantic()
                        .toString())) {
                    // build the texture buffer
                    String key = tri.getinputAt(i).getsource().getValue()
                            .toString();

                    if (key.startsWith("#")) {
                        key = key.substring(1);
                    }
                    Object data = resourceLibrary.get(key);
                    while (data instanceof String) {
                        key = (String) data;
                        if (key.startsWith("#")) {
                            key = key.substring(1);
                        }
                        data = resourceLibrary.get(key);
                    }
                    Vector3f[] v = (Vector3f[]) data;
                    StringTokenizer st = new StringTokenizer(tri.getp()
                            .getValue());
                    int colorCount = Integer
                            .parseInt(tri.getcount().toString()) * 3;
                    FloatBuffer colorBuffer = BufferUtils
                            .createColorBuffer(colorCount);
                    int offset = Integer.parseInt(tri.getinputAt(i).getoffset()
                            .toString());
                    for (int j = 0; j < offset; j++) {
                        st.nextToken();
                    }

                    ColorRGBA tempColor = new ColorRGBA();
                    for (int j = 0; j < colorCount; j++) {

                        int index = Integer.parseInt(st.nextToken());
                        Vector3f value = v[index];

                        tempColor.set(value.x, value.y, value.z, 1);
                        BufferUtils.setInBuffer(tempColor, colorBuffer, j);
                        for (int k = 0; k < maxOffset; k++) {
                            if (st.hasMoreTokens()) {
                                st.nextToken();
                            }
                        }
                    }

                    out.setColorBuffer(batchIndex, colorBuffer);
                }
            }
        }
        out.setModelBound(new BoundingBox());
        out.updateModelBound();

        return out;
    }

    /**
     * processLines will process the triangles tag from the mesh section of the
     * COLLADA file. A jME Line is returned that defines the vertices, normals,
     * texture coordinates and colors.
     * 
     * @param mesh
     *            the meshType to process for the lines.
     * @param geom
     *            the geomType for the lines
     * @return the jME tri mesh representing the COLLADA mesh.
     */
    private Line processLines(meshType mesh, geometryType geom) {
        if (!squelch) {
            ErrorManager.getInstance().addError(Level.WARNING,
                    "Line are not supported.");
        }
        return null;
    }

    /**
     * The library of visual scenes defines how the loaded geometry is stored in
     * the scene graph, including scaling, translation, rotation, etc.
     * 
     * @param libScene
     *            the library of scenes
     * @param node
     *            the node to attach the scenes to.
     * @throws Exception
     *             thrown if there is a problem processing the xml.
     */
    private void processVisualSceneLibrary(library_visual_scenesType libScene)
            throws Exception {

        for (int i = 0; i < libScene.getvisual_sceneCount(); i++) {
            Node scene = new Node(libScene.getvisual_sceneAt(i).getid()
                    .toString());
            processVisualScene(libScene.getvisual_sceneAt(i), scene);
            resourceLibrary.put(libScene.getvisual_sceneAt(i).getid()
                    .toString(), scene);
        }

    }

    /**
     * the visual scene will contain any number of nodes that define references
     * to geometry. These are then placed into the scene as needed.
     * 
     * @param scene
     *            the scene to process.
     * @param node
     *            the jME node to attach this scene to.
     * @throws Exception
     *             thrown if there is a problem with the processing.
     */
    private void processVisualScene(visual_sceneType scene, Node node)
            throws Exception {
        for (int i = 0; i < scene.getnodeCount(); i++) {
            processNode(scene.getnodeAt(i), node);
        }

        for (int i = 0; i < node.getQuantity(); i++) {
            Spatial s = node.getChild(i);
            if (s instanceof Bone) {
                s.updateGeometricState(0, true);
                s.removeFromParent();
                node.attachChild(s);
            }
        }

    }

    /**
     * a node tag
     * 
     * @param xmlNode
     * @param node
     * @throws Exception
     */
    private void processNode(nodeType2 xmlNode, Node node) throws Exception {
        Node child = null;
        if (xmlNode.hastype() && "JOINT".equals(xmlNode.gettype().toString())) {
            child = (Bone) resourceLibrary.get(xmlNode.getsid().toString());
            if (child == null) {
                child = new Bone(xmlNode.getsid().toString());
                if (!squelch) {
                    ErrorManager.getInstance().addError(
                            Level.WARNING,
                            "Bone " + xmlNode.getsid().toString()
                                    + " is not attached to any vertices.");
                }
            }
            if (!(node instanceof Bone)) {
                if (skeletonNames == null) {
                    skeletonNames = new ArrayList<String>();
                }
                skeletonNames.add(xmlNode.getsid().toString());
            }
            node.attachChild(child);
        } else {
            child = new Node(xmlNode.getid().toString());
        }

        // this node has a skeleton and skin
        if (xmlNode.hasinstance_controller()) {
            for (int i = 0; i < xmlNode.getinstance_controllerCount(); i++) {
                processInstanceController(xmlNode.getinstance_controllerAt(i),
                        node);
            }
        }

        if (xmlNode.hasinstance_light()) {
            for (int i = 0; i < xmlNode.getinstance_lightCount(); i++) {
                processInstanceLight(xmlNode.getinstance_lightAt(i), child);
            }
        }

        // parse translation
        if (xmlNode.hastranslate()) {
            Vector3f translate = new Vector3f();
            StringTokenizer st = new StringTokenizer(xmlNode.gettranslate()
                    .getValue().toString());
            translate.x = Float.parseFloat(st.nextToken());
            translate.y = Float.parseFloat(st.nextToken());
            translate.z = Float.parseFloat(st.nextToken());
            child.setLocalTranslation(translate);
        }

        if (xmlNode.hasrotate()) {
            Quaternion rotation = null;
            for (int i = 0; i < xmlNode.getrotateCount(); i++) {
                Quaternion temp = new Quaternion();
                Vector3f axis = new Vector3f();
                StringTokenizer st = new StringTokenizer(xmlNode.getrotateAt(i)
                        .getValue().toString());
                axis.x = Float.parseFloat(st.nextToken());
                axis.y = Float.parseFloat(st.nextToken());
                axis.z = Float.parseFloat(st.nextToken());
                axis.normalizeLocal();

                float angle = Float.parseFloat(st.nextToken());
                angle *= FastMath.DEG_TO_RAD;

                temp.fromAngleNormalAxis(angle, axis);

                if (rotation == null) {
                    rotation = new Quaternion();
                    rotation.set(temp);
                } else {
                    rotation.multLocal(temp);
                }

            }
            child.setLocalRotation(rotation);
        }

        if (xmlNode.hasmatrix()) {
            Matrix4f tm = new Matrix4f();
            StringTokenizer st = new StringTokenizer(xmlNode.getmatrix()
                    .getValue().toString());
            tm.m00 = Float.parseFloat(st.nextToken());
            tm.m01 = Float.parseFloat(st.nextToken());
            tm.m02 = Float.parseFloat(st.nextToken());
            tm.m03 = Float.parseFloat(st.nextToken());
            tm.m10 = Float.parseFloat(st.nextToken());
            tm.m11 = Float.parseFloat(st.nextToken());
            tm.m12 = Float.parseFloat(st.nextToken());
            tm.m13 = Float.parseFloat(st.nextToken());
            tm.m20 = Float.parseFloat(st.nextToken());
            tm.m21 = Float.parseFloat(st.nextToken());
            tm.m22 = Float.parseFloat(st.nextToken());
            tm.m23 = Float.parseFloat(st.nextToken());
            tm.m30 = Float.parseFloat(st.nextToken());
            tm.m31 = Float.parseFloat(st.nextToken());
            tm.m32 = Float.parseFloat(st.nextToken());
            tm.m33 = Float.parseFloat(st.nextToken());

            child.setLocalTranslation(tm.toTranslationVector());
            Quaternion q = tm.toRotationQuat();
            q.normalize();
            child.setLocalRotation(q);
        }

        if (xmlNode.hasscale()) {
            Vector3f scale = new Vector3f();
            StringTokenizer st = new StringTokenizer(xmlNode.getscale()
                    .getValue().toString());
            scale.x = Float.parseFloat(st.nextToken());
            scale.y = Float.parseFloat(st.nextToken());
            scale.z = Float.parseFloat(st.nextToken());
            child.setLocalScale(scale);

        }
        // get the Geometry to attach to this node.
        if (xmlNode.hasinstance_geometry()) {
            for (int i = 0; i < xmlNode.getinstance_geometryCount(); i++) {
                String key = xmlNode.getinstance_geometryAt(i).geturl()
                        .toString();
                // references contain a #, but our key does not.
                if (key.startsWith("#")) {
                    key = key.substring(1);
                }

                Geometry g = (Geometry) resourceLibrary.get(key);
                if (g != null) {
                    if (g instanceof TriMesh) {
                        SharedMesh instance = new SharedMesh(xmlNode.getname()
                                .toString(), (TriMesh) g);
                        child.attachChild(instance);
                    } else {
                        child.attachChild(g);
                    }
                }
            }
        }

        // parse subnodes
        if (xmlNode.hasnode()) {
            for (int i = 0; i < xmlNode.getnodeCount(); i++) {
                processNode(xmlNode.getnodeAt(i), child);
            }
        }

        node.attachChild(child);
    }

    private void processInstanceLight(InstanceWithExtra light, Node node)
            throws Exception {

        String key = light.geturl().toString();

        if (key.startsWith("#")) {
            key = key.substring(1);
        }
        LightNode ln = (LightNode) resourceLibrary.get(key);
        if (ln != null) {
            node.attachChild(ln);
        }
    }

    private void processInstanceController(instance_controllerType controller,
            Node node) throws Exception {

        SkinNode sNode = (SkinNode) resourceLibrary.get(controller.geturl()
                .toString().substring(1));

        if (sNode != null) {
            node.attachChild(sNode);
        } else {
            if (!squelch) {
                ErrorManager.getInstance().addError(
                        Level.WARNING,
                        "Instance "
                                + controller.geturl().toString().substring(1)
                                + " does not exist.");
            }
        }

        if (controller.hasskeleton()) {
            for (int i = 0; i < controller.getskeletonCount(); i++) {
                String url = controller.getskeletonAt(i).getValue();
                if (url.startsWith("#")) {
                    url = url.substring(1);
                }
                Bone b = (Bone) resourceLibrary.get(url);
                if (b != null) {
                    sNode.addSkeleton(b);
                }
            }
        }

    }

    /**
     * getColor uses a string tokenizer to parse the value of a colorType into a
     * ColorRGBA type used internally by jME.
     * 
     * @param color
     *            the colorType to parse (RGBA format).
     * @return the ColorRGBA object to be used by jME.
     */
    private ColorRGBA getColor(colorType color) {
        ColorRGBA out = new ColorRGBA();
        StringTokenizer st = new StringTokenizer(color.getValue().toString());
        out.r = Float.parseFloat(st.nextToken());
        out.g = Float.parseFloat(st.nextToken());
        out.b = Float.parseFloat(st.nextToken());
        out.a = Float.parseFloat(st.nextToken());
        return out;
    }

    /**
     * BatchVertPair simply contain a batch and an index. This defines where a
     * specific vertex may be found.
     */
    private class BatchVertPair {
        public int batch;
        public int index;

        public BatchVertPair(int batch, int index) {
            this.batch = batch;
            this.index = index;
        }
    }

    public static void squelchErrors(boolean b) {

        squelch = b;
    }
}
