package com.netzhansa.ayceGateway;

import java.awt.Color;

public class LEDMatrix {
    private int rows;
    private int columns;

    private LED world[][];

    public LEDMatrix(int r, int c)
    {
        rows = r;
        columns = c;

        world = new LED[r][c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                world[i][j] = new LED();
            }
        }

    }

    public int getRows()
    {
        return rows;
    }

    public int getColumns()
    {
        return columns;
    }
    
    public LED getLED(int row, int col) {
        return world[row][col];
    }

    public void print()
    {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(world[i][j]);
            }
            System.out.println();
        }

    }
}
