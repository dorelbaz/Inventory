package client.inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

/*
    Deletes a product from the database.
 */
public class Delete extends SQLOperation
{
    /*
        Executes the appropriate query and returns the list of products without it.
     */
    public ArrayList<String> execute(Connection connection, Map<String, String> parameters) throws SQLException
    {
        ArrayList<String> response = new ArrayList<>();

        // We first search if the product exists in the database.
        // Sets the search field, within the query, to be either catalogue or name, depending on the client's choice.
        int field = verifyNameOrCatalogue(parameters.get("delete product"));
        String productToDelete = (field == isCatalogue) ? "catalogue"  : "name";
        String query = String.format("SELECT * FROM fooditems WHERE %s = ?", productToDelete);

        // Prepares the query for execution.
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        // Replaces the ? in the query with the product's catalogue or name.
        if (field == isCatalogue)
        {
            preparedStatement.setInt(1, Integer.parseInt(parameters.get("delete product")));
        }
        else
        {
            preparedStatement.setString(1, parameters.get("delete product"));
        }

        // Executes the search query.
        ResultSet resultSet = preparedStatement.executeQuery();

        // If product is found within the database, we proceed to delete it.
        if (resultSet.next())
        {
            // Sets the deletion field to be either catalogue or name.
            query = String.format("DELETE FROM fooditems WHERE %s = ?", productToDelete);

            preparedStatement = connection.prepareStatement(query);

            // Replaces the ? in the query with the product's catalogue or name.
            if (field == isCatalogue)
            {
                preparedStatement.setInt(1, Integer.parseInt(parameters.get("delete product")));
            }
            else
            {
                preparedStatement.setString(1, parameters.get("delete product"));
            }

            // Executes the deletion query.
            preparedStatement.executeUpdate();

            // Gets the list of products from the database without the deleted product and returns it as a response.
            query = "SELECT * FROM fooditems";
            preparedStatement = connection.prepareStatement(query);
            prepareResponse(preparedStatement, response);
        }

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
            result = "Item has been deleted.";
        }
        else
        {
            result = "No item has been deleted.";
        }
        return result;
    }
}
