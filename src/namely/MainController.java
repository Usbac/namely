package namely;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.ResourceBundle; 
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

public class MainController implements Initializable {
    
    ListController listController;
    DirectoryChooser directoryChooser;
    File directory, folder;
    boolean previewActive;

    
    @FXML
    protected Text folderPath;
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
        directoryChooser.setTitle("Choose a Directory...");
        directory = directoryChooser.showDialog(null);
        if (directory!=null) {
            table.getItems().clear();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            previewButton.setText("Preview");
            previewActive = false;
            listController.updateListView(false);
        }
    }
    
    
    @FXML 
    private void switchRecursive() {
        if (listController.recursive) {
            listController.recursive = false;
            recursiveImage.setOpacity(0.5f);
        } else {
            listController.recursive = true;
            recursiveImage.setOpacity(1f);            
        }
        listController.updateListView(previewActive);
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
        listController.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchSpacing() {
        if (spacingOption.getText().equals("A-B")) {
            spacingOption.setText("A - B");
            listController.isSpaceInChangeOrder = true;
        } else {
            spacingOption.setText("A-B");
            listController.isSpaceInChangeOrder = false;
        }
        if (previewActive)
            listController.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchCasesOption() {
        listController.casesOptionSelected = casesOption.getSelectionModel().getSelectedIndex(); 
        if (previewActive)
            listController.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchDateFilter() {
        if (dateFilter.getText().equals("Older than"))
            dateFilter.setText("Newer than");
        else
            dateFilter.setText("Older than");
        listController.setDateFilter(dateFilter.getText());
        listController.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchSizeFilter() {
        if (sizeFilter.getText().equals("Bigger than"))
            sizeFilter.setText("Smaller than");
        else
            sizeFilter.setText("Bigger than");
        listController.setSizeFilter(sizeFilter.getText());
        listController.updateListView(previewActive);
    }
    
        
    @FXML
    private void apply() {
        if (directory != null) {
            folderPath.setText(directory.getPath());
            folder = new File(folderPath.getText());
            if (folder.exists()) {
                table.getItems().clear();
                File[] listOfFiles = folder.listFiles();
                ListFiles(listOfFiles, true);
            }
            table.setPlaceholder(new Label("Changes applied!"));
        }
        previewButton.setText("Preview");
        previewActive = false;
    }
    
    
    public void ListFiles(File[] listOfFiles, boolean preview) {
        for (File file: listOfFiles)
            if (file.isFile())
                file.renameTo(listController.getFilePreview(file, true));
            else if (listController.recursive)
                ListFiles(file.listFiles(), true);
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        recursiveImage.setMouseTransparent(true);
        listController = new ListController(this);
        directoryChooser = new DirectoryChooser();
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
            new Tooltip("Namely \nCreated by Usbac")
        );
        recursiveButton.setTooltip(
            new Tooltip("Recursive \nWhen active, the files in the directory's subfolders will be modified too.")
        );
        //Load Original File view when moving between Tabs
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<?extends Tab> old, Tab oldTab, Tab newTab) {
                previewButton.setText("Preview");
                previewActive = false;
                listController.updateListView(previewActive);
            }
        });
    }    
}