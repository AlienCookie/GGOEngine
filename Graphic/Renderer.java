package Graphic;

import org.lwjgl.opengl.*;

/**
 * Created by andri on 04-Jun-16.
 */
public class Renderer {

    int PosX = 0, PosY = 0;
    int WIDTH = 640, HEIGHT = 360;

    public Renderer(int posX, int posY, int width, int height){
        PosX = posX;
        PosY = posY;
        WIDTH = width;
        HEIGHT = height;

        GL11.glViewport(PosX, PosY, WIDTH, HEIGHT);
        GL11.glScissor(PosX, PosY, WIDTH, HEIGHT);

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable ( GL11.GL_BLEND);
        GL11.glBlendFunc ( GL11.GL_SRC_ALPHA,  GL11.GL_ONE_MINUS_SRC_ALPHA);
        
    }

    
    public void onSurfaceChanged(int width, int height) {
	    GL11.glViewport(0, 0, width, height);
    }

    public void onPrepareDraw() {
        GL11.glScissor(PosX, PosY, WIDTH, HEIGHT);
        GL11.glViewport(PosX, PosY, WIDTH, HEIGHT);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void onPostDraw() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable ( GL11.GL_BLEND);
    }
    
    public void onPause(){}

    public void onResume(){}

    public void cleanUp(){}
}