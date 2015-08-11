package com.temporaryteam.noticeditor;

import com.temporaryteam.noticeditor.controller.NoticeController;
import com.temporaryteam.noticeditor.controller.NoticeSettingsController;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {

	private Stage primaryStage;

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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"),
					ResourceBundle.getBundle("resources.translate.Language", Locale.getDefault()));
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
						noticeController.setNoticeSettingsController(noticeSettingsController);
						return noticeSettingsController;
					}
					return null;
				}
			});
			Scene scene = new Scene(loader.load());
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
