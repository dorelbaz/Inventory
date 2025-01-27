package client.inventory;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
    Operations class represents the available client operations.
 */
public class Operations
{
    private final HashMap<String, SQLOperation> operations;

    /*
        Initializes the SQL operations the server can execute with the appropriate classes instances that executes them.
     */
    public Operations()
    {
        operations = new HashMap<>();

        operations.put("Initialize", new Print());
        operations.put("Print All", new Print());
        operations.put("Search", new Search());
        operations.put("Add", new Add());
        operations.put("Update", new Update());
        operations.put("Delete", new Delete());
    }

    /*
        Executes the client's request on the parameters and according to operation.
     */
    public synchronized ArrayList<String> executeOperation(String operation, Connection connection, Map<String, String> parameters)
            throws SQLException, ParseException
    {
        return operations.get(operation).execute(connection, parameters);
    }

    /*
        Sets the server's message according to the operation and its result (response).
     */
    public synchronized String setServerMessage(String operation, ArrayList<String> response)
    {
        return operations.get(operation).setServerMessage(response);
    }
}
