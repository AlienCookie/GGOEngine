package Graphic;

import java.util.Vector;

/**
 * Created by andri on 19-Sep-16.
 */
public abstract class ObjectLoader {

    protected float[] vertices = new float[0];
    protected float[] textureCoord = new float[0];
    protected float[] normals = new float[0];

    protected Vector<Integer> animationDelay = new Vector<>();
    protected int[] objOffsets = new int[1];

    protected Vector<String> textureSource = new Vector<>();
    protected Vector<String> vertexShaderSource = new Vector<>();
    protected Vector<String> fragmentShaderSource = new Vector<>();
    protected Vector<String> objectName = new Vector<>();
    protected Vector<Float> objCube = new Vector<>();
    protected Vector<Integer> cubeOffsets = new Vector<>();
    protected Vector<Integer> texturesOffsets = new Vector<>();


    public float[] findCube(int objIndex) {
        return new float[6];
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextureCoord() {
        return textureCoord;
    }

    public float[] getNormals() {
        return normals;
    }

    public int[] getObjOffsets() {
        return objOffsets;
    }

    public Vector<Integer> getCubeOffsets() {
        return cubeOffsets;
    }

    public Vector<Integer> getAnimationDelay() {
        return animationDelay;
    }

    public Vector<String> getTextureSource() {
        return textureSource;
    }

    public Vector<String> getVertexShaderSource() {
        return vertexShaderSource;
    }

    public Vector<String> getFragmentShaderSource() {
        return fragmentShaderSource;
    }

    public Vector<String> getObjectName() {
        return objectName;
    }

    public Vector<Float> getObjCube() {
        return objCube;
    }

    public Vector<Integer> getTexturesOffsets() {
        return texturesOffsets;
    }
}
