package Graphic;

import org.lwjgl.opengl.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * Created by andri on 04-Jun-16.
 */
public class GLObject {

    private int offset;
    private int program;
    private int vertexArraySize;
    private int indicesArraySize;
    private int activeTextureIndex = 0;
    private int currentTexturePack = 0;
    private int lastPackTexture = 1;
    private int animationDelay;

    private int[] VAO;
    private int[] vboArray;
    private int[] textures;

    private boolean textured;
    private boolean animated;
    private boolean animationEnd = false;

    private long drawTime;
    private Vector<Integer> texturesOffsets;

    protected GLObject(GLObjectBuilder builder){
        this.program = builder.program;
        this.vertexArraySize = builder.vertexArrayLength;
        this.indicesArraySize = builder.indicesArraySize;
        this.offset = builder.offset;
        this.VAO = builder.VAO;
        this.textures = builder.textures;
        this.textured = builder.textured;
        this.animated = builder.animated;
        this.animationDelay = builder.animationDelay;
        this.texturesOffsets = builder.texturesOffsets;

        if(!texturesOffsets.isEmpty()) {
            activeTextureIndex = texturesOffsets.get(currentTexturePack);
            lastPackTexture = (currentTexturePack >= texturesOffsets.size() - 1) ? textures.length
                    : texturesOffsets.get(currentTexturePack + 1);
        }
        else lastPackTexture = textures.length;
        drawTime = System.currentTimeMillis();
        ListIterator<Integer> intListIterator = builder.vboList.listIterator();
        vboArray = new int[builder.vboList.size()];
        while (intListIterator.hasNext())
            vboArray[intListIterator.nextIndex()] = intListIterator.next();
    }

    public void draw(){

        // Prepare shader program
        GL20.glUseProgram(program);

        // Binding Object's VAO
        GL30.glBindVertexArray(VAO[0]);

        if(animated){
            if(System.currentTimeMillis() - drawTime >= animationDelay){
                animationEnd = !(activeTextureIndex + 1 < lastPackTexture);
                activeTextureIndex = !animationEnd ? activeTextureIndex + 1
                        : texturesOffsets.isEmpty() ? 0 : texturesOffsets.get(currentTexturePack);
                drawTime = System.currentTimeMillis();
            }
        }

        // Activating texture
        if(textured) {
            //GL13.glActiveTexture(textures[activeTextureIndex]);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[activeTextureIndex]);
        }
        // Drawing by indices
        GL11.glDrawElements(GL11.GL_TRIANGLES, this.indicesArraySize, GL11.GL_UNSIGNED_INT, offset);

        // Unbinding Object's VAO
        GL30.glBindVertexArray (0);
    }

    public void drawAsArray(){

        // Prepare shader program
	    GL20.glUseProgram(program);

        // Binding Object's VAO
        GL30.glBindVertexArray(VAO[0]);

        if(animated){
            if(System.currentTimeMillis() - drawTime >= animationDelay){
                animationEnd = !(activeTextureIndex + 1 < lastPackTexture);
                activeTextureIndex = !animationEnd ? activeTextureIndex + 1
                        : texturesOffsets.isEmpty() ? 0 : texturesOffsets.get(currentTexturePack);
                drawTime = System.currentTimeMillis();
            }
        }

        // Activating texture
        if(textured) {
           // GL13.glActiveTexture(textures[activeTextureIndex]);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[activeTextureIndex]);
        }
        // Drawing by indices
        GL11.glDrawArrays(GL11.GL_TRIANGLES, offset, vertexArraySize);

        // Unbinding Object's VAO
        GL30.glBindVertexArray (0);
    }

    public void finalize(){

        // Clean up device data
        GL20.glDeleteProgram(program);
        GL11.glDeleteTextures(textures);
        GL30.glDeleteVertexArrays(VAO);
        GL15.glDeleteBuffers(vboArray);
    }


