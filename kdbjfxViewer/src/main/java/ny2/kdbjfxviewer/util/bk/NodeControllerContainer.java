package ny2.kdbjfxviewer.util.bk;

import javafx.scene.Node;

/* Node(Scene)とそれに対するControllerを保持するクラス。
*
* @param <N> JavaFXのノード
* @param <C> Controller
*/
public class NodeControllerContainer<N extends Node, C> {

   /** JavaFXノード */
   private final N node;

   /** Controller */
   private final C controller;

   public NodeControllerContainer(final N node, final C controller) {
       this.node = node;
       this.controller = controller;
   }

   public N getNode() {
       return node;
   }

   public C getController() {
       return controller;
   }
}
