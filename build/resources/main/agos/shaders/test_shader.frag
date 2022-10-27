#version 330 core

uniform sampler2D tex;
uniform float time;

in vec2 texCoords0;

out vec4 fragColor;

void main()
{
    vec4 color = texture2D(tex, texCoords0);

    vec4 weighted = vec4(dot(color.rgb, vec3(.299, .587, .114)));
    fragColor = mix(color, weighted, sin(time));
}