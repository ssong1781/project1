package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {
	
	

	@Override
	public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
		Parent root = loader.load();
		ClientController con = loader.getController();
		primaryStage.setScene(new Scene(root));
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event->{
			con.stopClient();
		});
		primaryStage.setTitle("CLIENT");
		primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	
	
	
}
