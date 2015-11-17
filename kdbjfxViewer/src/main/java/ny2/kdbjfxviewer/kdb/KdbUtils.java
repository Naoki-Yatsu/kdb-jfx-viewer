package ny2.kdbjfxviewer.kdb;

import java.lang.reflect.Array;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exxeleron.qjava.QDictionary;
import com.exxeleron.qjava.QKeyedTable;
import com.exxeleron.qjava.QReaderException;
import com.exxeleron.qjava.QTable;
import com.exxeleron.qjava.QTable.Row;
import com.exxeleron.qjava.QType;
import com.exxeleron.qjava.QWriterException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Kdb Utility
 */
public class KdbUtils {

    private static final Logger logger = LoggerFactory.getLogger(KdbUtils.class);

    public static boolean isQTable(Object qResult) {
        if (getQType(qResult) == QType.TABLE) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isQDictionary(Object qResult) {
        if (getQType(qResult) == QType.DICTIONARY) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isQList(Object qResult) {
        try {
            Class<?> clazz = QType.getType(getQType(qResult));
            if (clazz.isArray()) {
                return true;
            } else {
                return false;
            }
        } catch (QReaderException e) {
            logger.error("", e);
            return false;
        }
    }

    public static void convertQResuktToTableView(TableView<ObservableList<Object>> tableView, Object qResult) {
        try {
            QType qType = QType.getQType(qResult);
            switch (qType) {
                case TABLE:
                    convertToTableView(tableView, (QTable) qResult);
                    break;
                case KEYED_TABLE:
                    convertToTableView(tableView, (QKeyedTable) qResult);
                    break;
                case DICTIONARY:
                    convertToTableView(tableView, (QDictionary) qResult);
                    break;
                default:
                    if (isQList(qResult)) {
                        convertListToTableView(tableView, qResult);
                    } else {
                        convertAtomToTableView(tableView, qResult);
                    }
                    break;
            }

        } catch (QWriterException e) {
            logger.error("", e);
            throw new RuntimeException(qResult.toString() + "is NOT Q Object.");
        }
    }

    /**
     * Convert QTable into TableView
     * @param qTable
     * @return
     */
    public static TableView<ObservableList<Object>> convertToTableView(TableView<ObservableList<Object>> tableView, QTable qTable) {
        clearTableView(tableView);

        // Columns
        for (int i = 0; i < qTable.getColumnsCount(); i++) {
            final int j = i;
            TableColumn<ObservableList<Object>, String> tableColumn = new TableColumn<>(qTable.getColumns()[i]);
            tableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            tableView.getColumns().add(tableColumn);
        }

        // Data
        ObservableList<ObservableList<Object>> allData = FXCollections.observableArrayList();
        for (Row row : qTable) {
            ObservableList<Object> rowData = FXCollections.observableArrayList();
            for (Object item : row) {
                rowData.add(item);
            }
            allData.add(rowData);
        }
        tableView.getItems().addAll(allData);
        return tableView;
    }

    /**
     * Convert QDictionary into TableView
     * @param dict
     * @return
     */
    @SuppressWarnings("unchecked")
    public static TableView<ObservableList<Object>> convertToTableView(TableView<ObservableList<Object>> tableView, QDictionary dict) {
        clearTableView(tableView);

        // Add column
        TableColumn<ObservableList<Object>, String> keyColumn = new TableColumn<>("key");
        keyColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0).toString()));
        TableColumn<ObservableList<Object>, String> valueColumn = new TableColumn<>("value");
        valueColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(1).toString()));
        tableView.getColumns().addAll(keyColumn, valueColumn);

        // Add Data
        ObservableList<ObservableList<Object>> allData = FXCollections.observableArrayList();
