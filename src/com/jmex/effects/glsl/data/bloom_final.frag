uniform sampler2D RT;

varying vec2 vTexCoord;

void main(void)
{
   gl_FragColor = texture2D(RT, vTexCoord);
}