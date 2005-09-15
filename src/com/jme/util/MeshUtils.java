/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * This class is a set of static mesh creation utilities.
 */
final public class MeshUtils {

    private MeshUtils(){}

    /**
     * Creates a dodecahedron mesh.  If rpkMesh is null, a new mesh is used and returned.
     * @param rpkMesh The TriMesh to store the result in.
     * @param bWantNormals If true, normals are created.
     * @param bWantColors If true, colors are created.
     * @param bWantUVs If true, UV coords are created.
     * @param bOutsideView
     * @return rpkMesh after creation.
     */
    public static TriMesh createDodecahedronMesh(
        TriMesh rpkMesh,
        boolean bWantNormals,
        boolean bWantColors,
        boolean bWantUVs,
        boolean bOutsideView) {
        float fA = 1.0f / FastMath.sqrt(3.0f);
        float fB = FastMath.sqrt((3.0f - FastMath.sqrt(5.0f)) / 6.0f);
        float fC = FastMath.sqrt((3.0f + FastMath.sqrt(5.0f)) / 6.0f);
        int i;

        // allocate vertices
        int iVQuantity = 20;
        Vector3f[] akVertex = new Vector3f[20];
        akVertex[0] = new Vector3f(fA, fA, fA);
        akVertex[1] = new Vector3f(fA, fA, -fA);
        akVertex[2] = new Vector3f(fA, -fA, fA);
        akVertex[3] = new Vector3f(fA, -fA, -fA);
        akVertex[4] = new Vector3f(-fA, fA, fA);
        akVertex[5] = new Vector3f(-fA, fA, -fA);
        akVertex[6] = new Vector3f(-fA, -fA, fA);
        akVertex[7] = new Vector3f(-fA, -fA, -fA);
        akVertex[8] = new Vector3f(fB, fC, 0.0f);
        akVertex[9] = new Vector3f(-fB, fC, 0.0f);
        akVertex[10] = new Vector3f(fB, -fC, 0.0f);
        akVertex[11] = new Vector3f(-fB, -fC, 0.0f);
        akVertex[12] = new Vector3f(fC, 0.0f, fB);
        akVertex[13] = new Vector3f(fC, 0.0f, -fB);
        akVertex[14] = new Vector3f(-fC, 0.0f, fB);
        akVertex[15] = new Vector3f(-fC, 0.0f, -fB);
        akVertex[16] = new Vector3f(0.0f, fB, fC);
        akVertex[17] = new Vector3f(0.0f, -fB, fC);
        akVertex[18] = new Vector3f(0.0f, fB, -fC);
        akVertex[19] = new Vector3f(0.0f, -fB, -fC);
        FloatBuffer vertBuf = BufferUtils.createFloatBuffer(akVertex);

        // allocate normals if requested
        FloatBuffer normBuf = null;
        if (bWantNormals)
            normBuf = BufferUtils.createFloatBuffer(akVertex);

        // allocate texture coordinates if requested
        Vector2f[] akUV = null;
        if (bWantUVs) {
            akUV = new Vector2f[iVQuantity];
            for (i = 0; i < iVQuantity; i++) {
                if (FastMath.abs(akVertex[i].z) < 1.0f) {
                    akUV[i].x =
                        0.5f
                            * (1.0f
                                + FastMath.atan2(akVertex[i].y, akVertex[i].x)
                                    * FastMath.INV_PI);
                } else {
                    akUV[i].x = 0.5f;
                }
                akUV[i].y = FastMath.acos(akVertex[i].z) * FastMath.INV_PI;
            }
        }
        FloatBuffer texBuf = BufferUtils.createFloatBuffer(akUV);

        // allocate connectivity
        int iTQuantity = 36;
        int[] aiConnect = new int[3 * iTQuantity];
        aiConnect[0] = 0;
        aiConnect[1] = 8;
        aiConnect[2] = 9;
        aiConnect[3] = 0;
        aiConnect[4] = 9;
        aiConnect[5] = 4;
        aiConnect[6] = 0;
        aiConnect[7] = 4;
        aiConnect[8] = 16;
        aiConnect[9] = 0;
        aiConnect[10] = 12;
        aiConnect[11] = 13;
        aiConnect[12] = 0;
        aiConnect[13] = 13;
        aiConnect[14] = 1;
        aiConnect[15] = 0;
        aiConnect[16] = 1;
        aiConnect[17] = 8;
        aiConnect[18] = 0;
        aiConnect[19] = 16;
        aiConnect[20] = 17;
        aiConnect[21] = 0;
        aiConnect[22] = 17;
        aiConnect[23] = 2;
        aiConnect[24] = 0;
        aiConnect[25] = 2;
        aiConnect[26] = 12;
        aiConnect[27] = 8;
        aiConnect[28] = 1;
        aiConnect[29] = 18;
        aiConnect[30] = 8;
        aiConnect[31] = 18;
        aiConnect[32] = 5;
        aiConnect[33] = 8;
        aiConnect[34] = 5;
        aiConnect[35] = 9;
        aiConnect[36] = 12;
        aiConnect[37] = 2;
        aiConnect[38] = 10;
        aiConnect[39] = 12;
        aiConnect[40] = 10;
        aiConnect[41] = 3;
        aiConnect[42] = 12;
        aiConnect[43] = 3;
        aiConnect[44] = 13;
        aiConnect[45] = 16;
        aiConnect[46] = 4;
        aiConnect[47] = 14;
        aiConnect[48] = 16;
        aiConnect[49] = 14;
        aiConnect[50] = 6;
        aiConnect[51] = 16;
        aiConnect[52] = 6;
        aiConnect[53] = 17;
        aiConnect[54] = 9;
        aiConnect[55] = 5;
        aiConnect[56] = 15;
        aiConnect[57] = 9;
        aiConnect[58] = 15;
        aiConnect[59] = 14;
        aiConnect[60] = 9;
        aiConnect[61] = 14;
        aiConnect[62] = 4;
        aiConnect[63] = 6;
        aiConnect[64] = 11;
        aiConnect[65] = 10;
        aiConnect[66] = 6;
        aiConnect[67] = 10;
        aiConnect[68] = 2;
        aiConnect[69] = 6;
        aiConnect[70] = 2;
        aiConnect[71] = 17;
        aiConnect[72] = 3;
        aiConnect[73] = 19;
        aiConnect[74] = 18;
        aiConnect[75] = 3;
        aiConnect[76] = 18;
        aiConnect[77] = 1;
        aiConnect[78] = 3;
        aiConnect[79] = 1;
        aiConnect[80] = 13;
        aiConnect[81] = 7;
        aiConnect[82] = 15;
        aiConnect[83] = 5;
        aiConnect[84] = 7;
        aiConnect[85] = 5;
        aiConnect[86] = 18;
        aiConnect[87] = 7;
        aiConnect[88] = 18;
        aiConnect[89] = 19;
        aiConnect[90] = 7;
        aiConnect[91] = 11;
        aiConnect[92] = 6;
        aiConnect[93] = 7;
        aiConnect[94] = 6;
        aiConnect[95] = 14;
        aiConnect[96] = 7;
        aiConnect[97] = 14;
        aiConnect[98] = 15;
        aiConnect[99] = 7;
        aiConnect[100] = 19;
        aiConnect[101] = 3;
        aiConnect[102] = 7;
        aiConnect[103] = 3;
        aiConnect[104] = 10;
        aiConnect[105] = 7;
        aiConnect[106] = 10;
        aiConnect[107] = 11;
        IntBuffer indBuf = BufferUtils.createIntBuffer(aiConnect);

        if (!bOutsideView) {
            for (i = 0; i < iTQuantity; i++) {
                int iSave = aiConnect[3 * i + 1];
                aiConnect[3 * i + 1] = aiConnect[3 * i + 2];
                aiConnect[3 * i + 2] = iSave;
            }
        }

        if (rpkMesh != null) {
            rpkMesh.reconstruct(vertBuf, normBuf, null, texBuf, indBuf);
        } else {
            rpkMesh =
                new TriMesh(
                    "dodecahedron",
                    vertBuf,
                    normBuf,
                    null,
                    texBuf,
                    indBuf);
        }
        if (bWantColors)
            rpkMesh.setSolidColor(ColorRGBA.white);

        return rpkMesh;
    }

