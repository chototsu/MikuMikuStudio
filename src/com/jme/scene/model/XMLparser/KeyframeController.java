package com.jme.scene.model.XMLparser;

import com.jme.scene.TriMesh;
import com.jme.scene.Controller;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.util.LoggingSystem;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.lang.reflect.Array;

/**
 * Started Date: Jun 12, 2004<br><br>
 *
 * Class can do linear interpolation of a TriMesh between units of time.  Similar to
 * <code>VertexKeyframeController</code> but interpolates float units of time
 * instead of integer keyframes.<br><br>
 * Controller.setSpeed(float) sets a speed relative to the defined speed.  For example, the default is 1.  A speed
 * of 2 would run twice as fast and a speed of .5 would run half as fast<br><br>
 * Controller.setMinTime(float) and Controller.setMaxTime(float) both define the bounds that KeyframeController should
 * follow.  It is the programmer's responsiblity to make sure that the MinTime and MaxTime are within the span of the
 * defined <code>setKeyframe</code><br><br>
 * Controller functions RepeatType and isActive are both defined in their default way for KeyframeController<br><br>
 * When this controller is saved/loaded to XML format, it assumes that the mesh it morphs is the TriMesh it belongs to,
 * so it is recomended to only attach this controller to the TriMesh it animates.
 *
 * @author Jack Lindamood
 */
public class KeyframeController extends Controller{

    /**
     * An array of <code>PointInTime</code>s
     */
    ArrayList keyframes;
    ArrayList prevKeyframes;

    /**
     * The mesh that is actually morphed
     */
    TriMesh morphMesh;
    float curTime;
    float tempTime;
    int curFrame;
    int direction;

    Vector3f tempV3f=new Vector3f();
    Vector2f tempV2f=new Vector2f();
    ColorRGBA tempColor=new ColorRGBA();

    /**
     * The PointInTime before <code>curTime</code>
     */
    PointInTime before;

    /**
     * The PointInTime after <code>curTime</code>
     */
    PointInTime after;

    /**
     * If true, the animation is moving forward, if false the animation is moving backwards
     */
    boolean movingForward;

    private boolean isSmooth;
    private float tempMinTime;
    private float tempMaxTime;

    /**
     * Default constructor.  Speed is 1, MinTime is 0 MaxTime is 0.  Both MinTime and MaxTime are automatically
     * adjusted by setKeyframe if the setKeyframe time is less than MinTime or greater than MaxTime.  Default RepeatType
     * is RT_WRAP.
     */
    public KeyframeController(){
        this.setSpeed(1);
        keyframes=new ArrayList();
        curFrame=0;
        this.setRepeatType(Controller.RT_WRAP);
        movingForward=true;
        this.setMinTime(0);
        this.setMaxTime(0);
        isSmooth=false;
    }



    /**
     * Sets the Mesh that will be physically changed by this KeyframeController
     * @param morph
     */
    public void setMorphingMesh(TriMesh morph){
        morphMesh=morph;
        keyframes.clear();
        keyframes.add(new PointInTime(0,null));
    }

    /**
     * Tells the controller to change its morphMesh to <code>shape</code> at <code>time</code> seconds.  Time
     * must be >=0 and shape must be non-null and shape must have the same number of vertexes as the current shape.  If
     * not, then nothing happens.  It is also required that <code>setMorphingMesh(TriMesh)</code> is called before
     * <code>setKeyframe</code>.  It is assumed that shape.indices == morphMesh.indices, otherwise morphing may look
     * funny
     * @param time The time for the change
     * @param shape The new shape at that time
     */
    public void setKeyframe(float time,TriMesh shape){
        if (morphMesh==null || time<0 || shape.getVertices().length!=morphMesh.getVertices().length) return;
        for (int i=0;i<keyframes.size();i++){
            PointInTime lookingTime=(PointInTime)keyframes.get(i);
            if (lookingTime.time==time){
                lookingTime.newShape=shape;
                return;
            }
            if (lookingTime.time > time){
                keyframes.add(i,new PointInTime(time,shape));
                return;
            }
        }
        keyframes.add(new PointInTime(time,shape));
        if (time > this.getMaxTime()) this.setMaxTime(time);
        if (time < this.getMinTime()) this.setMinTime(time);
    }

