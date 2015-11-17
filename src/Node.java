
class Node<Key ,T>{
	Key key;
    T value;
    Node<Key ,T> pre;
    Node<Key ,T> next;
 
    public Node(Key key, T value){
        this.key = key;
        this.value = value;
    }
}