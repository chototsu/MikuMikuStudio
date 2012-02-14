#ifdef GL_ES
# define LOWP       lowp
# define MEDIUMP    mediump
#else
# define LOWP    
# define MEDIUMP   
#endif

#import "Common/ShaderLib/Optics.glsllib"

#ifdef SPHERE_MAP_A
  uniform sampler2D m_SphereMap_A;
#endif
#ifdef SPHERE_MAP_H
  uniform sampler2D m_SphereMap_H;
#endif


#define ATTENUATION
//#define HQ_ATTENUATION


varying vec2 texCoord;

varying LOWP vec4 AmbientSum;
varying LOWP vec4 DiffuseSum;
varying LOWP vec4 SpecularSum;

#ifndef VERTEX_LIGHTING
  varying vec3 vPosition;
  varying vec3 vViewDir;
  varying vec4 vLightDir;
#endif

#ifdef DIFFUSEMAP
  uniform sampler2D m_DiffuseMap;
#endif

#ifdef SPECULARMAP
  uniform sampler2D m_SpecularMap;
#endif

#ifdef PARALLAXMAP
  uniform sampler2D m_ParallaxMap;
#endif
  
#ifdef NORMALMAP
  uniform sampler2D m_NormalMap;
#else
  varying vec3 vNormal;
#endif

#ifdef ALPHAMAP
  uniform sampler2D m_AlphaMap;
#endif

#ifdef COLORRAMP
  uniform sampler2D m_ColorRamp;
#endif
uniform float m_AlphaDiscardThreshold;
#ifndef VERTEX_LIGHTING
uniform float m_Shininess;

#ifdef HQ_ATTENUATION
uniform vec4 g_LightPosition;
varying vec3 lightVec;
#endif

#ifdef USE_REFLECTION 
    uniform float m_ReflectionPower;
    uniform float m_ReflectionIntensity;
//    varying vec4 refVec;

    uniform ENVMAP m_EnvMap;
#endif

float tangDot(in vec3 v1, in vec3 v2){
    float d = dot(v1,v2);
    #ifdef V_TANGENT
        d = 1.0 - d*d;
        return step(0.0, d) * sqrt(d);
    #else
        return d;
    #endif
}

float lightComputeDiffuse(in vec3 norm, in vec3 lightdir, in vec3 viewdir){
    #ifdef MINNAERT
        float NdotL = max(0.0, dot(norm, lightdir));
        float NdotV = max(0.0, dot(norm, viewdir));
        return NdotL * pow(max(NdotL * NdotV, 0.1), -1.0) * 0.5;
    #else
        return max(0.0, dot(norm, lightdir));
    #endif
}

float lightComputeSpecular(in vec3 norm, in vec3 viewdir, in vec3 lightdir, in float shiny){
    #ifdef LOW_QUALITY
       // Blinn-Phong
       // Note: preferably, H should be computed in the vertex shader
       vec3 H = (viewdir + lightdir) * vec3(0.5);
       return pow(max(tangDot(H, norm), 0.0), shiny);
    #elif defined(WARDISO)
        // Isotropic Ward
        vec3 halfVec = normalize(viewdir + lightdir);
        float NdotH  = max(0.001, tangDot(norm, halfVec));
        float NdotV  = max(0.001, tangDot(norm, viewdir));
        float NdotL  = max(0.001, tangDot(norm, lightdir));
        float a      = tan(acos(NdotH));
        float p      = max(shiny/128.0, 0.001);
        return NdotL * (1.0 / (4.0*3.14159265*p*p)) * (exp(-(a*a)/(p*p)) / (sqrt(NdotV * NdotL)));
    #else
       // Standard Phong
       vec3 R = reflect(-lightdir, norm);
       return pow(max(tangDot(R, viewdir), 0.0), shiny);
    #endif
}

vec2 computeLighting(in vec3 wvPos, in vec3 wvNorm, in vec3 wvViewDir, in vec3 wvLightDir){
   float diffuseFactor = lightComputeDiffuse(wvNorm, wvLightDir, wvViewDir);
   float specularFactor = lightComputeSpecular(wvNorm, wvViewDir, wvLightDir, m_Shininess);
   specularFactor *= step(1.0, m_Shininess);

   #ifdef HQ_ATTENUATION
    float att = clamp(1.0 - g_LightPosition.w * length(lightVec), 0.0, 1.0);
   #else
    float att = vLightDir.w;
   #endif

   return vec2(diffuseFactor, specularFactor) * vec2(att);
}
#endif
    varying vec4 refVec;
vec2 Optics_SphereCoord2(in vec3 dir){
    float dzplus1 = dir.z + 1.0;
    float m = 2.0 * sqrt(dir.x * dir.x + dir.y * dir.y + dzplus1 * dzplus1);
    return vec2(dir.x / m + 0.5, dir.y / m + 0.5);
}

