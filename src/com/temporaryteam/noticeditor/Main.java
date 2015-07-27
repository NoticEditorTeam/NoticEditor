package com.temporaryteam.noticeditor;

import com.temporaryteam.noticeditor.model.Notice;
import com.temporaryteam.noticeditor.view.NoticeController;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Main extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private ObservableList<Notice> noticeData = FXCollections.observableArrayList();

	public void Main() {
		noticeData.add(new Notice("Hello, world!"));
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("NoticEditor");
		initRootLayout();
		showNotice();
	}

	
	/**
	 * Initializes root layout
	 */
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/Main.fxml"));
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
	 * Shows notice
	 */
	public void showNotice() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/Notice.fxml"));
			AnchorPane notice = (AnchorPane) loader.load();
			rootLayout.setCenter(notice);
			NoticeController controller = loader.getController();
			controller.setMain(this);
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

	public ObservableList<Notice> getNoticeData() {
		return noticeData;
	}

	public Notice getCurrentNotice() {
		return noticeData.get(0);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
