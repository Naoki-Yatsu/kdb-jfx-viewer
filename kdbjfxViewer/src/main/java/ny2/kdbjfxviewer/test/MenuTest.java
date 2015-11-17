package ny2.kdbjfxviewer.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // FXMLのロード
        Parent root = FXMLLoader.load(getClass().getResource("HelloView.fxml"));

        // シーンの生成
        Scene scene = new Scene(root);

        // ステージにシーンをセット
        stage.setScene(scene);

        // ステージを表示
        stage.show();
    }

    public static void main(String[] args) {
        // JavaFX のスレッドの起動
        launch(args);
    }


}
