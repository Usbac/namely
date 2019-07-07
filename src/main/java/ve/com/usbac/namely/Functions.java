package ve.com.usbac.namely;

import java.io.File;

public final class Functions {
    
    private final static String EMPTY = "";
    private final static float KB = 1024f;
    
    /**
     * Returns the number of matches with the indicated string in the file name
     * @param file the file
     * @param string the string that will be looked for
     * @return the number of matches that are in the file name
     */
    public static int numberOfMatches(File file, String string) {
        return file.getName().length() - file.getName().replace(string, EMPTY).length();
    }
    
    
    /**
     * Returns <code>true</code> if the file name has an extension, <code>false</code> otherwise
     * @param file the file
     * @return <code>true</code> if the file name has an extension, <code>false</code> otherwise
     */
    public static boolean hasExtension(File file) {
        return file.getName().length() != getExtension(file.getName()).length();
    }
    
    
    /**
     * Returns the extension of the indicated file
     * @param fileName the name of the file
     * @return the extension
     */
    public static String getExtension(String fileName) {
        if (!fileName.contains(".")) {
            return fileName;
        }
        
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }
    
    
    /**
     * Returns the name of the file without the extension
     * @param file the file
     * @return the name of the file without the extension
     */
    public static String getNameNoExtension(File file) {
        String name = file.getName();
        String extension = Functions.getExtension(name);
        
        if (name.length() > extension.length()) {
            return name.substring(0, name.length() - extension.length());
        }
        
        return name;
    }
    
    
    /**
     * Invert the text of the file name
     * @param file the file
     * @return the file with the name inverted
     */
    public static File inverse(File file) {
        String extension = hasExtension(file) ? getExtension(file.getName()) : EMPTY;
        String invertedName = new StringBuffer(getNameNoExtension(file))
                                              .reverse()
                                              .toString();
        
        return new File(file.getParent(), invertedName + extension);
    }
    
    
    /**
     * Invert the position of two parts of the file name which are split by a separator. <p>
     * Example: A - B > B - A
     * @param file the file
     * @param separator the separator which is between the two parts
     * @param spacing whether apply or not a white space between the text and the separator
     * @return the file with the order changed
     */
    public static File changeOrder(File file, char separator, boolean spacing) {
        //Check if the Separator is in the file's name, if it isn't return the file without changes.
        int totalSeparators = numberOfMatches(file, String.valueOf(separator));
        if (totalSeparators != 1 || separator == '.') {
            return new File(file.getParent(), file.getName());
        }
        
        String extension = hasExtension(file) ? getExtension(file.getName()) : EMPTY;
        
        //Adjust the file name length and delete the extension if the file doesn't have an extension
        int length = hasExtension(file)? getLengthNoExtension(file) : file.getName().length();
        
        int separatorIndex = file.getName().indexOf(separator);
        //Get the substrings and delete extra whitespaces at the beginning or ending of them
        String partOne = file.getName()
                             .substring(0, separatorIndex)
                             .trim();
        String partTwo = file.getName()
                             .substring(separatorIndex + 1, length)
                             .trim();
        
        String newName = partTwo + getSpacing(spacing) + String.valueOf(separator) + getSpacing(spacing) + partOne + extension;
        return new File(file.getParent(), newName);
    }
    
    
    private static String getSpacing(boolean spacing) {
        return spacing ? " " : EMPTY;
    }
    
    /**
     * Add text at the start or end of a file name
     * @param file the file
     * @param text the text to add
     * @param begin add the text at the begin or end of the file name
     * @return the file with the text added
     */
    public static File add(File file, String text, boolean begin) {
        String extension = hasExtension(file) ? getExtension(file.getName()) : EMPTY;
        String newName = begin ? text + getNameNoExtension(file) : getNameNoExtension(file) + text;
        
        return new File(file.getParent(), newName + extension);
    }
    
    
    /**
     * Replace every occurrence of <code>original</code> with <code>replacement</code> in the file name
     * @param file the file
     * @param original the original text
     * @param replacement the new text
     * @return the file with the indicated text replaced
     */
    public static File replace(File file, String original, String replacement) {
        String extension = hasExtension(file) ? getExtension(file.getName()) : EMPTY;
        String newName = file.getName()
                             .replace(original, replacement);
        
        if (hasExtension(file))
            newName = newName.substring(0, newName.length() - extension.length());
        
        return new File(file.getParent(), newName + extension);
    }
    
    
    /**
     * Change the letter case of the file name
     * @param file the file
     * @param option the indicated option (1.All Lowercase 2.All Uppercase 3.Inverse Cases)
     * @return the file with the letter case modified
     */
    public static File cases(File file, int option) {
        String extension = hasExtension(file) ? getExtension(file.getName()) : EMPTY,
               newName = file.getName();
        int length = hasExtension(file) ? getLengthNoExtension(file) : file.getName().length();
        
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
        
        return new File(file.getParent(), newName + extension);    
    }
    
    
    /**
     * Returns the length of the file name without the extension
     * @param file the file
     * @return the length of the file name without the extension
     */
    private static int getLengthNoExtension(File file) {
        return file.getName().length() - getExtension(file.getName()).length();
    }
    
    
    /**
     * Returns <code>true</code> if the file name matches the regular expression, <code>false</code> otherwise
     * @param file the file
     * @param regex the regular expression
     * @return <code>true</code> if the file name matches the regular expression, <code>false</code> otherwise
     */
    public static boolean matchesRegex(File file, String regex) {
        return (regex.isEmpty() || (getNameNoExtension(file) != null && getNameNoExtension(file).matches(regex)));
    }
    
    
    /**
     * Returns the size of the file rounded to two decimal places
     * @param file the file
     * @return the size of the file rounded to two decimal places and expressed in kB
     */
    public static float getSizeInKb(File file) {
        float size = (file.length() / KB) * 100;
        size = Math.round(size);
        
        return size / 100;
    }
}
