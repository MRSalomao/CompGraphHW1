package fantasy;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glViewport;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main 
{
	App app;
	
	long lastFrame;
	int fps;
	long lastFPS;
	
	public Main() 
	{
		// Initialize our application
		app = new App();
		
		// Initialize OpenGL (Display)
		setupOpenGL();
		
		// Called once, before running
		App.singleton.init();
		
		// call once before loop to initialize lastFrame
		getDelta(); 
		
		// call before loop to initialize fps timer
		lastFPS = getTime(); 
		
		while (!Display.isCloseRequested()) 
		{
			// Do a single loop (logic/render)
			App.singleton.render(getDelta());
			
			// Update FPS, if logging it
			updateFPS();
			
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}
		
		// Destroy OpenGL (Display)
		this.destroyOpenGL();
	}

	private void setupOpenGL() 
	{
		// Setup an OpenGL context with highest API version available
		try 
		{
			Display.setDisplayMode(new DisplayMode(App.canvasWidth, App.canvasHeight));
			Display.setVSyncEnabled(true); // VSync not enabled by default on Windows
			Display.setTitle("Fantasy scene");
			Display.create();
//			Display.setFullscreen(true);
		} 
		catch (LWJGLException e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
		
		// Enable depth testing and backface culling
		glEnable(GL_DEPTH_TEST);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		// Map the internal OpenGL coordinate system to the entire screen
		glViewport(0, 0, App.canvasWidth, App.canvasHeight);
		
		// default background color: dark gray
		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		
		Utils.exitOnGLError("setupOpenGL");
	}
	
	private void destroyOpenGL() 
	{	
		Display.destroy();
	}
	
	public float getDelta() 
	{
	    long time = getTime();
	    float delta = (float) (time - lastFrame);
	    lastFrame = time;
	 
	    return delta;
	}
	
	public long getTime() 
	{
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public void updateFPS() 
	{
		if (getTime() - lastFPS > 1000) 
		{
			System.out.println("FPS: " + fps);
			Display.setTitle("Fantasy scene  |  " + fps + " frames per second");
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	public static void main(String[] args) 
	{		
		new Main();
	}
}