package namely;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import javafx.scene.control.DatePicker;

public class ListController {
    private final MainController main;
    private final SimpleDateFormat date;
    protected boolean showFile, isSpaceInChangeOrder, recursive;
    protected int casesOptionSelected;
    private DateFilter dateFilter;
    private SizeFilter sizeFilter;

    private enum DateFilter {
        OLDER,
        NEWER
    }

    protected enum SizeFilter {
        SMALLER,
        BIGGER
    }


    public ListController(MainController main) {
        this.main = main;
        date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        recursive = true;
    }


    public void setDateFilter(String date) {
        dateFilter = (date.equals("Older than"))?
            DateFilter.OLDER : DateFilter.NEWER;
    }


    public void setSizeFilter(String size) {
        sizeFilter = (size.equals("Smaller than"))?
            SizeFilter.SMALLER : SizeFilter.BIGGER;
    }


    public void updateListView(boolean preview) {
        if (main.directory != null) {
            main.folderPath.setText(main.directory.getPath());
        }
        main.folder = new File(main.folderPath.getText());
        if (main.folder.isDirectory()) {
            main.table.getItems().clear();
            main.filesNumber = 0;
            main.foldersNumber = 0;
            ListFiles(main.folder.listFiles(), preview);
            main.countItemsQuantity();
        }
    }


    public void ListFiles(File[] listOfFiles, boolean preview) {
        for (File file: listOfFiles) {
            if (file.isFile()) {
                showFile = true;
                String name = preview?
                    getFilePreview(file, false).getName():file.getName();
                SingleFile newfile = new SingleFile(name,
                                                    date.format(file.lastModified()),
                                                    FileFunctions.getSizeInKb(file)+" kB");
                if (showFile) {
                    main.table.getItems().add(newfile);
                    main.filesNumber++;
                }
            } else if (recursive) {
                main.foldersNumber++;
                if (file.listFiles()!=null)
                    ListFiles(file.listFiles(), preview);
            }
        }
    }


    public File getFilePreview(File aux, boolean deleteFile) {
        String regex = main.regexInput.getText();
        int selectedTab = main.tabPane.getSelectionModel().getSelectedIndex();
        if ((!regex.isEmpty() && FileFunctions.getNameNoExtension(aux)!=null && FileFunctions.getNameNoExtension(aux).matches(regex)) ||
             (regex.isEmpty() && FileFunctions.getNameNoExtension(aux)!=null) || selectedTab == 4)
            switch (selectedTab) {
                case 0:
                    return FileFunctions.inverse(aux);
                case 1:
                    char charSeparator = (main.separator.getText().length() > 0)?
                            main.separator.getText().charAt(0): ' ';
                    return FileFunctions.change_order(aux, charSeparator, isSpaceInChangeOrder);
                case 2:
                    return FileFunctions.cases(aux, casesOptionSelected);
                case 3:
                    return FileFunctions.replace(aux, main.renameOriginal.getText(), main.renameReplacement.getText());
                case 4:
                    showFile = deleteFile(aux, deleteFile);
            }
        return aux;
    }


    public boolean deleteFile(File file, boolean delete) {
        boolean fileMatchesFields = true;
        String regex = main.regexInput.getText();
        String fileExtension = FileFunctions.getExtension(file.getName())
                                            .substring(1);
        float fileSize = Float.parseFloat(FileFunctions.getSizeInKb(file));
        long fileModified = file.lastModified();
        //If file doesn't matches the indicated extension
        if (!main.extensionField.getText().isEmpty() && !main.extensionField.getText().matches(fileExtension))
            fileMatchesFields = false;
        //If file doesn't matches Date filter (Older or Newer than the indicated date)
        if (main.datePicker.getValue()!=null && !main.datePicker.getValue().toString().isEmpty()) {
            if ((dateFilter == DateFilter.NEWER && fileModified < getDateInMilli(main.datePicker)) ||
                (dateFilter == DateFilter.OLDER && fileModified > getDateInMilli(main.datePicker)))
                fileMatchesFields = false;
        }
        //If file doesn't matches Size filter (Smaller or Bigger than the indicated size)
        if (!main.sizeField.getText().isEmpty() && main.sizeField.getText().chars().allMatch(Character::isDigit)) {
            float comparativeSize = Float.parseFloat(main.sizeField.getText());
            if ((sizeFilter == SizeFilter.SMALLER && fileSize > comparativeSize) ||
                (sizeFilter == SizeFilter.BIGGER && fileSize < comparativeSize))
                fileMatchesFields = false;
        }
        //If file doesn't matches de Regex
        if (!regex.isEmpty() && FileFunctions.getNameNoExtension(file)!=null && !FileFunctions.getNameNoExtension(file).matches(regex))
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
}
