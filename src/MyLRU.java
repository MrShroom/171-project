import java.util.LinkedHashMap;
import java.util.Map;

public class MyLRU<Key,T> extends LinkedHashMap<Key,T>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int capacity;

    public MyLRU(final int capacity) {
        super(capacity + 1, 1.0f, true);
        this.capacity = capacity;        
    }
    
    protected boolean removeEldestEntry(Map.Entry<Key,T> eldest) {
        return size() > capacity;
     }
}
