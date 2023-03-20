#version 330 core

//#define PI 3.14
#define MAX_LIGHTS 50
// 1. tends to be the best, any lower than .8 makes it too bright
// 1.5 is a good level of 'darkness', any higher makes it hard to see
#define DARKNESS 1.5

struct Light
{
    vec2 position;
    vec3 color;
    float radius;
};

uniform vec3 uAmbient = vec3(.05);
uniform vec2 uResolution;

uniform Light uLights[MAX_LIGHTS];
//uniform sampler2D uTexture;

out vec4 outColor;

void main()
{
//    vec2 uv = gl_FragCoord.xy / uResolution;
//    vec3 diffuse = texture(uTexture, uv).rgb / PI;
    vec3 color = uAmbient;

    // Add light colors together
    for (int i = 0; i < uLights.length(); i++)
    {
        if (uLights[i].color == vec3(0.)) break;
        float dist = distance(uLights[i].position, gl_FragCoord.xy);
        float attenuation = 16. * (pow(uLights[i].radius, 2.) / max(.001, pow(dist * DARKNESS, 2.)));

        color += uLights[i].color * attenuation;//diffuse * attenuation
    }

    // outColor = texture(uTexture, inTexCoords) * vec4(color, 1.);
    outColor = vec4(color, 1.);
}