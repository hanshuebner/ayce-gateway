package com.netzhansa.ayceGateway;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AYCEGateway extends Frame implements ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private MatrixDisplay display;
    private LEDMatrix world;
    private ServerSocket serverSocket;
    private DMXInterface dmxInterface;
    boolean interfaceOpenError = false;
    
    public AYCEGateway(int width, int height, int cellSize, int port, String title)
    {
        super(title);

        // initialize cell world
        world = new LEDMatrix(height, width);
        setLayout(new BorderLayout());

        display = new MatrixDisplay(world, cellSize);

        add(display, "Center");

        WindowAdapter wa = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
                System.exit(0);
            }
        };
        addWindowListener(wa);

        this.pack();
        this.setVisible(true);
        
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            System.out.println("Can't listen to port " + port + ": " + e);
            System.exit(-1);
        }
    }
    
    public void openDMXInterface(String portName) {
        try {
            dmxInterface = new DMXInterface(portName);
        }
        catch (FileNotFoundException e) {
            System.out.println("Cannot open port " + portName);
            interfaceOpenError = true;
        }
    }
    
    public void sendDMXStreams() {
        byte streams[][] = new byte[dmxInterface.getChannels()][512];
        int count = 0;
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                LED led = world.getLED(y, x);
                if (led.addressDefined()) {
                    int universe = led.getUniverse();
                    int address = led.getAddress();
                    Color color = led.getColor();
                    streams[universe][address++] = (byte) color.getRed();
                    streams[universe][address++] = (byte) color.getGreen();
                    streams[universe][address++] = (byte) color.getBlue();
                    count = Math.max(address, count);
                }
            }
        }
        dmxInterface.transmitStreams(streams, count);
    }

    public void repaint()
    {
        super.repaint();
        display.repaint();
    }

    public void setColor(int col, int row, Color color)
    {
        world.getLED(row, col).setColor(color);
    }

    public int getHeight()
    {
        return world.getHeight();
    }

    public int getWidth()
    {
        return world.getWidth();
    }
    
    public void clear()
    {
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                setColor(col, row, Color.BLACK);
            }
        }
        repaint();
    }
    
    private static int logo[] =
    { 0x78, 0x7F, 0x09, 0x09, 0x0F, 
      0x78, 0x00, 0x0F, 0x08, 0x78,
      0x78, 0x08, 0x0F, 0x00, 0x7F,
      0x79, 0x41, 0x41, 0x63, 0x00, 
      0x7F, 0x79, 0x49, 0x49, 0x49 };
    
    public void drawLogo()
    {
        clear();
        final Color logoColor = interfaceOpenError ? new Color(200, 100, 100) : new Color(100, 200, 100);
        for (int i = 0; i < logo.length; i++) {
            int data = logo[i];
            for (int bit = 0; bit < 8; bit++) {
                try {
                    setColor(i + 1, bit + 1, ((data & 1) == 0) ? Color.BLACK : logoColor);
                }
                catch (ArrayIndexOutOfBoundsException e) {
                }
                data >>= 1;
            }
            try {
                Thread.sleep(15);
            }
            catch (InterruptedException e) {
            }
            repaint();
        }
    }

    private void run()
    {
        for (;;) {
            try {
                drawLogo();
                System.out.println("waiting for connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("connection established");
                clear();
                try {
                    runClientConnection(clientSocket);
                }
                catch (Exception e) {
                }
                clientSocket.close();
            }
            catch (IOException e) {
                // fall through
            }
        }
    }
    
    private final short PROTOCOL_VERSION = 1;
    
    private void runClientConnection(Socket socket) throws InterruptedException, IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        
        out.writeShort(PROTOCOL_VERSION);
        out.writeShort(getWidth());
        out.writeShort(getHeight());
        
        while (true) {
            @SuppressWarnings("unused")
            int timestamp = in.readInt();
            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    int b = in.readUnsignedByte();
                    int g = in.readUnsignedByte();
                    int r = in.readUnsignedByte();
                    setColor(x, y, new Color(r, g, b));
                }
            }
            if (dmxInterface != null) {
                sendDMXStreams();
            }
            repaint();
        }
    }

    public void actionPerformed(ActionEvent ae)
    {
        // if (ae.getSource() == btnIterate) {
        // cellWorldCanvas.nextIteration();
        // }
    }
    
    public static void main(String[] args)
    {
        int width = Integer.parseInt(System.getProperty("width", "100"));
        int height = Integer.parseInt(System.getProperty("height", "18"));
        int cellSize = Integer.parseInt(System.getProperty("cellSize", "10"));
        int port = Integer.parseInt(System.getProperty("port", "9321"));
        
        AYCEGateway gateway = new AYCEGateway(width, height, cellSize, port, "All You Can Eat DMX Server");
        
        if (args.length > 0) {
            gateway.openDMXInterface(args[0]);
        }
        
        gateway.run();
    }
}
