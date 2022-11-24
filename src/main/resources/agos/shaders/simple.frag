#version 330 core

uniform sampler2D tex;

in vec2 vTexCoords;
in vec4 vTexColor;

out vec4 outColor;

void main()
{
    outColor = vTexColor * texture(tex, vTexCoords);
//    outColor = vec4(vTexCoords, 0, 1);
}