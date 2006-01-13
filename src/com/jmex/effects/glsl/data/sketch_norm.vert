varying vec3 vNormal;
varying vec4 pos;
varying float depth;

uniform float nearClip;
uniform float diffClip;

void main(void)
{
    gl_Position = ftransform();
    vNormal = (normalize(gl_NormalMatrix * gl_Normal)+1.0)*0.5;

    pos = gl_ModelViewMatrix * gl_Vertex;
    depth = -(pos.z-nearClip)/diffClip;
}