#version 330 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;
uniform sampler2D texture_diffuse2;
uniform float time;

in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;
in vec3 vertPos;
in vec3 v_eyePosition;

const vec4 tangent = vec4(1.0, 0.0, 0.0, 0.0);
const vec4 viewNormal = vec4(0.0, 0.0, 1.0, 0.0);
const vec4 bitangent = vec4(0.0, 1.0, 0.0, 0.0);

out vec4 out_Color;

void main() 
{
	vec4 normTex = texture(texture_diffuse0, pass_TextureCoord1*2+ vec2(0,time*.00002));
	vec4 normal = normalize(normTex*2.0-1.0);
		
	vec4 refraction = texture(texture_diffuse1, gl_FragCoord.xy/vec2(480,360)+normTex.xy*.01) * vec4(.3,.4,.7,1.0);
	
	vec4 reflection = texture(texture_diffuse2, gl_FragCoord.xy/vec2(480,360)+normTex.xy*.01);

	// Move to tangent space
	vec4 viewDir = normalize(vec4(v_eyePosition, 1.0));
	vec4 viewTanSpace = normalize(vec4(dot(viewDir, tangent), dot(viewDir, bitangent), dot(viewDir, viewNormal), 1.0));	
	vec4 viewReflection = normalize(reflect(-1.0 * viewTanSpace, normal));
	
	// Calculate fresnel and specular coefs.
	float fresnel = dot(normal, viewReflection);	
	vec3 specular = vec3(clamp(pow(dot(viewTanSpace, normal), 128), 0.0, 1.0));
	
	// Final Color
	out_Color = mix(reflection, refraction, fresnel) + vec4(specular, 1.0)*.3;
} 