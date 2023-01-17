#version 330 core

uniform sampler2D uTexture;

in vec2 vTexCoords;
in vec4 vTexColor;

out vec4 outColor;

void main()
{
    outColor = vTexColor * texture(uTexture, vTexCoords);
}