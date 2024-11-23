import java.util.LinkedList;

/**
 * A generic PriorityQueue implementation using a LinkedList.
 * Elements are stored in sorted order based on their natural ordering (as defined by Comparable).
 * The element with the highest priority (smallest value) is dequeued first.
 *
 * @param <E> The type of elements stored in the queue, which must implement Comparable.
 */
public class PriorityQueue<E extends Comparable<? super E>> {
    // List to store elements of the priority queue in sorted order
    private LinkedList<E> arr;

    /**
     * Constructs an empty PriorityQueue.
     */
    public PriorityQueue() {
        arr = new LinkedList<>();
    }

    /**
     * Adds an element to the priority queue, maintaining sorted order.
     * 
     * @param node The element to add.
     */
    public void enqueue(E node) {
        int index = 0;
        if (arr.size() == 0) {
            // If the queue is empty add the element
            arr.add(node);
        } else {
            // Find the correct position to maintain sorted order
            while (index < arr.size() && arr.get(index).compareTo(node) <= 0) {
                index++;
            }
            arr.add(index, node);
        }
    }

    /**
     * Removes and returns the highest-priority element (smallest value) from the queue.
     * 
     * @return The element with the highest priority.
     * @throws IllegalArgumentException If the queue is empty.
     */
    public E dequeue() {
        if (arr.size() == 0) {
            throw new IllegalArgumentException("Cannot dequeue from an empty queue");
        }
        return arr.remove(0);
    }

    /**
     * Returns the number of elements currently in the priority queue.
     * 
     * @return The size of the queue.
     */
    public int size() {
        return arr.size();
    }

    /**
     * Returns a string representation of the priority queue, displaying its elements in order.
     * 
     * @return A string representing the queue.
     */
    public String toString() {
        return arr.toString();
    }
}
