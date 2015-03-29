#version 330 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;

in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

void main() 
{
	out_Color = texture(texture_diffuse0, pass_TextureCoord1);
}