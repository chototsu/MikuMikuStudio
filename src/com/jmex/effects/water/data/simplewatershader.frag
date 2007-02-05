varying vec4 refrCoords; 
varying vec4 normCoords; 
varying vec4 viewCoords;
varying vec4 viewTangetSpace;

uniform sampler2D normalMap;
uniform sampler2D reflection;
uniform sampler2D dudvMap;

uniform vec4 waterColor;
uniform int abovewater;
//varying float fog;

void main()
{
	const float kShine = 128.0;
	const float kDistortion = 0.01;
	const float kRefraction = 0.01;

	vec4 distOffset = texture2D(dudvMap, refrCoords.xy) * kDistortion;
	vec4 dudvColor = texture2D(dudvMap, vec2(normCoords + distOffset));
	dudvColor = normalize(dudvColor * 2.0 - 1.0) * kRefraction;

	vec4 normalVector = texture2D(normalMap, vec2(normCoords + distOffset));
	normalVector = normalVector * 2.0 - 1.0;
	normalVector.a = 0.0;

	vec4 localView = normalize(viewTangetSpace);

	vec4 fresnelTerm = 1.0 - vec4( dot(normalVector, localView ) );
	fresnelTerm = fresnelTerm * 0.65 + 0.3;

	vec4 projCoord = viewCoords / viewCoords.q;
	projCoord = (projCoord + 1.0) * 0.5;
	if ( abovewater == 1 ) {
		projCoord.x = 1.0 - projCoord.x;
	}

	projCoord += dudvColor * 0.8 + normalVector * 0.2;
	projCoord = clamp(projCoord, 0.001, 0.999);

	vec4 reflectionColor  = texture2D(reflection, projCoord.xy);
	if ( abovewater == 0 ) {
		reflectionColor *= vec4(0.5,0.6,0.7,1.0);
	}

	vec4 endColor = mix(waterColor,reflectionColor,fresnelTerm);

	gl_FragColor = mix(endColor,reflectionColor,clamp((viewCoords.z-gl_Fog.start)*gl_Fog.scale,0.0,1.0));
//	gl_FragColor = mix(endColor,gl_Fog.color,clamp((viewCoords.z-gl_Fog.start)*gl_Fog.scale,0.0,1.0));
//   gl_FragColor = mix(gl_Fog.color,endColor,fog);
}