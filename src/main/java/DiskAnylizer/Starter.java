package DiskAnylizer;

import DiskAnylizer.ApplicationLogic.Analyzer;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class Starter extends Application {
    private Stage stage;
    private Map<String, Long> sizes;
    private ObservableList<PieChart.Data> pieChartDate  = FXCollections.observableArrayList();
    private PieChart pieChart;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("Disk Analyzer");

        Button button = new Button("Choose directory");
        button.setOnAction(event ->  {
            File file = new DirectoryChooser().showDialog(stage);
            String absolutePath = file.getAbsolutePath();
            sizes = new Analyzer().calculateDirectorySize(Path.of(absolutePath));
            buildChart(absolutePath);
        });

        StackPane pane = new StackPane();
        pane.getChildren().add(button);
        stage.setScene(new Scene(pane, 300, 250));
        stage.show();




    }

    private void buildChart(String absolutePath) {
        pieChart = new PieChart(pieChartDate);

        refillChart(absolutePath);

        Button button = new Button(absolutePath);
        button.setOnAction(event -> refillChart(absolutePath));

        BorderPane pane = new BorderPane();
        pane.setTop(button);
        pane.setCenter(pieChart);

        stage.setScene(new Scene(pane, 900, 600));
        stage.show();
    }

    private void refillChart(String absolutePath) {
        pieChartDate.clear();
        pieChartDate.addAll(
                sizes.entrySet()
                        .parallelStream()
                        .filter(entry -> {
                            Path parent = Path.of(entry.getKey()).getParent();
                            return parent != null && parent.toString().equals(absolutePath);
                        })
                        .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList())
        );
        pieChart.getData().forEach(data -> {
            data
                    .getNode()
                    .addEventHandler(
                            MouseEvent.MOUSE_PRESSED,
                            event -> refillChart(data.getName())
                    );
        });
    }
}




