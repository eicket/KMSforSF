// Erik Icket, ON4PB - 2024
package kmsforsf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Start extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("/kmsforsf/main.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("KMS for Superfox by ON4PB");
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
