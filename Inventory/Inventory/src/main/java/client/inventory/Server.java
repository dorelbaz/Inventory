package client.inventory;

import com.dosse.upnp.UPnP;
import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/*
    Launches the server and connects to the SQL server.
*/
public class Server
{
    private String url;
    private int port;

    public Server()
    {
        ServerSocket sc = null;
        try
        {
            getSQLServerDetails();

            // Loads server encryption key and client decryption key.
            String publicKey = "/ServerPublicKey.key";
            String privateKey = "/ClientPrivateKey.key";
            RSA rsa = new RSA(new File("src/main/java/client/inventory/ServerKeys").getAbsolutePath(), publicKey, privateKey, "RSA");

            // Connects to the same port as the client.
            sc = new ServerSocket(port);
            System.out.println("Server online");

            // Connects to the SQL server.
            DataSource dataSource = createDataSource(url);
            System.out.println("Connecting to postgresql server.");

            // Enables communication between server and client on different networks. Also works on the same network.
            forwardPort();

            while (true)
            {
                // Gets connection with the SQL server.
                Connection connection = dataSource.getConnection();

                // Establishes connection with the client.
                Socket s = sc.accept();

                System.out.println("External ip: " + UPnP.getExternalIP());

                // Starts a server thread that will handle the client's request.
                new ServerThread(s, connection, rsa).start();
            }
        }
        catch (IOException e)
        {
            System.out.println("Error in connecting to server.");
            try
            {
                if (sc != null)
                {
                    sc.close();
                }
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error in connecting to postgresql server.");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
        Gets the local SQL server's details.
     */
    private void getSQLServerDetails() throws FileNotFoundException
    {
        String path = new File("src/main/java/client/inventory/SQLServerDetails/SQLServerDetails.txt").getAbsolutePath();
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        url = scanner.nextLine();
        port = scanner.nextInt();
    }

    /*
        Establishes connection with the SQL server.
     */
    private static DataSource createDataSource(String url)
    {
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url);
        return dataSource;
    }

    /*
        Opens a TCP port for communication.
     */
    private void forwardPort()
    {
        // Resets TCP port.
        UPnP.closePortTCP(port);

        System.out.println("Attempting UPnP port forwarding...");

        //Is UPnP available?
        if (UPnP.isUPnPAvailable())
        {
            //Is the port already mapped?
            if (UPnP.isMappedTCP(port))
            {
                System.out.println("UPnP port forwarding not enabled: port is already mapped");
            }
            //Try to map port.
            else if (UPnP.openPortTCP(port))
            {
                System.out.println("UPnP port forwarding enabled");
            } else
            {
                System.out.println("UPnP port forwarding failed");
            }
        } else
        {
            System.out.println("UPnP is not available");
        }
    }

    public static void main (String[] args)
    {
        new Server();
    }
}
