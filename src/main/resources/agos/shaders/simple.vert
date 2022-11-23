#version 330 core

uniform mat4 projection;
uniform mat4 view;

attribute vec3 position;
attribute vec2 texCoords;

varying vec2 vTexCoords;

void main()
{
    vTexCoords = texCoords;
    gl_Position = projection * view * vec4(position, 1);
}