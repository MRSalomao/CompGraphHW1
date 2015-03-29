package fantasy;


import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL42.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class App 
{
	public static App singleton;
	
	static public int canvasWidth = 480, canvasHeight = 360;
	
	public Camera mainCamera;
	FpsCameraHandler fpsCameraHandler;
	
	// Land and water
	Node waterSurface, grassFloor, grassFloorReflect, grassFloorRefract;
	
	// Our trees
	Node treeTrunks, treeLeaves0, treeLeaves1, treeLeaves2, treeLeaves3;
	
	// Rocks and grass patches
	Node rocks, grassPatches;
	
	Shader diffuseShader, waterShader;
	
	OffscreenFBO refractionFBO, reflectionFBO;
	int refractionFBO_ID, reflectionFBO_ID;
	
	int timeLoc;
	float currentTime = 0;
	
	public void init()
	{
		// Setup our camera
		mainCamera = new Camera(new Vector3f(-10,0,10));
		mainCamera.setActive();
		mainCamera.rotation.x = -.70f;
		fpsCameraHandler = new FpsCameraHandler(mainCamera);
		
		// Since we are not using a skybox yet, define solid sky color
		mainCamera.clearScreenColor(0.4f, 0.5f, 0.7f, 1.0f);
		
		// Create our shaders
		diffuseShader  = new Shader("diffuse", false, false, false);
		waterShader = new Shader("water", false, false, true);
		
		// Use to create moving wave effect
		timeLoc = glGetUniformLocation(waterShader.programId, "time");
		
		// Create some nodes to populate our scene
		waterSurface = new Node(new Mesh("waterSurface.mrs"), new Texture("wave2test.png", Texture.DIFFUSE));
		
		grassFloor = new Node(new Mesh("grassFloor.mrs"), new Texture("grass.png", Texture.DIFFUSE));
		grassFloorReflect = new Node(new Mesh("grassFloorReflect.mrs"), new Texture("grass.png", Texture.DIFFUSE));
		grassFloorRefract = new Node(new Mesh("grassFloorRefract.mrs"), new Texture("grass.png", Texture.DIFFUSE));
		
		treeTrunks = new Node(new Mesh("trunks.mrs"), new Texture("BarkPine.png", Texture.DIFFUSE));
		treeLeaves0 = new Node(new Mesh("leaves0.mrs"), new Texture("leaves.png", Texture.DIFFUSE));
		treeLeaves1 = new Node(new Mesh("leaves1.mrs"), new Texture("leaves.png", Texture.DIFFUSE));
		treeLeaves2 = new Node(new Mesh("leaves2.mrs"), new Texture("leaves.png", Texture.DIFFUSE));
		treeLeaves3 = new Node(new Mesh("leaves3.mrs"), new Texture("leaves.png", Texture.DIFFUSE));
		
		rocks = new Node(new Mesh("rocks.mrs"), new Texture("rock.png", Texture.DIFFUSE));
		grassPatches = new Node(new Mesh("grassPatches.mrs"), new Texture("grassSingle.png", Texture.DIFFUSE));
		
		// Create FBO to render refraction
		refractionFBO_ID = glGenTextures();	
		refractionFBO = new OffscreenFBO(canvasWidth, canvasHeight, true);
		refractionFBO.attachTexture(refractionFBO_ID,  GL_RGBA, GL_NEAREST, GL_COLOR_ATTACHMENT0_EXT);
		
		// Create FBO to render reflection
		reflectionFBO_ID = glGenTextures();	
		reflectionFBO = new OffscreenFBO(canvasWidth, canvasHeight, true);
		reflectionFBO.attachTexture(reflectionFBO_ID,  GL_RGBA, GL_NEAREST, GL_COLOR_ATTACHMENT0_EXT);
	}

	public void render(float dt)
	{
		// Update our time counter
		currentTime += dt;
		
		// Process user input and update camera using the fpsHandler
		fpsCameraHandler.update(dt);
		
		// Activate the regular shader
		diffuseShader.setActive();
		
		// Render the refraction
		refractionFBO.bind();
		Camera.activeCamera.clearScreen();
		grassFloorRefract.render(); 
		
		// Render the reflected scene
		reflectionFBO.bind();
		Camera.activeCamera.clearScreen();
		grassFloorReflect.scale.z = -1f;
		grassFloorReflect.render(); 
		
		// Go back to our main FBO
		reflectionFBO.unbind();

		// Render base grass floor
		grassFloor.render();
		
		// Render the water
		waterShader.setActive();
		glActiveTexture(GL_TEXTURE0 + 1);
		glBindTexture(GL_TEXTURE_2D, refractionFBO_ID);
		
		glActiveTexture(GL_TEXTURE0 + 2);
		glBindTexture(GL_TEXTURE_2D, reflectionFBO_ID);
		
		glUniform1f(timeLoc, currentTime);
		waterSurface.render(); 
		
		
		// Go back to regular shader
		diffuseShader.setActive();
		
		// Render rocks and grass patches
		rocks.render();
		grassPatches.render();
		
		// Render our trees - first the trunks
		treeTrunks.render();

		// Render the transparent leaves (bottom to top) 
		glEnable(GL_BLEND);
		glDepthMask(false);
		
		treeLeaves0.render();
		treeLeaves1.render();
		treeLeaves2.render();
		treeLeaves3.render();

		glDepthMask(true);
		glDisable(GL_BLEND);
		
		// If ESC is pressed, exit application
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			System.exit(0);
		}
	}
	

	public App()
	{
		singleton = this;
	}
}
