package com.netzhansa.ayceGateway;

import java.io.IOException;

public class DMXInterface {
    private DMXCommPort interfaceStream;
    static final private byte DLE = 0x55;
    static final private byte CMD_START = 0x00;
    static final private int channels = 16;
    
    public int getChannels() {
        return channels;
    }

    public DMXInterface(String portName) throws Exception {
        interfaceStream = new DMXCommPort(portName);
    }

    void transmitStreams(byte[][] streams, int count)
    {
        byte[] buf = new byte[32 * count + 18]; // allocate double space for possible DLE escaping + header
        int output_pos = 0;
        buf[output_pos++] = DLE; // DLE
        buf[output_pos++] = CMD_START; // CMD 0 => start DMX frame
        // Send 0 byte on all channels to start DMX frame
        for (int i = 0; i < 16; i++) {
            buf[output_pos++] = 0;
        }
        // Send 3 0 bytes as the first LED seems not to be addressable
        for (int i = 0; i < 16; i++) {
            buf[output_pos++] = 0x00;
        }
        for (int i = 0; i < 16; i++) {
            buf[output_pos++] = 0x00;
        }
        for (int i = 0; i < 16; i++) {
            buf[output_pos++] = 0x00;
        }
        for (int pos = 0; pos < count; pos++) {
            byte slice[] = new byte[16];
            for (int channel = 0; channel < 16; channel++) {
                slice[channel] = streams[channel][pos];
            }
            for (byte bit = 0; bit < 8; bit++) {
                byte b = (byte) (slice[0] & 1
                                 | ((slice[1] & 1) << 1)
                                 | ((slice[2] & 1) << 2)
                                 | ((slice[3] & 1) << 3)
                                 | ((slice[4] & 1) << 4)
                                 | ((slice[5] & 1) << 5)
                                 | ((slice[6] & 1) << 6)
                                 | ((slice[7] & 1) << 7));
                buf[output_pos++] = b;
                if (b == DLE) {
                    buf[output_pos++] = b;
                }
                b = (byte) (slice[8] & 1
                            | ((slice[9] & 1) << 1)
                            | ((slice[10] & 1) << 2)
                            | ((slice[11] & 1) << 3)
                            | ((slice[12] & 1) << 4)
                            | ((slice[13] & 1) << 5)
                            | ((slice[14] & 1) << 6)
                            | ((slice[15] & 1) << 7));
                buf[output_pos++] = b;
                if (b == DLE) {
                    buf[output_pos++] = b;
                }
                for (int channel = 0; channel < 16; channel++) {
                    slice[channel] >>= 1;
                }
            }
        }
        try {
            interfaceStream.write(buf, output_pos);
        }
        catch (IOException x) {
            System.out.println("IO exception");
        }
    }
}
