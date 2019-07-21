package ve.com.usbac.namely;

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
    private final String ADD_START = "Start";
    private final String ADD_END = "End";
    private final String INVERSECASE = "Inverse";
    private final String SUCCESS = "Changes applied!";
    
    Model model;
    boolean previewActive;
    
    @FXML
    protected Text folderPath, itemsQuantity;
    @FXML
    protected TextField separator, renameOriginal, renameReplacement, extensionField, sizeField, regexInput, addText;
    @FXML
    protected TableView table;
    @FXML
    protected TabPane tabPane;
    @FXML
    protected DatePicker datePicker;
    @FXML
    protected CheckBox modifyFormat;
    @FXML
    private TableColumn tableName, tableModified, tableSize;
    @FXML
    private ToggleButton previewButton;
    @FXML
    private Button recursiveButton, spacingOption, regexInfo, aboutButton, dateFilter, sizeFilter;
    @FXML
    private ImageView recursiveImage;
    @FXML
    private ComboBox addOption, casesOption;

    
    @FXML
    private void selectFolder() {
        model.directoryChooser.setTitle(DIRECTORY);
        model.directory = model.directoryChooser.showDialog(null);
        
        if (model.directory == null) {
            return;
        }
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        previewButton.setText(PREVIEW);
        previewActive = false;
        model.updateListView(false);
    }
    
    
    @FXML 
    private void switchRecursive() {
        model.recursive = !model.recursive;
        recursiveImage.setOpacity(model.recursive ? 1f : 0.5f);     
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
        model.spaceInChangeOrder = spacingOption.getText().equals(NO_SPACING);
        spacingOption.setText(spacingOption.getText().equals(NO_SPACING) ? SPACING : NO_SPACING);
        updateListView();
    }
   
    
    @FXML
    private void switchAddOption() {
        updateAddOption();
        updateListView();
    }

    
    @FXML
    private void switchCasesOption() {
        updateCasesOption();
        updateListView();
    }
    
    
    @FXML
    private void switchModifyFormat() {
        updateListView();
    }
        
        
    @FXML
    private void updateList() {
        updateListView();
    }
    
    
    private void updateCasesOption() {
        model.casesOptionSelected = casesOption.getSelectionModel().getSelectedIndex();
    }
    
    
    private void updateAddOption() {
        model.addOptionSelected = addOption.getSelectionModel().getSelectedIndex() == 0; 
    }
    
    
    private void updateListView() {
        if (previewActive) {
            model.updateListView(previewActive);
        }
    }
    
    
    @FXML
    private void switchDateFilter() {
        dateFilter.setText(dateFilter.getText().equals(OLDER_MSG) ? NEWER_MSG : OLDER_MSG);
        model.setDateFilter(dateFilter.getText());
        model.updateListView(previewActive);
    }
    
    
    @FXML
    private void switchSizeFilter() {
        sizeFilter.setText(sizeFilter.getText().equals(BIGGER_MSG) ? SMALLER_MSG : BIGGER_MSG);
        
        model.setSizeFilter(sizeFilter.getText());
        model.updateListView(previewActive);
    }
    
        
    @FXML
    private void apply() {
        if (model.directory == null) {
            return;
        }
        
        folderPath.setText(model.directory.getPath());
        if (model.directory.exists()) {
            model.clearFilesNumber();
            model.applyChangesToFiles();
        }
        
        table.setPlaceholder(new Label(SUCCESS));
        previewButton.setText(PREVIEW);
        previewActive = false;
    }
    
    
    public String getRegex() {
        return regexInput.getText();
    }
    
    
    public char getSeparator() {
        return separator.getText().length() > 0 ? separator.getText().charAt(0) : ' ';
    }
    
    
    public int getTab() {
        return tabPane.getSelectionModel().getSelectedIndex();
    }
    
    
    public void initializeTable() {
        tableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableModified.setCellValueFactory(new PropertyValueFactory<>("modified"));
        tableSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label(NO_FILES));
    }
    
    
    public void initializeToolTips() {
        regexInfo.setTooltip(
            new Tooltip("If the file's name matches the regex modify it, otherwise don't. \n"
                      + "If this field is empty all files will be modified.")
        );
        aboutButton.setTooltip(
            new Tooltip("Namely v1.6 \n Created by Usbac")
        );
        recursiveButton.setTooltip(
            new Tooltip("Recursive \n When active, the files in the directory's subfolders will be modified too.")
        );
    }
    
    
    public void initializeCasesOption() {
        casesOption.getItems().add(LOWERCASE);
        casesOption.getItems().add(UPPERCASE);
        casesOption.getItems().add(INVERSECASE);
        casesOption.getSelectionModel().selectFirst();
        updateCasesOption();
    }
       
    
    public void initializeAddOption() {
        addOption.getItems().add(ADD_START);
        addOption.getItems().add(ADD_END);
        addOption.getSelectionModel().selectFirst();
        updateAddOption();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = new Model(this);
        recursiveImage.setMouseTransparent(true);
        
        initializeTable();
        
        initializeCasesOption();
        initializeAddOption();

        switchDateFilter();
        switchSizeFilter();
        
        initializeToolTips();
        
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