    public void setUniformMatrix(float[] data,  String shaderVariableName){
        // Get shader uniform location
        int handle = GL20.glGetUniformLocation(program, shaderVariableName);
        if(handle != -1) {
            GL20.glUseProgram(program);
            GL20.glUniformMatrix4fv(handle, false, data);
        }
    }

    public void setUniformVector(float[] data,  String shaderVariableName){
        int handle = GL20.glGetUniformLocation(program, shaderVariableName);
        if(handle != -1) {
            GL20.glUseProgram(program);

            if(data.length == 2)
                GL20.glUniform2fv(handle, data);
            else if(data.length == 3)
                GL20.glUniform3fv(handle, data);
            else if(data.length == 4)
                GL20.glUniform4fv(handle, data);
        }
    }

    public void setUnifom(float data,  String shaderVariableName){
        int handle = GL20.glGetUniformLocation(program, shaderVariableName);
        if(handle != -1) {
            GL20.glUseProgram(program);
            GL20.glUniform1f(handle, data);
        }
    }

    public void setProgram(int program) {
        this.program = program;
    }

    public void setActiveTextureIndex(int _index){
        this.activeTextureIndex = _index;
    }

    public void setCurrentTexturePack(int _index){
        if(!texturesOffsets.isEmpty()) {
            animationEnd = false;
            currentTexturePack = _index <= texturesOffsets.size() - 1 ? _index : texturesOffsets.size() - 1;
            lastPackTexture = (currentTexturePack >= texturesOffsets.size() - 1) ? textures.length
                    : texturesOffsets.get(currentTexturePack + 1);
            activeTextureIndex = texturesOffsets.get(currentTexturePack);
        }
    }

    public void nextTexturePack(){
        if(!texturesOffsets.isEmpty()) {
            currentTexturePack = currentTexturePack + 1 <= texturesOffsets.size() - 1 ? currentTexturePack + 1 : 0;
            lastPackTexture = (currentTexturePack >= texturesOffsets.size() - 1) ? textures.length
                    : texturesOffsets.get(currentTexturePack + 1);
            activeTextureIndex = texturesOffsets.get(currentTexturePack);
        }
    }

    public boolean isAnimationEnd() {
        return animationEnd;
    }

    public void getVertices(){
        GL30.glBindVertexArray(VAO[0]);
    }

    public int getVertexArraySize() {
        return vertexArraySize;
    }

    public int getIndicesArraySize() {
        return indicesArraySize;
    }

    public int getOffset() {
        return offset;
    }

    public int getActiveTextureIndex() {
        return activeTextureIndex;
    }

    public int getCurrentTexturePack() {
        return currentTexturePack;
    }

    public int getLastPackTexture() {
        return lastPackTexture;
    }

    public boolean isTextured() {
        return textured;
    }

    public boolean isAnimated() {
        return animated;
    }

    public Vector<Integer> getTexturesOffsets(){ return texturesOffsets;}

    public void setTexture(int[] texture) {
        this.textures = texture;
    }

    public void setAnimationDelay(int animationDelay) {
        this.animationDelay = animationDelay;
    }

