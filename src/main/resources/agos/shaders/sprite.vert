#version 330 core

uniform mat4 projection;
uniform mat4 view;
uniform vec4 uvCoords;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoords;

out vec2 vTexCoords;

void main()
{
    vTexCoords = vec2(clamp(texCoords.x, uvCoords.x, uvCoords.x + uvCoords.z), clamp(texCoords.y, uvCoords.y, uvCoords.y + uvCoords.w));
    gl_Position = projection * view * vec4(position, 1);
}