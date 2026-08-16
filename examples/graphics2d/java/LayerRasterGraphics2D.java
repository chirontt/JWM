package io.github.humbleui.jwm.examples;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;

import io.github.humbleui.jwm.LayerRaster;

/// Raster layer implementation for Java2D/Graphics2D graphics-drawing library.
/// 
public class LayerRasterGraphics2D extends LayerRaster {

    private BufferedImage renderBuffer;

    @Override
    public void frame() {
        // re-initialize the raster buffer if needed
        initRasterBuffer(getWidth(), getHeight());
        _window.accept(new EventFrameGraphics2D(renderBuffer));
        swapBuffers();
    }

    private void initRasterBuffer(int width, int height) {
        if (width <= 0 || height <= 0) return;
        
        // If the buffer size hasn't changed, skip re-allocation
        if (renderBuffer != null && renderBuffer.getWidth() == width && renderBuffer.getHeight() == height) {
            return;
        }

        // Use custom DataBuffer wrapper around the JWM native pixel buffer
        var nativeDataBuffer = new NativeIntDataBuffer(getPixelsPtr(), width * height);

        // Construct a Custom WritableRaster linking to native memory buffer
        int[] bandMasks = {0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000}; // RGBA masks
        var sampleModel = new SinglePixelPackedSampleModel(
            DataBuffer.TYPE_INT, width, height, width, bandMasks
        );
        var raster = Raster.createWritableRaster(sampleModel, nativeDataBuffer, null);

        // Build the BufferedImage for rendering
        renderBuffer = new BufferedImage(ColorModel.getRGBdefault(), raster, true, null);
    }

}
