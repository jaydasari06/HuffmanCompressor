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

public class PriorityQueue<E extends Comparable<? super E>> {
    private ArrayList<E> arr;

    public PriorityQueue() {
        arr = new ArrayList<E>();
    }

    public void enqueue(E node) {
        int index = binarySearch(arr, node);
        arr.add(index, node);
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
