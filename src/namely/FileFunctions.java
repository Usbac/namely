package namely;

import java.io.File;

public final class FileFunctions {
    
    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }
    
    
    public static String getNameNoExtension(File file) {
        String name = file.getName();
        String extension = FileFunctions.getExtension(name);
        if (name.length() > extension.length())
            return name.substring(0, name.length()-extension.length());
        return null;
    }
    
    
    public static File inverse(File file) {
        String extension = getExtension(file.getName());
        String inverseName = "";
        int length = getLength(file)-1;
        for (int i = length; i >= 0; i--)
            inverseName += String.valueOf(file.getName().charAt(i));

        return new File(file.getParent(), inverseName+extension);
    }
    
    
    public static File change_order(File file, char separator, boolean spacing) {
        //Check if the Separator is in the file's name, if it isn't return the file without changes.
        int totalSeparators = 0;
        for (int aux = 0; aux < file.getName().length(); aux++) {
            if (file.getName().charAt(aux) == separator) 
                totalSeparators++;
        }
        if (totalSeparators > 1 || totalSeparators == 0 || separator == '.' || separator == ' ')
            return new File(file.getParent(), file.getName());
        
        String extension = getExtension(file.getName());
        String parts[] = new String[2];
        for (int i = 0; i < parts.length; i++)
            parts[i] = "";
        
        int partIndex = 0;
        for (int i = 0; i < file.getName().length()-extension.length(); i++) {
            if (file.getName().charAt(i) == separator)
                partIndex++;
            else 
                parts[partIndex] += String.valueOf(file.getName().charAt(i));
        }
        String space = spacing? " ":"";
        
        //Delete extra whitespaces at the beginning or ending of the Parts
        if (parts[0].charAt(parts[0].length()-1) == ' ') 
            parts[0] = parts[0].substring(0, parts[0].length()-1);
        if (parts[1].length() > 0 && parts[1].charAt(0) == ' ') 
            parts[1] = parts[1].substring(1, parts[1].length());
        
        String newName = parts[1]+space+String.valueOf(separator)+space+parts[0]+extension;
        return new File(file.getParent(), newName);
    }
    
    
    public static File replace(File file, String original, String replacement) {
        String extension = getExtension(file.getName());
        String newName = file.getName()
                             .replace(original, replacement);
        newName = newName.substring(0, newName.length()-extension.length());
        return new File(file.getParent(), newName+extension);
    }
    
    
    public static File cases(File file, int option) {
        String extension = getExtension(file.getName());
        int length = getLength(file);
        String newName = "";
        switch (option) {
            //All Lowercase
            case 0: newName = file.getName().toLowerCase().substring(0, length);
                break;
            //All Uppercase
            case 1: newName = file.getName().toUpperCase().substring(0, length);
                break;
            //Inverse cases
            case 2:
                char chars[] = file.getName().toCharArray();
                for (int i = 0; i < length; i++) {
                    chars[i] = Character.isUpperCase(chars[i]) ? 
                        Character.toLowerCase(chars[i]):Character.toUpperCase(chars[i]);
                }
                newName = String.valueOf(chars).substring(0, length);
                break;
        }
        return new File(file.getParent(), newName+extension);    
    }
    
    
    public static int getLength(File file) {
        return file.getName().length() - getExtension(file.getName()).length();
    }
    
    
    public static String getSizeInKb(File file) {
        return String.valueOf(Math.round((file.length()/1024f) * 100d) / 100d);
    }
    
}
