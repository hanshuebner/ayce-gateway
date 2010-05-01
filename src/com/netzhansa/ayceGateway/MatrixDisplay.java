package com.netzhansa.ayceGateway;

import java.awt.*;

public class MatrixDisplay extends Canvas {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private LEDMatrix world;
    private int cellSize;
    int width;
    int height;
    private Image offscreenImage = null;
    private Graphics offscreenImageGraphics = null;

    public MatrixDisplay(LEDMatrix world, int cellSize)
    {
        this.world = world;
        this.cellSize = cellSize;
        this.width = world.getColumns() * cellSize;
        this.height = world.getRows() * cellSize;
        this.setSize(width, height);
    }

    public void nextIteration()
    {
        repaint();
    }

    public void update(Graphics g)
    {
        if (offscreenImage == null) {
            offscreenImage = createImage(this.width, this.height);
            offscreenImageGraphics = offscreenImage.getGraphics();
        }
        paint(offscreenImageGraphics);
        g.drawImage(offscreenImage, 0, 0, null);
    }

    @Override
    public void paint(Graphics g)
    {
        for (int i = 0; i < world.getRows(); i++) {
            for (int j = 0; j < world.getColumns(); j++) {
                g.setColor(world.getLED(i, j).getColor());
                g.fillRect(cellSize * j + 1, cellSize * i + 1, cellSize - 2, cellSize - 2);
            }

        }
    }
}
