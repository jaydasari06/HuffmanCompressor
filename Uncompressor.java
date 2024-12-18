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

/**
 * The Uncompressor class handles decompression of a file compressed with Huffman encoding.
 * It reads the input stream, reconstructs the Huffman tree based on the header format,
 * and writes the decompressed data to the output stream.
 */
public class Uncompressor {
    // Stream for reading the compressed input
    private BitInputStream bin;
    // Stream for writing the decompressed output
    private BitOutputStream bout;

    /**
     * Constructor for the Uncompressor class.
     * Initializes input and output streams for decompression.
     * pre: bi != null, bo != null
     * 
     * @param bi BitInputStream for reading compressed data.
     * @param bo BitOutputStream for writing decompressed data.
     */
    public Uncompressor(BitInputStream bi, BitOutputStream bo) {
        if (bi == null || bo == null) {
            throw new IllegalArgumentException("BitInputStream and BitOutputStream cannot be null");
        }
        bin = bi;
        bout = bo;
    }

    /**
     * Decompresses the input data using Huffman encoding.
     * Reads the file header, reconstructs the Huffman tree, and decodes the input stream.
     * 
     * @return The total number of bits written during decompression.
     * @throws IOException If an error occurs during reading or writing.
     */
    public int uncompress() throws IOException {
        int bitsWritten = 0;
        // Check if magic number is in the header
        if (bin.readBits(IHuffConstants.BITS_PER_INT) == IHuffConstants.MAGIC_NUMBER) {
            HuffmanTree newTree = null;
            // Determine the header format and reconstruct the Huffman tree
            int num = bin.readBits(IHuffConstants.BITS_PER_INT);
            if (num == IHuffConstants.STORE_COUNTS) {
                // Read frequency counts and build the tree
                int[] freq = new int[IHuffConstants.ALPH_SIZE];
                for (int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
                    freq[i] = bin.readBits(IHuffConstants.BITS_PER_INT);
                }
                newTree = new HuffmanTree(freq);
            } else if (num == IHuffConstants.STORE_TREE) {
                // Rebuild the tree from the header information
                newTree = new HuffmanTree(bin);
            }
            // Traverse the Huffman tree and write decompressed data to the output stream
            bitsWritten = newTree.traverseTree(bin, bout, bitsWritten);
        }
        return bitsWritten;
    }
}
