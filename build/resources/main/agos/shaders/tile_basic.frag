#version 330 core

uniform sampler2D tex;

in vec2 texCoords0;

out vec4 fragColor;

void main()
{
    fragColor = texture2D(tex, texCoords0);
}