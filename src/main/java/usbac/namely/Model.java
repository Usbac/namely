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
    private final String FILES = " Files";
    private final String FOLDERS = " Folders";
    
    private DateFilter dateFilter;
    private SizeFilter sizeFilter;
    boolean showFile, isSpaceInChangeOrder, recursive;
    int casesOptionSelected;
    boolean addOptionSelected;
    DirectoryChooser directoryChooser;
    File directory;
    int foldersNumber, filesNumber;

    private enum DateFilter {
        OLDER,
        NEWER
    }

    private enum SizeFilter {
        SMALLER,
        BIGGER
    }


    public Model(Controller c) {
        this.controller = c;
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
                if (!preview) {
                    showFile = true;
                }
                
                SingleFile newFile = new SingleFile(getFileName(file, preview),
                                                    date.format(file.lastModified()),
                                                    FileFunctions.getSizeInKb(file) + " kB");
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
    
    
    public String getFileName(File file, boolean preview) {
        return preview? getFilePreview(file, false).getName():file.getName();
    }


    public File getFilePreview(File file, boolean deleteFile) {
        showFile = false;
        if (FileFunctions.matchesRegex(file, controller.getRegex()) ||
             (controller.getRegex().isEmpty() && FileFunctions.getNameNoExtension(file)!=null) || controller.getTab() == 4) {
            showFile = true;
            switch (controller.getTab()) {
                case 0:
                    return FileFunctions.add(file, controller.addText.getText(), addOptionSelected);
                case 1:
                    return FileFunctions.inverse(file);
                case 2:
                    char charSeparator = (controller.getSeparator().length() > 0)?
                            controller.getSeparator().charAt(0): ' ';
                    return FileFunctions.changeOrder(file, charSeparator, isSpaceInChangeOrder);
                case 3:
                    return FileFunctions.cases(file, casesOptionSelected);
                case 4:
                    return FileFunctions.replace(file, controller.renameOriginal.getText(), controller.renameReplacement.getText());
                case 5:
                    showFile = deleteFile(file, deleteFile);
            }
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
        controller.itemsQuantity.setText(NO_FILES);  
        filesNumber = 0;
        foldersNumber = 0;
    }

    
    public boolean fileMatchesFields(File file) {
        String fileExtension = FileFunctions.getExtension(file.getName()).substring(1);
        float fileSize = FileFunctions.getSizeInKb(file);
        long fileModified = file.lastModified();
        
        //If file doesn't matches the indicated extension
        if (!controller.extensionField.getText().isEmpty() && !controller.extensionField.getText().matches(fileExtension))
            return false;
        
        //If file doesn't matches Date filter (Older or Newer than the indicated date)
        if (controller.datePicker.getValue() != null && !controller.datePicker.getValue().toString().isEmpty()) {
            if ((dateFilter == DateFilter.NEWER && fileModified < getDateInMilli(controller.datePicker)) ||
                (dateFilter == DateFilter.OLDER && fileModified > getDateInMilli(controller.datePicker)))
                    return false;
        }
        
        //If file doesn't matches Size filter (Smaller or Bigger than the indicated size)
        if (!controller.sizeField.getText().isEmpty() && controller.sizeField.getText().chars().allMatch(Character::isDigit)) {
            float comparativeSize = Float.parseFloat(controller.sizeField.getText());
            
            if ((sizeFilter == SizeFilter.SMALLER && fileSize > comparativeSize) ||
                (sizeFilter == SizeFilter.BIGGER && fileSize < comparativeSize)) {
                    return false;
            }
        }
        
        return true;
    }

    
    public boolean deleteFile(File file, boolean delete) {
        boolean fileMatchesFields = fileMatchesFields(file);
        if (!controller.getRegex().isEmpty() && !FileFunctions.matchesRegex(file, controller.getRegex()))
            fileMatchesFields = false;

        if (delete && fileMatchesFields) {
            file.delete();
        }
        
        return fileMatchesFields;
    }


    public long getDateInMilli(DatePicker d) {
        return LocalDate.of(d.getValue().getYear(), d.getValue().getMonth(), d.getValue().getDayOfMonth())
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant()
                        .toEpochMilli();
    }


    public void countItemsQuantity() {
        String msg;
        
        if (filesNumber > 0) {
            msg = String.valueOf(filesNumber) + FILES;
            if (foldersNumber > 0) {
                msg += COMMA + String.valueOf(foldersNumber) + FOLDERS;
            }
        } else {
            msg = NO_FILES;
        }
        
        controller.itemsQuantity.setText(msg);
    }

}