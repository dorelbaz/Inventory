package client.inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

/*
    Returns the list of products from the database to the client.
 */
public class Print extends SQLOperation
{
    private static boolean initialize = true;

    /*
        Returns the list of products in the database.
     */
    public ArrayList<String> execute(Connection connection, Map<String, String> parameters) throws SQLException
    {
        ArrayList<String> response = new ArrayList<>();
        String query = "SELECT * FROM fooditems";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        prepareResponse(preparedStatement, response);
        return response;
    }

    /*
        Sets the server's message according to the response.
     */
    public String setServerMessage(ArrayList<String> response)
    {
        String result;
        if (initialize)
        {
            // Executes only on initialize (when the client starts up).
            result = "Connection with server is successful.";
        }
        else if (!response.isEmpty())
        {
            result = "Item list retrieved successfully.";
        }
        else
        {
            result = "Failed to retrieve item list.";
        }
        initialize = false;
        return result;
    }
}
