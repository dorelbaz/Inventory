package client.inventory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/*
    Launches the client's GUI.
 */
public class InventoryApplication extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(InventoryApplication.class.getResource("Inventory.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1045, 710);
        stage.setTitle("Inventory");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}