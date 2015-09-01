package com.temporaryteam.noticeditor;

import com.temporaryteam.noticeditor.controller.NoticeController;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("NoticEditor");
		initRootLayout();
	}
	
	/**
	 * Initializes root layout
	 */
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"),
					ResourceBundle.getBundle("resources.i18n.Language", Locale.getDefault()));
			Scene scene = new Scene(loader.load());
			primaryStage.setScene(scene);
			NoticeController controller = (NoticeController) loader.getController();
			controller.setApplication(this);
			primaryStage.setOnCloseRequest(controller::onExit);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns primary stage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