//        Iterator<KeyValuePair> iterator = dict.iterator();
//        while (iterator.hasNext()) {
//            KeyValuePair keyValue = iterator.next();
//            allData.add(FXCollections.observableArrayList(keyValue.getKey(), keyValue.getValue()));
//        }
//        tableView.getItems().addAll(allData);

        dict.forEach(keyValue -> {
            ObservableList<Object> rowData = FXCollections.observableArrayList(keyValue.getKey(), keyValue.getValue());
            allData.add(rowData);
        });
        tableView.getItems().addAll(allData);
        return tableView;
    }


    /**
     * Convert QKeyedTable into TableView
     * @param tableView
     * @param dict
     * @return
     */
    public static TableView<ObservableList<Object>> convertToTableView(TableView<ObservableList<Object>> tableView, QKeyedTable keyedTable) {
        clearTableView(tableView);
        QTable qTable1 = keyedTable.getKeys();
        QTable qTable2 = keyedTable.getValues();

        // Add column
        // keys
        for (int i = 0; i < qTable1.getColumnsCount(); i++) {
            final int index = i;
            TableColumn<ObservableList<Object>, String> column = new TableColumn<>(qTable1.getColumns()[i]);
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index).toString()));
            tableView.getColumns().add(column);
        }
        // values
        for (int i = 0; i < qTable2.getColumnsCount(); i++) {
            final int index = i + qTable1.getColumnsCount();
            TableColumn<ObservableList<Object>, String> column = new TableColumn<>(qTable2.getColumns()[i]);
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index).toString()));
            tableView.getColumns().add(column);
        }

        // Add Data
        ObservableList<ObservableList<Object>> allData = FXCollections.observableArrayList();
        for (int i = 0; i < qTable1.getRowsCount(); i++) {
            ObservableList<Object> rowData = FXCollections.observableArrayList();
            for (Object item : qTable1.get(i)) {
                rowData.add(item);
            }
            for (Object item : qTable2.get(i)) {
                rowData.add(item);
            }
            allData.add(rowData);
        }
        tableView.getItems().addAll(allData);
        return tableView;
    }

    /**
     * Convert List(Array) into TableView
     * @param qTable
     * @return
     */
    public static TableView<ObservableList<Object>> convertListToTableView(TableView<ObservableList<Object>> tableView, Object arrayObj) {
        if (!isQList(arrayObj)) {
            throw new RuntimeException(arrayObj.toString() + " is NOT List Object.");
        }
        clearTableView(tableView);

        // Add column
        TableColumn<ObservableList<Object>, String> column = new TableColumn<>("Result [" + getQType(arrayObj).name().toLowerCase() + "]");
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0).toString()));
        tableView.getColumns().add(column);

        // Add Data
        ObservableList<ObservableList<Object>> allData = FXCollections.observableArrayList();
        for (int i = 0; i < Array.getLength(arrayObj); i++) {
            ObservableList<Object> rowData = FXCollections.observableArrayList(Array.get(arrayObj, i));
            allData.add(rowData);
        }
        tableView.getItems().addAll(allData);
        return tableView;
    }

    /**
     * Convert atom (or others) into TableView
     * @param qTable
     * @return
     */
    public static TableView<ObservableList<Object>> convertAtomToTableView(TableView<ObservableList<Object>> tableView, Object obj) {
        clearTableView(tableView);

        // Add column
        TableColumn<ObservableList<Object>, String> column = new TableColumn<>("Result [" + getQType(obj).name().toLowerCase() + "]");
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0).toString()));
        tableView.getColumns().add(column);

        // Add Data
        ObservableList<ObservableList<Object>> allData = FXCollections.observableArrayList();
        //allData.add(FXCollections.observableArrayList("[" + getQType(obj).name().toLowerCase() + "]"));
        allData.add(FXCollections.observableArrayList(obj));
        tableView.getItems().addAll(allData);
        return tableView;
    }


    /**
     * clear last result of TableView
     * @param tableView
     */
    private static void clearTableView(TableView<?> tableView) {
        tableView.getColumns().clear();
        tableView.setItems(FXCollections.observableArrayList());
    }


    /**
     * Wrapper of QType#getQType
     * @param obj
     * @return
     */
    private static QType getQType(Object obj) {
        try {
            return QType.getQType(obj);
        } catch (QWriterException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }
}
