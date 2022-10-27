package io.github.coffeecatrailway.shipthemagical.render;

import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Shader {
	
	private int program;
	private int vs;
	private int fs;
	
	/**
	 * Shader.java constructor.
	 * 
	 * @param filename {@code String}
	 */
	public Shader(String filename) {
		program = glCreateProgram();
		
		vs = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vs, readFile(filename+".vs"));
		glCompileShader(vs);
		if (glGetShaderi(vs, GL_COMPILE_STATUS) != 1) {
			System.err.println(glGetShaderInfoLog(vs));
			System.exit(1);
		}
		
		fs = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fs, readFile(filename+".fs"));
		glCompileShader(fs);
		if (glGetShaderi(fs, GL_COMPILE_STATUS) != 1) {
			System.err.println(glGetShaderInfoLog(fs));
			System.exit(1);
		}
		
		glAttachShader(program, vs);
		glAttachShader(program, fs);
		
		glBindAttribLocation(program, 0, "vertices");
		glBindAttribLocation(program, 1, "textures");
		
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
			System.err.println(glGetProgramInfoLog(program));
			System.exit(1);
		}
		glValidateProgram(program);
		if (glGetProgrami(program, GL_VALIDATE_STATUS) != 1) {
			System.err.println(glGetProgramInfoLog(program));
			System.exit(1);
		}
	}
	
	/**
	 * Finalizes model.
	 * 
	 * @throws Throwable
	 */
	protected void finalize() {
		glDetachShader(program, vs);
		glDetachShader(program, fs);
		glDeleteShader(vs);
		glDeleteShader(fs);
		glDeleteProgram(program);
	}
	
	/**
	 * Sets a uniform.
	 * 
	 * @param name {@code String}
	 * @param value {@code Integer}
	 */
	public void setUniform(String name, int value) {
		int location = glGetUniformLocation(program, name);
		if (location != -1) {
			glUniform1i(location, value);
		}
	}
	
	/**
	 * Sets a uniform.
	 * 
	 * @param name {@code String}
	 * @param value {@code Vector4f}
	 */
	public void setUniform(String name, Vector4f value) {
		int location = glGetUniformLocation(program, name);
		if (location != -1) {
			glUniform4f(location, value.x, value.y, value.z, value.w);
		}
	}
	
	/**
	 * Sets a uniform.
	 * 
	 * @param name {@code String}
	 * @param value {@code Matrix4f}
	 */
	public void setUniform(String name, Matrix4f value) {
		int location = glGetUniformLocation(program, name);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		value.get(buffer);
		if (location != -1) {
			glUniformMatrix4fv(location, false, buffer);
		}
	}
	
	/**
	 * Binds the shader.
	 */
	public void bind() {
		glUseProgram(program);
	}
	
	/**
	 * Reads the shader file.
	 * 
	 * @param filename {@code String}
	 * @return {@code String}
	 */
	private String readFile(String filename) {
		StringBuilder string = new StringBuilder();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("./res/shaders/"+filename));
			String line;
			while((line = br.readLine()) != null) {
				string.append(line);
				string.append("\n");
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return string.toString();
	}
}
