package ny2.kdbjfxviewer.util;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Pair;

@Component
public class SpringFXMLLoader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext context;


    public Parent loadFXML(Class<?> controllerClass) {
        return loadFXML(controllerClass, Locale.getDefault());
    }

    /**
     * Load FXML file
     * @param controllerClass
     * @param locale
     * @return
     */
    public Parent loadFXML(Class<?> controllerClass, Locale locale) {
        try {
            // FXML Loader
            URL fxml = controllerClass.getResource(controllerClass.getSimpleName().replaceFirst("Controller", "") + ".fxml");
            ResourceBundle resource = getResource(controllerClass, locale);
            FXMLLoader loader = new FXMLLoader(fxml, resource);

            // set controller of SpringBean
            Object controller = context.getBean(controllerClass);
            loader.setController(controller);

            // Load FXML
            Parent root = loader.load();
            return root;
        } catch (IOException e) {
            logger.error("Failed to load FXML file of " + controllerClass.getSimpleName(), e);
            return null;
        }
    }

    public <C> Pair<Parent, C> loadFXMLWithController(Class<C> controllerClass, Locale locale) {
        try {
            // FXML Loader
            URL fxml = controllerClass.getResource(controllerClass.getSimpleName().replaceFirst("Controller", "") + ".fxml");
            FXMLLoader loader = new FXMLLoader(fxml, null);

            // set controller of SpringBean
            C controller = context.getBean(controllerClass);
            loader.setController(controller);

            // Load FXML
            Parent root = loader.load();
            return new Pair<>(root, controller);
        } catch (IOException e) {
            logger.error("Failed to load FXML file of " + controllerClass.getSimpleName(), e);
            return null;
        }
    }

    public static ResourceBundle getResource(Class<?> controllerClass, Locale locale) {
        try {
            if(locale == null) {
                return ResourceBundle.getBundle(controllerClass.getCanonicalName());
            } else {
                return ResourceBundle.getBundle(controllerClass.getCanonicalName(), locale);
            }
        } catch(MissingResourceException e) {
            return null;
        }
    }
}
