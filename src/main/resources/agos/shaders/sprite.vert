#version 330 core

uniform mat4 projection;
uniform mat4 view;
uniform vec4 uvCoords;

attribute vec3 position;
attribute vec2 texCoords;

varying vec2 vTexCoords;

void main()
{
    vTexCoords = vec2(clamp(texCoords.x, uvCoords.x, uvCoords.x + uvCoords.z), clamp(texCoords.y, uvCoords.y, uvCoords.y + uvCoords.w));
    gl_Position = projection * view * vec4(position, 1);
}