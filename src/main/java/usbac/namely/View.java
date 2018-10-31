package usbac.namely;

import java.net.URL;
import java.util.ResourceBundle; 
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public final class View implements Initializable {
    
    Controller controller;
    boolean previewActive;
    
    @FXML
    protected Text folderPath, itemsQuantity;
    @FXML
    protected TextField separator, renameOriginal, renameReplacement, extensionField, sizeField, regexInput;
    @FXML
    protected TableView table;
    @FXML
    protected TabPane tabPane;
    @FXML
    protected DatePicker datePicker;
    @FXML
    private TableColumn tableName, tableModified, tableSize;
    @FXML
    private ToggleButton previewButton;
    @FXML
    private Button recursiveButton, spacingOption, regexInfo, aboutButton, dateFilter, sizeFilter;
    @FXML
    private ImageView recursiveImage;
    @FXML
    private ComboBox casesOption;

    
    @FXML
    private void selectFolder() {
        controller.directoryChooser.setTitle("Choose a Directory...");
        controller.directory = controller.directoryChooser.showDialog(null);
        if (controller.directory == null) 
            return;
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        previewButton.setText("Preview");
        previewActive = false;
        controller.updateListView(false);
    }
    
    
    @FXML 
    private void switchRecursive() {
        controller.recursive = !controller.recursive;
        recursiveImage.setOpacity(controller.recursive? 1f:0.5f);     
        controller.updateListView(previewActive);
    }


    @FXML
    private void switchPreview() {
        if (previewButton.getText().equals("Preview")) {
            previewButton.setText("Original");
            previewActive = true;
        } else {
            previewButton.setText("Preview");
            previewActive = false;
        }
        controller.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchSpacing() {
        if (spacingOption.getText().equals("A-B")) {
            spacingOption.setText("A - B");
            controller.isSpaceInChangeOrder = true;
        } else {
            spacingOption.setText("A-B");
            controller.isSpaceInChangeOrder = false;
        }
        if (previewActive)
            controller.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchCasesOption() {
        controller.casesOptionSelected = casesOption.getSelectionModel().getSelectedIndex(); 
        if (previewActive)
            controller.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchDateFilter() {
        if (dateFilter.getText().equals("Older than"))
            dateFilter.setText("Newer than");
        else
            dateFilter.setText("Older than");
        controller.setDateFilter(dateFilter.getText());
        controller.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchSizeFilter() {
        if (sizeFilter.getText().equals("Bigger than"))
            sizeFilter.setText("Smaller than");
        else
            sizeFilter.setText("Bigger than");
        controller.setSizeFilter(sizeFilter.getText());
        controller.updateListView(previewActive);
    }
    
        
    @FXML
    private void apply() {
        if (controller.directory == null) 
            return;
        folderPath.setText(controller.directory.getPath());
        if (controller.directory.exists()) {
            controller.clearFilesNumber();
            table.setPlaceholder(new Label("Loading"));
            controller.applyChangesToFiles(controller.directory.listFiles());
        }
        table.setPlaceholder(new Label("Changes applied!"));
        previewButton.setText("Preview");
        previewActive = false;
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        recursiveImage.setMouseTransparent(true);
        controller = new Controller(this);
        tableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableModified.setCellValueFactory(new PropertyValueFactory<>("modified"));
        tableSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No files"));
        casesOption.getItems().add("All Lowercase");
        casesOption.getItems().add("All Uppercase");
        casesOption.getItems().add("Inverse");
        casesOption.getSelectionModel().selectFirst();
        switchDateFilter();
        switchSizeFilter();
        regexInfo.setTooltip(
            new Tooltip("If the file's name matches the regex modify it, otherwise don't. \n"
                    + "If this field is empty all files will be modified.")
        );
        aboutButton.setTooltip(
            new Tooltip("Namely v1.4 \n Created by Usbac")
        );
        recursiveButton.setTooltip(
            new Tooltip("Recursive \n When active, the files in the directory's subfolders will be modified too.")
        );
        //Load Original File view when moving between Tabs
        tabPane.getSelectionModel()
               .selectedItemProperty()
               .addListener((ObservableValue<?extends Tab> old, Tab oldTab, Tab newTab) -> {
            previewButton.setText("Preview");
            previewActive = false;
            controller.updateListView(previewActive);
        });
    }    
}