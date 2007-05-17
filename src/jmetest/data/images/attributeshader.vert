attribute vec4 vertexColors;
attribute float vertexOffset;

void main(void)
{
    gl_FrontColor = vertexColors;

    gl_Position = gl_ModelViewProjectionMatrix * (gl_Vertex + vec4(gl_Normal, 0.0) * vec4(vec3(vertexOffset), 0.0) );
}