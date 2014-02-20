/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
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
