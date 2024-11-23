/*  Student information for assignment:
 *
 *  On OUR honor, JAYACHANDRA DASARI and MUGUNTH SIDDHESH SURESH KANNA, this programming assignment is OUR own work
 *  and WE have not provided this code to any other student.
 *
 *  Number of slip days used: 1
 *
 *  Student 1 (Student whose Canvas account is being used)
 *  UTEID: jd53398
 *  email address: jay.dasari@utexas.edu
 *  Grader name: Bersam Basagaoglu
 *
 *  Student 2
 *  UTEID: ms94655
 *  email address: mugunth.sureshkanna@gmail.com
 *
 */

import java.util.ArrayList;

/**
 * A generic PriorityQueue implementation using a LinkedList.
 * Elements are stored in sorted order based on their natural ordering (as defined by Comparable).
 * The element with the highest priority (smallest value) is dequeued first.
 *
 * @param <E> The type of elements stored in the queue, which must implement Comparable.
 */
public class PriorityQueue<E extends Comparable<? super E>> {
    // List to store elements of the priority queue in sorted order
    private ArrayList<E> arr;

    /**
     * Constructs an empty PriorityQueue.
     */
    public PriorityQueue() {
        arr = new ArrayList<E>();
    }

    /**
     * Adds an element to the priority queue, maintaining sorted order.
     * pre: node != null
     * 
     * @param node The element to add.
     */
    public void enqueue(E node) {
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null");
        }
        int index = binarySearch(arr, node);
        arr.add(index, node);
    }

    /**
     * Removes and returns the highest-priority element (smallest value) from the queue.
     * pre: size > 0
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

    // adapted from code for binary search from class slides
    private int binarySearch(ArrayList<E> data, E target) {
        return binarySearch(data, target, 0, data.size() - 1);
    }

    private int binarySearch(ArrayList<E> data, E target, int low, int high) {
        if(low <= high) {
            int mid = low + ((high - low) / 2);
            if (data.get(mid).equals(target)) {
                return binarySearch(data, target, mid + 1, high);
            } else if (data.get(mid).compareTo(target) > 0) {
                return binarySearch(data, target, low, mid - 1);
            } else {
                return binarySearch(data, target, mid + 1, high);
            }
        }
        return low;
    }
}
