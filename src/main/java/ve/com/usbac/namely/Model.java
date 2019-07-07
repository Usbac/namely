package ve.com.usbac.namely;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.*;
import javafx.scene.control.DatePicker;
import javafx.stage.DirectoryChooser;

public final class Model {
    
    private final Controller controller;
    private final SimpleDateFormat date;
    private final String KB = "kB";
    private final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    private final String NO_FILES = "No files...";
    private final String OLDER_MSG = "Older than";
    private final String SMALLER_MSG = "Smaller than";
    private final String EMPTY = " ";
    private final String COMMA = "," + EMPTY;
    private final String FILES = " Files";
    private final String FOLDERS = " Folders";
    
    private DateFilter dateFilter;
    private SizeFilter sizeFilter;
    boolean showFile, spaceInChangeOrder, recursive;
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

    
    /**
     * Set the Date filter if older or newer
     * @param date the date
     */
    public void setDateFilter(String date) {
        dateFilter = (date.equals(OLDER_MSG)) ?
            DateFilter.OLDER : DateFilter.NEWER;
    }

    
    /**
     * Set the Size filter if smaller or bigger
     * @param size the size
     */
    public void setSizeFilter(String size) {
        sizeFilter = (size.equals(SMALLER_MSG)) ?
            SizeFilter.SMALLER : SizeFilter.BIGGER;
    }

    
    /**
     * Populate the file list 
     * @param preview if <code>true</code> no changes will be applied, if <code>false</code> the changes will be applied
     */
    public void updateListView(boolean preview) {
        if (directory == null || !directory.isDirectory()) {
            return;
        }
        
        controller.folderPath.setText(directory.getPath());
        clearFilesNumber();
        addFilesToList(directory.listFiles(), preview);
        setItemQuantityText();
    }
    
    
    /**
     * Add a file to the file list
     * @param file the file to add
     * @param preview show or not the change
     */
    private void addFile(File file, boolean preview) {
        if (!preview) {
            showFile = true;
        }

        SingleFile newFile = new SingleFile(getFileName(file, preview),
                                            date.format(file.lastModified()),
                                            Functions.getSizeInKb(file) + EMPTY + KB);
        if (showFile) {
            controller.table.getItems().add(newFile);
            filesNumber++;
        }
    }

    
    /**
     * Populate the file list 
     * @param listOfFiles the files to add
     * @param preview if <code>true</code> no changes will be applied, 
     * if <code>false</code> the changes will be applied
     */
    public void addFilesToList(File[] listOfFiles, boolean preview) {
        foldersNumber++;
        
        for (File file: listOfFiles) {
            if (file.isFile()) {
                addFile(file, preview);
            } else if (recursive && file.listFiles() != null) {
                addFilesToList(file.listFiles(), preview);
            }
        }
    }
    
    
    /**
     * Get or not the new filename
     * @param file the original file
     * @param preview get or not the new filename
     * @return the new filename if the preview parameter is <code>true</code> 
     * or the original filename otherwise
     */
    public String getFileName(File file, boolean preview) {
        return preview ? getNewFilename(file, false).getName() : file.getName();
    }
    
    
    /**
     * Get the new filename
     * @param file the original file
     * @param deleteFile if <code>true</code> the file will be deleted
     * @return the new file with the changes applied
     */
    public File getNewFilename(File file, boolean deleteFile) {
        showFile = false;
        
        if (!Functions.matchesRegex(file, controller.getRegex())) {
            return file;
        }
        
        showFile = true;
        switch (controller.getTab()) {
            case 0:
                return Functions.add(file, controller.addText.getText(), addOptionSelected);
            case 1:
                return Functions.inverse(file);
            case 2:
                return Functions.changeOrder(file, controller.getSeparator(), spaceInChangeOrder);
            case 3:
                return Functions.cases(file, casesOptionSelected);
            case 4:
                return Functions.replace(file, controller.renameOriginal.getText(), controller.renameReplacement.getText());
            case 5:
                showFile = deleteFile(file, deleteFile);
        }
        
        return file;
    }
    
    
    /**
     * Apply the changes to all the files
     */
    public void applyChangesToFiles() {
       applyChangesToFiles(directory.listFiles());
    }
    
    
    /**
     * Apply the changes to the files in the specified list
     * @param listOfFiles the files where the changes will be applied on
     */
    public void applyChangesToFiles(File[] listOfFiles) {
        for (File file: listOfFiles) {
            if (file.isFile()) {
                file.renameTo(getNewFilename(file, true));
            } else if (recursive) {
                applyChangesToFiles(file.listFiles());
            }
        }
    }
    
    
    /**
     * Clear the file list and the file number indicators
     */
    public void clearFilesNumber() {
        controller.table.getItems().clear();
        controller.itemsQuantity.setText(NO_FILES);  
        filesNumber = 0;
        foldersNumber = -1;
    }
    
    
    /**
     * Returns <code>true</code> if the specified date matches with the date filters, <code>false</code> otherwise
     * @param date the date
     * @return <code>true</code> if the specified date matches with the date filters, <code>false</code> otherwise
     */
    public boolean matchesDateFilters(long date) {
        return (dateFilter == DateFilter.NEWER && date < getDateInMilli(controller.datePicker)) ||
               (dateFilter == DateFilter.OLDER && date > getDateInMilli(controller.datePicker));
    }

    
    /**
     * Returns <code>true</code> if the specified file matches all the filters, <code>false</code> otherwise
     * @param file the file
     * @return <code>true</code> if the specified file matches all the filters, <code>false</code> otherwise
     */
    public boolean fileMatchesFields(File file) {
        String fileExtension = Functions.getExtension(file.getName()).substring(1);
        float fileSize = Functions.getSizeInKb(file);
        long fileModified = file.lastModified();
        
        //If file doesn't matches the indicated extension
        if (!controller.extensionField.getText().isEmpty() && !controller.extensionField.getText().matches(fileExtension)) {
            return false;
        }
        
        //If file doesn't matches Date filter (Older or Newer than the indicated date)
        if (controller.datePicker.getValue() != null && !controller.datePicker.getValue().toString().isEmpty()) {
            if (matchesDateFilters(fileModified)) {
                return false;
            }
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

    
    /**
     * Returns <code>true</code> if the specified file matches the delete filters, <code>false</code> otherwise
     * @param file the file to delete
     * @param delete delete or not the file
     * @return <code>true</code> if the specified file matches the delete filters, <code>false</code> otherwise.
     */
    public boolean deleteFile(File file, boolean delete) {
        boolean fileMatchesFields = fileMatchesFields(file);
        
        if (!Functions.matchesRegex(file, controller.getRegex())) {
            fileMatchesFields = false;
        }
            
        if (delete && fileMatchesFields) {
            file.delete();
        }
        
        return fileMatchesFields;
    }

    
    /**
     * Returns the specified date picker in milliseconds
     * @param d the date picker
     * @return the specified date picker in milliseconds
     */
    public long getDateInMilli(DatePicker d) {
        return LocalDate.of(d.getValue().getYear(), d.getValue().getMonth(), d.getValue().getDayOfMonth())
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant()
                        .toEpochMilli();
    }

    
    /**
     * Set the text of the items quantity
     */
    public void setItemQuantityText() {
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