    /**
     * Does a smooth transform from the current state of the keyframe animation to the new minimum time.
     * The duration of the transform is transformLength (which is affected by this.getSpeed()).  The new maximum time
     * sets the new animation boundaries after the new minimum time is reached.  This function does nothing if it is
     * already running or if the controller isn't active.
     * @param newMinTime The time to transform towards
     * @param newMaxTime The new time to reach
     * @param transformLength The length of the duration (in seconds) between the current state and the new minimum time
     */
    public void smoothTransform(float newMinTime,float newMaxTime,float transformLength){
        if (!isActive()) return;
        if (isSmooth) return;
        tempMinTime=newMinTime;
        tempMaxTime=newMaxTime;
        EmptyTriMesh before=new EmptyTriMesh();
        getCurrent(before);
        curFrame=0;
        curTime=newMinTime;
        update(0);
        EmptyTriMesh after=new EmptyTriMesh();
        getCurrent(after);

        prevKeyframes=keyframes;
        keyframes=new ArrayList();
        this.setMinTime(0);
        this.setMaxTime(0);
        movingForward=true;
        setKeyframe(0,before);
        setKeyframe(transformLength,after);
        curTime=0;
        curFrame=0;
        isSmooth=true;
    }


    private void getCurrent(TriMesh dataCopy) {
        if (morphMesh.getColors()!=null){
            ColorRGBA[] newColors=new ColorRGBA[morphMesh.getColors().length];
            for (int i=0;i<newColors.length;i++)
                newColors[i]=new ColorRGBA(morphMesh.getColors()[i]);
            dataCopy.setColors(newColors);
        }
        if (morphMesh.getVertices()!=null){
            Vector3f[] newVerts=new Vector3f[morphMesh.getVertices().length];
            for (int i=0;i<newVerts.length;i++)
                newVerts[i]=new Vector3f(morphMesh.getVertices()[i]);
            dataCopy.setVertices(newVerts);
        }
        if (morphMesh.getNormals()!=null){
            Vector3f[] newNorms=new Vector3f[morphMesh.getNormals().length];
            for (int i=0;i<newNorms.length;i++)
                newNorms[i]=new Vector3f(morphMesh.getNormals()[i]);
            dataCopy.setNormals(newNorms);
        }
        if (morphMesh.getIndices()!=null){
            int[] newInds=new int[morphMesh.getIndices().length];
            System.arraycopy(morphMesh.getIndices(),0,newInds,0,newInds.length);
            dataCopy.setIndices(newInds);
        }
        if (morphMesh.getTextures()!=null){
            Vector2f[] newTex=new Vector2f[morphMesh.getTextures().length];
            for (int i=0;i<newTex.length;i++)
                newTex[i]=new Vector2f(morphMesh.getTextures()[i]);
            dataCopy.setTextures(newTex);
        }
    }

