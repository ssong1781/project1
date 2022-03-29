package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerMain extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("Server.fxml")
		);
		Parent root = loader.load();
		ServerController server = loader.getController();
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Server");
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event->{
			server.stopServer();
		});
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
