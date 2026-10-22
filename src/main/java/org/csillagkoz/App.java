package org.csillagkoz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.csillagkoz.controller.ModelAware;
import org.csillagkoz.model.BookingModel;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;
    private static BookingModel model;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        model = new BookingModel();

        scene = new Scene(loadFXML("ticket-booking"), 960, 600);

        stage.setTitle("Csillagköz - Jegyfoglalás");
        stage.setScene(scene);
        stage.show();
    }

    //Ez kezeli az ablakok váltását
    public static void setRoot(String fxml, String title) throws IOException {
        scene.setRoot(loadFXML(fxml));
        if (primaryStage != null && title != null) {
            primaryStage.setTitle(title);
        }
    }

    public static void setRoot(String fxml) throws IOException {
        setRoot(fxml, null);
    }

    //FXML fileok betöltése és inicialázása
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof ModelAware) {
            ((ModelAware) controller).setModel(model);
        }

        return root;
    }

    public static void main(String[] args) {
        launch();
    }
}