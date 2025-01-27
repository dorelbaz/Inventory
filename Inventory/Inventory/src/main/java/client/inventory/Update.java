package client.inventory;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/*
    Updates a product in the database.
 */
public class Update extends SQLOperation
{
    private int quantity = -1;

    /*
        Executes the appropriate query and returns the updated product (on success).
     */
    public ArrayList<String> execute(Connection connection, Map<String, String> parameters) throws SQLException, ParseException
    {
        ArrayList<String> response = new ArrayList<>();

        // We first search if the product exists in the database.
        // Sets the search field, within the query, to be either catalogue or name, depending on the client's choice.
        int field = verifyNameOrCatalogue(parameters.get("update product"));
        String productToUpdate = (field == isCatalogue) ? "catalogue"  : "name";
        String query = String.format("SELECT * FROM fooditems WHERE %s = ?", productToUpdate);

        // Prepares the query for execution.
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        // Replaces the ? in the query with the product's catalogue or name.
        if (field == isCatalogue)
        {
            preparedStatement.setInt(1, Integer.parseInt(parameters.get("update product")));
        }
        else
        {
            preparedStatement.setString(1, parameters.get("update product"));
        }

        // Executes the search query.
        ResultSet resultSet = preparedStatement.executeQuery();

        // If product is found within the database, we proceed to update it.
        if (resultSet.next())
        {
            // Sets the update field (first %s) to be either quantity/price/production date/expiry date
            // and the condition field (second %s) to be either catalogue or name.
            query = String.format("UPDATE fooditems SET %s = ? WHERE %s = ?", parameters.get("field to update"), productToUpdate);

            preparedStatement = connection.prepareStatement(query);

            // Replaces the first ? with the new quantity, price, production date or expiry date, depending on the client's choice.
            if (parameters.get("field to update").equals("quantity"))
            {
                preparedStatement.setInt(1, Integer.parseInt(parameters.get("new value")));
                quantity = Integer.parseInt(parameters.get("new value"));
            }
            else if (parameters.get("field to update").equals("price"))
            {
                preparedStatement.setFloat(1, Float.parseFloat(parameters.get("new value")));
            }
            else
            {
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy");
                java.util.Date date = sdf1.parse(parameters.get("new value"));
                preparedStatement.setDate(1, new java.sql.Date(date.getTime()));
            }

            // Replaces the second ? in the query with the product's catalogue or name.
            if (field == isCatalogue)
            {
                preparedStatement.setInt(2, Integer.parseInt(parameters.get("update product")));
            }
            else
            {
                preparedStatement.setString(2, parameters.get("update product"));
            }

            // Executes the update query.
            preparedStatement.executeUpdate();

            // Gets the updated product from the database and returns it as a response.
            query = String.format("SELECT * FROM fooditems WHERE %s = ?", productToUpdate);
            preparedStatement = connection.prepareStatement(query);
            if (field == isCatalogue)
            {
                preparedStatement.setInt(1, Integer.parseInt(parameters.get("update product")));
            }
            else
            {
                preparedStatement.setString(1, parameters.get("update product"));
            }
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
        if (quantity == 0)
        {
            result = "Item has been deleted.";
        }
        else if (!response.isEmpty())
        {
            result = "Item has been updated.";

            // Checks which field has been violated.
            for (String violation : listOfViolations)
            {
                if (response.getFirst().contains(violation))
                {
                    result = "Invalid " + violation.substring(0, violation.indexOf("_")) + ".";
                }
            }
        }
        else
        {
            result = "No item has been updated.";
        }

        return result;
    }
}
