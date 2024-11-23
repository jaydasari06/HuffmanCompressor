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
 * The Compressor class handles the core logic for compressing data using Huffman encoding.
 * It stores the state required for compression, including frequency counts, Huffman codes, 
 * the Huffman tree, and format details for the compressed file.
 */
public class Compressor {
    
    private int[] freq;
    private String[] codes;
    private HuffmanTree tree;
    private int formatNumber;
    private int count;
    private int bitsSaved;

    /**
     * Constructor for the Compressor class.
     * Initializes the state required for compression.
     * 
     * @param f Frequency array for characters.
     * @param c Huffman codes for characters.
     * @param t Huffman tree representing the encoding.
     * @param fn Format number for the compression header.
     * @param co Count of valid nodes in the Huffman tree.
     * @param b Number of bits saved by compression.
     */
    public Compressor(int[] f, String[] c, HuffmanTree t, int fn, int co, int b) {
        freq = f;
        codes = c;
        tree = t;
        formatNumber = fn;
        count = co;
        bitsSaved = b;
    }

    /**
     * Compresses the input data stream into a compressed format.
     * 
     * @param bin BitInputStream for reading the input data.
     * @param bout BitOutputStream for writing the compressed data.
     * @return The total number of bits written during compression.
     * @throws IOException If an error occurs during reading or writing.
     */
    public int compress(BitInputStream bin, BitOutputStream bout) throws IOException {
        int numBitsWritten = 0;
        // Write the header information to the output stream
        numBitsWritten = writeHeader(bout, numBitsWritten);
        // Encode each character in the input stream
        int num = bin.read();
        while (num != -1) {
            for (int i = 0; i < codes[num].length(); i++) {
                bout.writeBits(1, codes[num].charAt(i) == '0' ? 0 : 1);
                numBitsWritten++;
            }
            num = bin.read();
        }
        // Write the PSEUDO_EOF code to mark the end of the compressed file
        for (int i = 0; i < codes[IHuffConstants.PSEUDO_EOF].length(); i++) {
            bout.writeBits(1, codes[IHuffConstants.PSEUDO_EOF].charAt(i) == '0' ? 0 : 1);
            numBitsWritten++;
        }
        // Close the streams
        bin.close();
        bout.close();
        return numBitsWritten;
    }

    /**
     * Gets the number of bits saved during compression.
     * 
     * @return The number of bits saved.
     */
    public int getBitsSaved() {
        return bitsSaved;
    }

    /**
     * Writes the header information to the output stream.
     * 
     * @param bout BitOutputStream for writing the header.
     * @param numBits The current count of bits written.
     * @return The updated count of bits written after writing the header.
     * @throws IOException If an error occurs during writing.
     */
    private int writeHeader(BitOutputStream bout, int numBits) throws IOException {
        // Write the magic number and format number to the header
        bout.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.MAGIC_NUMBER);
        bout.writeBits(IHuffConstants.BITS_PER_INT, formatNumber);
        numBits += IHuffConstants.BITS_PER_INT * 2;
        if (formatNumber == IHuffConstants.STORE_COUNTS) {
            // Write frequency counts for all characters
            for (int i = 0; i < freq.length; i++) {
                bout.writeBits(IHuffConstants.BITS_PER_INT, freq[i]);
                numBits += IHuffConstants.BITS_PER_INT;
            }
        } else if (formatNumber == IHuffConstants.STORE_TREE) {
            // Write the size of the tree and its structure
            bout.writeBits(IHuffConstants.BITS_PER_INT, count * (IHuffConstants.BITS_PER_WORD + 1) + tree.size());
            numBits += IHuffConstants.BITS_PER_INT;
            String treeOutput = tree.printTreeHeader();
            for (int i = 0; i < treeOutput.length(); i++) {
                bout.writeBits(1, treeOutput.charAt(i) == '0' ? 0 : 1);
                numBits++;
            }
        }
        return numBits;
    }
}
