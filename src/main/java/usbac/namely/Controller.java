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

public final class Controller implements Initializable {
    
    private final String PREVIEW = "Preview";
    private final String ORIGINAL = "Original";
    private final String NO_FILES = "No files";
    private final String DIRECTORY = "Choose a Directory...";
    private final String SPACING = "A - B";
    private final String NO_SPACING = "A-B";
    private final String OLDER_MSG = "Older than";
    private final String NEWER_MSG = "Newer than";
    private final String BIGGER_MSG = "Bigger than";
    private final String SMALLER_MSG = "Smaller than";
    private final String LOWERCASE = "All Lowercase";
    private final String UPPERCASE = "All Uppercase";
    private final String INVERSECASE = "Inverse";
    private final String SUCCESS = "Changes applied!";
    
    Model model;
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
        model.directoryChooser.setTitle(DIRECTORY);
        model.directory = model.directoryChooser.showDialog(null);
        if (model.directory == null) 
            return;
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        previewButton.setText(PREVIEW);
        previewActive = false;
        model.updateListView(false);
    }
    
    
    @FXML 
    private void switchRecursive() {
        model.recursive = !model.recursive;
        recursiveImage.setOpacity(model.recursive? 1f:0.5f);     
        model.updateListView(previewActive);
    }


    @FXML
    private void switchPreview() {
        if (previewButton.getText().equals(PREVIEW)) {
            previewButton.setText(ORIGINAL);
            previewActive = true;
        } else {
            previewButton.setText(PREVIEW);
            previewActive = false;
        }
        model.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchSpacing() {
        if (spacingOption.getText().equals(NO_SPACING)) {
            spacingOption.setText(SPACING);
            model.isSpaceInChangeOrder = true;
        } else {
            spacingOption.setText(NO_SPACING);
            model.isSpaceInChangeOrder = false;
        }
        if (previewActive)
            model.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchCasesOption() {
        model.casesOptionSelected = casesOption.getSelectionModel().getSelectedIndex(); 
        if (previewActive)
            model.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchDateFilter() {
        dateFilter.setText(dateFilter.getText().equals(OLDER_MSG)? NEWER_MSG:OLDER_MSG);
        model.setDateFilter(dateFilter.getText());
        model.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchSizeFilter() {
        if (sizeFilter.getText().equals(BIGGER_MSG))
            sizeFilter.setText(SMALLER_MSG);
        else
            sizeFilter.setText(BIGGER_MSG);
        model.setSizeFilter(sizeFilter.getText());
        model.updateListView(previewActive);
    }
    
        
    @FXML
    private void apply() {
        if (model.directory == null) 
            return;
        
        folderPath.setText(model.directory.getPath());
        if (model.directory.exists()) {
            model.clearFilesNumber();
            model.applyChangesToFiles();
        }
        
        table.setPlaceholder(new Label(SUCCESS));
        previewButton.setText(PREVIEW);
        previewActive = false;
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        recursiveImage.setMouseTransparent(true);
        model = new Model(this);
        
        tableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableModified.setCellValueFactory(new PropertyValueFactory<>("modified"));
        tableSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label(NO_FILES));
        
        casesOption.getItems().add(LOWERCASE);
        casesOption.getItems().add(UPPERCASE);
        casesOption.getItems().add(INVERSECASE);
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
            previewButton.setText(PREVIEW);
            previewActive = false;
            model.updateListView(previewActive);
        });
    }    
}