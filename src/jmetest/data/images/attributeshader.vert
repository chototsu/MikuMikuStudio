attribute vec4 vertexColors;
attribute float vertexLight;

void main(void)
{
    gl_FrontColor = vertexColors * vec4(vertexLight);

    gl_Position = ftransform();
}