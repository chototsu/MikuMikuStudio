#include "projectkyoto_jme3_mmd_nativelib_SkinUtil.h"
#include <memory.h>
#include <string.h>
#include <math.h>
/*
 * Class:     projectkyoto_jme3_mmd_SkinUtil
 * Method:    copy
 * Signature: (Ljava/nio/Buffer;Ljava/nio/Buffer;I)V
 */
JNIEXPORT void JNICALL Java_projectkyoto_jme3_mmd_nativelib_SkinUtil_copy
  (JNIEnv *env, jclass clazz, jobject src, jobject dist, jint size) {
    memcpy(env->GetDirectBufferAddress(dist), env->GetDirectBufferAddress(src), size);
}

/*
 * Class:     projectkyoto_jme3_mmd_nativelib_SkinUtil
 * Method:    setSkin
 * Signature: (Ljava/nio/FloatBuffer;Ljava/nio/ShortBuffer;Ljava/nio/FloatBuffer;F)V
 */
JNIEXPORT void JNICALL Java_projectkyoto_jme3_mmd_nativelib_SkinUtil_setSkin
  (JNIEnv *env, jclass clazz, jobject buf, jobject indexBuf, jobject skinBuf, jfloat weight) {
    jfloat *dist = (jfloat *)env->GetDirectBufferAddress(buf);
    jshort *ip = (jshort *)env->GetDirectBufferAddress(indexBuf);
    jfloat *skin = (jfloat *)env->GetDirectBufferAddress(skinBuf);
    jlong size = env->GetDirectBufferCapacity(indexBuf);
    for(int i=0;i<size;i++) {
        int index = ip[i];
        jfloat *p1 = &dist[index * 3];
        jfloat *p2 = &skin[i * 3];
        *p1 = *p1 + *p2 * weight;
        p1++;
        p2++;
        *p1 = *p1 + *p2 * weight;
        p1++;
        p2++;
        *p1 = *p1 + *p2 * weight;
    }
}
JNIEXPORT void JNICALL Java_projectkyoto_jme3_mmd_nativelib_SkinUtil_copyBoneMatrix
  (JNIEnv *env, jclass clazz, jobject srcBuf, jobject distBuf, jobject indexBuf) {
    jfloat *src = (jfloat *)env->GetDirectBufferAddress(srcBuf);
    jfloat *dist = (jfloat *)env->GetDirectBufferAddress(distBuf);
    jshort *index = (jshort *)env->GetDirectBufferAddress(indexBuf);
    jlong size = env->GetDirectBufferCapacity(indexBuf);
    jfloat *p = dist;
    for(int i=0;i<size;i++) {
        memcpy(p, src + index[i] * 16, sizeof(jfloat) * 16);
        p += 16;
    }
}
/*
 * Class:     projectkyoto_jme3_mmd_nativelib_SkinUtil
 * Method:    clear
 * Signature: (Ljava/nio/Buffer;)V
 */
JNIEXPORT void JNICALL Java_projectkyoto_jme3_mmd_nativelib_SkinUtil_clear
  (JNIEnv *env, jclass clazz, jobject buf) {
    jfloat *src = (jfloat *)env->GetDirectBufferAddress(buf);
    jlong size = env->GetDirectBufferCapacity(buf);
    memset(src, 0, size);
}

