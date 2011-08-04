/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectkyoto.jme3.mmd.vmd;

import java.util.concurrent.Callable;
import projectkyoto.jme3.mmd.PMDNode;
import projectkyoto.jme3.mmd.vmd.VMDControl;
import projectkyoto.mmd.file.VMDFile;

/**
 *
 * @author kobayasi
 */
public class VMDCallable implements Callable<Void> {
    final PMDNode pmdNode;
    final VMDControl vmdControl;
    float tpf;
    public VMDCallable(PMDNode pmdNode, VMDFile vmdFile) {
        this.pmdNode = pmdNode;
        vmdControl = new VMDControl(pmdNode, vmdFile);
    }
    
    public Void call() throws Exception {
        vmdControl.update(tpf);
//        pmdNode.getSkeleton().updateWorldVectors();
        pmdNode.calcOffsetMatrices();
//        pmdNode.setSkinWeight("笑い", 1f);
        pmdNode.updateSkinBackData();
        return null;
    }

    public float getTpf() {
        return tpf;
    }

    public void setTpf(float tpf) {
        this.tpf = tpf;
    }

    public PMDNode getPmdNode() {
        return pmdNode;
    }

    public VMDControl getVmdControl() {
        return vmdControl;
    }

    
}
