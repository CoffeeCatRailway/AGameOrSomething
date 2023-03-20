#version 330 core

#define MAX_LIGHTS 32
#define MAX_BOXES 32
#define ANTI_ALIASING 0
#define PIXELATE 0
#define PIXEL_SIZE 4.

struct Light
{
    vec2 position;
    vec3 color;
    float min;
    float max;
    float brightness;
};

// Used to define lines & boxes
struct Line {
    vec2 start;
    vec2 end;
};

uniform vec4 uAmbient = vec4(0.);
uniform vec2 uResolution;
uniform Light uLights[MAX_LIGHTS];
uniform Line uBoxes[MAX_BOXES];
uniform sampler2D uTexture;
out vec4 outColor;

float hyperstep(float min, float max, float x)
{
    if (x < min)
        return 1.;
    else if (x > max)
        return 0.;
    else
    {
        //linear interpolation of x between min and max
        float value = (x - min) / (max - min);

        //hyperbolic function: 100 / 99 * (9x + 1)^2 - 1 / 99
        return (100. / 99.) / ((9. * value + 1.) * (9. * value + 1.)) - (1. / 99.);
    }
}

vec2 intersectPoint(Line line_0, Line line_1)
{
    float slope_0, slope_1, x, y;

    if (line_0.start.x == line_0.end.x)
    {
        //slope_0 is infinite
        slope_1 = (line_1.start.y - line_1.end.y) / (line_1.start.x - line_1.end.x);

        x = line_0.start.x;
        y = slope_1 * x + line_1.start.y;
    } else if (line_1.start.x == line_1.end.x)
    {
        //slope_1 is infinite
        slope_0 = (line_0.start.y - line_0.end.y) / (line_0.start.x - line_0.end.x);

        x = line_1.start.x;
        y = slope_0 * (x - line_0.start.x) + line_0.start.y;
    } else
    {
        slope_0 = (line_0.start.y - line_0.end.y) / (line_0.start.x - line_0.end.x);
        slope_1 = (line_1.start.y - line_1.end.y) / (line_1.start.x - line_1.end.x);

        if (slope_0 != slope_1)
        {
            //calculate y-intercept of line_1 based on line_0.start
            float b= slope_1 * (line_0.start.x - line_1.start.x) + line_1.start.y;

            x = (b - line_0.start.y) / (slope_0 - slope_1);
            y = slope_0 * x + line_0.start.y;
            x = x + line_0.start.x;
        }
        //lines are parallel
        else
            return vec2(-1.);
    }

    return vec2(x, y);
}

bool inside(Line box, vec2 point)
{
    vec2 minValues = vec2(min(box.start.x, box.end.x), min(box.start.y, box.end.y));
    vec2 maxValues = vec2(max(box.start.x, box.end.x), max(box.start.y, box.end.y));

    if (point.x < minValues.x) return false;
    if (point.x > maxValues.x) return false;
    if (point.y < minValues.y) return false;
    if (point.y > maxValues.y) return false;
    return true;
}

bool intersects(Line a, Line b)
{
    vec2 point = intersectPoint(a, b);
    return inside(a, point) && inside(b, point);
}

vec3 calculateLighting(vec2 pixel, Light light)
{
    Line LoS = Line(pixel, light.position);

    for (int i = 0; i < uBoxes.length(); i++)
    {
        Line box = uBoxes[i];
        if (intersects(LoS, Line(box.start, vec2(box.end.x, box.start.y))) ||
            intersects(LoS, Line(box.start, vec2(box.start.x, box.end.y))) ||
            intersects(LoS, Line(box.end, vec2(box.start.x, box.end.y))) ||
            intersects(LoS, Line(box.end, vec2(box.end.x, box.start.y))))
            return vec3(0.);
    }

    return hyperstep(light.min, light.max,  distance(pixel, light.position)) * light.brightness * light.color;
}

vec3 sampleLights(vec3 color, vec2 pixel)
{
    for (int i = 0; i < uLights.length(); i++)
        if (uLights[i].color != vec3(0.))
            color += calculateLighting(pixel, uLights[i]);
    return color;
}

vec3 multisample(vec2 pixel)
{
    vec2 offset = (1. / uResolution) * vec2(.5, .7);
    vec2 points[4];
    points[0] = pixel + vec2(offset.x, offset.y);
    points[1] = pixel + vec2(-offset.x, -offset.y);
    points[2] = pixel + vec2(offset.y, -offset.x);
    points[3] = pixel + vec2(-offset.y, -offset.x);

    vec3 color = vec3(0.);
    for (int i= 0; i < 4; i++)
        color = sampleLights(color, points[i]);
    return color / 4.;
}

void main() {
    vec2 pixel = gl_FragCoord.xy / uResolution;
    vec3 color = uAmbient.rgb;

    if (PIXELATE == 1)
    {
        float sizeX = 1. / uResolution.x;
        float sizeY = 1. / uResolution.y;

        float cellSizeX = PIXEL_SIZE * sizeX;
        float cellSizeY = PIXEL_SIZE * sizeY;

        float x = cellSizeX * floor(pixel.x / cellSizeX);
        float y = cellSizeY * floor(pixel.y / cellSizeY);
        pixel = vec2(x, y);
    }

    if (ANTI_ALIASING == 1)
        color += multisample(pixel);
    else
        color = sampleLights(color, pixel);

    //box color
//    for (int i = 0; i < uBoxes.length(); i++)
//        if (inside(uBoxes[i], pixel))
//            color = vec3(.25, .4, 0.);

    outColor = vec4(color, 1.) * (texture(uTexture, pixel) + uAmbient);
}
