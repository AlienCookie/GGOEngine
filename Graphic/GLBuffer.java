package Graphic;
import org.lwjgl.opengl.*;


/**
 * Created by andri on 22-Jun-16.
 */
public class GLBuffer {
    private int id;
    private int type;
    private int step;
    private int usage;
    private int dataLenght;
    private int[] objectsOffsets;

    public int getUsage() {
        return usage;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getStep() {
        return step;
    }

    public int getDataLenght() {
        return dataLenght;
    }

    public int[] getObjectsOffsets() {
        return objectsOffsets;
    }

    private GLBuffer(GLBufferBuilder builder){
        this.id = builder.id;
        this.dataLenght = builder.dataLength;
        this.type = builder.bufferType;
        this.step = builder.bufferStep;
        this.usage = builder.bufferUsage;
        this.objectsOffsets = builder.objectsOffsets;
    }

    public static class GLBufferBuilder{
        private int id;
        private int bufferType = GL15.GL_ARRAY_BUFFER;
        private int bufferStep = 3;
        private int bufferUsage = GL15.GL_STATIC_DRAW;
        private int dataLength;
        private int[] objectsOffsets = new int[1];
        private GLBufferData data;

        public GLBufferBuilder(GLBufferData glBufferData){
            this.data = glBufferData;
        }

        public GLBufferBuilder type(int _bufferType){
            this.bufferType = _bufferType;
            return this;
        }

        public GLBufferBuilder step(int _bufferStep){
            this.bufferStep = _bufferStep;
            return this;
        }

        public GLBufferBuilder usage(int _bufferUsage){
            this.bufferUsage = _bufferUsage;
            return this;
        }

        public GLBufferBuilder offsets(int[] _objectsOffsets){
            this.objectsOffsets = _objectsOffsets;
            return this;
        }

        public GLBuffer build (){
            int buffer;

            buffer = GL15.glGenBuffers();

            this.id = buffer;
            this.dataLength = data.getDataLenght();

            GL15.glBindBuffer(this.bufferType, this.id);

            GL15.glBufferData(data.getBufferType(), data.getData(), data.getUsage());

            return new GLBuffer(this);
        }
    }
}
