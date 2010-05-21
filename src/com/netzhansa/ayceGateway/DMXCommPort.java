package com.netzhansa.ayceGateway;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;

public class DMXCommPort
{
    SerialPort serialPort = null;
    
    DMXCommPort(String portName) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            throw new Exception("Port " + portName + " is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            } else {
                throw new Exception("Only serial ports are handled by this example.");
            }
        }
    }
    
    void
    write(byte[] data, int length) throws IOException
    {
        serialPort.getOutputStream().write(data, 0, length);
    }

    public static void main(String[] args)
    {
        try {
            DMXCommPort port = new DMXCommPort(args[0]);
            byte[] data = new byte[20];
            
            port.write(data, data.length);
            
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}