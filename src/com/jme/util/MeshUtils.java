package com.jme.util;

import com.jme.scene.*;
import com.jme.math.*;
import com.jme.renderer.*;

public class MeshUtils {

    /** */
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

        // allocate normals if requested
        Vector3f[] akNormal = null;
        if (bWantNormals) {
            akNormal = new Vector3f[iVQuantity];
            for (i = 0; i < iVQuantity; i++)
                akNormal[i] = akVertex[i];
        }

        // allocate colors if requested
        ColorRGBA[] akColor = null;
        if (bWantColors) {
            akColor = new ColorRGBA[iVQuantity];
            for (int x = 0; x < iVQuantity; x++)
                akColor[x] = new ColorRGBA();
        }

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

        if (!bOutsideView) {
            for (i = 0; i < iTQuantity; i++) {
                int iSave = aiConnect[3 * i + 1];
                aiConnect[3 * i + 1] = aiConnect[3 * i + 2];
                aiConnect[3 * i + 2] = iSave;
            }
        }

        if (rpkMesh != null) {
            rpkMesh.reconstruct(akVertex, akNormal, akColor, akUV, aiConnect);
        } else {
            rpkMesh =
                new TriMesh(
                    "dodecahedron",
                    akVertex,
                    akNormal,
                    akColor,
                    akUV,
                    aiConnect);
        }
        return rpkMesh;
    }
    /** */
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

        // allocate normals if requested
        Vector3f[] akNormal = null;
        if (bWantNormals) {
            akNormal = new Vector3f[iVQuantity];
            for (i = 0; i < iVQuantity; i++)
                akNormal[i] = akVertex[i];
        }

        // allocate colors if requested
        ColorRGBA[] akColor = null;
        if (bWantColors) {
            akColor = new ColorRGBA[iVQuantity];
            for (int x = 0; x < iVQuantity; x++)
                akColor[x] = new ColorRGBA();
        }

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

        if (!bOutsideView) {
            for (i = 0; i < iTQuantity; i++) {
                int iSave = aiConnect[3 * i + 1];
                aiConnect[3 * i + 1] = aiConnect[3 * i + 2];
                aiConnect[3 * i + 2] = iSave;
            }
        }

        if (rpkMesh != null) {
            rpkMesh.reconstruct(akVertex, akNormal, akColor, akUV, aiConnect);
        } else {
            rpkMesh =
                new TriMesh(
                    "icosahedron",
                    akVertex,
                    akNormal,
                    akColor,
                    akUV,
                    aiConnect);
        }
        return rpkMesh;
    }
}