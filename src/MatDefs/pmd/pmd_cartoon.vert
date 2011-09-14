#ifdef USE_HWSKINNING
uniform mat4 m_BoneMatrices[20];
#endif
uniform float m_EdgeSize; 
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

#ifdef USE_HWSKINNING
attribute vec4 inBoneWeight;
attribute vec4 inBoneIndices;
attribute vec4 inBoneIndex;


void Skinning_Compute(inout vec4 position, inout vec4 normal){
//    vec4 index  = inBoneIndices;
    vec4 index  = inBoneIndex;
    vec4 weight = inBoneWeight;

    vec4 newPos    = vec4(0.0);
    vec4 newNormal = vec4(0.0);

    //for (float i = 1.0; i < 2.0; i += 1.0){
        mat4 skinMat = m_BoneMatrices[int(index.x)];
        newPos    = weight.x * (skinMat * position);
        newNormal = weight.x * (skinMat * normal);
        //index = index.yzwx;
        //weight = weight.yzwx;
        skinMat = m_BoneMatrices[int(index.y)];
        newPos    = newPos + weight.y * (skinMat * position);
        newNormal = newNormal + weight.y * (skinMat * normal);
    //}

    position = newPos;
    normal = newNormal;
}

#endif

void main(){
    if (m_EdgeSize != 0.0) {
   vec4 pos = vec4(inPosition, 1.0);
   vec4 normal = vec4(inNormal,0.0);
#ifdef USE_HWSKINNING
   Skinning_Compute(pos, normal);
#endif
   normal = normalize(normal);
   pos = pos + normal * m_EdgeSize;
   gl_Position = g_WorldViewProjectionMatrix * pos;
   } else {
     gl_Position = vec4(1000.0,1000.0,1000.0,1000.0);
   }
}
