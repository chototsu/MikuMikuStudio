uniform sampler2D depth;

uniform float normalMult;
uniform float depthMult;
uniform float off;

varying vec2 vTexCoord;

void main(void)
{
	vec4 s00 = texture2D(depth, vTexCoord + vec2(-off, -off));
	vec4 s01 = texture2D(depth, vTexCoord + vec2( 0,   -off));
	vec4 s02 = texture2D(depth, vTexCoord + vec2( off, -off));

	vec4 s10 = texture2D(depth, vTexCoord + vec2(-off,  0));
	vec4 s12 = texture2D(depth, vTexCoord + vec2( off,  0));

	vec4 s20 = texture2D(depth, vTexCoord + vec2(-off,  off));
	vec4 s21 = texture2D(depth, vTexCoord + vec2( 0,    off));
	vec4 s22 = texture2D(depth, vTexCoord + vec2( off,  off));

	vec4 sobelX = s00 + 2.0 * s10 + s20 - s02 - 2.0 * s12 - s22;
	vec4 sobelY = s00 + 2.0 * s01 + s02 - s20 - 2.0 * s21 - s22;

	vec4 edgeSqr = sobelX * sobelX + sobelY * sobelY;
	float col = 1.0 - dot(edgeSqr, vec4(normalMult,normalMult,normalMult,depthMult));

    gl_FragColor = vec4(col);
}