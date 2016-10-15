package Editor;

import GUI.EditorUI;
import GUI.NuklearGUI;
import Gameplay.ObjectsManager;
import Graphic.*;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andri on 02-Aug-16.
 */
public class GameEditor {
    private List<GLEditorObject> gameObject = new ArrayList();
    private Renderer gameRenderer;
    private NuklearGUI nuklearGUI;
    private ObjectsManager objectsManager;
    private EditorUI editorUI;
    private Camera camera;

    private static int SCENE_HEIGHT = 480;
    private static int SCENE_WIDTH = 853;

    public GameEditor(long win){
        // Getting window params
        int[] WIDTH = new int[1];
        int[] HEIGHT = new int[1];
        GLFW.glfwGetWindowSize(win, WIDTH, HEIGHT);

        // Init render
        gameRenderer = new Renderer(30, HEIGHT[0] - 520, SCENE_WIDTH, SCENE_HEIGHT);

        // Init camera
        camera = new Camera();

        // Init UI
        nuklearGUI = new NuklearGUI(win);
        editorUI = new EditorUI(nuklearGUI.getCtx(), this);

        // Object manager to test changes
        objectsManager = new ObjectsManager();
    }

    public void draw(){

        // Drawing GUI
        nuklearGUI.prepareToRender();
        editorUI.layout(900, 30);
        nuklearGUI.render();

        gameRenderer.onPrepareDraw();


        // Camera update
        camera.move();

        if(editorUI.getMode())
            objectsManager.draw();

        else {
            // Drawing our editor scene
            for (GLEditorObject ob : gameObject) {
                if (editorUI.getActiveObject().contains(ob))
                    ob.drawTouchBox();
                ob.updateProjectMatrix();
                ob.drawAsArray();
            }
        }

        gameRenderer.onPostDraw();
    }



    public void addObject(String source){
        try {
            // Adding new object to object list
            gameObject.addAll(EditorObjectsCreator.getGLObjectsFromSource(source));
            gameObject.forEach(GLEditorObject::updateProjectMatrix);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveObject(String fileName){


        int charSizeInBytes = 1;//Character.SIZE;
        int intSizeInBytes = Integer.SIZE / 8;
        int floatSizeInBytes = Float.SIZE / 8;
        int bufferSize = intSizeInBytes; // for saving amount of objects

        List<GLEditorObject> toSave = editorUI.getActiveObject();

        bufferSize += toSave.size() * intSizeInBytes;// * intSizeInBytes; // for saving offset and animation time

        // Configuring how many bytes needs to save all objects
        for(GLEditorObject obj : toSave){
            bufferSize += obj.getObjectName().length() * charSizeInBytes;
            bufferSize += obj.getFShaderSource().length() * charSizeInBytes;
            bufferSize += obj.getVShaderSource().length() * charSizeInBytes;
            bufferSize += obj.getAllBoxes().length * floatSizeInBytes;

            bufferSize += intSizeInBytes * 4; // for saving lengths of items above

            bufferSize += intSizeInBytes; // for saving texture amount
            for (String texture : obj.getTextureSource())
                bufferSize += texture.length() * charSizeInBytes + intSizeInBytes;

            bufferSize += intSizeInBytes; //for saving texture packs amount
            bufferSize += obj.getTexturesOffsets().size() * intSizeInBytes;

            // Adding VBOs data
            bufferSize += obj.getClientVerticesData().length * floatSizeInBytes;
            bufferSize += obj.getClientNormalsData().length * floatSizeInBytes;
            bufferSize += obj.getClientTexturesData().length * floatSizeInBytes;

            bufferSize += intSizeInBytes * 3; // for saving lengths of VBOs
        }

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        buffer.putInt(toSave.size());

        for(GLEditorObject obj : toSave) {

            // Adding Name
            buffer.putInt(obj.getObjectName().length() * charSizeInBytes);
            buffer.put(obj.getObjectName().getBytes());

            // Adding Shader Sources
            buffer.putInt(obj.getVShaderSource().length() * charSizeInBytes);
            buffer.put(obj.getVShaderSource().getBytes()); // vertex shader

            buffer.putInt(obj.getFShaderSource().length() * charSizeInBytes);
            buffer.put(obj.getFShaderSource().getBytes()); // fragment shader

            // Adding textures
            buffer.putInt(obj.getTextureSource().size()); // textures amount

            for (String texture : obj.getTextureSource()) {
                buffer.putInt(texture.length() * charSizeInBytes);
                buffer.put(texture.getBytes());
            }

            // Adding animation delay
            buffer.putInt(obj.getAnimationDelay());

            // Adding textures offsets
            buffer.putInt(obj.getTexturesOffsets().size()); // textures offsets amount
            for (Integer packOffset : obj.getTexturesOffsets())
                buffer.putInt(packOffset);

            // Adding object cube
            buffer.putInt( obj.getAllBoxes().length * floatSizeInBytes);
            buffer.asFloatBuffer().put( obj.getAllBoxes());   // we need to update cube according to project matrix
            buffer.position(buffer.position()
                    + buffer.getInt(buffer.position() - intSizeInBytes)); // increase position to length of item

            // Updating coordinates according to project matrix
            obj.updateCoordinates();

            // Adding vertex data
            buffer.putInt(obj.getClientVerticesData().length * floatSizeInBytes);
            buffer.asFloatBuffer().put(obj.getClientVerticesData());
            buffer.position(buffer.position()
                    + buffer.getInt(buffer.position() - intSizeInBytes)); // increase position to length of item

            // Adding normals data
            buffer.putInt(obj.getClientNormalsData().length * floatSizeInBytes);
            buffer.asFloatBuffer().put(obj.getClientNormalsData());
            buffer.position(buffer.position()
                    + buffer.getInt(buffer.position() - intSizeInBytes)); // increase position to length of item

            // Adding textures data
            buffer.putInt(obj.getClientTexturesData().length * floatSizeInBytes);
            buffer.asFloatBuffer().put(obj.getClientTexturesData());
            buffer.position(buffer.position()
                    + buffer.getInt(buffer.position() - intSizeInBytes)); // increase position to length of item
        }
        buffer.flip();

        FileOutputStream file = null;
        try {
            file = new FileOutputStream("C:\\Users\\andri\\IdeaProjects\\GameEngine2.0\\src\\res\\" + fileName + ".ggo");
            file.write(buffer.array());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inputManaging() {
        nuklearGUI.inputManaging();
    }

    public void prepareInput() {
        nuklearGUI.prepareInput();
    }

    public List<GLEditorObject> getGameObject() {
        return gameObject;
    }
}
