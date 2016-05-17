package ny2.kdbjfxviewer.util;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 *
 * @see https://community.oracle.com/thread/2621389
 *
 * @param <S>
 * @param <T>
 */
public class DragSelectionCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Override
    public TableCell<S, T> call(final TableColumn<S, T> col) {
        return new DragSelectionCell();
    }

    public class DragSelectionCell extends TableCell<S, T> {

        public DragSelectionCell() {
            setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    startFullDrag();
                    getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
                }
            });
            setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
                @Override
                public void handle(MouseDragEvent event) {
                    getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
                }

            });
        }

        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else {
                setText(item.toString());
            }
        }

    }

}
