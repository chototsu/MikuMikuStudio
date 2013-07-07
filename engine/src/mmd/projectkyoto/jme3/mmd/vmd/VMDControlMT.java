/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectkyoto.jme3.mmd.vmd;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import projectkyoto.jme3.mmd.PMDNode;
import projectkyoto.jme3.mmd.nativebullet.PhysicsControl;
import projectkyoto.jme3.mmd.vmd.VMDControl;
import projectkyoto.mmd.file.PMDModel;
import projectkyoto.mmd.file.VMDFile;

/**
 *
 * @author kobayasi
 */
public class VMDControlMT extends AbstractControl {

    final ScheduledThreadPoolExecutor executor;
    final VMDCallable callable;
    final PMDNode pmdNode;
    Future<Void> future;

    public VMDControlMT(ScheduledThreadPoolExecutor executor, PMDNode pmdNode, VMDFile vmdFile) {
        this.executor = executor;
        callable = new VMDCallable(pmdNode, vmdFile);
        this.pmdNode = pmdNode;
    }
    public VMDControlMT(ScheduledThreadPoolExecutor executor, PMDNode pmdNode, VMDFile vmdFile, PhysicsControl physicsControl) {
        this(executor, pmdNode, vmdFile, physicsControl, true);
    }
    public VMDControlMT(ScheduledThreadPoolExecutor executor, PMDNode pmdNode, VMDFile vmdFile, PhysicsControl physicsControl, boolean addPmdNodeFlag) {
        this.executor = executor;
        callable = new VMDCallable(pmdNode, vmdFile, physicsControl, addPmdNodeFlag);
        this.pmdNode = pmdNode;
    }

    @Override
    protected void controlUpdate(float f) {
        sync();
        pmdNode.update();
        callable.setTpf(f);
        future = executor.submit(callable);
    }
    public synchronized void sync() {
        if (future != null) {
            try {
                future.get();
//                callable.vmdControl.getPhysicsControl().getWorld().updateRigidBodyPos();
            } catch (InterruptedException ex) {
                Logger.getLogger(VMDControlMT.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(VMDControlMT.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                future = null;
            }
        }
    }
    public VMDCallable getCallable() {
        return callable;
    }
    public ScheduledThreadPoolExecutor getPool() {
        return executor;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial sptl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial == null) {
            Logger.getLogger(VMDControlMT.class.getName()).log(Level.INFO,"setSpatial remove");
            getCallable().getVmdControl().setSpatial(null);
            if (future != null) {
                try {
                    future.get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(VMDControlMT.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(VMDControlMT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
//            callable.vmdControl.getPhysicsControl().getWorld().removePMDNode(pmdNode);
        } else {
            Logger.getLogger(VMDControlMT.class.getName()).log(Level.INFO,"setSpatial add");
        }
    }
}
