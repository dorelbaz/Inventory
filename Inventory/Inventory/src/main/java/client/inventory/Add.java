package client.inventory;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/*
    Adds a product to the database.
 */
public class Add extends SQLOperation
{
    /*
        Executes the appropriate query and returns the added product (on success).
     */
    public ArrayList<String> execute(Connection connection, Map<String, String> parameters) throws SQLException, ParseException
    {
        ArrayList<String> response = new ArrayList<>();

        // We first examine whether the product already exists in the database.
        String query = "SELECT * FROM fooditems WHERE catalogue = ?";

        // Prepares the query for execution.
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        // Replaces the ? in the query with the product's catalogue.
        preparedStatement.setInt(1, Integer.parseInt(parameters.get("catalogue")));

        // Executes the search query.
        ResultSet resultSet = preparedStatement.executeQuery();

        // If no result has been returned it means that the product does not exist in the database.
        if (!resultSet.next())
        {
            query = "INSERT INTO fooditems (name, catalogue, quantity, production_date, expiry_date, min_temperature, max_temperature, price) VALUES (?,?,?,?,?,?,?,?)";

            preparedStatement = connection.prepareStatement(query);

            // Replaces each of the ? in the query with the corresponding product field.
            preparedStatement.setString(1, parameters.get("product"));
            preparedStatement.setInt(2, Integer.parseInt(parameters.get("catalogue")));
            preparedStatement.setInt(3, Integer.parseInt(parameters.get("quantity")));

            SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy");

            java.util.Date date = sdf1.parse(parameters.get("prod date"));
            preparedStatement.setDate(4, new java.sql.Date(date.getTime()));

            date = sdf1.parse(parameters.get("ex date"));
            preparedStatement.setDate(5, new java.sql.Date(date.getTime()));

            preparedStatement.setFloat(6, Float.parseFloat(parameters.get("min temp")));
            preparedStatement.setFloat(7, Float.parseFloat(parameters.get("max temp")));
            preparedStatement.setFloat(8, Float.parseFloat(parameters.get("price")));

            // Executes the insertion query.
            preparedStatement.executeUpdate();

            // Gets the added product from the database and returns it as a response.
            query = "SELECT * FROM fooditems WHERE catalogue = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(parameters.get("catalogue")));
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
            result = "Item has been added.";

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
            result = "No item has been added.";
        }

        return result;
    }
}
