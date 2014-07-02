#ifdef USE_HWSKINNING
uniform mat4 m_BoneMatrices[NUM_BONES];
uniform mat4 m_PPShadowMat;

attribute vec2 inBoneWeight;
attribute vec2 inBoneIndex;
#endif
// #import "MatDefs/pmd/Skinning.glsllib"
#define ATTENUATION
// #define HQ_ATTENUATION

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;


void main(){
   vec4 pos = vec4(inPosition, 1.0);
   vec4 normal = vec4(inNormal,0.0);
#ifdef USE_HWSKINNING
//   Skinning_Compute(pos, normal);
//    vec4 index  = inBoneIndices;
    vec2 index  = inBoneIndex;
    vec2 weight = inBoneWeight;

    vec4 newPos;
    vec4 newNormal;

    //for (float i = 1.0; i < 2.0; i += 1.0){
        mat4 skinMat;
#if NUM_BONES != 1
    skinMat = m_BoneMatrices[int(index.x)];
    newPos    = weight.x * (skinMat * pos);
    newNormal = weight.x * (skinMat * normal);

    skinMat = m_BoneMatrices[int(index.y)];
    newPos    = newPos + weight.y * (skinMat * pos);
    newNormal = newNormal + weight.y * (skinMat * normal);
#else
    skinMat = m_BoneMatrices[0];
    newPos    = (skinMat * pos);
    newNormal = (skinMat * normal);
#endif
    //}

    pos = newPos;
    normal = newNormal;
#endif
   normal = normalize(normal);

   vec4 pos2 = m_PPShadowMat * pos;

   gl_Position = g_WorldViewProjectionMatrix * pos2;
}
