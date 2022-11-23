#version 330 core

uniform sampler2D tex;

varying vec2 vTexCoords;

void main()
{
    gl_FragColor = texture2D(tex, vTexCoords);
}