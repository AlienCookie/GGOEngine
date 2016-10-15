package Editor;

import Graphic.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by andri on 02-Aug-16.
 */
public class EditorObjectsCreator {

    // Default model view matrix
    private static float[] mvMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
    };

    public static Vector<GLEditorObject> getGLObjectsFromSource(String source) throws IOException {

        // Get file extension
        String extension = source.substring(source.length()-4, source.length());

        // Calling different loaders depending of extension
        if(extension.equals(".obj"))
            return getObjectsBuilders(new GLObjectLoader(EditorObjectsCreator.class.getResourceAsStream(source)));
        else if(extension.equals(".ggo"))
            return  getObjectsBuilders(new BinObjectLoader(EditorObjectsCreator.class.getResourceAsStream(source)));
        return null;
    }

    public static Vector<GLEditorObject> getObjectsBuilders(ObjectLoader glObjectLoader) throws IOException
    {
        // First get base (glObjects builders)
        Vector<GLEditorObject.GLObjectBuilder> figureBuilder = GLObjectsCreator.getGLObjectsBuilders(glObjectLoader);

        Vector<GLEditorObject> figures = new Vector<>();

        // To get texture sources
        Vector<String> texturesSources = glObjectLoader.getTextureSource();

        for (int i = 0; i < figureBuilder.size(); i++) {

            // Textures sources for particular object
            Vector<String> objectTextureSources = new Vector<>();

            for(int t = 0; t < texturesSources.size(); t++){
                if(texturesSources.get(t) == "end"){
                    texturesSources.remove(t);
                    break;
                }
                objectTextureSources.add(texturesSources.get(t));
                texturesSources.remove(t);
                t--;
            }

            // Adding new figure
            figures.add(new GLEditorObject(figureBuilder.get(i)));

            GLEditorObject activeObject = figures.get(i);

            // Setting up figure bounding box (to calculate intersection)
            int boxBegin = (i == 0) ? 0 : glObjectLoader.getCubeOffsets().get(i-1);
            int boxLength = glObjectLoader.getCubeOffsets().get(i) - boxBegin;
            for(int t = 0; t < boxLength/24; t++)
                activeObject.addTouchBox(new TouchBox(glObjectLoader.getObjCube()
                        .subList(boxBegin + t*24, boxBegin + t*24 + 24)));

            // Setting up additional params (name, v-f shader sources, etc.)
            activeObject.setObjectName(glObjectLoader.getObjectName().get(i));
            activeObject.setVShaderSource(glObjectLoader.getVertexShaderSource().get(i));
            activeObject.setFShaderSource(glObjectLoader.getFragmentShaderSource().get(i));
            activeObject.setTextureSource(objectTextureSources);

            // We keep all copy of all vertices and indices in user space memory
            // (for saving object in future)
            activeObject.setClientVerticesData(Arrays.copyOfRange(glObjectLoader.getVertices(),
                    activeObject.getOffset()*3, (activeObject.getOffset() + activeObject.getVertexArraySize())*3));
            activeObject.setClientNormalsData(Arrays.copyOfRange(glObjectLoader.getNormals(),
                    activeObject.getOffset()*3, (activeObject.getOffset() + activeObject.getVertexArraySize())*3));
            activeObject.setClientTexturesData(Arrays.copyOfRange(glObjectLoader.getTextureCoord(),
                    activeObject.getOffset()*2, (activeObject.getOffset() + activeObject.getVertexArraySize())*2));
        }
        return figures;
    }
}
