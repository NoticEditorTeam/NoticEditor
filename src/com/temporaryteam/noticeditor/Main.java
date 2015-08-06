package com.temporaryteam.noticeditor;

import com.temporaryteam.noticeditor.controller.NoticeController;
import com.temporaryteam.noticeditor.controller.NoticeSettingsController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	public void Main() {
	}
	
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
			loader.setControllerFactory(new Callback<Class<?>, Object>() {

				NoticeController noticeController;
				NoticeSettingsController noticeSettingsController;
				
				@Override
				public Object call(Class<?> param) {
					if (param == NoticeController.class) {
						noticeController = new NoticeController(Main.this);
						return noticeController;
					} else if (param == NoticeSettingsController.class) {
						noticeSettingsController = new NoticeSettingsController(noticeController);
						return noticeSettingsController;
					}
					return null;
				}
			});
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch(Exception e) {
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
