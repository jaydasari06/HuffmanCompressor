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

import java.io.IOException;

public class HuffmanTree {
    private TreeNode root;
    private int size;

    public HuffmanTree(int[] arr) {
        PriorityQueue<TreeNode> pq = new PriorityQueue<TreeNode>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0) {
                pq.enqueue(new TreeNode(i, arr[i]));
            }
        }
        pq.enqueue(new TreeNode(IHuffConstants.ALPH_SIZE, 1));
        size = pq.size();
        while (pq.size() >= 2) {
            pq.enqueue(new TreeNode(pq.dequeue(), 0, pq.dequeue()));
            size++;
        }
        root = pq.dequeue();
    }

    public HuffmanTree(BitInputStream bin) throws IOException {
        size = bin.readBits(IHuffConstants.BITS_PER_INT);
        TreeNode newNode = new TreeNode(0, 0);
        root = buildTree(new int[1], bin, newNode);
    }

    private TreeNode buildTree(int[] treeSize, BitInputStream in, TreeNode temp) throws IOException {
        int val = in.readBits(1);
        if (treeSize[0] < size) {
            if (val == 0) {
                treeSize[0]++;
                temp = new TreeNode(buildTree(treeSize, in, temp), 0,
                        buildTree(treeSize, in, temp));
            } else if (val == 1) {
                val = in.readBits(IHuffConstants.BITS_PER_WORD + 1);
                treeSize[0]++;
                return new TreeNode(val, 1);
            } else {
                throw new IllegalArgumentException("Bits are not 0 and 1");
            }
        }
        return temp;
    }
    
    public String[] getHuffCodes() {
        String[] huffCodes = new String[IHuffConstants.ALPH_SIZE + 1];
        getCodes(root, huffCodes, "");
        return huffCodes;
    }

    public int size() {
        return size;
    }

    public int traverseTree(BitInputStream bitsIn, BitOutputStream bitsOut, int bitsWritten) throws IOException {
    TreeNode node = root;
    boolean seenPEOF = false;
    while(!seenPEOF) {
        int bit = bitsIn.readBits(1);
        if(bit == -1) {
            throw new IOException("Error reading compressed file. \n" +
            "unexpected end of input. No PSEUDO_EOF value.");
        } else {
            if (bit == 0) {
                node = node.getLeft();
            } else {
                node = node.getRight();
            }
            if (node.isLeaf()) {
                if (node.getValue() == IHuffConstants.PSEUDO_EOF) {
                    seenPEOF = true;
                } else {
                    bitsOut.writeBits(IHuffConstants.BITS_PER_WORD, node.getValue());
                    bitsWritten += IHuffConstants.BITS_PER_WORD;
                    node = root;
                }
            }
        }
    }
    return bitsWritten;
}


    public String printTreeHeader() {
        StringBuilder sb = new StringBuilder();
        printTreeHeaderHelper(root, sb);
        return sb.toString();
    }

    private void printTreeHeaderHelper(TreeNode node, StringBuilder sb) {
        if (node.isLeaf()) {
            sb.append("1").append(String.format("%9s",
                    Integer.toBinaryString(node.getValue())).replace(' ', '0'));
        } else {
            sb.append("0");
            printTreeHeaderHelper(node.getLeft(), sb);
            printTreeHeaderHelper(node.getRight(), sb);            
        }
    }

    private void getCodes(TreeNode node, String[] con, String currentCode) {
        if (node.isLeaf()) {
            con[node.getValue()] = currentCode;
        } else {
            getCodes(node.getLeft(), con, currentCode + "0");
            getCodes(node.getRight(), con, currentCode + "1");
        }
    }
}
