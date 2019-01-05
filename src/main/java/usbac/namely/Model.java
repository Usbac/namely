package usbac.namely;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.*;
import javafx.scene.control.DatePicker;
import javafx.stage.DirectoryChooser;

public final class Model {
    
    private final Controller controller;
    private final SimpleDateFormat date;
    private final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    private final String NO_FILES = "No files...";
    private final String OLDER_MSG = "Older than";
    private final String SMALLER_MSG = "Smaller than";
    private final String COMMA = ", ";
    
    DirectoryChooser directoryChooser;
    File directory;
    int foldersNumber, filesNumber;
    private DateFilter dateFilter;
    private SizeFilter sizeFilter;
    protected boolean showFile, isSpaceInChangeOrder, recursive;
    protected int casesOptionSelected;

    private enum DateFilter {
        OLDER,
        NEWER
    }

    private enum SizeFilter {
        SMALLER,
        BIGGER
    }


    public Model(Controller controller) {
        this.controller = controller;
        date = new SimpleDateFormat(DATE_FORMAT);
        directoryChooser = new DirectoryChooser();
        recursive = true;
    }


    public void setDateFilter(String date) {
        dateFilter = (date.equals(OLDER_MSG))?
            DateFilter.OLDER : DateFilter.NEWER;
    }


    public void setSizeFilter(String size) {
        sizeFilter = (size.equals(SMALLER_MSG))?
            SizeFilter.SMALLER : SizeFilter.BIGGER;
    }


    public void updateListView(boolean preview) {
        if (directory == null || !directory.isDirectory())
            return;
        controller.folderPath.setText(directory.getPath());
        clearFilesNumber();
        addFilesToList(directory.listFiles(), preview);
        countItemsQuantity();
    }


    public void addFilesToList(File[] listOfFiles, boolean preview) {
        for (File file: listOfFiles) {
            if (file.isFile()) {
                showFile = true;
                String name = preview? getFilePreview(file, false).getName():file.getName();
                SingleFile newFile = new SingleFile(name,
                                                    date.format(file.lastModified()),
                                                    FileFunctions.getSizeInKb(file)+" kB");
                if (showFile) {
                    controller.table.getItems().add(newFile);
                    filesNumber++;
                }
            } else if (recursive) {
                foldersNumber++;
                if (file.listFiles() != null)
                    addFilesToList(file.listFiles(), preview);
            }
        }
    }


    public File getFilePreview(File file, boolean deleteFile) {
        String regex = controller.regexInput.getText();
        int selectedTab = controller.tabPane.getSelectionModel().getSelectedIndex();
        
        if ((!regex.isEmpty() && FileFunctions.matchesRegex(file, regex)) ||
             (regex.isEmpty() && FileFunctions.getNameNoExtension(file)!=null) || selectedTab == 4)
            switch (selectedTab) {
                case 0:
                    return FileFunctions.inverse(file);
                case 1:
                    char charSeparator = (controller.separator.getText().length() > 0)?
                            controller.separator.getText().charAt(0): ' ';
                    return FileFunctions.changeOrder(file, charSeparator, isSpaceInChangeOrder);
                case 2:
                    return FileFunctions.cases(file, casesOptionSelected);
                case 3:
                    return FileFunctions.replace(file, controller.renameOriginal.getText(), controller.renameReplacement.getText());
                case 4:
                    showFile = deleteFile(file, deleteFile);
            }
        return file;
    }
    
    
    public void applyChangesToFiles() {
       applyChangesToFiles(directory.listFiles());
    }
    
    
    public void applyChangesToFiles(File[] listOfFiles) {
        for (File file: listOfFiles)
            if (file.isFile())
                file.renameTo(getFilePreview(file, true));
            else if (recursive)
                applyChangesToFiles(file.listFiles());
    }
    
    
    public void clearFilesNumber() {
        controller.table.getItems().clear();
        filesNumber = 0;
        foldersNumber = 0;
        controller.itemsQuantity.setText(NO_FILES);  
    }

    
    public boolean fileMatchesFields(String fileExtension, float fileSize, long fileModified) {
        //If file doesn't matches the indicated extension
        if (!controller.extensionField.getText().isEmpty() && !controller.extensionField.getText().matches(fileExtension))
            return false;
        //If file doesn't matches Date filter (Older or Newer than the indicated date)
        if (controller.datePicker.getValue()!=null && !controller.datePicker.getValue().toString().isEmpty()) {
            if ((dateFilter == DateFilter.NEWER && fileModified < getDateInMilli(controller.datePicker)) ||
                (dateFilter == DateFilter.OLDER && fileModified > getDateInMilli(controller.datePicker)))
                    return false;
        }
        //If file doesn't matches Size filter (Smaller or Bigger than the indicated size)
        if (!controller.sizeField.getText().isEmpty() && controller.sizeField.getText().chars().allMatch(Character::isDigit)) {
            float comparativeSize = Float.parseFloat(controller.sizeField.getText());
            if ((sizeFilter == SizeFilter.SMALLER && fileSize > comparativeSize) ||
                (sizeFilter == SizeFilter.BIGGER && fileSize < comparativeSize))
                    return false;
        }
        return true;
    }

    
    public boolean deleteFile(File file, boolean delete) {
        String regex = controller.regexInput.getText();
        String fileExtension = FileFunctions.getExtension(file.getName())
                                            .substring(1);
        float fileSize = Float.parseFloat(FileFunctions.getSizeInKb(file));
        long fileModified = file.lastModified();
        
        boolean fileMatchesFields = fileMatchesFields(fileExtension, fileSize, fileModified);
        if (!regex.isEmpty() && !FileFunctions.matchesRegex(file, regex))
            fileMatchesFields = false;

        if (delete && fileMatchesFields)
            file.delete();
        return fileMatchesFields;
    }


    public long getDateInMilli(DatePicker d) {
        return LocalDate.of(d.getValue().getYear(), d.getValue().getMonth(), d.getValue().getDayOfMonth())
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant()
                        .toEpochMilli();
    }
    
        
    public void countItemsQuantity() {
        if (filesNumber > 0) {
            controller.itemsQuantity.setText(String.valueOf(filesNumber) + " Files");
            if (foldersNumber > 0)
                controller.itemsQuantity.setText(controller.itemsQuantity.getText() + COMMA + String.valueOf(foldersNumber) + " Folders");
        } else {
            controller.itemsQuantity.setText(NO_FILES);   
        }
    }

}
