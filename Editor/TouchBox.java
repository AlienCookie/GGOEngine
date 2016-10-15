package Editor;

import Graphic.FileManager;
import Graphic.GLProgram;
import ToolBox.Matrix;
import Graphic.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

/**
 * Created by andri on 30-Aug-16.
 */
public class TouchBox {
    private static float hW = 720 / 1280f;
    int VAO;
    int buffer;
    int program;
    int vShader;
    int fShader;

    float[] angle = {0.f, 0.f, 0.f};

    float[] vertices;
    private float[] mvMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
    };

    private float[] pMatrix = mvMatrix.clone(), scaleMoveM = mvMatrix.clone(),
            pointRotationM = mvMatrix.clone(), tempM = mvMatrix.clone(),
            RotationM = mvMatrix.clone(), xRotationM = mvMatrix.clone(),
            yRotationM = mvMatrix.clone(), zRotationM = mvMatrix.clone(),
            cameraPrM = mvMatrix.clone(), additionalPMatrix = mvMatrix.clone(),
            mainPMatrix = mvMatrix.clone();


    public TouchBox(float[] _vertices){
        initialize(_vertices);
    }

    public TouchBox(List<Float> _vertices){
        float[] vertexArray = new float[_vertices.size()];
        for(int i = 0; i < _vertices.size(); i++)
            vertexArray[i] = _vertices.get(i);
        initialize(vertexArray);
    }

    public TouchBox(List<Float> _vertices, float[] _additionalPMatrix){
        float[] vertexArray = new float[_vertices.size()];
        for(int i = 0; i < _vertices.size(); i++)
            vertexArray[i] = _vertices.get(i);
        initialize(vertexArray);
        this.additionalPMatrix = _additionalPMatrix;
    }

    private void initialize(float[] boxCoordinates){

       float[] glBoxCoordinates = new float[]{
                boxCoordinates[0], boxCoordinates[1], boxCoordinates[2],
                boxCoordinates[3], boxCoordinates[4], boxCoordinates[5],

                boxCoordinates[3], boxCoordinates[4], boxCoordinates[5],
                boxCoordinates[6], boxCoordinates[7], boxCoordinates[8],

                boxCoordinates[6], boxCoordinates[7], boxCoordinates[8],
                boxCoordinates[9], boxCoordinates[10], boxCoordinates[11],

                boxCoordinates[9], boxCoordinates[10], boxCoordinates[11],
                boxCoordinates[0], boxCoordinates[1], boxCoordinates[2],


                boxCoordinates[12], boxCoordinates[13], boxCoordinates[14],
                boxCoordinates[15], boxCoordinates[16], boxCoordinates[17],

                boxCoordinates[15], boxCoordinates[16], boxCoordinates[17],
                boxCoordinates[18], boxCoordinates[19], boxCoordinates[20],

                boxCoordinates[18], boxCoordinates[19], boxCoordinates[20],
                boxCoordinates[21], boxCoordinates[22], boxCoordinates[23],

                boxCoordinates[21], boxCoordinates[22], boxCoordinates[23],
                boxCoordinates[12], boxCoordinates[13], boxCoordinates[14],


                boxCoordinates[0], boxCoordinates[1], boxCoordinates[2],
                boxCoordinates[12], boxCoordinates[13], boxCoordinates[14],

                boxCoordinates[3], boxCoordinates[4], boxCoordinates[5],
                boxCoordinates[15], boxCoordinates[16], boxCoordinates[17],

                boxCoordinates[6], boxCoordinates[7], boxCoordinates[8],
                boxCoordinates[18], boxCoordinates[19], boxCoordinates[20],

                boxCoordinates[9], boxCoordinates[10], boxCoordinates[11],
                boxCoordinates[21], boxCoordinates[22], boxCoordinates[23],
        };

        vertices = boxCoordinates;

        VAO = GL30.glGenVertexArrays();
        buffer = GL15.glGenBuffers();

        mvMatrix[0] = hW;

        vShader = Shader.generateShader(GL20.GL_VERTEX_SHADER, FileManager.streamToString(getClass().getResourceAsStream("/res/raw/vshader3.txt")));
        fShader = Shader.generateShader(GL20.GL_FRAGMENT_SHADER, FileManager.streamToString(getClass().getResourceAsStream("/res/raw/fshader3.txt")));
        program = GLProgram.generateProgram(vShader, fShader);

        GL20.glUseProgram(program);
        GL30.glBindVertexArray(VAO);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, glBoxCoordinates, GL15.GL_STATIC_DRAW);

        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

        int handle = GL20.glGetUniformLocation(program, "u_mvMatrix");
        if(handle != -1) {
            GL20.glUseProgram(program);
            GL20.glUniformMatrix4fv(handle, false, mvMatrix);
        }
    }

    public void draw(){
        GL11.glLineWidth(3);
        GL20.glUseProgram(program);
        GL30.glBindVertexArray(VAO);
        updateProjectMatrix();
        GL11.glDrawArrays(GL11.GL_LINES, 0, 24);

        GL30.glBindVertexArray (0);
    }

    public void mainPMatrixCalculation(){
        Matrix.multiplyMM(tempM, xRotationM, pointRotationM);
        Matrix.multiplyMM(pMatrix, yRotationM, tempM);
        Matrix.multiplyMM(RotationM, zRotationM, pMatrix);
        Matrix.multiplyMM(pMatrix, scaleMoveM, RotationM);
        Matrix.multiplyMM(cameraPrM, additionalPMatrix, pMatrix);
        Matrix.multiplyMM(mainPMatrix, Camera.projectionMatrix, cameraPrM);
    }

    public void updateProjectMatrix() {
        mainPMatrixCalculation();

        GL20.glEnableVertexAttribArray(0);

        int handle = GL20.glGetUniformLocation(program, "u_pMatrix");
        if(handle != -1) {
            GL20.glUseProgram(program);
            GL20.glUniformMatrix4fv(handle, false, mainPMatrix);
        }
    }

    public void setAdditionalPMatrix(float[] objectPMatrix){
        additionalPMatrix = objectPMatrix;
    }

    public void cleanUp(){
        GL20.glDeleteShader(vShader);
        GL20.glDeleteShader(fShader);
        GL20.glDeleteProgram(program);
        GL15.glDeleteBuffers(buffer);
        GL30.glDeleteVertexArrays(VAO);
    }

    public float[] getUpdatedVertices(){
        mainPMatrixCalculation();
        float[] vec = new float[vertices.length];

        for(int i = 0; i<vertices.length/3; i++){

            vec[i*3] = cameraPrM[0] * vertices[i*3]
                    + cameraPrM[4] * vertices[i*3+1]
                    + cameraPrM[8] * vertices[i*3+2]
                    + cameraPrM[12];


            vec[i*3+1] = cameraPrM[1] * vertices[i*3]
                    + cameraPrM[5] * vertices[i*3+1]
                    + cameraPrM[9] * vertices[i*3+2]
                    + cameraPrM[13];


            vec[i*3+2] = cameraPrM[2] * vertices[i*3]
                    + cameraPrM[6] * vertices[i*3+1]
                    + cameraPrM[10] * vertices[i*3+2]
                    + cameraPrM[14];
        }
        return vec;
    }



    public float[] getVertices() {
        return vertices;
    }

    public void rotateXYZ(float[] anglesInDegrees){
        angle[0] += anglesInDegrees[0] * (float)Math.PI/180f;
        angle[1] += anglesInDegrees[1] * (float)Math.PI/180f;
        angle[2] += anglesInDegrees[2] * (float)Math.PI/180f;

        xRotationM[5] = (float) Math.cos(angle[0]);
        xRotationM[6] = (float) -Math.sin(angle[0]);
        xRotationM[9] = (float) Math.sin(angle[0]);
        xRotationM[10] = (float) Math.cos(angle[0]);

        yRotationM[0] = (float) Math.cos(angle[1]);
        yRotationM[2] = (float) Math.sin(angle[1]);
        yRotationM[8] = (float) -Math.sin(angle[1]);
        yRotationM[10] = (float) Math.cos(angle[1]);

        zRotationM[0] = (float) Math.cos(angle[2]);
        zRotationM[1] = (float) -Math.sin(angle[2]);
        zRotationM[4] = (float) Math.sin(angle[2]);
        zRotationM[5] = (float) Math.cos(angle[2]);
    }

    public void rotateAroundXYZ(float[] anglesInDegrees, float[] rotationPoint){

        /*if(rotationPoint.length < 3) return;
        else
            rotationPoint = new float[]{rotationPoint[0], rotationPoint[1], rotationPoint[2], 1.f};
        anglesInDegrees= new float[]{rotationPoint[0], rotationPoint[1], rotationPoint[2], 1.f};
        Matrix.multiplyMV(rotationPoint, rotationPoint.clone(), pointRotationM);
*/
        float []relativeAngle = {
                anglesInDegrees[0] * (float)Math.PI / 180f,
                anglesInDegrees[1] * (float)Math.PI / 180f,
                anglesInDegrees[2] * (float)Math.PI / 180f,};

        float[] transRotationMatrix = {
                1.f, 0.f, 0.f, 0.f,
                0.f, 1.f, 0.f, 0.f,
                0.f, 0.f, 1.f, 0.f,
                0.f, 0.f, 0.f, 1.f,};

        float[] xPointRot = transRotationMatrix.clone();
        float[] yPointRot = transRotationMatrix.clone();
        float[] zPointRot = transRotationMatrix.clone();

        transRotationMatrix[12] = -rotationPoint[0];
        transRotationMatrix[13] = -rotationPoint[1];
        transRotationMatrix[14] = -rotationPoint[2];

        xPointRot[5] = (float) Math.cos(relativeAngle[0]);
        xPointRot[6] = (float) -Math.sin(relativeAngle[0]);
        xPointRot[9] = (float) Math.sin(relativeAngle[0]);
        xPointRot[10] = (float) Math.cos(relativeAngle[0]);

        yPointRot[0] = (float) Math.cos(relativeAngle[1]);
        yPointRot[2] = (float) Math.sin(relativeAngle[1]);
        yPointRot[8] = (float) -Math.sin(relativeAngle[1]);
        yPointRot[10] = (float) Math.cos(relativeAngle[1]);

        zPointRot[0] = (float) Math.cos(relativeAngle[2]);
        zPointRot[1] = (float) -Math.sin(relativeAngle[2]);
        zPointRot[4] = (float) Math.sin(relativeAngle[2]);
        zPointRot[5] = (float) Math.cos(relativeAngle[2]);


        Matrix.multiplyMM(tempM, transRotationMatrix, pointRotationM);
        Matrix.multiplyMM(pointRotationM, xPointRot, tempM);

        transRotationMatrix[12] = rotationPoint[0];
        transRotationMatrix[13] = rotationPoint[1];
        transRotationMatrix[14] = rotationPoint[2];
        //rotationPosition = rotationPoint;

        Matrix.multiplyMM(xPointRot, yPointRot, pointRotationM);
        Matrix.multiplyMM(tempM, zPointRot, xPointRot);
        Matrix.multiplyMM(pointRotationM, transRotationMatrix, tempM);

    }

    public void translate(float[] position){
        scaleMoveM[12] = position[0];
        scaleMoveM[13] = position[1];
        scaleMoveM[14] = position[2];
    }

    public void move(float[] shift){
        scaleMoveM[12] += shift[0];
        scaleMoveM[13] += shift[1];
        scaleMoveM[14] += shift[2];
    }

    public void scale(float[] scale){
        scaleMoveM[0] *= scale[0];
        scaleMoveM[5] *= scale[1];
        scaleMoveM[10] *= scale[2];
    }

    public void setScale(float[] scale){
        scaleMoveM[0] = scale[0];
        scaleMoveM[5] = scale[1];
        scaleMoveM[10] = scale[2];
    }

    public void setRotationXYZ(float[] rotationXYZ){
        angle[0] = rotationXYZ[0] * (float)Math.PI/180f;
        angle[1] = rotationXYZ[1] * (float)Math.PI/180f;
        angle[2] = rotationXYZ[2] * (float)Math.PI/180f;

        xRotationM[5] = (float) Math.cos(angle[0]);
        xRotationM[6] = (float) -Math.sin(angle[0]);
        xRotationM[9] = (float) Math.sin(angle[0]);
        xRotationM[10] = (float) Math.cos(angle[0]);

        yRotationM[0] = (float) Math.cos(angle[1]);
        yRotationM[2] = (float) Math.sin(angle[1]);
        yRotationM[8] = (float) -Math.sin(angle[1]);
        yRotationM[10] = (float) Math.cos(angle[1]);

        zRotationM[0] = (float) Math.cos(angle[2]);
        zRotationM[1] = (float) -Math.sin(angle[2]);
        zRotationM[4] = (float) Math.sin(angle[2]);
        zRotationM[5] = (float) Math.cos(angle[2]);
    }

    public float[] getPosition(){ return new float[]{scaleMoveM[12], scaleMoveM[13], scaleMoveM[14]}; }

    public float[] getScale(){return new float[]{scaleMoveM[0], scaleMoveM[5], scaleMoveM[10]};}

    public float[] getRotationAngles(){return angle;}
}
