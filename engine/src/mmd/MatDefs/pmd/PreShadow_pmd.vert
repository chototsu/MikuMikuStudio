attribute vec3 inPosition;
attribute vec2 inTexCoord;

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;

#ifdef USE_HWSKINNING
uniform mat4 m_BoneMatrices[NUM_BONES];
#endif
attribute vec2 inBoneWeight;
attribute vec2 inBoneIndex;
#ifdef USE_HWSKINNING
void Skinning_Compute(inout vec4 position){
//    vec4 index  = inBoneIndices;
    vec2 index  = inBoneIndex;
    vec2 weight = inBoneWeight;

    vec4 newPos;

    //for (float i = 1.0; i < 2.0; i += 1.0){
        mat4 skinMat;
#if NUM_BONES != 1
    skinMat = m_BoneMatrices[int(index.x)];
    newPos    = weight.x * (skinMat * position);

    skinMat = m_BoneMatrices[int(index.y)];
    newPos    = newPos + weight.y * (skinMat * position);
#else
    skinMat = m_BoneMatrices[0];
    newPos    = (skinMat * position);
#endif
        //index = index.yzwx;
        //weight = weight.yzwx;
    //}

    position = newPos;
}
#endif
varying vec2 texCoord;

void main(){
   vec4 pos = vec4(inPosition, 1.0);
#ifdef USE_HWSKINNING
   Skinning_Compute(pos);
#endif
    gl_Position = g_WorldViewProjectionMatrix * pos;
    texCoord = inTexCoord;
}