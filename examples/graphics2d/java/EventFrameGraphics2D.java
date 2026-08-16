package io.github.humbleui.jwm.examples;

import java.awt.image.BufferedImage;

import io.github.humbleui.jwm.Event;

/// Frame event for Java2D/Graphics2D graphics-drawing library.
/// 
public class EventFrameGraphics2D implements Event {

    private final BufferedImage bufferedImage;

    public EventFrameGraphics2D(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    @Override
    public String toString() {
        return "EventFrameGraphics2D(bufferedImage=" + System.identityHashCode(bufferedImage) + ")";
    }

}
