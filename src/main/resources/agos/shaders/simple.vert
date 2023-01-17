#version 330 core

uniform mat4 uProjection;
uniform mat4 uView;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoords;
layout (location = 2) in vec4 texColor;

out vec2 vTexCoords;
out vec4 vTexColor;

void main()
{
    vTexCoords = texCoords;
    vTexColor = texColor;
    gl_Position = uProjection * uView * vec4(position, 1);
}