    public void setTextured(boolean textured) {
        this.textured = textured;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    // Object Builder class
    public static class GLObjectBuilder{


        private int[] VAO = {0};
        private int vertexArrayLength;
        private int indicesArraySize;
        private int program;
        private int offset = 0;
        private int[] textures = new int[0];
        private boolean textured = false;
        private boolean animated = false;
        private List<Integer> vboList = new ArrayList<Integer>();
        private int animationDelay;
        private Vector<Integer> texturesOffsets = new Vector<>(1);

        public GLObjectBuilder(){
            // Creating Object's VAO
           GL30.glGenVertexArrays(VAO);
        }
        
        public GLObjectBuilder(int _vao){
            // Creating Object's VAO
           VAO[0] = _vao;
        }

        // Creating shaders program
        public GLObjectBuilder program(InputStream vShaderStream, InputStream fShaderStream){
            this.program = GLProgram.generateProgram(FileManager.streamToString(vShaderStream),
                    FileManager.streamToString(fShaderStream));

            return this;
        }

        public GLObjectBuilder addProgram(int program){
            this.program = program;
            return this;
        }

        // Adding new VBOs
        public GLObjectBuilder addVBO(GLBufferData buffer){
            BufferLoader.loadArrayToVAO(VAO[0], buffer, vboList);
            return this;
        }

        public GLObjectBuilder addVBO(int[] data){
            this.indicesArraySize = data.length;
            BufferLoader.loadElementToVAO(VAO[0], new GLBufferData(data), vboList);
            return this;
        }

        public GLObjectBuilder addVBO(GLBuffer buffer, int shaderPosition){
            GL30.glBindVertexArray(VAO[0]);
            GL15.glBindBuffer(buffer.getType(), buffer.getId());
            GL20.glEnableVertexAttribArray(shaderPosition);
            GL20.glVertexAttribPointer(shaderPosition, buffer.getStep(), GL11.GL_FLOAT, false, 0, 0);
            GL30.glBindVertexArray (0);

            this.vboList.add(buffer.getId());

            return this;
        }

        public GLObjectBuilder texturedOption(boolean isTextured){
            this.textured = isTextured;
            return this;
        }

        public GLObjectBuilder setUniformMatrix(float[] data,  String shaderVariableName){
            int handle;

            handle = GL20.glGetUniformLocation(program, shaderVariableName);
            if(handle != -1) {
        	GL20.glUseProgram(program);
                GL20.glUniformMatrix4fv(handle, false, data);
            }
            return this;
        }

        public GLObjectBuilder addUniformFv(float[] data, int size,  String shaderVariableName){
            int handle;
            handle = GL20.glGetUniformLocation(program, shaderVariableName);
            if(handle != -1) {
        	GL20.glUseProgram(program);
                if(size == 1)
                    GL20.glUniform1fv(handle, data);
                else if(size == 2)
                    GL20.glUniform2fv(handle, data);
                else if(size == 3)
                    GL20.glUniform3fv(handle, data);
                else if(size == 4)
                    GL20.glUniform4fv(handle, data);
            }
            return this;
        }

        public GLObjectBuilder addTexture(InputStream source) throws IOException{
            if(textures.length != 0)
                textures = Arrays.copyOf(textures, textures.length + 1);
            else textures = new int[1];

            textures[textures.length - 1] = GLTextureLoader.loadTexture(source);
            this.textured = true;
            return this;
        }

        public GLObjectBuilder addTexture(int _texture){
            if(textures.length != 0)
                textures = Arrays.copyOf(textures, textures.length + 1);
            else textures = new int[1];

            textures[textures.length - 1] = _texture;
            this.textured = true;
            return this;
        }

        public GLObjectBuilder addTexture(Vector<Integer> _texture){
            if(_texture.isEmpty()) return this;
            int len = textures.length;
            if(len != 0)
                textures = Arrays.copyOf(textures, textures.length + _texture.size());
            else textures = new int[_texture.size()];

            for(int i = 0; i <_texture.size() - len; i++)
                textures[len + i] = _texture.get(i);
            this.textured = true;
            return this;
        }

        public GLObjectBuilder addOffset(int _offset){
           this.offset = _offset;
            return this;
        }

        public GLObjectBuilder addTexturesOffsets(Vector<Integer> _offsets){
            if(!_offsets.isEmpty())
                this.texturesOffsets = _offsets;
            return this;
        }

        public GLObjectBuilder addVertexArrayLenght(int lenght){
            this.vertexArrayLength = lenght;
            return this;
        }

        public GLObjectBuilder addAnimationDelay(int _delay){
            if(_delay >= 0) {
                this.animationDelay = _delay;
                this.animated = true;
            }
            return this;
        }

        public GLObject buildObject(){
            return new GLObject(this);
        }

    }


    public int getAnimationDelay() {
        return animationDelay;
    }
}