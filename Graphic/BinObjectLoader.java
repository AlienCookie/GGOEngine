package Graphic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 * Created by andri on 08-Aug-16.
 */
public class BinObjectLoader extends ObjectLoader {

    public BinObjectLoader(InputStream fileInputStream) throws IOException {

        byte[] data = new byte[fileInputStream.available()];
        byte[] temp;
        fileInputStream.read(data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int numOfObj = buffer.getInt();
        objOffsets = new int[numOfObj];
        for (int i = 0; i < numOfObj; i++) {
            // Reading name
            temp = new byte[buffer.getInt()];
            buffer.get(temp, 0, temp.length);
            objectName.add(new String(temp));

            // Reading shader
            int k = buffer.getInt();
            temp = new byte[k];
            buffer.get(temp, 0, temp.length);
            vertexShaderSource.add(new String(temp));

            k = buffer.getInt();
            temp = new byte[k];
            buffer.get(temp, 0, temp.length);
            fragmentShaderSource.add(new String(temp));

            // Reading textures
            int texturesNum = buffer.getInt();
            for (int t = 0; t < texturesNum; t++) {
                temp = new byte[buffer.getInt()];
                buffer.get(temp, 0, temp.length);
                textureSource.add(new String(temp));
            }
            textureSource.add("end");

            // Reading animation delay
            animationDelay.add(buffer.getInt());

            // Reading texture offsets
            int offsetsNum = buffer.getInt();
            for (int t = 0; t < offsetsNum; t++)
                texturesOffsets.add(buffer.getInt());
            texturesOffsets.add(-1);

            // Reading object cube
            float[] fBuffer = new float[buffer.getInt()/4];
            buffer.asFloatBuffer().get(fBuffer, 0, fBuffer.length);
            buffer.position(buffer.position() +  fBuffer.length * 4);

            for(float f : fBuffer)
                objCube.add(f);

            cubeOffsets.add(objCube.size());

            // Setting offset
            objOffsets[i] = vertices.length / 3;

            // Reading vertices
            fBuffer = new float[buffer.getInt()/4];
            buffer.asFloatBuffer().get(fBuffer, 0, fBuffer.length);
            buffer.position(buffer.position() +  fBuffer.length * 4);

            int offs = vertices.length;
            vertices = Arrays.copyOf(vertices, vertices.length + fBuffer.length);
            for(int v = 0; v < fBuffer.length; v++)
                vertices[v + offs] = fBuffer[v];

            // Reading normals
            fBuffer = new float[buffer.getInt()/4];
            buffer.asFloatBuffer().get(fBuffer, 0, fBuffer.length);
            buffer.position(buffer.position() +  fBuffer.length * 4);

            offs = normals.length;
            normals = Arrays.copyOf(normals, normals.length + fBuffer.length);
            for(int v = 0; v < fBuffer.length; v++)
                normals[v + offs] = fBuffer[v];

            // Reading textures
            fBuffer = new float[buffer.getInt()/4];
            buffer.asFloatBuffer().get(fBuffer, 0, fBuffer.length);
            buffer.position(buffer.position() +  fBuffer.length * 4);

            offs = textureCoord.length;
            textureCoord = Arrays.copyOf(textureCoord, textureCoord.length + fBuffer.length);
            for(int v = 0; v < fBuffer.length; v++)
                textureCoord[v + offs] = fBuffer[v];

        }

    }
}
