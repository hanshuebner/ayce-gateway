package com.netzhansa.ayceGateway;

import java.awt.Color;

public class LED {
    private Color color = Color.black;
    private int universe = -1;
    private int address = -1;
    
    public boolean addressDefined() {
        return universe != -1 && address != -1;
    }
    
    public Color getColor()
    {
        return color;
    }
    public void setColor(Color color)
    {
        this.color = color;
    }
    public int getUniverse()
    {
        return universe;
    }
    public void setUniverse(int universe)
    {
        this.universe = universe;
    }
    public int getAddress()
    {
        return address;
    }
    public void setAddress(int address)
    {
        this.address = address;
    }
    
    
}
