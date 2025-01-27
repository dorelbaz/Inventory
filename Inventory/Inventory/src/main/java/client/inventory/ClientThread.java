package client.inventory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
    ClientThread delivers the client's request to the server and displays its response.
 */
public class ClientThread extends Thread
{
    private final String ip;
    private final String action;
    private final int port;
    private final ClientController controller;
    private Map<String, ByteArrayWrapper> arguements;

    // Each ClientThread carries a client's request.
    public ClientThread(ClientController controller, String ip, int port, String action, Map<String, ByteArrayWrapper> arguments)
    {
        this.controller = controller;
        this.ip = ip;
        this.port = port;
        if (arguments != null)
        {
            this.arguements = new HashMap<>(arguments);
        }
        this.action = action;
    }

    // The routine each ClientThread will execute.
    public void run()
    {
        super.run();
        try
        {
            handleReadAndWrite();
        }
        catch (Exception e)
        {
            // If handleReadAndWrite fails, we assume that the connection with the server failed.
            controller.setMessage(null, false);
        }
    }

    // Handles the client's request and the server's response.
    public void handleReadAndWrite() throws Exception
    {
        // Creates a connection with the server.
        Socket s = new Socket(ip, port);

        // Creates an output stream (data flows from client to server) with the server.
        OutputStream outputStream = s.getOutputStream();

        // Creates an object that sends data from client to server.
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        // Creates an input stream (data flows from server to client) with the server.
        InputStream inputStream = s.getInputStream();

        // Creates an object that receives data from the server.
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        // Write the data to server - serialization of objects on the client side.
        objectOutputStream.writeObject(action);
        objectOutputStream.writeObject(arguements);

        // Gets updated encrypted data and message from server.
        ArrayList<ByteArrayWrapper> responseFromServer = new ArrayList<>((ArrayList<ByteArrayWrapper>) objectInputStream.readObject());
        ByteArrayWrapper encryptedServerMessage = (ByteArrayWrapper) objectInputStream.readObject();

        // Decrypts and displays updated data and message in table.
        controller.setMessage(encryptedServerMessage, true);
        controller.updateTable(responseFromServer);

        // Closes streams with server and ends connection with server.
        outputStream.close();
        objectOutputStream.close();
        inputStream.close();
        objectInputStream.close();
        s.close();
    }
}