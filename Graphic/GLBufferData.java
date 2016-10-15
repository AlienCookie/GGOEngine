package Graphic;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by andri on 04-Jun-16.
 */
public class GLBufferData {
    private int bufferType = GL15.GL_ARRAY_BUFFER;
    private FloatBuffer data;
    private int shaderVarRef = 0;
    private int usage = GL15.GL_STATIC_DRAW;
    private int dataLenght;
    private int step = 3;
    private int type = 0;

    public GLBufferData(float[] arrayData){
        dataLenght = arrayData.length * Float.SIZE/8;
        data = toFloatBuffer(arrayData);
    }

    public GLBufferData(int[] arrayData){
	type = 1;
        dataLenght = arrayData.length * Integer.SIZE/8;
        //data = toFloatBuffer(arrayData);
        bufferType = GL15.GL_ELEMENT_ARRAY_BUFFER;
    }

    public GLBufferData(float[] arrayData, int shaderVarRef){
        dataLenght = arrayData.length * Float.SIZE/8;
        data = toFloatBuffer(arrayData);
        this.shaderVarRef = shaderVarRef;
    }

    public GLBufferData(int[] arrayData, int shaderVarRef){
	type = 1;
        dataLenght = arrayData.length * Integer.SIZE/8;
        //data = toByteBuffer(arrayData);
        this.shaderVarRef = shaderVarRef;
        bufferType = GL15.GL_ELEMENT_ARRAY_BUFFER;
    }

    public GLBufferData(float[] arrayData, int shaderVarRef, int step){
        dataLenght = arrayData.length * Float.SIZE/8;
        data = toFloatBuffer(arrayData);
        this.shaderVarRef = shaderVarRef;
        this.step = step;
    }

    public GLBufferData(int[] arrayData, int shaderVarRef, int step){
	type = 1;
        dataLenght = arrayData.length * Integer.SIZE/8;
        //data = toByteBuffer(arrayData);
        this.shaderVarRef = shaderVarRef;
        this.step = step;
        bufferType = GL15.GL_ELEMENT_ARRAY_BUFFER;
    }


    private IntBuffer toIntBuffer(int[] data){
        IntBuffer buffer = IntBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    
    private ByteBuffer toByteBuffer(float[] data){
	ByteBuffer byteBuffer = ByteBuffer.allocate(4 * data.length);

        for (float value : data){
            byteBuffer.putFloat(value);
        }
        byteBuffer.flip();
        return byteBuffer;
    }
    
    private ByteBuffer toByteBuffer(int[] data){
	ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * Integer.SIZE/8);
	byteBuffer.asIntBuffer().put(data);
        return byteBuffer;
    }

    private FloatBuffer toFloatBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public int getDataLenght() {
        return dataLenght;
    }

    public void setDataLenght(int dataLenght) {
        this.dataLenght = dataLenght;
    }

    public int getUsage() {
        return usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public FloatBuffer getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    
    public void setData(FloatBuffer data) {
        this.data = data;
    }

    public int getShaderVarRef() {
        return shaderVarRef;
    }

    public void setShaderVarRef(int shaderVarRef) {
        this.shaderVarRef = shaderVarRef;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getBufferType() {
        return bufferType;
    }

    public void setBufferType(int bufferType) {
        this.bufferType = bufferType;
    }
}
