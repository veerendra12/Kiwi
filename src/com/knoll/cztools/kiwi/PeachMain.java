package com.knoll.cztools.kiwi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PeachMain extends Application {

    public static void main(String[] args) {
        launch(PeachMain.class, args);
    }
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Kiwi - Integrated Configurator Development");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/media/images/Kiwi Logo.gif")));
		
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		primaryStage.setX(bounds.getMinX());
		primaryStage.setY(bounds.getMinY());
		primaryStage.setWidth(bounds.getWidth());
		primaryStage.setHeight(bounds.getHeight());

		Parent root = FXMLLoader.load(getClass().getResource("/com/knoll/cztools/kiwi/view/PeachUI.fxml"));
		Scene scene = new Scene(root, 1020, 700);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}