    /**
     * As defined in Controller
     * @param time as defined in Controller
     */
    public void update(float time) {
        if (!this.isActive()) return;
        if (movingForward) curTime+=time*this.getSpeed(); else curTime-=time*this.getSpeed();
        findFrame();
        before=((PointInTime)keyframes.get(curFrame));
        after=((PointInTime)keyframes.get(curFrame+1));
        TriMesh oldShape=before.newShape;
        TriMesh newShape=after.newShape;
        float delta=(curTime-before.time)/
                (after.time-before.time);
        Vector3f[] verts=morphMesh.getVertices();
        Vector3f[] norms=morphMesh.getNormals();
        Vector2f[] texts=morphMesh.getTextures();
        ColorRGBA[] colors=morphMesh.getColors();

        Vector3f[] oldverts=oldShape.getVertices();
        Vector3f[] oldnorms=oldShape.getNormals();
        Vector2f[] oldtexts=oldShape.getTextures();
        ColorRGBA[] oldcolors=oldShape.getColors();

        Vector3f[] newverts=newShape.getVertices();
        Vector3f[] newnorms=newShape.getNormals();
        Vector2f[] newtexts=newShape.getTextures();
        ColorRGBA[] newcolors=newShape.getColors();
        if (verts==null || oldverts==null || newverts==null) return;
//        boolean hitnorms=false,hittexts=false,hitcolors=false;
        for (int i=0;i<verts.length;i++){
            verts[i].interpolate(oldverts[i],newverts[i],delta);
            morphMesh.setVertex(i,verts[i]);
            if (norms!=null && oldnorms!=null && newnorms!=null){
                norms[i].interpolate(oldnorms[i],newnorms[i],delta);
//                hitnorms=true;
                morphMesh.setNormal(i,norms[i]);
            }
            if (texts!=null && oldtexts!=null && newtexts!=null){
                texts[i].interpolate(oldtexts[i],newtexts[i],delta);
//                hittexts=true;
                morphMesh.setTexture(i,texts[i]);
            }
            if (colors!=null && oldcolors!=null && newcolors!=null){
                colors[i].interpolate(oldcolors[i],newcolors[i],delta);
//                hitcolors=true;
                morphMesh.setColor(i,colors[i]);
            }
        }
//        morphMesh.updateVertexBuffer();
//        if (hitnorms) morphMesh.updateNormalBuffer();
//        if (hittexts) morphMesh.updateTextureBuffer();
//        if (hitcolors) morphMesh.updateColorBuffer();
//          Both methods seem equivalent in speed
    }

    /**
     * This is used by update(float).  It calculates PointInTime <code>before</code>
     * and <code>after</code> as well as makes adjustments on what to do when <code>curTime</code> is beyond the
     * MinTime and MaxTime bounds
     */
    private void findFrame() {
        if (curTime>this.getMaxTime()){
            if (isSmooth){
                keyframes=prevKeyframes;
                prevKeyframes=null;
                curTime = tempMinTime;
                this.setMinTime(tempMinTime);
                this.setMaxTime(tempMaxTime);
                movingForward=true;
                curFrame=0;
                isSmooth=false;
                findFrame();
                return;
            }
            if (this.getRepeatType()==Controller.RT_WRAP){
                curTime=this.getMinTime();
                curFrame=0;
            } else if (this.getRepeatType()==Controller.RT_CLAMP){
                this.setActive(false);
                return;
            } else {    // Then assume it's RT_CYCLE
                movingForward=false;
                curTime=this.getMaxTime();
            }
        }else if (curTime<this.getMinTime()){
            if (this.getRepeatType()==Controller.RT_WRAP){
                curTime=this.getMaxTime();
                curFrame=0;
            } else if (this.getRepeatType()==Controller.RT_CLAMP){
                this.setActive(false);
                return;
            } else {    // Then assume it's RT_CYCLE
                movingForward=true;
                curTime=this.getMinTime();
            }
        }
        if (curTime>((PointInTime)keyframes.get(curFrame)).time){
            if (curTime<((PointInTime)keyframes.get(curFrame+1)).time)
                return;
            else{
                for (;curFrame<keyframes.size()-1;curFrame++){
                    if (curTime<=((PointInTime)keyframes.get(curFrame+1)).time)
                        return;
                }
                // This -should- be unreachable because of the above
                curTime=this.getMinTime();
                curFrame=0;
                return;
            }
        } else{
            for (;curFrame>=0;curFrame--){
                if (curTime>=((PointInTime)keyframes.get(curFrame)).time){
                    return;
                }
            }
            // This should be unreachable because curTime>=0 and keyframes[0].time=0;
            curFrame=0;
            return;
        }
    }

    /**
     * This class defines a point in time that states <code>morphShape</code> should look like <code>newShape</code> at
     * <code>time</code> seconds
     */
    public class PointInTime{
        TriMesh newShape;
        float time;

        public PointInTime(float time, TriMesh shape) {
            this.time=time;
            this.newShape=shape;
        }
    }
}