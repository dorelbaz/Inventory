package client.inventory;

import java.io.Serial;
import java.io.Serializable;

/*
    Wrapper class for a byte array. An instance of this class holds a byte array returned by the encryption process.
    Instances of this class are used in the bidirectional communication of the server and client.
*/
public class ByteArrayWrapper implements Serializable
{
    private final byte[] bytes;
    @Serial
    private static final long serialVersionUID = 10000;

    public ByteArrayWrapper(byte[] bytes)
    {
        this.bytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
        {
            this.bytes[i] = bytes[i];
        }
    }

    public byte[] getByteArray()
    {
        return bytes;
    }
}
