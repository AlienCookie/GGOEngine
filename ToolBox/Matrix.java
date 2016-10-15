package ToolBox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Matrix {

	public static void multiplyMM(float[] result, float[] second, float[] first) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result[i * 4 + j] =
						first[i * 4] * second[j]
								+ first[i * 4 + 1] * second[j + 4]
								+ first[i * 4 + 2] * second[j + 8]
								+ first[i * 4 + 3] * second[j + 12];
			}
		}
	}

	public static void multiplyMV(float[] result, float[] second, float[] first) {
		for (int i = 0; i < 4; i++) {
			result[i] = first[i] * second[0]
					+ first[i + 4] * second[1]
					+ first[i + 8] * second[2]
					+ first[i + 12] * second[3];
		}
	}

	public static void vMultiplyVV(float[] result, float[] second, float[] first) {
		result[0] = first[1] * second[2] - first[2] * second[1];
		result[1] = first[2] * second[0] - first[0] * second[2];
		result[2] = first[0] * second[1] - first[1] * second[0];
	}


	public static void addMM(float[] result, float[] second, float[] first) {
		for (int i = 0; i < first.length; i++)
			result[i] = first[i] + second[i];
	}

	public static void subtractMM(float[] result, float[] second, float[] first) {
		for (int i = 0; i < first.length; i++)
			result[i] = first[i] - second[i];
	}

	public static void normaliseMatrix(float[] matrix) {
		float min = matrix[0];
		float max = matrix[0];

		for (int i = 0; i < matrix.length; i++)
			if (matrix[i] < min) min = matrix[i];

		if (min < 0)
			for (int i = 0; i < matrix.length; i++)
				matrix[i] -= min;

		for (int i = 0; i < matrix.length; i++)
			if (matrix[i] > max) max = matrix[i];

		for (int i = 0; i < matrix.length; i++)
			matrix[i] /= max;
	}

	public static float[] createViewMatrix(float pitch, float yaw, float[] position) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix,
				viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);

		Vector3f cameraPos = new Vector3f(position[0], position[1], position[2]);
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		float[] matrix = {
				-viewMatrix.m00, viewMatrix.m01,viewMatrix.m02, viewMatrix.m03,
				viewMatrix.m10, viewMatrix.m11,viewMatrix.m12, viewMatrix.m13,
				viewMatrix.m20, viewMatrix.m21,viewMatrix.m22, viewMatrix.m23,
				viewMatrix.m30, viewMatrix.m31,viewMatrix.m32, viewMatrix.m33,};
		return matrix;
	}

}
