package ny2.kdbjfxviewer.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.yaml.snakeyaml.Yaml;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ny2.kdbjfxviewer.util.SpringFXMLLoader;

public class StartApp extends Application {

    private static final String SPRING_CONFIG_FILE = "classpath:applicationContext.xml";

    private static final Logger logger = LoggerFactory.getLogger(StartApp.class);

    @Override
    public void start(Stage primaryStage) {
        logger.info("Start Application.");
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE);
        SpringFXMLLoader fxmlLoader = context.getBean(SpringFXMLLoader.class);

        Parent menu = fxmlLoader.loadFXML(MenuController.class);
        // Parent menu = FXMLLoader.load(getClass().getResource("Menu.fxml"));
        Scene scene = new Scene(menu, 250, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Menu");
        primaryStage.setResizable(false);
        primaryStage.show();
    }



    public static void main(String[] args) {
        // loadYaml();
        launch(args);
    }










    public static void loadYaml() {
        try {
            Yaml yaml = new Yaml();
            // File file = new File("test.yml");
            // FileReader filereader = new FileReader(file);
            Iterable<Object> loadAll = yaml.loadAll(StartApp.class.getResourceAsStream("/test.yml"));
            for (Object object : loadAll) {
                System.out.println(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
