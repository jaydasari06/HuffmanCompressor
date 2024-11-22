import java.util.LinkedList;

public class PriorityQueue<E extends Comparable<? super E>> {
    private LinkedList<E> arr;

    public PriorityQueue() {
        arr = new LinkedList<E>();
    }

    public void enqueue(E node) {
        int index = 0;
        if (arr.size() == 0) {
            arr.add(node);
        } else {
            while (index < arr.size() && arr.get(index).compareTo(node) <= 0) {
                index++;
            }
            arr.add(index, node);
        }
    }

    // pre: size > 0
    public E dequeue() {
        if (arr.size() == 0) {
            throw new IllegalArgumentException("cannot dequeue empty queue");
        }
        return arr.remove(0);
    }

    public int size() {
        return arr.size();
    }

    public String toString() {
        return arr.toString();
    }
}