void main(){
    LOWP vec2 newTexCoord;
 
    #if defined(PARALLAXMAP) || defined(NORMALMAP_PARALLAX)
       float h;
       #ifdef PARALLAXMAP
          h = texture2D(m_ParallaxMap, texCoord).r;
       #else
          h = texture2D(m_NormalMap, texCoord).a;
       #endif
       float heightScale = 0.05;
       float heightBias = heightScale * -0.5;
       vec3 normView = normalize(vViewDir);
       h = (h * heightScale + heightBias) * normView.z;
       newTexCoord = texCoord + (h * -normView.xy);
    #else
       newTexCoord = texCoord;
    #endif
    
   #ifdef DIFFUSEMAP
      vec4 diffuseColor = texture2D(m_DiffuseMap, newTexCoord);
    #else
      vec4 diffuseColor = vec4(1.0);
    #endif
    LOWP float alpha = DiffuseSum.a * diffuseColor.a;
    //float alpha = (DiffuseSum.a + diffuseColor.a)/2;
    #ifdef ALPHAMAP
       alpha = alpha * texture2D(m_AlphaMap, newTexCoord).r;
    #endif
    if(alpha < 0.1 /*m_AlphaDiscardThreshold*/){
        discard;
    }

    // ***********************
    // Read from textures
    // ***********************
    #if defined(NORMALMAP) && !defined(VERTEX_LIGHTING)
      vec4 normalHeight = texture2D(m_NormalMap, newTexCoord);
      vec3 normal = (normalHeight.xyz * vec3(2.0) - vec3(1.0));
      #ifdef LATC
        normal.z = sqrt(1.0 - (normal.x * normal.x) - (normal.y * normal.y));
      #endif
      normal.y = -normal.y;
    #elif !defined(VERTEX_LIGHTING)
      vec3 normal = vNormal;
      #if !defined(LOW_QUALITY) && !defined(V_TANGENT)
         normal = normalize(normal);
      #endif
    #endif

    #ifdef SPECULARMAP
      vec4 specularColor = texture2D(m_SpecularMap, newTexCoord);
    #else
      vec4 specularColor = vec4(1.0);
    #endif

    #ifdef VERTEX_LIGHTING
       LOWP vec2 light = vec2(AmbientSum.a, SpecularSum.a);
       #ifdef COLORRAMP
           // light.x = texture2D(m_ColorRamp, vec2(light.x, 0.0)).r;
           // light.y = texture2D(m_ColorRamp, vec2(light.y, 0.0)).r;
           diffuseColor.rgb  *= texture2D(m_ColorRamp, vec2(0.0,light. x)).rgb;
           //specularColor.rgb *= texture2D(m_ColorRamp, vec2(0.0,light. y)).rgb;
       #endif
// adero200
       //if (light.y != light.y) {
       //     light.y = 0.0;
       //}
       LOWP vec4 output_color = (((AmbientSum + DiffuseSum) * diffuseColor)
                      + SpecularSum * specularColor * light.y );

    #else
       vec4 lightDir = vLightDir;
       lightDir.xyz = normalize(lightDir.xyz);

       vec2 light = computeLighting(vPosition, normal, vViewDir.xyz, lightDir.xyz);
       #ifdef COLORRAMP
           // diffuseColor.rgb  *= texture2D(m_ColorRamp, vec2(light.x, 0.0)).rgb;
           // specularColor.rgb *= texture2D(m_ColorRamp, vec2(light.y, 0.0)).rgb;
           diffuseColor.rgb  *= texture2D(m_ColorRamp, vec2(0.0,light. x)).rgb;
           specularColor.rgb *= texture2D(m_ColorRamp, vec2(0.0,light. y)).rgb;
       #endif

       // Workaround, since it is not possible to modify varying variables
       vec4 SpecularSum2 = SpecularSum;
       #ifdef USE_REFLECTION
            vec4 refColor = Optics_GetEnvColor(m_EnvMap, refVec.xyz);

            // Interpolate light specularity toward reflection color
            // Multiply result by specular map
            specularColor = mix(SpecularSum2 * light.y, refColor, refVec.w) * specularColor;

            SpecularSum2 = vec4(1.0);
            light.y = 1.0;
       #endif
//       if (isnan(light.y)) {
       if (light.y != light.y) {
            light.y = 0.0;
       }
//       gl_FragColor =  (AmbientSum * diffuseColor +
//                       DiffuseSum * diffuseColor + //* light.x +
//                       SpecularSum2 * specularColor * light.y ) * 0.8;
       vec4 output_color = (((AmbientSum + DiffuseSum) * diffuseColor)  +
                       SpecularSum2 * specularColor * light.y );
// output_color=vec4(0);
#ifdef SPHERE_MAP_A
        vec2 v2 = Optics_SphereCoord(normalize(refVec.xyz));
        v2.y = 1.0 - v2.y;
        output_color.xyz +=  (texture2D(m_SphereMap_A, v2).xyz);
        // output_color.xyz = vec3(normalize(refVec.xyz).x);
#endif
#ifdef SPHERE_MAP_H
        vec2 v2 = Optics_SphereCoord(normalize(refVec.xyz));
        v2.y = 1.0 - v2.y;
        output_color.xyz *= (texture2D(m_SphereMap_H, v2).xyz);
#endif

    #endif
#ifdef SPHERE_MAP_A
        vec2 v2 = Optics_SphereCoord(normalize(refVec.xyz));
        v2.y = 1.0 - v2.y;
        output_color.xyz +=  (texture2D(m_SphereMap_A, v2).xyz);
        // output_color.xyz = vec3(normalize(refVec.xyz).x);
#endif
#ifdef SPHERE_MAP_H
        vec2 v2 = Optics_SphereCoord(normalize(refVec.xyz));
        v2.y = 1.0 - v2.y;
        output_color.xyz *= (texture2D(m_SphereMap_H, v2).xyz);
#endif
    output_color.a = alpha;
    gl_FragColor = output_color;
}
