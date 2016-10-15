package Graphic;

import org.lwjgl.opengl.*;

/**
 * Created by andri on 04-Jun-16.
 */
public class GLProgram {

    public static int generateProgram (String vShaderSource, String fShaderSource){

        int program = 0;
        int[] linked = {0};
        //  Shader shader = new Shader();

        int vShader = Shader.generateShader(GL20.GL_VERTEX_SHADER, vShaderSource);
        int fShader = Shader.generateShader(GL20.GL_FRAGMENT_SHADER, fShaderSource);

        program = GL20.glCreateProgram();

        GL20.glAttachShader(program, vShader);
        GL20.glAttachShader(program, fShader);

        GL20.glLinkProgram(program);

        GL20.glGetProgramiv(program, GL20.GL_LINK_STATUS, linked);

        if(linked[0] == 0){
            GL20.glDeleteProgram(program);
            return 0;
        }

        return program;
    }

    public static int generateProgram (int vShader, int fShader){

        int program = 0;
        int[] linked = {0};
        //  Shader shader = new Shader();

        program = GL20.glCreateProgram();

        GL20.glAttachShader(program, vShader);
        GL20.glAttachShader(program, fShader);

        GL20.glLinkProgram(program);

        GL20.glGetProgramiv(program, GL20.GL_LINK_STATUS, linked);

        if(linked[0] == 0){
            GL20.glDeleteProgram(program);
            return 0;
        }

        return program;
    }
}
