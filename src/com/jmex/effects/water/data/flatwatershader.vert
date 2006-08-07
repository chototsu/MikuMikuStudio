varying vec2 refrCoords;
varying vec2 normCoords;
varying vec4 viewCoords;
varying vec3 viewTangetSpace;

uniform vec3 cameraPos;
uniform vec3 tangent;
uniform vec3 binormal;
uniform float normalTranslation, refractionTranslation;

void main()
{
	// Because we have a flat plane for water we already know the vectors for tangent space
	vec3 normal = gl_Normal;

	// Calculate the vector coming from the vertex to the camera
	vec3 viewDir = cameraPos - gl_Vertex.xyz;

	// Compute tangent space for the view direction
	viewTangetSpace.x = dot(viewDir, tangent);
	viewTangetSpace.y = dot(viewDir, binormal);
	viewTangetSpace.z = dot(viewDir, normal);

	refrCoords = gl_MultiTexCoord0.xy * vec2(0.8) + vec2(0.0,refractionTranslation);
	normCoords = gl_MultiTexCoord0.xy + vec2(0.0,normalTranslation);

	// This calculates our current projection coordinates
	viewCoords = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_Position = viewCoords;
}
