uniform float exposurePow;
uniform float exposureCutoff;
uniform sampler2D RT;

varying vec2 vTexCoord;

void main(void)
{
   vec4 sum = texture2D(RT, vTexCoord);
   if ( (sum.r+sum.g+sum.b)/3.0 < exposureCutoff ) {
      sum = vec4(0.0);
   }
   sum = pow(sum,vec4(exposurePow));
   gl_FragColor = sum;
}