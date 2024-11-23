import java.io.IOException;

/**
 * Represents a Huffman Tree used for encoding and decoding data based on Huffman coding.
 * 
 * Supports building the tree from character frequencies or reading a serialized tree
 * from a bit stream. Provides methods for generating Huffman codes, traversing the tree,
 * and printing the tree's header.
 */
public class HuffmanTree {
    private TreeNode root;
    private int size;     

    /**
     * Constructs a HuffmanTree using character frequencies.
     * 
     * @param arr An array where each index represents a character's frequency.
     *            Only characters with non-zero frequencies are included in the tree.
     */
    public HuffmanTree(int[] arr) {
        PriorityQueue<TreeNode> pq = new PriorityQueue<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0) {
                pq.enqueue(new TreeNode(i, arr[i]));
            }
        }
        // Add a pseudo EOF node to the priority queue
        pq.enqueue(new TreeNode(IHuffConstants.ALPH_SIZE, 1));
        size = pq.size();
        // Combine nodes until only the root remains
        while (pq.size() >= 2) {
            pq.enqueue(new TreeNode(pq.dequeue(), 0, pq.dequeue()));
            size++;
        }
        root = pq.dequeue();
    }

    /**
     * Constructs a HuffmanTree from a serialized representation in a BitInputStream.
     * 
     * @param bin The input stream containing the serialized tree data.
     * @throws IOException If an error occurs while reading the serialized data.
     */
    public HuffmanTree(BitInputStream bin) throws IOException {
        size = bin.readBits(IHuffConstants.BITS_PER_INT);
        root = buildTree(new int[1], bin, null);
    }

    /**
     * Recursively builds the Huffman Tree from serialized input data.
     * 
     * @param treeSize Tracks the number of nodes added to the tree (via array reference).
     * @param in       The BitInputStream containing serialized tree data.
     * @param temp     A temporary TreeNode used during recursion.
     * @return The constructed TreeNode at the current recursion level.
     * @throws IOException If invalid data is encountered during tree construction.
     */
    private TreeNode buildTree(int[] treeSize, BitInputStream in, TreeNode temp) throws IOException {
        int val = in.readBits(1);
        if (treeSize[0] < size) {
            if (val == 0) { // Internal node
                treeSize[0]++;
                temp = new TreeNode(buildTree(treeSize, in, temp), 0, buildTree(treeSize, in, temp));
            } else if (val == 1) { // Leaf node
                val = in.readBits(9);
                treeSize[0]++;
                return new TreeNode(val, 1);
            } else {
                throw new IllegalArgumentException("Bits are not 0 or 1");
            }
        }
        return temp;
    }

    /**
     * Generates Huffman codes for all characters in the tree.
     * 
     * @return An array of Huffman codes where the index corresponds to the character's value.
     */
    public String[] getHuffCodes() {
        String[] huffCodes = new String[IHuffConstants.ALPH_SIZE + 1];
        getCodes(root, huffCodes, "");
        return huffCodes;
    }

    /**
     * Returns the size of the Huffman Tree (total number of nodes).
     * 
     * @return The size of the tree.
     */
    public int size() {
        return size;
    }

    /**
     * Decodes a compressed file by traversing the Huffman Tree.
     * 
     * @param bitsIn      The input stream of compressed data.
     * @param bitsOut     The output stream for decompressed data.
     * @param bitsWritten Tracks the total number of bits written to the output stream.
     * @return The updated count of bits written.
     * @throws IOException If an error occurs during traversal or reading input.
     */
    public int traverseTree(BitInputStream bitsIn, BitOutputStream bitsOut, int bitsWritten) throws IOException {
        TreeNode node = root;
        boolean seenPEOF = false;
        while (!seenPEOF) {
            int bit = bitsIn.readBits(1);
            if (bit == -1) {
                throw new IOException("Error reading compressed file. Unexpected end of input. No PSEUDO_EOF value.");
            } else {
                node = (bit == 0) ? node.getLeft() : node.getRight();
                if (node.isLeaf()) {
                    if (node.getValue() == IHuffConstants.PSEUDO_EOF) {
                        seenPEOF = true;
                    } else {
                        bitsOut.writeBits(IHuffConstants.BITS_PER_WORD, node.getValue());
                        bitsWritten += IHuffConstants.BITS_PER_WORD;
                        node = root; // Reset to root for decoding the next sequence
                    }
                }
            }
        }
        return bitsWritten;
    }

    /**
     * Serializes the Huffman Tree into a string representation.
     * 
     * @return A string representing the serialized tree structure.
     */
    public String printTreeHeader() {
        StringBuilder sb = new StringBuilder();
        printTreeHeaderHelper(root, sb);
        return sb.toString();
    }

    /**
     * Recursively generates the serialized representation of the tree.
     * 
     * @param node The current node being processed.
     * @param sb   The StringBuilder accumulating the tree representation.
     */
    private void printTreeHeaderHelper(TreeNode node, StringBuilder sb) {
        if (node.isLeaf()) {
            sb.append("1").append(String.format("%9s", Integer.toBinaryString(node.getValue())).replace(' ', '0'));
        } else {
            sb.append("0");
            printTreeHeaderHelper(node.getLeft(), sb);
            printTreeHeaderHelper(node.getRight(), sb);
        }
    }

    /**
     * Helper method to recursively generate Huffman codes for characters.
     * 
     * @param node        The current node being processed.
     * @param codes       The array storing Huffman codes for characters.
     * @param currentCode The Huffman code being built for the current node.
     */
    private void getCodes(TreeNode node, String[] codes, String currentCode) {
        if (node.isLeaf()) {
            codes[node.getValue()] = currentCode;
        } else {
            getCodes(node.getLeft(), codes, currentCode + "0");
            getCodes(node.getRight(), codes, currentCode + "1");
        }
    }
}
