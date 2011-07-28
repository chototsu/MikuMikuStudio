/*
 * Copyright (c) 2011 Kazuhiko Kobayashi All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * * Neither the name of 'MMDLoaderJME' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package projectkyoto.jme3.mmd;

import com.jme3.animation.Bone;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;

/**
 *
 * @author kobayasi
 */
public class BoneUtil {

    public static Matrix4f getBoneToModelMatrix(Bone bone, Matrix4f m, Matrix3f tmp1) {
        m.setTransform(bone.getModelSpacePosition(), bone.getModelSpaceScale(), bone.getModelSpaceRotation().toRotationMatrix(tmp1));
        return m;
    }

    public static Matrix4f getModelToBoneMatrix(Bone bone, Matrix4f m, Matrix3f tmp1) {
        getBoneToModelMatrix(bone, m, tmp1).invertLocal();
        return m;
    }

//    public static void setBoneModelPos2(Bone bone, Vector3f pos, Vector3f tmpV1, Matrix4f tmp1, Matrix3f tmp2) {
//        getModelToBoneMatrix(bone, tmp1, tmp2);
//        tmp1.mult(pos, tmpV1);
//        bone.getLocalRotation().multLocal(tmpV1);
//        tmpV1.addLocal(bone.getLocalPosition()).subtractLocal(bone.getInitialPos());
//        bone.setUserTransforms(tmpV1, bone.getLocalRotation(), Vector3f.ZERO);
//    }
//    public static void setBoneModelPos3(Bone bone, Vector3f pos, Vector3f tmpV1, Matrix4f tmp1, Matrix3f tmp2) {
//        getModelToBoneMatrix(bone, tmp1, tmp2);
//        tmp1.mult(pos, tmpV1);
//        bone.getLocalRotation().multLocal(tmpV1);
//        tmpV1.addLocal(bone.getLocalPosition());
//        bone.getLocalPosition().set(tmpV1);
//    }
    public static void setBoneModelPos(Bone bone, Vector3f pos, Vector3f tmpV1, Matrix4f tmp1, Matrix3f tmp2) {
        Bone parentBone = bone.getParent();
        if (parentBone != null) {
            getModelToBoneMatrix(bone.getParent(), tmp1, tmp2);
            tmp1.mult(pos, tmpV1);
            bone.getLocalPosition().set(tmpV1);
        } else {
            bone.getLocalPosition().set(pos);
        }
    }
}
