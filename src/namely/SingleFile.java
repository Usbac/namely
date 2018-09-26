package namely;

public class SingleFile {
    private String name, modified, size;
    
    public SingleFile(String n, String m, String s) {
        this.name = n;
        this.modified = m;
        this.size = s;
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
