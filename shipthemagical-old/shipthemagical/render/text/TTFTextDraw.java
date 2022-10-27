package io.github.coffeecatrailway.shipthemagical.render.text;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

import java.nio.ByteBuffer;
 
public class TTFTextDraw {
 
    private TTFText fontUtil;
    private int fontTextureId;
 
    public TTFTextDraw(String ttfFilename) throws Throwable {
        fontUtil = new TTFText(ttfFilename);
        generateTexture(fontUtil.getFontAsByteBuffer());
    }
 
    private void generateTexture(ByteBuffer bb) {
        this.fontTextureId = glGenTextures();
 
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, this.fontTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                (int) fontUtil.getFontImageWidth(),
                (int) fontUtil.getFontImageHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, bb);
 
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }
 
    public void drawFontTexture(int x, int y) {
        glBindTexture(GL_TEXTURE_2D, this.fontTextureId);
        glBegin(GL_QUADS);
 
        glTexCoord2f(0, 0);
        glVertex3f(x, y, 0);
 
        glTexCoord2f(1, 0);
        glVertex3f(x + fontUtil.getFontImageWidth(), y, 0);
 
        glTexCoord2f(1, 1);
        glVertex3f(x + fontUtil.getFontImageWidth(), y + fontUtil.getFontImageHeight(), 0);
 
        glTexCoord2f(0, 1);
        glVertex3f(x, y + fontUtil.getFontImageHeight(), 0);
 
        glEnd();
    }
 
    public void drawText(String text, int xPosition, int yPosition) {
        glBindTexture(GL_TEXTURE_2D, this.fontTextureId);
        
        glBegin(GL_QUADS);
        int xTmp = xPosition;
        for (char c : text.toCharArray()) {
            float width = fontUtil.getCharWidth(c);
            float height = fontUtil.getCharHeight();
            float w = 1f / fontUtil.getFontImageWidth() * width;
            float h = 1f / fontUtil.getFontImageHeight() * height;
            float x = 1f / fontUtil.getFontImageWidth() * fontUtil.getCharX(c);
            float y = 1f / fontUtil.getFontImageHeight() * fontUtil.getCharY(c);
 
            glTexCoord2f(x, y);
            glVertex3f(xTmp, yPosition, 0);
 
            glTexCoord2f(x + w, y);
            glVertex3f(xTmp + width, yPosition, 0);
 
            glTexCoord2f(x + w, y + h);
            glVertex3f(xTmp + width, yPosition + height, 0);
 
            glTexCoord2f(x, y + h);
            glVertex3f(xTmp, yPosition + height, 0);
 
            xTmp += width;
        }
        glEnd();
    }
}
