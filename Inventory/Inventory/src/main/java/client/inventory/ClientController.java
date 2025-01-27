package client.inventory;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
    Controller class for client's GUI.
 */
public class ClientController 
{

    @FXML
    private TableColumn<Product, Long> catalogueColumn;

    @FXML
    private TextField catalogueField;

    @FXML
    private TableColumn<Product, String> exDateColumn;

    @FXML
    private TextField exDateField;

    @FXML
    private TextField deleteProduct;

    @FXML
    private TableColumn<Product, Integer> maxTempColumn;

    @FXML
    private TextField maxTempField;

    @FXML
    private TableColumn<Product, Integer> minTempColumn;

    @FXML
    private TextField minTempField;

    @FXML
    private TextField newValueField;

    @FXML
    private TableColumn<Product, Integer> priceColumn;

    @FXML
    private TextField priceField;

    @FXML
    private TableColumn<Product, String> prodDateColumn;

    @FXML
    private TextField prodDateField;

    @FXML
    private MenuItem quantityOption;

    @FXML
    private MenuItem exDateOption;

    @FXML
    private MenuItem prodDateOption;

    @FXML
    private MenuItem priceOption;

    @FXML
    private Label message;

    @FXML
    private TableColumn<Product, String> productColumn;

    @FXML
    private TextField productField;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Product> tableView;

    @FXML
    private TextField updateProduct;

    @FXML
    private Label updateField;

    @FXML
    private MenuButton updateOptions;

    private Map<String, ByteArrayWrapper> parameters;
    private String menuItemSelected = "quantity";
    private String ip;
    private int port;
    private RSA rsa;

