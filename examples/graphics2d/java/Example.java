package io.github.humbleui.jwm.examples;

import io.github.humbleui.jwm.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.function.Consumer;

/// An example of JWM working with Java2D/Graphics2D as graphics-drawing library.
/// 
/// This example shows some text and an orange circle at the center of the main window.
/// 
public class Example implements Consumer<Event> {

    private Window window;

    public Example(int width, int height) {
        window = App.makeWindow();
        window.setEventListener(this);
        window.setTitle("JWM with Java2D/Graphics2D Fixed Scale");

        window.setLayer(new LayerRasterGraphics2D());

        window.setWindowSize(width, height);
        window.setVisible(true);
    }

    @Override
    public void accept(Event e) {
        if (e instanceof EventWindowCloseRequest) {
            window.close();
            App.terminate();
        }
        else if (e instanceof EventFrameGraphics2D eg) {
            var image = eg.getBufferedImage();
            var g2d = image.createGraphics();
            drawJava2DScene(g2d, image.getWidth(), image.getHeight());
            g2d.dispose();
        }
    }

    private void drawJava2DScene(Graphics2D g2d, int w, int h) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Clear background
        g2d.setColor(new Color(30, 30, 30));
        g2d.fillRect(0, 0, w, h);

        // Draw an orange circle
        int circleSize = Math.min(w, h) / 2;
        int x = (w - circleSize) / 2;
        int y = (h - circleSize) / 2;

        g2d.setColor(Color.ORANGE);
        g2d.fillOval(x, y, circleSize, circleSize);

        // Draw some text
        g2d.setColor(Color.WHITE);
        g2d.drawString("Hello from High-DPI Correlated Java2D!", 50, 50);
    }

    public static void main(String[] args) {
        App.start(() -> new Example(800, 600));
    }

}
