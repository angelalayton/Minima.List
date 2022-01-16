
package minima_list;

public class Tag 
{
    private String tagName;
    private int tagColor;
    private int tagIndex;
    
    public Tag (String name, int color, int index)
    {
        this.tagName = name;
        this.tagColor = color;
        this.tagIndex = index;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagColor() {
        return tagColor;
    }

    public void setTagColor(int tagColor) {
        this.tagColor = tagColor;
    }

    public int getTagIndex() {
        return tagIndex;
    }

    public void setTagIndex(int tagIndex) {
        this.tagIndex = tagIndex;
    }
    
}
