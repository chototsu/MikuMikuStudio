varying vec3 vNormal;
varying vec4 pos;
varying float depth;

void main(void)
{
   gl_FragColor = vec4(vNormal,depth);
}