    public void initialize() throws Exception
    {
        parameters = new HashMap<>();

        // Initializes the table's columns and the data types they hold (the product's fields).
        productColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("product"));
        catalogueColumn.setCellValueFactory(new PropertyValueFactory<Product, Long>("catalogue"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("quantity"));
        prodDateColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("productionDate"));
        exDateColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("expiryDate"));
        minTempColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("minTemperature"));
        maxTempColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("maxTemperature"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("price"));

        // Sets an event handler for the switching of the update options.
        EventHandler<ActionEvent> event = new EventHandler<>() {
            public void handle(ActionEvent e)
            {
                menuItemSelected = ((MenuItem)e.getSource()).getId();
                updateOptions.setText(((MenuItem)e.getSource()).getText());
                updateField.setText(((MenuItem)e.getSource()).getText());
            }
        };
        quantityOption.setOnAction(event);
        prodDateOption.setOnAction(event);
        exDateOption.setOnAction(event);
        priceOption.setOnAction(event);

        // Limits the amount of characters within text fields.
        limitTextFields();

        // Sets IP and port.
        getServerDetails();

        // Loads client encryption key and server decryption key.
        String publicKey = "/ClientPublicKey.key";
        String privateKey = "/ServerPrivateKey.key"; // src/main/java/client/inventory
        rsa = new RSA(new File("src/main/java/client/inventory/ClientKeys").getAbsolutePath(), publicKey, privateKey, "RSA");

        // Gets the existing information from the server and displays it for the client in the table.
        new ClientThread(this, ip, port, "Initialize", null).start();
    }

    @FXML
    void onAddPressed(ActionEvent event) throws Exception
    {
        // Clears previously held parameters.
        parameters.clear();

        // Gets client input for a new product and encrypts it.
        parameters.put("product", new ByteArrayWrapper(rsa.encrypt(productField.getText())));
        parameters.put("catalogue", new ByteArrayWrapper(rsa.encrypt(catalogueField.getText())));
        parameters.put("quantity", new ByteArrayWrapper(rsa.encrypt(quantityField.getText())));
        parameters.put("prod date", new ByteArrayWrapper(rsa.encrypt(prodDateField.getText())));
        parameters.put("ex date", new ByteArrayWrapper(rsa.encrypt(exDateField.getText())));
        parameters.put("min temp", new ByteArrayWrapper(rsa.encrypt(minTempField.getText())));
        parameters.put("max temp", new ByteArrayWrapper(rsa.encrypt(maxTempField.getText())));
        parameters.put("price", new ByteArrayWrapper(rsa.encrypt(priceField.getText())));

        // Clears input fields.
        productField.clear();
        catalogueField.clear();
        quantityField.clear();
        prodDateField.clear();
        exDateField.clear();
        minTempField.clear();
        maxTempField.clear();
        priceField.clear();

        // Sends the input from the client to the server.
        new ClientThread(this, ip, port, "Add", parameters).start();
    }



    @FXML
    void onSearchPressed(ActionEvent event) throws Exception
    {
        parameters.clear();

        // Gets client input, it can be either product name or its catalogue, and encrypts it.
        parameters.put("find product", new ByteArrayWrapper(rsa.encrypt(searchField.getText())));

        searchField.clear();

        // Sends to the server the product to find, by name or catalogue. The current table will not change if product is not found.
        new ClientThread(this, ip, port,"Search", parameters).start();
    }

    @FXML
    void onUpdatePressed(ActionEvent event) throws Exception
    {
        parameters.clear();

        // Gets client input for the product to update with its field, split between different fields, and encrypts them.
        parameters.put("update product", new ByteArrayWrapper(rsa.encrypt(updateProduct.getText())));
        parameters.put("field to update", new ByteArrayWrapper(rsa.encrypt(menuItemSelected)));
        parameters.put("new value", new ByteArrayWrapper(rsa.encrypt(newValueField.getText())));

        updateProduct.clear();
        newValueField.clear();

        // Sends to the server the product to update, by name or catalogue, and the field to update.
        new ClientThread(this, ip, port,"Update", parameters).start();
    }

    @FXML
    void onPrintAllPressed(ActionEvent event) throws Exception
    {
        // Gets from the server the full list of items in the database.
        new ClientThread(this, ip, port,"Print All", null).start();
    }

    @FXML
    void onDeletePressed(ActionEvent event) throws Exception
    {
        parameters.clear();

        // Gets client input for product, either by name or catalogue, and encrypts it.
        parameters.put("delete product", new ByteArrayWrapper(rsa.encrypt(deleteProduct.getText())));

        deleteProduct.clear();

        // Sends product to server for it to delete.
        new ClientThread(this, ip, port,"Delete", parameters).start();
    }


    /*
        Updates the table accordingly for each of the client's requests, after the server is done processing it.
    */
    public void updateTable(ArrayList<ByteArrayWrapper> responseFromServer) throws Exception
    {
        int TOTAL_FIELDS = 8, PRODUCT = 0, CATALOGUE = 1, QUANTITY = 2, PRODUCTION_DATE = 3, EXPIRY_DATE = 4,
                MAX_TEMPERATURE = 5, MIN_TEMPERATURE = 6, PRICE = 7;

        if (!responseFromServer.isEmpty())
        {
            List<Product> products = new ArrayList<>();

            String[] foodItemFields = new String[TOTAL_FIELDS];
            int i = 0;

            // Gets each encrypted item from the item list sent back by the server, decrypts it, creates a new product representing it and adds to table.
            for (ByteArrayWrapper byteArrayWrapper : responseFromServer)
            {
                // Each foodItemField represents a single field of a product in an orderly manner,
                // every 8 foodItemField represents a full product.
                foodItemFields[i++] = rsa.decrypt(byteArrayWrapper.getByteArray());

                if (i >= TOTAL_FIELDS)
                {
                    // Creates a product object representing an entire food item.
                    Product product = new Product(
                            foodItemFields[PRODUCT],
                            Long.parseLong(foodItemFields[CATALOGUE]),
                            Integer.parseInt(foodItemFields[QUANTITY]),
                            foodItemFields[PRODUCTION_DATE],
                            foodItemFields[EXPIRY_DATE],
                            Float.parseFloat(foodItemFields[MIN_TEMPERATURE]),
                            Float.parseFloat(foodItemFields[MAX_TEMPERATURE]),
                            Float.parseFloat(foodItemFields[PRICE]));

                    // Adds product to the list of products
                    products.add(product);

                    i = 0;
                }
            }

            // Displays list of products in the table.
            tableView.setItems(FXCollections.observableArrayList(products));
        }
    }

    /*
        Sets different character limits within the text fields.
     */
    private void limitTextFields()
    {
        int  PRODUCT_LIMIT = 32, CATALOGUE_LIMIT = 8, QUANTITY_LIMIT = 3, PRODUCTION_DATE_LIMIT = 10, EXPIRY_DATE_LIMIT = 10,
                MAX_TEMPERATURE_LIMIT = 3, MIN_TEMPERATURE_LIMIT = 3, PRICE_LIMIT = 4;

        limitCharactersInTextField(catalogueField, CATALOGUE_LIMIT);
        limitCharactersInTextField(exDateField, EXPIRY_DATE_LIMIT);
        limitCharactersInTextField(deleteProduct, PRODUCT_LIMIT);
        limitCharactersInTextField(maxTempField, MAX_TEMPERATURE_LIMIT);
        limitCharactersInTextField(minTempField, MIN_TEMPERATURE_LIMIT);
        limitCharactersInTextField(newValueField, PRODUCTION_DATE_LIMIT);
        limitCharactersInTextField(priceField, PRICE_LIMIT);
        limitCharactersInTextField(searchField, PRODUCT_LIMIT);
        limitCharactersInTextField(prodDateField, PRODUCTION_DATE_LIMIT);
        limitCharactersInTextField(quantityField, QUANTITY_LIMIT);
        limitCharactersInTextField(updateProduct, PRODUCT_LIMIT);
        limitCharactersInTextField(productField, PRODUCT_LIMIT);
    }


    /*
        Sets a listener for each text field that will monitor the amount of characters entered by the user
        so that they won't exceed the given limit.
     */
    private void limitCharactersInTextField(TextField textField, int limit)
    {
        textField.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue)
            {
                if (textField.getText().length() > limit)
                {
                    String s = textField.getText().substring(0, limit);
                    textField.setText(s);
                }
            }
        });
    }


    /*
        Gets the IP and port information from the ServerDetails file.
     */
    private void getServerDetails() throws FileNotFoundException
    {
        String path = new File("src/main/java/client/inventory/ServerDetails/ServerDetails.txt").getAbsolutePath();
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        ip = scanner.nextLine();
        port = scanner.nextInt();
    }

    /*
        Sets a background task, that will be activated from the ClientThread class,
        that will update the server's message label for the client.
     */
    public void setMessage(ByteArrayWrapper encryptedServerMessage, boolean isConnected)
    {
        Platform.runLater(new Runnable()
        {
            public void run()
            {
                if (isConnected)
                {
                    try
                    {
                        message.setText(rsa.decrypt(encryptedServerMessage.getByteArray()));
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    message.setText("Connection with server lost.");
                }
            }
        });
    }
}
