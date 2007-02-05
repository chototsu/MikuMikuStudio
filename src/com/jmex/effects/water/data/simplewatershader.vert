varying vec4 refrCoords;
varying vec4 normCoords;
varying vec4 viewCoords;
varying vec4 viewTangetSpace;

uniform vec4 cameraPos;
uniform float normalTranslation, refractionTranslation;
//varying float fog;

void main()
{
	// Because we have a flat plane for water we already know the vectors for tangent space
	vec4 tangent = vec4(1.0, 0.0, 0.0, 0.0);
	vec4 normal = vec4(0.0, 1.0, 0.0, 0.0);
	vec4 biTangent = vec4(0.0, 0.0, 1.0, 0.0);

	// Calculate the vector coming from the vertex to the camera
	vec4 viewDir = cameraPos - gl_Vertex;

	// Compute tangent space for the view direction
	viewTangetSpace.x = dot(viewDir, tangent);
	viewTangetSpace.y = dot(viewDir, biTangent);
	viewTangetSpace.z = dot(viewDir, normal);
	viewTangetSpace.w = 1.0;

	refrCoords = gl_MultiTexCoord0+vec4(0.0,refractionTranslation,0.0,0.0);
	normCoords = gl_MultiTexCoord1+vec4(0.0,normalTranslation,0.0,0.0);

	// This calculates our current projection coordinates
	viewCoords = gl_ModelViewProjectionMatrix * gl_Vertex;

	gl_Position = viewCoords;
//	fog = (gl_Fog.end - gl_Position.z) * gl_Fog.scale;
//	fog = clamp( fog, 0.0, 1.0);
}
