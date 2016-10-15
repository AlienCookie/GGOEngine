package Editor;

import Graphic.GLObject;
import ToolBox.Matrix;

import java.util.List;
import java.util.Vector;

/**
 * Created by andri on 02-Aug-16.
 */
public class GLEditorObject extends GLObject {

    private static float hW = 720 / 1280f;

    // Object matrices
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
            cameraPrM = mvMatrix.clone();

    // Rotation angles
    private float[] angle = new float[3];

    private String objectName;
    private String vShaderSource;
    private String fShaderSource;

    private Vector<String> textureSource = new Vector<>();
    private List<TouchBox> boxes = new Vector<>();

    private float[] clientVerticesData = new float[1];
    private float[] clientNormalsData = new float[1];
    private float[] clientTexturesData = new float[1];

    // Active bounding box index
    private int activeBox = 0;

    // Is object selected
    public Boolean selected;

    public GLEditorObject(GLObjectBuilder builder) {
        super(builder);

        // To properly display aspects ratio
        mvMatrix[0] = hW;

        selected = false;

        // To change bounding box matrices relative to objects
        for(TouchBox box : boxes)
            box.setAdditionalPMatrix(pMatrix);
    }

    public void updateProjectMatrix() {

        // Multiplying object matrices (order is important !!!)
        Matrix.multiplyMM(tempM, xRotationM, pointRotationM);
        Matrix.multiplyMM(pMatrix, yRotationM, tempM);
        Matrix.multiplyMM(RotationM, zRotationM, pMatrix);
        Matrix.multiplyMM(pMatrix, scaleMoveM, RotationM);
        Matrix.multiplyMM(cameraPrM, Camera.projectionMatrix, pMatrix);
       // super.setUniformVector(Camera.getLightDirection(), "u_lightDirection");

        // Update shader uniform
        super.setUniformMatrix(cameraPrM, "u_pMatrix");
    }

    public void rotateXYZ(float[] anglesInDegrees){

        // Transforming angle to radians and adding to current angle
        angle[0] += anglesInDegrees[0] * (float)Math.PI/180f;
        angle[1] += anglesInDegrees[1] * (float)Math.PI/180f;
        angle[2] += anglesInDegrees[2] * (float)Math.PI/180f;

        // Setting up rotation matrix
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
        // Setting up translation matrix
        scaleMoveM[12] = position[0];
        scaleMoveM[13] = position[1];
        scaleMoveM[14] = position[2];
    }

    public void move(float[] shift){
        // Setting up translation matrix
        scaleMoveM[12] += shift[0];
        scaleMoveM[13] += shift[1];
        scaleMoveM[14] += shift[2];
    }

    public void scale(float[] scale){
        // Setting up scale matrix
        scaleMoveM[0] *= scale[0];
        scaleMoveM[5] *= scale[1];
        scaleMoveM[10] *= scale[2];
    }

    public void setScale(float[] scale){
        // Setting up scale matrix
        scaleMoveM[0] = scale[0];
        scaleMoveM[5] = scale[1];
        scaleMoveM[10] = scale[2];
    }

