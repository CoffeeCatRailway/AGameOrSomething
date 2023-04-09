package io.github.coffeecatrailway.orsomething.anengine.client.graphics.texture.atlas;

import com.mojang.logging.LogUtils;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.system.NativeResource;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.stb.STBRectPack.stbrp_init_target;
import static org.lwjgl.stb.STBRectPack.stbrp_pack_rects;

/**
 * Efficiently stitches squares into an atlas using {@link STBRPRect}.
 *
 * @param <T> The type of elements to stitch
 * @author Ocelot
 */
public class STBStitcher<T> implements NativeResource {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final int maxWidth;
    private final int maxHeight;
    private final int levels;
    private final List<Entry<T>> entries;
    private STBRPRect.Buffer buffer;
    private int width;
    private int height;

    static {
        LOGGER.info("Using STB Stitcher");
    }

    public STBStitcher(int maxWidth, int maxHeight, int levels) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.levels = levels;
        this.entries = new LinkedList<>();
    }

    private static int smallestEncompassingPowerOfTwo(int value) {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    /**
     * Adds the specified element to be stitched.
     *
     * @param value  The entry to stitch
     * @param width  The width of the element
     * @param height The height of the element
     */
    public void add(T value, int width, int height) {
        STBRPRect rect = STBRPRect.malloc();
        rect.id(this.entries.size());
        rect.w((short) smallestFittingMinTexel(width, this.levels));
        rect.h((short) smallestFittingMinTexel(height, this.levels));
        rect.x((short) 0);
        rect.y((short) 0);
        this.entries.add(new Entry<>(value, rect));
    }

    /**
     * Stitches every registered entry in this stitcher.
     */
    public void stitch() {
        if (this.entries.isEmpty())
            return;

        STBRPRect.Buffer buffer = STBRPRect.calloc(this.entries.size());
        try (STBRPContext context = STBRPContext.calloc(); STBRPNode.Buffer nodes = STBRPNode.calloc(this.entries.size())) {
            for (int i = 0; i < this.entries.size(); i++) {
                Entry<T> entry = this.entries.get(i);
                buffer.position(i);
                buffer.put(entry.rect);
                entry.rect = buffer.get(i);
                if (entry.rect.w() > this.width)
                    this.width = smallestEncompassingPowerOfTwo(entry.rect.w());
                if (entry.rect.h() > this.height)
                    this.height = smallestEncompassingPowerOfTwo(entry.rect.h());
            }
            buffer.position(0);

            if (this.width > this.maxWidth || this.height > this.maxHeight)
                throw new RuntimeException(String.format("Failed to fit sprites into a %dx%d atlas", this.maxWidth, this.maxHeight));
            stbrp_init_target(context, this.width, this.height, nodes);
            while (stbrp_pack_rects(context, buffer) == 0) {
                if (this.width > this.height) {
                    this.height <<= 1;
                } else {
                    this.width <<= 1;
                }

                if (this.width > this.maxWidth || this.height > this.maxHeight)
                    throw new RuntimeException(String.format("Failed to fit sprites into a %dx%d atlas", this.maxWidth, this.maxHeight));
                stbrp_init_target(context, this.width, this.height, nodes);
            }

            if (this.buffer != null)
                this.buffer.free();
            this.buffer = buffer;
        }
    }

    /**
     * Iterates through every stitched element.
     *
     * @param walker The consumer for each element
     */
    public void walk(StitchWalker<T> walker) {
        for (Entry<T> entry : this.entries) {
            walker.load(entry.value, entry.rect.x(), entry.rect.y(), entry.rect.w(), entry.rect.h());
        }
    }

    @Override
    public void free() {
        if (this.buffer != null) {
            this.buffer.free();
            this.buffer = null;
        }
        this.entries.clear();
    }

    /**
     * @return The final width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return The final height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return The amount of entries stitched
     */
    public int getSize() {
        return this.entries.size();
    }

    private static int smallestFittingMinTexel(int dimension, int mipLevel) {
        return (dimension >> mipLevel) + ((dimension & (1 << mipLevel) - 1) == 0 ? 0 : 1) << mipLevel;
    }

    private static class Entry<T> {

        private final T value;
        private STBRPRect rect;

        private Entry(T value, STBRPRect rect) {
            this.value = value;
            this.rect = rect;
        }
    }

    @FunctionalInterface
    public interface StitchWalker<T> {
        void load(T entry, int x, int y, int width, int height);
    }
}
