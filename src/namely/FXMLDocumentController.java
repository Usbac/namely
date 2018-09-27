package namely;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle; 
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

public class FXMLDocumentController implements Initializable {
    
    DirectoryChooser directoryChooser;
    File directory, folder;
    boolean isSpaceInChangeOrder, previewActive, showFile;
    int casesOptionSelected;
    char charSeparator;
    
    @FXML
    private Text folderPath;
    @FXML
    private TextField separator, renameOriginal, renameReplacement, extensionOriginal, extensionReplacement, regexInput;
    @FXML
    private TableView table;
    @FXML
    private TabPane tabPane;
    @FXML
    private TableColumn tableName, tableModified, tableSize;
    @FXML
    private ToggleButton previewButton;
    @FXML
    private Button spacingOption, regexInfo, aboutButton;
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
            updateListView(false);
        }
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
        updateListView(previewActive);
    }
    
    
    @FXML
    private void switchSpacing() {
        if (spacingOption.getText().equals("A-B")) {
            spacingOption.setText("A - B");
            isSpaceInChangeOrder = true;
        } else {
            spacingOption.setText("A-B");
            isSpaceInChangeOrder = false;
        }
        if (previewActive)
            updateListView(previewActive);
    }
    
    
    @FXML
    private void switchCasesOption() {
        casesOptionSelected = casesOption.getSelectionModel().getSelectedIndex(); 
        if (previewActive)
            updateListView(previewActive);
    }
    
        
    @FXML
    private void apply() {
        if (directory != null) {
            folderPath.setText(directory.getPath());
            folder = new File(folderPath.getText());
            if (folder.exists()) {
                File[] listOfFiles = folder.listFiles();
                table.getItems().clear();
                for (File aux: listOfFiles)
                    if (aux.isFile())
                        aux.renameTo(getFilePreview(aux, true));
            }
        }
        previewButton.setText("Preview");
        previewActive = false;
        table.setPlaceholder(new Label("Changes applied!"));
    }
    
    
    private void updateListView(boolean preview) {
        SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        if (directory != null) 
            folderPath.setText(directory.getPath());
        folder = new File(folderPath.getText());
        if (folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            table.getItems().clear();
            for (File aux: listOfFiles) {
                if (aux.isFile()) {
                    String name = preview? getFilePreview(aux, false).getName():aux.getName();
                    SingleFile newfile = new SingleFile(name, 
                                                        date.format(aux.lastModified()), 
                                                        getSizeInKb(aux)+" kB");
                    if (showFile) 
                        table.getItems().add(newfile);
                    showFile = true;
                }
            }
        }
    }
    
    
    private static String getNameWithoutExtensions(String name) {
        String extension = NameFunctions.getExtension(name);
        if (name.length() > extension.length())
            return name.substring(0, name.length()-extension.length());
        else 
            return null;
    }

    
    private File getFilePreview(File aux, boolean deleteFile) {
        String regex = regexInput.getText(),
               name = aux.getName();
        if ((!regex.isEmpty() && getNameWithoutExtensions(name)!=null && getNameWithoutExtensions(name).matches(regex)) 
                || (regex.isEmpty() && getNameWithoutExtensions(name)!=null))
            switch (tabPane.getSelectionModel().getSelectedIndex()) {
                case 0: 
                    return NameFunctions.inverse(aux);
                case 1: 
                    charSeparator = (separator.getText().length() > 0)? separator.getText().charAt(0): ' ';
                    return NameFunctions.change_order(aux, charSeparator, isSpaceInChangeOrder);
                case 2:
                    return NameFunctions.cases(aux, casesOptionSelected);
                case 3:
                    return NameFunctions.replace(aux, renameOriginal.getText(), renameReplacement.getText());
                case 4:
                    if (extensionOriginal.getText().equals(NameFunctions.getExtension(aux.getName()).substring(1)) && 
                            extensionReplacement.getText().length()==0)
                        showFile = false;
                    return NameFunctions.changeExtensions(aux, extensionOriginal.getText(), extensionReplacement.getText(), deleteFile);
            }
        return aux;
    }
    
    
    private String getSizeInKb(File file) {
        return String.valueOf(Math.round((file.length()/1024f) * 100d) / 100d);
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        directoryChooser = new DirectoryChooser();
        folderPath.setText("Select a directory...");
        tableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableModified.setCellValueFactory(new PropertyValueFactory<>("modified"));
        tableSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No files"));
        casesOption.getItems().add("All Lowercase");
        casesOption.getItems().add("All Uppercase");
        casesOption.getItems().add("Inverse");
        casesOption.getSelectionModel().selectFirst();
        regexInfo.setTooltip(
            new Tooltip("If the file's name matches the regex modify it, otherwise don't. \n"
                    + "If this field is empty all files will be modified.")
        );
        aboutButton.setTooltip(
            new Tooltip("Namely. Created by Usbac \nCreative Commons Licence: CC BY")
        );
        //Load Original File view when moving between Tabs
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<?extends Tab> old, Tab oldTab, Tab newTab) {
                previewButton.setText("Preview");
                previewActive = false;
                updateListView(previewActive);
            }
        });
    }    
}