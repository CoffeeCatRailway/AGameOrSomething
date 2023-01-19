#version 330 core

uniform vec3 uColor = vec3(1., 0., 0.);

out vec4 fragColor;

void main()
{
    fragColor = vec4(uColor, 1.);
}