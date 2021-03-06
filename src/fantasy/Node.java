package fantasy;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Node 
{
	public Matrix4f modelMatrix;
	public Vector3f position;
	public Vector3f rotation;
	public Vector3f scale;
	
	FloatBuffer matrix44Buffer;
	
	Mesh mesh;
	Shader shader;
	Texture texture0, texture1;
	
	public Node(Mesh mesh, Texture texture0)
	{
		this.mesh = mesh;
		this.texture0 = texture0;
		
		// Initial state variables
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		scale    = new Vector3f(1, 1, 1);

		// Setup node matrix
		modelMatrix = new Matrix4f();
		
		// Create a FloatBuffer - used to upload matrix uniforms
		matrix44Buffer = BufferUtils.createFloatBuffer(16);
	}
	
	public void setLightmap(Texture lightmap)
	{
		this.texture1 = lightmap; 
	}
	
	
	public void render()
	{
		Matrix4f.setIdentity(modelMatrix);
		
		// Scale, translate and rotate model
		Matrix4f.translate(position, modelMatrix, modelMatrix);
		Matrix4f.scale(scale, modelMatrix, modelMatrix);
		Matrix4f.rotate(rotation.z, new Vector3f(0, 0, 1), modelMatrix, modelMatrix);
		Matrix4f.rotate(rotation.y, new Vector3f(0, 1, 0), modelMatrix, modelMatrix);
		Matrix4f.rotate(rotation.x, new Vector3f(1, 0, 0), modelMatrix, modelMatrix);
		
		// Upload modelMatrix
		modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		glUniformMatrix4(Shader.activeProgram.modelMatrixLocation, false, matrix44Buffer);

		texture0.setActive();
		if (texture1 != null) texture1.setActive();
		
		mesh.render();
	}
	
	public void dispose()
	{
		
	}
}
