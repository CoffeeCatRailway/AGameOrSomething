#version 330 core

uniform mat4 projection;
uniform mat4 view;
uniform vec4 uvCoords;

in vec3 position;
in vec2 texCoords;

out vec2 texCoords0;

void main()
{
    texCoords0 = vec2(clamp(texCoords.x, uvCoords.x, uvCoords.x + uvCoords.z), clamp(texCoords.y, uvCoords.y, uvCoords.y + uvCoords.w));
    gl_Position = projection * view * vec4(position, 1);
}