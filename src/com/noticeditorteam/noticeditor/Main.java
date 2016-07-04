package com.noticeditorteam.noticeditor;

import com.noticeditorteam.noticeditor.controller.NoticeController;
import com.noticeditorteam.noticeditor.model.Prefs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

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
                    ResourceBundle.getBundle("resources.i18n.Language", Prefs.getLocale()));
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            NoticeController controller = loader.getController();
            controller.setApplication(this);
            NoticeController.getNoticeTreeViewController().setMain(this);
            NoticeController.getNoticeViewController().setMain(this);
            primaryStage.setOnCloseRequest(controller::onExit);
            primaryStage.show();
        } catch (Exception e) {
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
