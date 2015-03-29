#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 camPos;

in vec4 in_Position;
in vec4 in_Normal;
in vec2 in_TextureCoord1;
in vec2 in_TextureCoord2;

out vec4 pass_Color;
out vec2 pass_TextureCoord1;
out vec2 pass_TextureCoord2;
out vec3 vertPos;
out vec3 v_eyePosition;

void main(void) 
{
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * in_Position;
	
	vertPos = gl_Position.xyz / gl_Position.w;
	
	//pass_Color = in_Normal;
	pass_TextureCoord1 = in_TextureCoord1;
	pass_TextureCoord2 = in_TextureCoord2;
	
	v_eyePosition = camPos - (modelMatrix * in_Position).xyz;
}