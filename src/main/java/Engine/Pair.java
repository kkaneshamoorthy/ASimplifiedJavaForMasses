package Engine;

/**
 * Created by kowrishankar on 26/03/17.
 */
public class Pair {
    private String key;
    private String value;


    public Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return  this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
