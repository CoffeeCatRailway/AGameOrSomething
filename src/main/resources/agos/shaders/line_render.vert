#version 420 core

uniform mat4 uProjection;
uniform mat4 uView;

layout (location = 0) in vec2 position;

void main()
{
    gl_Position = uProjection * uView * vec4(position, 0., 1.);
}