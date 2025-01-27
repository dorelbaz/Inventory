package client.inventory;

/*
    Product class represents food items sent back by the server.
    This class interacts with the table object (tableView) and its columns objects in the ClientController class.
 */
public class Product
{
    private final String product;

    private final float minTemperature, maxTemperature;

    private final long catalogue;

    private final String productionDate;
    private final String expiryDate;

    private final int quantity;
    private final float price;

    public Product(String product, long catalogue, int quantity, String productionDate, String expiryDate,
                   float maxTemperature, float minTemperature, float price)
    {
        this.product = product;
        this.catalogue = catalogue;
        this.quantity = quantity;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.price = price;
    }

    public String getProduct()
    {
        return  product;
    }

    public long getCatalogue()
    {
        return catalogue;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public String getProductionDate()
    {
        return productionDate;
    }

    public String getExpiryDate()
    {
        return expiryDate;
    }

    public float getMinTemperature()
    {
        return minTemperature;
    }

    public float getMaxTemperature()
    {
        return maxTemperature;
    }

    public float getPrice()
    {
        return price;
    }

    public String toString()
    {
        return product + " " + catalogue + " " + quantity + " " + productionDate + " "
                + expiryDate + " " + minTemperature + " " + maxTemperature + " " + price;
    }

}
