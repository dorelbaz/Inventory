package client.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/*
    SQLOperation class translates the client's requested operation to its appropriate SQL operation.
 */
abstract class SQLOperation
{
    public final int isCatalogue = 1, isName = 2;
    public final ArrayList<String> listOfViolations = new ArrayList<>(Arrays.asList("catalogue_violation",
            "date_violation",
            "temperature_violation",
            "price_violation",
            "quantity_violation"));

    /*
        Executes the SQL operation.
     */
    abstract ArrayList<String> execute(Connection connection, Map<String, String> parameters) throws SQLException, ParseException;

    /*
        Sets the server's message according to the operation's result (response).
     */
    abstract String setServerMessage(ArrayList<String> response);

    /*
        Prepares the server's response for the client.
     */
    public void prepareResponse(PreparedStatement preparedStatement,  ArrayList<String> response) throws SQLException
    {
        // Returns the query's result in the form of a matrix:
        // where the columns are the product's fields and the rows are the table entries that answer the query.
        ResultSet resultSet = preparedStatement.executeQuery();

        // Inserts the query's result to the response in an orderly manner according to the product's fields.
        // resultSet.next() moves from row to row and resultSet.getString() moves through the columns.
        while (resultSet.next())
        {
            // Begin at the first column hence why i=1.
            int i = 1;
            int TOTAL_FIELDS = 8;
            while (i <= TOTAL_FIELDS)
            {
                String item = resultSet.getString(i++);
                response.add(item);
            }
        }
    }

    /*
        Discerns whether client sent a product to delete/update/search by its name or catalogue.
     */
    public int verifyNameOrCatalogue(String arg)
    {
        for (int i = 0; i < arg.length(); i++)
        {
            if (!('0' <= arg.charAt(i) && arg.charAt(i) <= '9'))
            {
                return isName;
            }
        }

        return isCatalogue;
    }
}
