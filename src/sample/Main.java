package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setMinWidth(520);
        primaryStage.setMinHeight(400);
        primaryStage.setMaxWidth(1200);
        primaryStage.setMaxHeight(800);
        primaryStage.setTitle("串口调试助手 v1.0");
        Image image= new Image(this.getClass().getResource("/res/efg_icon.png").toString(), 100, 100, false, false);
        primaryStage.getIcons().add(image);
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();

    }

	@Override
	public void stop() throws Exception {
		super.stop();
		System.exit(1);
	}


	public static void main(String[] args) {
		Thread.currentThread().setName("我是主线程");
		System.out.println("controler->"+Thread.currentThread());
        launch(args);
    }
}

