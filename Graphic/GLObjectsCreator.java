package Graphic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * Created by andri on 02-Aug-16.
 */
public class GLObjectsCreator {

    private static float hW = 720 / 1280f;

    private static float[] mvMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
    };
    private static float[] pMatrix = mvMatrix.clone();


    public static Vector<GLObject.GLObjectBuilder> getGLObjectsBuilders(ObjectLoader glObjectLoader) throws IOException {

        Vector<GLObject.GLObjectBuilder> figures = new Vector<>();

        mvMatrix[0] = hW;

        GLBuffer glVertexBuffer = new GLBuffer
                .GLBufferBuilder(new GLBufferData(glObjectLoader.getVertices()))
                .offsets(glObjectLoader.getObjOffsets())
                .build();

        GLBuffer glTextureCoorBuffer = new GLBuffer
                .GLBufferBuilder(new GLBufferData(glObjectLoader.getTextureCoord()))
                .step(2)
                .build();

        GLBuffer glNormalBuffer = new GLBuffer
                .GLBufferBuilder(new GLBufferData(glObjectLoader.getNormals()))
                .build();


        int[] program = new int[glObjectLoader.getVertexShaderSource().size()];

        for(int i = 0; i < glObjectLoader.getVertexShaderSource().size(); i++){
            InputStream vShaderStream = GLObjectsCreator.class.getClass().getResourceAsStream(glObjectLoader.getVertexShaderSource().get(i));
            InputStream fShaderStream = GLObjectsCreator.class.getClass().getResourceAsStream(glObjectLoader.getFragmentShaderSource().get(i));
            program[i] = GLProgram.generateProgram(FileManager.streamToString(vShaderStream), FileManager.streamToString(fShaderStream));

        }

        figures.setSize(glVertexBuffer.getObjectsOffsets().length);

        Vector<String> texturesSources = (Vector<String>) glObjectLoader.getTextureSource().clone();
        Vector<Integer> texturesOffsets = glObjectLoader.getTexturesOffsets();

        for (int i = 0; i < figures.size(); i++) {

            Vector<Integer> objectTexturesOffsets = new Vector<>();
            Vector<Integer> textures = new Vector<>();
            for(int t = 0; t < texturesSources.size(); t++){
                if(texturesSources.get(t) == "end"){
                    texturesSources.remove(t);
                    break;
                }
                InputStream textureStream = GLObjectsCreator.class.getClass().getResourceAsStream(texturesSources.get(t));
                textures.add(GLTextureLoader.loadTexture(textureStream));
                texturesSources.remove(t);
                t--;
            }

            for(int t = 0; t < texturesOffsets.size(); t++){
                if(texturesOffsets.get(t) == -1){
                    texturesOffsets.remove(t);
                    break;
                }
                objectTexturesOffsets.add(texturesOffsets.get(t));
                texturesOffsets.remove(t);
                t--;
            }

            int arrayLen = (i == figures.size()-1) ? glVertexBuffer.getDataLenght() /12
                    - glVertexBuffer.getObjectsOffsets()[i] :
                    glVertexBuffer.getObjectsOffsets()[i+1]
                            - glVertexBuffer.getObjectsOffsets()[i];

            int offset = glVertexBuffer.getObjectsOffsets()[i];
            figures.setElementAt(new GLObject.GLObjectBuilder()
                    .addProgram(program[i])
                    .addVBO(glVertexBuffer, 0)
                    .addOffset(glVertexBuffer.getObjectsOffsets()[i])
                    .addVertexArrayLenght(arrayLen)
                    .addOffset(offset)
                    .addVBO(glTextureCoorBuffer, 1)
                    .addTexture(textures)
                    .addTexturesOffsets(objectTexturesOffsets)
                    .addAnimationDelay(glObjectLoader.getAnimationDelay().get(i))
                    .addVBO(glNormalBuffer, 2)
                    .setUniformMatrix(mvMatrix, "u_mvMatrix")
                    .setUniformMatrix(pMatrix, "u_pMatrix"), i);
        }

        return figures;
    }

    public static Vector<GLObject.GLObjectBuilder> getGLObjectsBuilders(InputStream objectStream) throws IOException
    {
        GLObjectLoader glObjectLoader = new GLObjectLoader(objectStream);

        return getGLObjectsBuilders(glObjectLoader);
    }
}
