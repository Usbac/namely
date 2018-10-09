package namely;

public class SingleFile {
    private String name, modified, size;
    
    public SingleFile(String name, String modified, String size) {
        this.name = name;
        this.modified = modified;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getModified() {
        return modified;
    }
    
    public String getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setSize(String size) {
        this.size = size;
    }
    
}
