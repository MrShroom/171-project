import java.util.HashMap;

public class MyLRU<Key,T> {
    int capacity;
    HashMap<Key, Node<Key ,T>> map = new HashMap<Key, Node<Key ,T>>();
    Node<Key, T> head=null;
    Node<Key, T> end=null;
 
    public MyLRU(int capacity) {
        this.capacity = capacity;
    }
 
    public T get(Key key) {
        if(map.containsKey(key)){
            Node<Key ,T> n = map.get(key);
            remove(n);
            setHead(n);
            return n.value;
        }
 
        return null;
    }
 
    public void remove(Node<Key ,T> n){
        if(n.pre!=null)
        {
            n.pre.next = n.next;
        }
        else
        {
            head = n.next;
        }
 
        if(n.next!=null)
        {
            n.next.pre = n.pre;
        }
        else
        {
            end = n.pre;
        }
 
    }
 
    public void setHead(Node<Key ,T> n)
    {
        n.next = head;
        n.pre = null;
 
        if(head != null)
            head.pre = n;
 
        head = n;
 
        if(end == null)
            end = head;
    }
 
    public void set(Key key, T value) 
    {
        if(map.containsKey(key))
        {
            Node<Key ,T> old = map.get(key);
            old.value = value;
            remove(old);
            setHead(old);
        }
        else
        {
            Node<Key ,T> created = new Node<Key ,T>(key, value);
            if(map.size() >= capacity)
            {
                map.remove(end.key);
                remove(end);
                setHead(created); 
            }
            else
            {
                setHead(created);
            }    
 
            map.put(key, created);
        }
    }
}
