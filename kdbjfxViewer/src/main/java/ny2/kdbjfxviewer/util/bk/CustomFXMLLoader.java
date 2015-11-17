package ny2.kdbjfxviewer.util.bk;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.context.ApplicationContext;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;

/**
 * http://d.hatena.ne.jp/tatsu-no-toshigo/20130503
 */
public class CustomFXMLLoader {

    /** Stringのアプリケーションコンテキスト */
    private static ApplicationContext applicationContext;

    /**
      *  Controllerに対応するリソースファイルを取得す
      */
     public static ResourceBundle getResource(final Class<?> controllerClass, final Locale locale) {
         try {
             if(locale == null) {
                 return ResourceBundle.getBundle(controllerClass.getCanonicalName());
             } else {
                 return ResourceBundle.getBundle(controllerClass.getCanonicalName(), locale);
             }
         } catch(MissingResourceException e) {
             // プロパティファイルがない場合はnullを返す
             return null;
         }
     }

     /**
      * コントローラを伴うFXMLを読み込む。
      */
     //@SuppressWarnings("unchecked")
     public static <N extends Node, C> NodeControllerContainer<N, C> loadNodeAndController(final Class<N> nodeClass, final Class<C> controllerClass, final Locale locale) {
         try {
             final FXMLLoader loader = new FXMLLoader();

             // リソースの設定
             ResourceBundle resources = getResource(controllerClass, locale);
             if(resources != null) {
                 loader.setResources(resources);
             }

             // FXMLの取得
             String fxmlPath = controllerClass.getCanonicalName().replaceFirst("Controller", "").concat(".fxml");
             N node = (N) loader.load(CustomFXMLLoader.class.getResourceAsStream(fxmlPath));

             // Controllerの取得
             C controller = loader.getController();

             return new NodeControllerContainer<N, C>(node, controller);
         } catch (IOException e) {
             throw new RuntimeException(String.format("fail load fxml : %s", controllerClass.getClass().getCanonicalName()), e);
         }
     }

     /**
      * コントローラを伴うFXMLを読み込む。
      * <p>ControllerはSpringコンテナから取得する
      */
     @SuppressWarnings("unchecked")
     public static <N extends Node, C> NodeControllerContainer<N, C> loadNodeAndControllerWithSpringInjection(
             final Class<N> nodeClass, final Class<C> controllerClass, final Locale locale) {

         final FXMLLoader loader = new FXMLLoader();

         // リソースの設定
         ResourceBundle resources = getResource(controllerClass, locale);
         if(resources != null) {
             loader.setResources(resources);
         }

         // Spring用のControllerのCallbackの設定
         loader.setControllerFactory(new Callback<Class<?>, Object>() {
             @Override
             public Object call(Class<?> param) {
                 // ControllerをSprign Beanコンテナから取得する
                 return applicationContext.getBean(controllerClass);
             }
         });

         // FXMLの取得
         final String fxmlPath = controllerClass.getCanonicalName().replaceFirst("Controller", "").concat(".fxml");
         final Node node;
         try {
             node = (Node) loader.load(CustomFXMLLoader.class.getResourceAsStream(fxmlPath));

         } catch (IOException e) {
             throw new RuntimeException(
                     String.format("fail load fxml : %s", controllerClass.getClass().getCanonicalName()),
                     e);
         }

         // Controllerの取得
         final C controller = loader.getController();

         return new NodeControllerContainer(node, controller);

     }

 }
