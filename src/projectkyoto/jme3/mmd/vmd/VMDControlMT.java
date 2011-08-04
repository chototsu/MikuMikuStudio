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

    @Override
    protected void controlUpdate(float f) {
        if (future != null) {
            try {
                future.get();
                callable.vmdControl.getPhysicsControl().getWorld().updateRigidBodyPos();
                pmdNode.update();
            } catch (InterruptedException ex) {
                Logger.getLogger(VMDControlMT.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(VMDControlMT.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                future = null;
            }
        }
        callable.setTpf(f);
        future = executor.submit(callable);
    }

    public VMDCallable getCallable() {
        return callable;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial sptl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