    /**
     * Creates a Icosahedron mesh.  If rpkMesh is null, a new mesh is used and returned.
     * @param rpkMesh The TriMesh to store the result in.
     * @param bWantNormals If true, normals are created.
     * @param bWantColors If true, colors are created.
     * @param bWantUVs If true, UV coords are created.
     * @param bOutsideView
     * @return rpkMesh after creation.
     */
    public static TriMesh createIcosahedronMesh(
        TriMesh rpkMesh,
        boolean bWantNormals,
        boolean bWantColors,
        boolean bWantUVs,
        boolean bOutsideView) {
        float fGoldenRatio = 0.5f * (1.0f + FastMath.sqrt(5.0f));
        float fInvRoot =
            1.0f / FastMath.sqrt(1.0f + fGoldenRatio * fGoldenRatio);
        float fU = fGoldenRatio * fInvRoot;
        float fV = fInvRoot;
        int i;

        // allocate vertices
        int iVQuantity = 12;
        Vector3f[] akVertex = new Vector3f[iVQuantity];
        akVertex[0] = new Vector3f(fU, fV, 0.0f);
        akVertex[1] = new Vector3f(-fU, fV, 0.0f);
        akVertex[2] = new Vector3f(fU, -fV, 0.0f);
        akVertex[3] = new Vector3f(-fU, -fV, 0.0f);
        akVertex[4] = new Vector3f(fV, 0.0f, fU);
        akVertex[5] = new Vector3f(fV, 0.0f, -fU);
        akVertex[6] = new Vector3f(-fV, 0.0f, fU);
        akVertex[7] = new Vector3f(-fV, 0.0f, -fU);
        akVertex[8] = new Vector3f(0.0f, fU, fV);
        akVertex[9] = new Vector3f(0.0f, -fU, fV);
        akVertex[10] = new Vector3f(0.0f, fU, -fV);
        akVertex[11] = new Vector3f(0.0f, -fU, -fV);
        FloatBuffer vertBuf = BufferUtils.createFloatBuffer(akVertex);
        
        // allocate normals if requested
        FloatBuffer normBuf = null;
        if (bWantNormals)
            normBuf = BufferUtils.createFloatBuffer(akVertex);

        // allocate texture coordinates if requested
        Vector2f[] akUV = null;
        if (bWantUVs) {
            akUV = new Vector2f[iVQuantity];
            for (i = 0; i < iVQuantity; i++) {
                if (FastMath.abs(akVertex[i].z) < 1.0f) {
                    akUV[i].x =
                        0.5f
                            * (1.0f
                                + FastMath.atan2(akVertex[i].y, akVertex[i].x)
                                    * FastMath.INV_PI);
                } else {
                    akUV[i].x = 0.5f;
                }
                akUV[i].y = FastMath.acos(akVertex[i].z) * FastMath.INV_PI;
            }
        }
        FloatBuffer texBuf = BufferUtils.createFloatBuffer(akUV);

        // allocate connectivity
        int iTQuantity = 20;
        int[] aiConnect = new int[3 * iTQuantity];
        aiConnect[0] = 0;
        aiConnect[1] = 8;
        aiConnect[2] = 4;
        aiConnect[3] = 0;
        aiConnect[4] = 5;
        aiConnect[5] = 10;
        aiConnect[6] = 2;
        aiConnect[7] = 4;
        aiConnect[8] = 9;
        aiConnect[9] = 2;
        aiConnect[10] = 11;
        aiConnect[11] = 5;
        aiConnect[12] = 1;
        aiConnect[13] = 6;
        aiConnect[14] = 8;
        aiConnect[15] = 1;
        aiConnect[16] = 10;
        aiConnect[17] = 7;
        aiConnect[18] = 3;
        aiConnect[19] = 9;
        aiConnect[20] = 6;
        aiConnect[21] = 3;
        aiConnect[22] = 7;
        aiConnect[23] = 11;
        aiConnect[24] = 0;
        aiConnect[25] = 10;
        aiConnect[26] = 8;
        aiConnect[27] = 1;
        aiConnect[28] = 8;
        aiConnect[29] = 10;
        aiConnect[30] = 2;
        aiConnect[31] = 9;
        aiConnect[32] = 11;
        aiConnect[33] = 3;
        aiConnect[34] = 11;
        aiConnect[35] = 9;
        aiConnect[36] = 4;
        aiConnect[37] = 2;
        aiConnect[38] = 0;
        aiConnect[39] = 5;
        aiConnect[40] = 0;
        aiConnect[41] = 2;
        aiConnect[42] = 6;
        aiConnect[43] = 1;
        aiConnect[44] = 3;
        aiConnect[45] = 7;
        aiConnect[46] = 3;
        aiConnect[47] = 1;
        aiConnect[48] = 8;
        aiConnect[49] = 6;
        aiConnect[50] = 4;
        aiConnect[51] = 9;
        aiConnect[52] = 4;
        aiConnect[53] = 6;
        aiConnect[54] = 10;
        aiConnect[55] = 5;
        aiConnect[56] = 7;
        aiConnect[57] = 11;
        aiConnect[58] = 7;
        aiConnect[59] = 5;
        IntBuffer indBuf = BufferUtils.createIntBuffer(aiConnect);

        if (!bOutsideView) {
            for (i = 0; i < iTQuantity; i++) {
                int iSave = aiConnect[3 * i + 1];
                aiConnect[3 * i + 1] = aiConnect[3 * i + 2];
                aiConnect[3 * i + 2] = iSave;
            }
        }

        if (rpkMesh != null) {
            rpkMesh.reconstruct(vertBuf, normBuf, null, texBuf, indBuf);
        } else {
            rpkMesh =
                new TriMesh(
                    "icosahedron",
                    vertBuf,
                    normBuf,
                    null,
                    texBuf,
                    indBuf);
        }
        if (bWantColors)
            rpkMesh.setSolidColor(ColorRGBA.white);
        return rpkMesh;
    }
}