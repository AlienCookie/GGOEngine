package Graphic;
import org.lwjgl.opengl.*;

/**
 * Created by andri on 04-Jun-16.
 */
public abstract class Shader {


    public static int generateShader (int type, String shaderCode){
        int shader;
        int[] compiled = new int[1];

        shader = GL20.glCreateShader(type);
        if ( shader == 0 )
        {
            return 0;
        }

        // Attach source
        GL20.glShaderSource(shader, shaderCode);

        // Compile shader
        GL20.glCompileShader(shader);

        // Check for compile status
        GL20.glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, compiled);
        if(compiled[0] == 0)
        {
            int[] infoLen = {0};
            GL20.glGetShaderiv ( shader, GL20.GL_INFO_LOG_LENGTH, infoLen);
            if ( infoLen[0] > 1 )
            {
                String infoLog = GL20.glGetShaderInfoLog (shader);
              //  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(new Activity());
               // dlgAlert.setMessage(infoLog);
            }
            GL20.glDeleteShader ( shader );
            return 0;
        }

    return shader;
    }
}