    public void setRotationXYZ(float[] rotationXYZ){
        // Transforming angle to radians and CHANGING current angle
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


    public TouchBox getActiveBox(){
        return this.boxes.get(activeBox);
    }

    public float[] getAllBoxes(){

        // Getting object bounding boxes
        int size = 0;

        for (TouchBox box : boxes){
            size += box.vertices.length;
        }

        float[] boxesVertices = new float[size];

        size = 0;
        for (TouchBox box : boxes){
            float[] arr = box.getUpdatedVertices();
            for(int i = 0; i<arr.length; i++) {
                boxesVertices[size + i] = arr[i];
            }
            size+=arr.length;
        }
        return boxesVertices;
    }

    public void updateCoordinates() {
        float[] vec = new float[3];
        int[] ind = new int[3];

        // Updating vertices depending on projection matrix
        for (int i = 0; i < clientVerticesData.length / 3; i++) {
            ind[0] = i * 3;
            ind[1] = i * 3 + 1;
            ind[2] = i * 3 + 2;

            vec[0] = clientVerticesData[ind[0]];
            vec[1] = clientVerticesData[ind[1]];
            vec[2] = clientVerticesData[ind[2]];

            clientVerticesData[ind[0]] =
                            pMatrix[0] * vec[0]
                            + pMatrix[4] * vec[1]
                            + pMatrix[8] * vec[2]
                            + pMatrix[12];

            clientVerticesData[ind[1]] =
                            pMatrix[1] * vec[0]
                            + pMatrix[5] * vec[1]
                            + pMatrix[9] * vec[2]
                            + pMatrix[13];

            clientVerticesData[ind[2]] =
                            pMatrix[2] * vec[0]
                            + pMatrix[6] * vec[1]
                            + pMatrix[10] * vec[2]
                            + pMatrix[14];

            vec[0] = clientNormalsData[ind[0]];
            vec[1] = clientNormalsData[ind[1]];
            vec[2] = clientNormalsData[ind[2]];

            clientNormalsData[ind[0]] =
                            RotationM[0] * vec[0]
                            + RotationM[4] * vec[1]
                            + RotationM[8] * vec[2]
                            + RotationM[12];

            clientNormalsData[ind[1]] =
                            RotationM[1] * vec[0]
                            + RotationM[5] * vec[1]
                            + RotationM[9] * vec[2]
                            + RotationM[13];

            clientNormalsData[ind[2]] =
                            RotationM[2] * vec[0]
                            + RotationM[6] * vec[1]
                            + RotationM[10] * vec[2]
                            + RotationM[14];

        }
    }

    public void addTouchBox(TouchBox box) {
        // Adding bounding box
        this.boxes.add(box);
        this.boxes.get(boxes.size()-1).setAdditionalPMatrix(pMatrix);
    }

    public void setBoxProjectMatrix() {
        if(!boxes.isEmpty())
            this.boxes.get(activeBox).setAdditionalPMatrix(pMatrix);

    }

    public void nextBox(){
        // Set next box as active
        activeBox++;
        if(activeBox >= boxes.size()) activeBox = 0;
    }

    public void setActiveBox(int index){
        // Set particular box as active
        if(activeBox < boxes.size() || activeBox >= 0)
            activeBox = index;
    }

    public void drawTouchBox() {
        if(!boxes.isEmpty())
            this.boxes.get(activeBox).draw();
    }

    public String getVShaderSource() {
        return vShaderSource;
    }

    public void setVShaderSource(String vShaderSource) {
        this.vShaderSource = vShaderSource;
    }

    public String getFShaderSource() {
        return fShaderSource;
    }

    public void setFShaderSource(String fShaderSource) {
        this.fShaderSource = fShaderSource;
    }

    public Vector<String> getTextureSource() {
        return textureSource;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public void setTextureSource(Vector<String> texture) {
        this.textureSource = texture;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public List<TouchBox> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<TouchBox> boxes) {
        this.boxes = boxes;
    }

    public float[] getClientVerticesData() {
        return clientVerticesData;
    }

    public void setClientVerticesData(float[] clientVerticesData) {
        this.clientVerticesData = clientVerticesData;
    }

    public float[] getClientNormalsData() {
        return clientNormalsData;
    }

    public void setClientNormalsData(float[] clientNormalsData) {
        this.clientNormalsData = clientNormalsData;
    }

    public float[] getClientTexturesData() {
        return clientTexturesData;
    }

    public void setClientTexturesData(float[] clientTexturesData) {
        this.clientTexturesData = clientTexturesData;
    }

    public float[] getPosition(){ return new float[]{scaleMoveM[12], scaleMoveM[13], scaleMoveM[14]}; }

    public float[] getScale(){return new float[]{scaleMoveM[0], scaleMoveM[5], scaleMoveM[10]};}

    public float[] getRotationAngles(){return angle;}
}
