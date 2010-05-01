package com.netzhansa.ayceGateway;

public class LEDMatrix {
    private int height;
    private int width;

    private LED world[][];

    public LEDMatrix(int width_, int height_)
    {
        width = width_;
        height = height_;

        world = new LED[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                world[y][x] = new LED();
            }
        }

    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }
    
    public LED getLED(int x, int y) {
        return world[y][x];
    }

    public void print()
    {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(world[i][j]);
            }
            System.out.println();
        }

    }
}
