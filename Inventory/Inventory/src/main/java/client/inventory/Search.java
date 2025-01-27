package client.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/*
    Searches the database for a product.
 */
public class Search extends SQLOperation
{
    /*
        Executes the appropriate query and returns the product (on success).
     */
    public ArrayList<String> execute(Connection connection, Map<String, String> parameters) throws SQLException
    {
        ArrayList<String> response = new ArrayList<>();
        PreparedStatement preparedStatement;

        // Verifies whether client wants to search for a product by name or catalogue.
        int field = verifyNameOrCatalogue(parameters.get("find product"));

        // Replaces the ? in the query with the product's catalogue or name.
        if (field == isCatalogue)
        {
            // Prepares the query for execution.
            preparedStatement = connection.prepareStatement("SELECT * FROM fooditems WHERE catalogue = ?");

            preparedStatement.setInt(1, Integer.parseInt(parameters.get("find product")));
        }
        else
        {
            preparedStatement = connection.prepareStatement("SELECT * FROM fooditems WHERE name LIKE CONCAT( '%',?,'%')");

            preparedStatement.setString(1, parameters.get("find product"));
        }
        prepareResponse(preparedStatement, response);

        return response;
    }

    /*
        Sets the server's message according to the response.
     */
    public String setServerMessage(ArrayList<String> response)
    {
        String result;
        if (!response.isEmpty())
        {
            result = "Item has been found.";
        }
        else
        {
            result = "No item has been found.";
        }
        return result;
    }
}
