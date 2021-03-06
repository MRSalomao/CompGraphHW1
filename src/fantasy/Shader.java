package fantasy;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL13.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.ARBShaderObjects;

public class Shader 
{	
	int projectionMatrixLocation, viewMatrixLocation, modelMatrixLocation;
	
	static int positionAttrib=0, normalAttrib=1, textureAttrib1=2, textureAttrib2=3;
	
	static Shader activeProgram;
	
	public int programId;
	
	public int tex0Location, tex1Location, tex2Location, imageTexLocation;
	
	public int camPosLocation;
	
	boolean useTex1, usesCamPos;
	
	public Shader(String shaderName, boolean hasNormals, boolean hasVertColors, boolean usesCamPos) 
	{		
		this.usesCamPos = usesCamPos;
		
		int vsId = this.loadShader("assets/shaders/" + shaderName + ".vert", GL_VERTEX_SHADER);
		int fsId = this.loadShader("assets/shaders/" + shaderName + ".frag", GL_FRAGMENT_SHADER);
		
		// Create a new shader program and link both shaders
		programId = glCreateProgram();
		glAttachShader(programId, vsId);
		glAttachShader(programId, fsId);
		
		glBindAttribLocation(programId, positionAttrib, "in_Position");
		glBindAttribLocation(programId, normalAttrib, "in_Normal");
		glBindAttribLocation(programId, textureAttrib1, "in_TextureCoord1");
		glBindAttribLocation(programId, textureAttrib2, "in_TextureCoord2");

		glLinkProgram(programId);
		if (ARBShaderObjects.glGetObjectParameteriARB(programId, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL_FALSE) {
            System.err.println(getLogInfo(programId));
            return;
        }
		
		glValidateProgram(programId);
		if (ARBShaderObjects.glGetObjectParameteriARB(programId, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL_FALSE) {
        	System.err.println(getLogInfo(programId));
        	return;
        }
		
		// Get matrices uniform locations
		projectionMatrixLocation = glGetUniformLocation(programId,"projectionMatrix");
		viewMatrixLocation = glGetUniformLocation(programId, "viewMatrix");
		modelMatrixLocation = glGetUniformLocation(programId, "modelMatrix");
		
		tex0Location = glGetUniformLocation(programId, "texture_diffuse0");
		tex1Location = glGetUniformLocation(programId, "texture_diffuse1");
		tex2Location = glGetUniformLocation(programId, "texture_diffuse2");
		
		if (usesCamPos) camPosLocation = glGetUniformLocation(programId, "camPos");
		
		imageTexLocation = glGetUniformLocation(programId, "output_buffer");

		Utils.exitOnGLError("setupShaders");
	}
	
	public void setActive()
	{
		activeProgram = this;
		
		glUseProgram(programId);
		
		Camera.activeCamera.projectionMatrix.store(Camera.activeCamera.matrix44Buffer); Camera.activeCamera.matrix44Buffer.flip();
		glUniformMatrix4(projectionMatrixLocation, false, Camera.activeCamera.matrix44Buffer);

		Camera.activeCamera.viewMatrix.store(Camera.activeCamera.matrix44Buffer); Camera.activeCamera.matrix44Buffer.flip();
		glUniformMatrix4(viewMatrixLocation, false, Camera.activeCamera.matrix44Buffer);
		
		glUniform1i(tex0Location, 0);
		glUniform1i(tex1Location, 1);
		glUniform1i(tex2Location, 2);
		glUniform1i(imageTexLocation, 3);
		
		if (usesCamPos) glUniform3f(camPosLocation, Camera.activeCamera.position.x, 
													Camera.activeCamera.position.y,
													Camera.activeCamera.position.z);

		Utils.exitOnGLError("Shader-setActive");
	}
	
	private int loadShader(String filename, int type) 
	{
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) 
			{
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} 
		catch (IOException e) 
		{
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) 
		{
			System.err.println("Could not compile shader: " + filename + ": \n" + getLogInfo(shaderID));
			System.exit(-1);
		}
		
		Utils.exitOnGLError("loadShader");
		
		return shaderID;
	}
	
    private static String getLogInfo(int obj) 
    {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
	
	public void dispose()
	{
		
	}
}
