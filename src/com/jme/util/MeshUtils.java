package com.jme.util;

import com.jme.scene.*;
import com.jme.math.*;
import com.jme.renderer.*;

public class MeshUtils {

    /** */
    public static TriMesh createRectangleMesh(
        TriMesh rpkMesh,
        Vector3f rkCenter,
        Vector3f rkU,
        Vector3f rkV,
        Vector3f rkAxis,
        float fUExtent,
        float fVExtent,
        boolean bWantNormals,
        boolean bWantColors,
        boolean bWantUVs) {
        // allocate vertices
        int iVQuantity = 4;
        Vector3f[] akVertex = new Vector3f[iVQuantity];

        // allocate normals if requested
        Vector3f[] akNormal = null;
        if (bWantNormals)
            akNormal = new Vector3f[iVQuantity];

        // allocate colors if requested
        ColorRGBA[] akColor = null;
        if (bWantColors) {
            akColor = new ColorRGBA[iVQuantity];
            for (int x = 0; x < iVQuantity; x++)
                akColor[x] = new ColorRGBA();
        }

        // allocate texture coordinates if requested
        Vector2f[] akUV = null;
        if (bWantUVs)
            akUV = new Vector2f[iVQuantity];

        // allocate connectivity
        int iTQuantity = 2;
        int[] aiConnect = new int[3 * iTQuantity];

        // generate geometry
        Vector3f kUTerm = rkU.mult(fUExtent);
        Vector3f kVTerm = rkV.mult(fVExtent);
        akVertex[0] = rkCenter.subtract(kUTerm).subtractLocal(kVTerm);
        akVertex[1] = rkCenter.add(kUTerm).subtractLocal(kVTerm);
        akVertex[2] = rkCenter.add(kUTerm).addLocal(kVTerm);
        akVertex[3] = rkCenter.subtract(kUTerm).addLocal(kVTerm);

        if (bWantNormals) {
            for (int i = 0; i < iVQuantity; i++)
                akNormal[i] = rkAxis;
        }

        if (bWantUVs) {
            akUV[0] = new Vector2f(0.0f, 0.0f);
            akUV[1] = new Vector2f(1.0f, 0.0f);
            akUV[2] = new Vector2f(1.0f, 1.0f);
            akUV[3] = new Vector2f(0.0f, 1.0f);
        }

        // generate connectivity
        aiConnect[0] = 0;
        aiConnect[1] = 1;
        aiConnect[2] = 2;
        aiConnect[3] = 0;
        aiConnect[4] = 2;
        aiConnect[5] = 3;

        if (rpkMesh != null) {
            rpkMesh.reconstruct(akVertex, akNormal, akColor, akUV, aiConnect);
        } else {
            rpkMesh =
                new TriMesh(
                    "rectangle",
                    akVertex,
                    akNormal,
                    akColor,
                    akUV,
                    aiConnect);
        }
        return rpkMesh;
    }
    /** */
    public static TriMesh createDiskMesh(
        TriMesh rpkMesh,
        int iShellSamples,
        int iRadialSamples,
        Vector3f rkCenter,
        float fRadius,
        Vector3f rkU,
        Vector3f rkV,
        Vector3f rkAxis,
        boolean bWantNormals,
        boolean bWantColors,
        boolean bWantUVs) {
        int iRSm1 = iRadialSamples - 1, iSSm1 = iShellSamples - 1;

        // allocate vertices
        int iVQuantity = 1 + iRadialSamples * iSSm1;
        Vector3f[] akVertex = new Vector3f[iVQuantity];

        // allocate normals if requested
        Vector3f[] akNormal = null;
        if (bWantNormals)
            akNormal = new Vector3f[iVQuantity];

        // allocate colors if requested
        ColorRGBA[] akColor = null;
        if (bWantColors) {
            akColor = new ColorRGBA[iVQuantity];
            for (int x = 0; x < iVQuantity; x++)
                akColor[x] = new ColorRGBA();
        }

        // allocate texture coordinates if requested
        Vector2f[] akUV = null;
        if (bWantUVs)
            akUV = new Vector2f[iVQuantity];

        // allocate connectivity
        int iTQuantity = iRadialSamples * (2 * iSSm1 - 1);
        int[] aiConnect = new int[3 * iTQuantity];

        // generate geometry
        int iR, i;

        // center of disk
        akVertex[0] = new Vector3f(0f, 0f, 0f);
        if (bWantNormals) {
            akNormal[0] = rkAxis;
        }
        if (bWantUVs) {
            if (akUV[0] == null)
                akUV[0] = new Vector2f();
            akUV[0].x = 0.5f;
            akUV[0].y = 0.5f;
        }

        float fInvSSm1 = 1.0f / (float) iSSm1;
        float fInvRS = 1.0f / (float) iRadialSamples;
        for (iR = 0; iR < iRadialSamples; iR++) {
            float fAngle = FastMath.TWO_PI * fInvRS * iR;
            float fCos = FastMath.cos(fAngle);
            float fSin = FastMath.sin(fAngle);
            Vector3f kRadial = rkU.mult(fCos).addLocal(rkV.mult(fSin));

            for (int iS = 1; iS < iShellSamples; iS++) {
                float fFraction = fInvSSm1 * iS; // in (0,R]
                Vector3f kFracRadial = kRadial.mult(fFraction);
                i = iS + iSSm1 * iR;
                akVertex[i] = kFracRadial.mult(fRadius);
                if (bWantNormals) {
                    akNormal[i] = rkAxis;
                }
                if (bWantUVs) {
                    if (akUV[i] == null)
                        akUV[i] = new Vector2f();
                    akUV[i].x = 0.5f * (1.0f + kFracRadial.x);
                    akUV[i].y = 0.5f * (1.0f + kFracRadial.y);
                }
            }
        }

        // rotate and translate disk to specified center
        for (i = 0; i < iVQuantity; i++)
            akVertex[i].addLocal(rkCenter);

        // generate connectivity
        int[] aiLocalConnect = aiConnect;
        int iT = 0;
        int index = 0;
        for (int iR0 = iRSm1, iR1 = 0; iR1 < iRadialSamples; iR0 = iR1++) {
            aiLocalConnect[index + 0] = 0;
            aiLocalConnect[index + 1] = 1 + iSSm1 * iR0;
            aiLocalConnect[index + 2] = 1 + iSSm1 * iR1;
            index += 3;
            iT++;
            for (int iS = 1; iS < iSSm1; iS++, index += 6) {
                int i00 = iS + iSSm1 * iR0;
                int i01 = iS + iSSm1 * iR1;
                int i10 = i00 + 1;
                int i11 = i01 + 1;
                aiLocalConnect[index + 0] = i00;
                aiLocalConnect[index + 1] = i10;
                aiLocalConnect[index + 2] = i11;
                aiLocalConnect[index + 3] = i00;
                aiLocalConnect[index + 4] = i11;
                aiLocalConnect[index + 5] = i01;
                iT += 2;
            }
        }
        //        assert( iT == iTQuantity );

        if (rpkMesh != null) {
            rpkMesh.reconstruct(akVertex, akNormal, akColor, akUV, aiConnect);
        } else {
            rpkMesh =
                new TriMesh(
                    "disk",
                    akVertex,
                    akNormal,
                    akColor,
                    akUV,
                    aiConnect);
        }
        return rpkMesh;
    }
    /** */
    public static TriMesh createBoxMesh(
        TriMesh rpkMesh,
        Vector3f rkCenter,
        Vector3f rkU,
        Vector3f rkV,
        Vector3f rkW,
        float fUExtent,
        float fVExtent,
        float fWExtent,
        boolean bWantNormals,
        boolean bWantColors,
        boolean bWantUVs,
        boolean bOutsideView) {
        // allocate vertices
        int iVQuantity = 8;
        Vector3f[] akVertex = new Vector3f[iVQuantity];

        // allocate normals if requested
        Vector3f[] akNormal = null;
        if (bWantNormals)
            akNormal = new Vector3f[iVQuantity];

        // allocate colors if requested
        ColorRGBA[] akColor = null;
        if (bWantColors) {
            akColor = new ColorRGBA[iVQuantity];
            for (int x = 0; x < iVQuantity; x++)
                akColor[x] = new ColorRGBA();
        }

        // allocate texture coordinates if requested
        Vector2f[] akUV = null;
        if (bWantUVs)
            akUV = new Vector2f[iVQuantity];

        // allocate connectivity
        int iTQuantity = 12;
        int[] aiConnect = new int[3 * iTQuantity];

        // generate geometry
        Vector3f kUTerm = rkU.mult(fUExtent);
        Vector3f kVTerm = rkV.mult(fVExtent);
        Vector3f kWTerm = rkW.mult(fWExtent);
        akVertex[0] =
            rkCenter.subtract(kUTerm).subtractLocal(kVTerm).subtractLocal(
                kWTerm);
        akVertex[1] =
            rkCenter.add(kUTerm).subtractLocal(kVTerm).subtractLocal(kWTerm);
        akVertex[2] =
            rkCenter.add(kUTerm).addLocal(kVTerm).subtractLocal(kWTerm);
        akVertex[3] =
            rkCenter.subtract(kUTerm).addLocal(kVTerm).subtractLocal(kWTerm);
        akVertex[4] =
            rkCenter.subtract(kUTerm).subtractLocal(kVTerm).addLocal(kWTerm);
        akVertex[5] =
            rkCenter.add(kUTerm).subtractLocal(kVTerm).addLocal(kWTerm);
        akVertex[6] = rkCenter.add(kUTerm).addLocal(kVTerm).addLocal(kWTerm);
        akVertex[7] =
            rkCenter.subtract(kUTerm).addLocal(kVTerm).addLocal(kWTerm);

        if (bWantUVs) {
            akUV[0] = new Vector2f(0.25f, 0.75f);
            akUV[1] = new Vector2f(0.75f, 0.75f);
            akUV[2] = new Vector2f(0.75f, 0.25f);
            akUV[3] = new Vector2f(0.25f, 0.25f);
            akUV[4] = new Vector2f(0.0f, 1.0f);
            akUV[5] = new Vector2f(1.0f, 1.0f);
            akUV[6] = new Vector2f(1.0f, 0.0f);
            akUV[7] = new Vector2f(0.0f, 0.0f);
        }

        // generate connectivity (outside view)
        aiConnect[0] = 0;
        aiConnect[1] = 2;
        aiConnect[2] = 1;
        aiConnect[3] = 0;
        aiConnect[4] = 3;
        aiConnect[5] = 2;
        aiConnect[6] = 0;
        aiConnect[7] = 1;
        aiConnect[8] = 5;
        aiConnect[9] = 0;
        aiConnect[10] = 5;
        aiConnect[11] = 4;
        aiConnect[12] = 0;
        aiConnect[13] = 4;
        aiConnect[14] = 7;
        aiConnect[15] = 0;
        aiConnect[16] = 7;
        aiConnect[17] = 3;
        aiConnect[18] = 6;
        aiConnect[19] = 4;
        aiConnect[20] = 5;
        aiConnect[21] = 6;
        aiConnect[22] = 7;
        aiConnect[23] = 4;
        aiConnect[24] = 6;
        aiConnect[25] = 5;
        aiConnect[26] = 1;
        aiConnect[27] = 6;
        aiConnect[28] = 1;
        aiConnect[29] = 2;
        aiConnect[30] = 6;
        aiConnect[31] = 2;
        aiConnect[32] = 3;
        aiConnect[33] = 6;
        aiConnect[34] = 3;
        aiConnect[35] = 7;

        if (!bOutsideView) {
            int[] aiLocalConnect = aiConnect;
            int index = 0;
            for (int iT = 0; iT < iTQuantity; iT++, index += 3) {
                int iSave = aiLocalConnect[index + 1];
                aiLocalConnect[index + 1] = aiLocalConnect[index + 2];
                aiLocalConnect[index + 2] = iSave;
            }
        }

        if (rpkMesh != null) {
            rpkMesh.reconstruct(akVertex, akNormal, akColor, akUV, aiConnect);
        } else {
            rpkMesh =
                new TriMesh(
                    "box",
                    akVertex,
                    akNormal,
                    akColor,
                    akUV,
                    aiConnect);
        }

        //        if ( bWantNormals )
        //            rpkMesh.updateModelNormals();
        return rpkMesh;
    }
    /** */
    public static TriMesh createCylinderMesh(
        TriMesh rpkMesh,
        int iAxisSamples,
        int iRadialSamples,
        Vector3f rkCenter,
        Vector3f rkU,
        Vector3f rkV,
        Vector3f rkAxis,
        float fRadius,
        float fHeight,
        boolean bWantNormals,
        boolean bWantColors,
        boolean bWantUVs,
        boolean bOutsideView) {
        // allocate vertices
        int iVQuantity = iAxisSamples * (iRadialSamples + 1);
        Vector3f[] akVertex = new Vector3f[iVQuantity];

        // allocate normals if requested
        Vector3f[] akNormal = null;
        if (bWantNormals)
            akNormal = new Vector3f[iVQuantity];

        // allocate colors if requested
        ColorRGBA[] akColor = null;
        if (bWantColors) {
            akColor = new ColorRGBA[iVQuantity];
            for (int x = 0; x < iVQuantity; x++)
                akColor[x] = new ColorRGBA();
        }

        // allocate texture coordinates if requested
        Vector2f[] akUV = null;
        if (bWantUVs)
            akUV = new Vector2f[iVQuantity];

        // allocate connectivity
        int iTQuantity = 2 * (iAxisSamples - 1) * iRadialSamples;
        int[] aiConnect = new int[3 * iTQuantity];

        // generate geometry
        float fInvRS = 1.0f / (float) iRadialSamples;
        float fInvASm1 = 1.0f / (float) (iAxisSamples - 1);
        float fHalfHeight = 0.5f * fHeight;
        int iR, iA, iAStart, i;

        // Generate points on the unit circle to be used in computing the mesh
        // points on a cylinder slice.
        float[] afSin = new float[iRadialSamples + 1];
        float[] afCos = new float[iRadialSamples + 1];
        for (iR = 0; iR < iRadialSamples; iR++) {
            float fAngle = FastMath.TWO_PI * fInvRS * iR;
            afCos[iR] = FastMath.cos(fAngle);
            afSin[iR] = FastMath.sin(fAngle);
        }
        afSin[iRadialSamples] = afSin[0];
        afCos[iRadialSamples] = afCos[0];

        // generate the cylinder itself
        for (iA = 0, i = 0; iA < iAxisSamples; iA++) {
            float fAxisFraction = iA * fInvASm1; // in [0,1]
            float fZ = -fHalfHeight + fHeight * fAxisFraction;

            // compute center of slice
            Vector3f kSliceCenter = rkCenter.add(rkAxis.mult(fZ));

            // compute slice vertices with duplication at end point
            int iSave = i;
            for (iR = 0; iR < iRadialSamples; iR++) {
                float fRadialFraction = iR * fInvRS; // in [0,1)
                Vector3f kNormal =
                    rkU.mult(afCos[iR]).addLocal(rkV.mult(afSin[iR]));
                akVertex[i] = kSliceCenter.add(kNormal.mult(fRadius));
                if (bWantNormals) {
                    if (bOutsideView)
                        akNormal[i] = kNormal;
                    else
                        akNormal[i] = kNormal.negate();
                }
                if (bWantUVs) {
                    if (akUV[i] == null)
                        akUV[i] = new Vector2f();
                    akUV[i].x = fRadialFraction;
                    akUV[i].y = fAxisFraction;
                }
                i++;
            }

            akVertex[i] = akVertex[iSave];
            if (bWantNormals) {
                akNormal[i] = akNormal[iSave];
            }
            if (bWantUVs) {
                if (akUV[i] == null)
                    akUV[i] = new Vector2f();
                akUV[i].x = 1.0f;
                akUV[i].y = fAxisFraction;
            }
            i++;
        }

        // generate connectivity
        int index = 0;
        int[] aiLocalConnect = aiConnect;
        for (iA = 0, iAStart = 0; iA < iAxisSamples - 1; iA++) {
            int i0 = iAStart;
            int i1 = i0 + 1;
            iAStart += iRadialSamples + 1;
            int i2 = iAStart;
            int i3 = i2 + 1;
            for (i = 0; i < iRadialSamples; i++, index += 6) {
                if (bOutsideView) {
                    aiLocalConnect[index + 0] = i0++;
                    aiLocalConnect[index + 1] = i1;
                    aiLocalConnect[index + 2] = i2;
                    aiLocalConnect[index + 3] = i1++;
                    aiLocalConnect[index + 4] = i3++;
                    aiLocalConnect[index + 5] = i2++;
                } else // inside view
                    {
                    aiLocalConnect[index + 0] = i0++;
                    aiLocalConnect[index + 1] = i2;
                    aiLocalConnect[index + 2] = i1;
                    aiLocalConnect[index + 3] = i1++;
                    aiLocalConnect[index + 4] = i2++;
                    aiLocalConnect[index + 5] = i3++;
                }
            }
        }

        afCos = null;
        afSin = null;

        if (rpkMesh != null) {
            rpkMesh.reconstruct(akVertex, akNormal, akColor, akUV, aiConnect);
        } else {
            rpkMesh =
                new TriMesh(
                    "cylinder",
                    akVertex,
                    akNormal,
                    akColor,
                    akUV,
                    aiConnect);
        }
        return rpkMesh;
    }

