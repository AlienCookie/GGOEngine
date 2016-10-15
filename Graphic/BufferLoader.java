package Graphic;

import org.lwjgl.opengl.*;

import java.util.List;

/**
 * Created by andri on 04-Jun-16.
 */
public abstract class BufferLoader {

    public static void loadElementToVAO(int targetVAO, GLBufferData glBufferData, List<Integer> vboList){
        int buffer;

        GL30.glBindVertexArray(targetVAO);

        buffer = GL15.glGenBuffers();
        vboList.add(buffer);

        GL15.glBindBuffer(glBufferData.getBufferType(), buffer);
        GL15.glBufferData(glBufferData.getBufferType(), glBufferData.getData(), glBufferData.getUsage());
        GL30.glBindVertexArray (0);
    }

    public static void loadArrayToVAO(int targetVAO, GLBufferData glBufferData, List<Integer> vboList){
        int buffer;

        GL30.glBindVertexArray(targetVAO);

        buffer = GL15.glGenBuffers();
        vboList.add(buffer);

        GL15.glBindBuffer(glBufferData.getBufferType(), buffer);
        GL15.glBufferData(glBufferData.getBufferType(), glBufferData.getData(), glBufferData.getUsage());
        GL20.glEnableVertexAttribArray(glBufferData.getShaderVarRef());
        GL20.glVertexAttribPointer(glBufferData.getShaderVarRef(), glBufferData.getStep(), GL11.GL_FLOAT, false, 0, 0);
        GL30.glBindVertexArray (0);
    }
}
