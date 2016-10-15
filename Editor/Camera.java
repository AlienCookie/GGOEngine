package Editor;

import GUI.InputManager;
import ToolBox.Matrix;

/**
 * Created by andri on 11-Sep-16.
 */
public class Camera {

    // Camera matrices
    public static float[] projectionMatrix = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f,};

    private static float[] perspectiveMatrix = projectionMatrix.clone(),
            tempMatrix = projectionMatrix.clone();

    // Radians transform coefficient
    public static final float RADIAN_COEFFICIENT = (float)Math.PI/180.f;

    // Camera view options
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.01f;
    private static final float FAR_PLANE = 10;

    // Camera position options
    private static float yaw = 180.f;
    private static float pitch = 0.f;
    private static float speed = 0.25f;
    private static float[] angle = new float[3];
    private static float[] position = new float[4];

    public Camera() {

        // Calculation camera screen relative params
        float aspectRatio = (float) 640 / (float) 360;
        float y_scale = (float) (1f / Math.tan((FOV / 2f)*RADIAN_COEFFICIENT)) * aspectRatio;
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        // Setting up camera perspective matrix
        perspectiveMatrix[0] = x_scale;
        perspectiveMatrix[5] = y_scale;
        perspectiveMatrix[10] = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        perspectiveMatrix[11] = -1.0f;
        perspectiveMatrix[14] = -((2f * NEAR_PLANE * FAR_PLANE) / frustum_length);
        perspectiveMatrix[15] = 0.0f;

        // Setting up camera starting position
        position[2] = -1.42f;
        position[1] = 0f;

        move();
    }

    public void move() {

        float dx = 0.0f;
        float dy = 0.0f;
        float dz = 0.0f;

        float angleX = this.getYaw();
        float angleY = this.getPitch();

        float PI = (float) Math.PI;

        // Camera movement (depend on angle)
        if (InputManager.onShift)
            speed *= 5f;

        if (InputManager.onW) {
            dx = (float) -Math.sin(angleX / 180 * PI) * speed;
            dy = (float) Math.tan(angleY / 180 * PI) * speed;
            dz = (float) -Math.cos(angleX / 180 * PI) * speed;
        }
        if (InputManager.onS) {
            dx = (float) Math.sin(angleX / 180 * PI) * speed;
            dy = (float) -Math.tan(angleY / 180 * PI) * speed;
            dz = (float) Math.cos(angleX / 180 * PI) * speed;
        }
        if (InputManager.onD) {
            dx = (float) -Math.sin((angleX + 90) / 180 * PI) * speed;
            dz = (float) -Math.cos((angleX + 90) / 180 * PI) * speed;
        }
        if (InputManager.onA) {
            dx = (float) -Math.sin((angleX - 90) / 180 * PI) * speed;
            dz = (float) -Math.cos((angleX - 90) / 180 * PI) * speed;
        }

        // Apply move
        this.getPosition()[0] -= dx;
        this.getPosition()[1] -= dy;
        this.getPosition()[2] += dz;


        // Apply camera angle changing
        if (InputManager.onClick && InputManager.onMove) {
            if (InputManager.clickPosition[0] > 30 && InputManager.clickPosition[0] < 883
                    && InputManager.clickPosition[1] > 30 && InputManager.clickPosition[1] < 520) {
                this.setPitch(this.getPitch() + InputManager.shift[1] / 2f);
                this.setYaw(this.getYaw() + InputManager.shift[0] / 2f);
                if (this.getPitch() < -89.9) this.setPitch(-89.9f);
                if (this.getPitch() > 89.9) this.setPitch(89.9f);
                InputManager.shift[0] = 0;
                InputManager.shift[1] = 0;
            }
        }

        // Updating projection matrix
        tempMatrix = Matrix.createViewMatrix(pitch, yaw, position);
        Matrix.multiplyMM(projectionMatrix, perspectiveMatrix, tempMatrix);

        // Matrix.multiplyMV(lightDirection, lightDirection.clone(), projectionMatrix);
    }

    public static float[] getAngle() {
        return angle;
    }

    public static float[] getPosition() {
        return position;
    }

    public static float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public static float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
