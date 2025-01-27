package client.inventory;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.text.ParseException;
import java.util.*;

/*
    ServerThread handles the client's request and returns the result to the client.
*/
public class ServerThread extends Thread
{
    private final Socket s;
    private final Connection connection;
    private static final Operations operations = new Operations();
    private final RSA rsa;


    // Each ServerThread handles a client's request.
    public ServerThread(Socket socket, Connection connection, RSA rsa)
    {
        s = socket;
        this.connection = connection;
        this.rsa = new RSA(rsa);
    }

    // The routine each ServerThread will execute.
    public void run()
    {
        super.run();
        try
        {
            handleReadAndWrite();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Handles the client's request and the server's response.
    public void handleReadAndWrite() throws Exception
    {
        ArrayList<String> response;
        ArrayList<ByteArrayWrapper> encryptedResponse = new ArrayList<>();
        String result = "", operation = "";

        // Creates an output stream (data flows from server to client) with the client.
        OutputStream outputStream = s.getOutputStream();

        // Creates an object that sends data from server to client.
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        // Creates an input stream (data flows from client to server) with the client.
        InputStream inputStream = s.getInputStream();

        // Creates an object that receives data from the client.
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        try
        {
            // Gets the operation to perform and list of encrypted parameters from client.
            operation = (String) objectInputStream.readObject();
            Map<String, ByteArrayWrapper> parameters = (Map<String, ByteArrayWrapper>) objectInputStream.readObject();

            System.out.println("Client's request: " + operation);

            // Decryption process.
            Map<String, String> decryptedParameters = null;
            if (parameters != null)
            {
                decryptedParameters = new HashMap<>(decryptParameters(parameters));
            }

            // Executes the client's request.
            response = new ArrayList<>(operations.executeOperation(operation, connection, decryptedParameters));
            result = operations.setServerMessage(operation, response);

            // Encrypts the server's response.
            for (String item : response)
            {
                encryptedResponse.add(new ByteArrayWrapper(rsa.encrypt(item)));
            }
        }
        catch (SQLException e)
        {
            String errorCode = e.getSQLState();
            if (Objects.equals(errorCode, "08000"))
            {
                result = "Error in connecting to postgresql server";
                System.out.println("Error in connecting to postgresql server");
            }
            else if (Objects.equals(errorCode, "23514"))
            {
                // e.getMessage() contains the violated field,
                // which we will notify the client of.
                // This exception might occur only when attempting to add or update a product.
                System.out.println(e.getMessage());
                response = new ArrayList<>();
                response.add(e.getMessage());
                result = operations.setServerMessage(operation, response);
            }
            else
            {
                result = "SQL fail with error code: " + errorCode;
                System.out.println("SQL fail with error code: " + errorCode);
            }
        }
        catch (ParseException e)
        {
            result = "Invalid date.";
            System.out.println("Invalid Date");
        }
        catch (NumberFormatException e)
        {
            result = "Invalid number.";
            System.out.println("Invalid Number");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Sends the server's encrypted response and message to the client.
        objectOutputStream.writeObject(encryptedResponse);
        objectOutputStream.writeObject(new ByteArrayWrapper(rsa.encrypt(result)));

        // Closes streams with client and ends connection with client.
        connection.close();
        outputStream.close();
        objectOutputStream.close();
        inputStream.close();
        objectInputStream.close();
        s.close();
    }

    // Decrypts the client's parameters and puts them in a new map for further processing.
    private Map<String, String>  decryptParameters(Map<String, ByteArrayWrapper> parameters) throws Exception
    {
        Map<String, String> decryptedParameters = new HashMap<>();

        for (Map.Entry<String, ByteArrayWrapper> entry : parameters.entrySet())
        {
            decryptedParameters.put(entry.getKey(), rsa.decrypt(entry.getValue().getByteArray()));
        }

        System.out.println("Parameters: ");
        for (Map.Entry<String, String> entry : decryptedParameters.entrySet())
        {
            System.out.println("\t" + entry.getKey() + ": " + decryptedParameters.get(entry.getKey()));
        }
        System.out.println("\n");

        return decryptedParameters;
    }
}