    /** */
    public static TriMesh createTetrahedronMesh(
        TriMesh rpkMesh,
        boolean bWantNormals,
        boolean bWantColors,
        boolean bWantUVs,
        boolean bOutsideView) {
        float fSqrt2Div3 = FastMath.sqrt(2.0f) / 3.0f;
        float fSqrt6Div3 = FastMath.sqrt(6.0f) / 3.0f;
        float fOneThird = 1.0f / 3.0f;
        int i;

        // allocate vertices
        int iVQuantity = 4;
        Vector3f[] akVertex = new Vector3f[iVQuantity];
        akVertex[0] = new Vector3f(0.0f, 0.0f, 1.0f);
        akVertex[1] = new Vector3f(2.0f * fSqrt2Div3, 0.0f, -fOneThird);
        akVertex[2] = new Vector3f(-fSqrt2Div3, fSqrt6Div3, -fOneThird);
        akVertex[3] = new Vector3f(-fSqrt2Div3, -fSqrt6Div3, -fOneThird);

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
        int iTQuantity = 4;
        int[] aiConnect = new int[3 * iTQuantity];
        aiConnect[0] = 0;
        aiConnect[1] = 1;
        aiConnect[2] = 2;
        aiConnect[3] = 0;
        aiConnect[4] = 2;
        aiConnect[5] = 3;
        aiConnect[6] = 0;
        aiConnect[7] = 3;
        aiConnect[8] = 1;
        aiConnect[9] = 1;
        aiConnect[10] = 3;
        aiConnect[11] = 2;

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
                    "tetrahedron",
                    akVertex,
                    akNormal,
                    akColor,
                    akUV,
                    aiConnect);
        }
        return rpkMesh;
    }
    /** */
    public static TriMesh createHexahedronMesh(
        TriMesh rpkMesh,
        boolean bWantNormals,
        boolean bWantColors,
        boolean bWantUVs,
        boolean bOutsideView) {
        float fSqrtThird = FastMath.sqrt(1.0f / 3.0f);
        int i;

        // allocate vertices
        int iVQuantity = 8;
        Vector3f[] akVertex = new Vector3f[iVQuantity];
        akVertex[0] = new Vector3f(-fSqrtThird, -fSqrtThird, -fSqrtThird);
        akVertex[1] = new Vector3f(fSqrtThird, -fSqrtThird, -fSqrtThird);
        akVertex[2] = new Vector3f(fSqrtThird, fSqrtThird, -fSqrtThird);
        akVertex[3] = new Vector3f(-fSqrtThird, fSqrtThird, -fSqrtThird);
        akVertex[4] = new Vector3f(-fSqrtThird, -fSqrtThird, fSqrtThird);
        akVertex[5] = new Vector3f(fSqrtThird, -fSqrtThird, fSqrtThird);
        akVertex[6] = new Vector3f(fSqrtThird, fSqrtThird, fSqrtThird);
        akVertex[7] = new Vector3f(-fSqrtThird, fSqrtThird, fSqrtThird);

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
        int iTQuantity = 12;
        int[] aiConnect = new int[3 * iTQuantity];
        aiConnect[0] = 0;
        aiConnect[1] = 3;
        aiConnect[2] = 2;
        aiConnect[3] = 0;
        aiConnect[4] = 2;
        aiConnect[5] = 1;
        aiConnect[6] = 0;
        aiConnect[7] = 1;
        aiConnect[8] = 5;
        aiConnect[9] = 0;
        aiConnect[10] = 5;
        aiConnect[11] = 4;
        aiConnect[12] = 0;
        aiConnect[13] = 4;
        aiConnect[14] = 7;
        aiConnect[15] = 0;
        aiConnect[16] = 7;
        aiConnect[17] = 3;
        aiConnect[18] = 6;
        aiConnect[19] = 5;
        aiConnect[20] = 1;
        aiConnect[21] = 6;
        aiConnect[22] = 1;
        aiConnect[23] = 2;
        aiConnect[24] = 6;
        aiConnect[25] = 2;
        aiConnect[26] = 3;
        aiConnect[27] = 6;
        aiConnect[28] = 3;
        aiConnect[29] = 7;
        aiConnect[30] = 6;
        aiConnect[31] = 7;
        aiConnect[32] = 4;
        aiConnect[33] = 6;
        aiConnect[34] = 4;
        aiConnect[35] = 5;

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
                    "hexahedron",
                    akVertex,
                    akNormal,
                    akColor,
                    akUV,
                    aiConnect);
        }
        return rpkMesh;
    }
    /** */
    public static TriMesh createOctahedronMesh(
        TriMesh rpkMesh,
        boolean bWantNormals,
        boolean bWantColors,
        boolean bWantUVs,
        boolean bOutsideView) {
        int i;

        // allocate vertices
        int iVQuantity = 6;
        Vector3f[] akVertex = new Vector3f[iVQuantity];
        akVertex[0] = new Vector3f(1.0f, 0.0f, 0.0f);
        akVertex[1] = new Vector3f(-1.0f, 0.0f, 0.0f);
        akVertex[2] = new Vector3f(0.0f, 1.0f, 0.0f);
        akVertex[3] = new Vector3f(0.0f, -1.0f, 0.0f);
        akVertex[4] = new Vector3f(0.0f, 0.0f, 1.0f);
        akVertex[5] = new Vector3f(0.0f, 0.0f, -1.0f);

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
        int iTQuantity = 8;
        int[] aiConnect = new int[3 * iTQuantity];
        aiConnect[0] = 4;
        aiConnect[1] = 0;
        aiConnect[2] = 2;
        aiConnect[3] = 4;
        aiConnect[4] = 2;
        aiConnect[5] = 1;
        aiConnect[6] = 4;
        aiConnect[7] = 1;
        aiConnect[8] = 3;
        aiConnect[9] = 4;
        aiConnect[10] = 3;
        aiConnect[11] = 0;
        aiConnect[12] = 5;
        aiConnect[13] = 2;
        aiConnect[14] = 0;
        aiConnect[15] = 5;
        aiConnect[16] = 1;
        aiConnect[17] = 2;
        aiConnect[18] = 5;
        aiConnect[19] = 3;
        aiConnect[20] = 1;
        aiConnect[21] = 5;
        aiConnect[22] = 0;
        aiConnect[23] = 3;

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
                    "octahedron",
                    akVertex,
                    akNormal,
                    akColor,
                    akUV,
                    aiConnect);
        }
        return rpkMesh;
    }
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