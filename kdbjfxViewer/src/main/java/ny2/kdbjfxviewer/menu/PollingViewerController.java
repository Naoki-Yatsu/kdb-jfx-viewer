package ny2.kdbjfxviewer.menu;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import ny2.kdbjfxviewer.kdb.KdbDao;
import ny2.kdbjfxviewer.kdb.KdbUtils;
import ny2.kdbjfxviewer.util.DialogUtils;
import ny2.kdbjfxviewer.util.SystemUtility;

@Scope("prototype")
@Component
public class PollingViewerController {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KdbDao kdbDao;

    //
    // FXML
    //

    @FXML
    private ToggleButton pollingToggle;
    @FXML
    private ComboBox<Integer> pollingTimeCombo;
    @FXML
    private TextArea queryArea;
    @FXML
    private TableView<ObservableList<Object>> tableView;

    @FXML
    private AnchorPane chartPane;

//    @FXML
//    private LineChart<Object, Object> chart;

    //
    // Others
    //

    private Service<Void> pollingService;

    // //////////////////////////////////////
    // Initialize
    // //////////////////////////////////////

    @FXML
    public void initialize() {
        logger.info("{} initializd.", getClass().getSimpleName());

        // pollingTimeCombo
        pollingTimeCombo.getItems().addAll(1, 2, 5, 10, 30, 60, 300);
        pollingTimeCombo.getSelectionModel().select(Integer.valueOf(5));

        // polling service
        pollingService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        while (true) {
                            // Stop
                            if (!pollingToggle.isSelected()) {
                                break;
                            }
                            Integer pollingTime = pollingTimeCombo.getSelectionModel().getSelectedItem();
                            if (pollingTime > 0) {
                                SystemUtility.waitSleep(pollingTime * 1000);
                                logger.info("Task executed.");
                                Platform.runLater(() -> executeQueryInternal());
                            } else {
                                DialogUtils.showWarnDialog("Polling time must be over 0.");
                                break;
                            }
                        }
                        return null;
                    }
                };
                return task;
            }
        };
    }


    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @FXML
    void executePolling(ActionEvent event) {
        if (pollingToggle.isSelected()) {
            DialogUtils.showInformationDialog("Is selected.");
            pollingService.restart();
        }
    }

    // Event Listener on TextArea[#queryArea].onKeyPressed
    @FXML
    public void executeQuery(KeyEvent event) {
        // Execute Query on Ctrl+Enter
        if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
            executeQueryInternal();
            createChart();
        }
    }

    /**
     * Execute Query
     * @return
     */
    private boolean executeQueryInternal() {
        String query = queryArea.getText();
        if (!StringUtils.isBlank(query)) {
            logger.info("execute query. {}", query.length() <= 100 ? query : query.substring(0, 101));
            Object res = kdbDao.query(query);
            KdbUtils.convertQResuktToTableView(tableView, res);
            return true;
        } else {
            DialogUtils.showWarnDialog("Query is not set.");
            return false;
        }
    }


    /**
     * Create Chart
     */
    private void createChart() {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Object, Double> lineChart = new LineChart(xAxis, yAxis);

        ObservableList<XYChart.Series<Object, Double>> seriesList = FXCollections.observableArrayList();
        Series<Object, Double> series1 = new Series<>();
        Series<Object, Double> series2 = new Series<>();
        Series<Object, Double> series3 = new Series<>();
        Series<Object, Double> series4 = new Series<>();

        series1.setName("Series1");
        series2.setName("Series2");
        series3.setName("Series3");
        series4.setName("Series4");

        ObservableList<ObservableList<Object>> items = tableView.getItems();
        for (int i = 0; i < items.size(); i++) {
            ObservableList<Object> item = items.get(i);
            series1.getData().add(new XYChart.Data(item.get(0).toString(), item.get(1)));
            series2.getData().add(new XYChart.Data(item.get(0).toString(), item.get(2)));
            series3.getData().add(new XYChart.Data(item.get(0).toString(), item.get(3)));
            series4.getData().add(new XYChart.Data(item.get(0).toString(), item.get(4)));
        }
        seriesList.addAll(series1, series2, series3, series4);
        lineChart.setData(seriesList);
        chartPane.getChildren().add(lineChart);
    }


//    private void setupTable(QTable qTable) {
//        // Add Listener
//        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//                    System.out.println(observable.toString() + " chosen in TableView");
//                });
//    }


}
