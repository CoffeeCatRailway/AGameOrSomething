#version 330 core

uniform mat4 projection;
uniform mat4 view;

in vec3 position;
in vec2 texCoords;

out vec2 texCoords0;

void main()
{
    texCoords0 = texCoords;
    gl_Position = projection * view * vec4(position, 1);
}