package io.github.coffeecatrailway.shipthemagical.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

/**
 * @author CoffeeCatTeam
 * @author CoffeeCatTeam - Duncan
 * 
 * @package io.github.coffeecatrailway.shipthemagical.render
 */
public class Texture {

	private int id;
	private int width;
	private int height;

	/**
	 * Texture.java constructor.
	 * 
	 * @param filename {@code String}
	 */
	public Texture(String filename) {
		BufferedImage bi;
		String path = "./res/textures/"+filename;
		File img = new File(path);
		if (!img.exists())img = new File("./res/textures/missing.png");
		
		try {
			bi = ImageIO.read(img);
			if (bi == null) {
				throw new FileNotFoundException("Texture '" + path + "' could not found!");
				// = ImageIO.read(new File("./res/textures/missing.png"));
			}
			width = bi.getWidth();
			height = bi.getHeight();

			int[] pixels_raw = new int[width * height*4];
			pixels_raw = bi.getRGB(0, 0, width, height, null, 0, width);

			ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					int pixel = pixels_raw[i * width + j];

					pixels.put((byte) ((pixel >> 16) & 0xFF));	// Red
					pixels.put((byte) ((pixel >> 8) & 0xFF));	// Green
					pixels.put((byte) (pixel & 0xFF));			// Blue
					pixels.put((byte) ((pixel >> 24) & 0xFF));	// Alpha
				}
			}
			pixels.flip();

			id = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, id);

			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Finalizes model.
	 * 
	 * @throws Throwable
	 */
	protected void finalize() throws Throwable {
		glDeleteTextures(id);
		super.finalize();
	}

	/**
	 * Bind texture.
	 * 
	 * @param sampler {@code Integer}
	 */
	public void bind(int sampler) {
		if (sampler >= 0 && sampler <= 31) {
			glActiveTexture(GL_TEXTURE0 + sampler);
			glBindTexture(GL_TEXTURE_2D, id);
		}
	}
}
