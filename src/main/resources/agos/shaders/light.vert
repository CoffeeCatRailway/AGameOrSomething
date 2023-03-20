#version 330 core

//uniform mat4 uProjection;
//uniform mat4 uView;

layout (location = 0) in vec3 position;

void main()
{
    gl_Position = vec4(position, 1.);
//    gl_Position = uProjection * uView * vec4(position, 1.);
}