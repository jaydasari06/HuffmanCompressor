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
import java.io.InputStream;
import java.io.OutputStream;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private Compressor compressor; // Handles compression process and retains state from preprocessing

    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     * @param in is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind of
     * header to use, standard count format, standard tree format, or
     * possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     * Note, to determine the number of
     * bits saved, the number of bits written includes
     * ALL bits that will be written including the
     * magic number, the header format number, the header to
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
        BitInputStream bin = new BitInputStream(in); 
        int[] freq = new int[ALPH_SIZE]; 
        int totalUncompressedBits = 0; 
        int totalCompressedBits = BITS_PER_INT * 2; 
        boolean bitsExist = true; 

        // Read and count character frequencies
        while (bitsExist) {
            int num = bin.read();
            if (num == -1) {
                bitsExist = false; // End of stream
            } else {
                freq[num]++;
                totalUncompressedBits += BITS_PER_WORD;
            }
        }
        bin.close();

        // Create Huffman tree and retrieve codes
        HuffmanTree tree = new HuffmanTree(freq);
        String[] huffCodes = tree.getHuffCodes();

        // Adjust total compressed bits based on header format
        if (headerFormat == STORE_COUNTS) {
            totalCompressedBits += BITS_PER_INT * ALPH_SIZE; // Include frequency table size
        } else if (headerFormat == STORE_TREE) {
            totalCompressedBits += BITS_PER_INT + tree.size(); // Include tree size
            totalCompressedBits += countLeafNodes(huffCodes) * (BITS_PER_WORD + 1); // Leaf nodes require 9 bits each
        }

        // Calculate bits required for compressed data
        for (int i = 0; i < huffCodes.length - 1; i++) {
            if (freq[i] > 0) {
                totalCompressedBits += huffCodes[i].length() * freq[i];
            }
        }
        totalCompressedBits += huffCodes[PSEUDO_EOF].length(); // Include EOF marker

        int totalBitsSaved = totalUncompressedBits - totalCompressedBits;
        compressor = new Compressor(freq, huffCodes, tree, headerFormat, countLeafNodes(huffCodes), totalBitsSaved);
        return totalBitsSaved;
    }

    /**
     * Compresses the input stream into the output stream using the preprocessed Huffman codes.
     * 
     * @param in the input stream to be compressed
     * @param out the output stream to write the compressed data
     * @param force whether to compress even if the output size is larger than the input
     * @return the number of bits written to the output
     * @throws IOException if an error occurs during reading or writing
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        int numBitsWritten = 0;
        if (compressor.getBitsSaved() >= 0 || force) {
            BitInputStream bin = new BitInputStream(in); 
            BitOutputStream bout = new BitOutputStream(out);
            numBitsWritten = compressor.compress(bin, bout); // Perform compression
        }
        showString(String.valueOf(numBitsWritten)); 
        return numBitsWritten;
    }

    /**
     * Uncompresses a previously compressed stream and writes the uncompressed data to the output stream.
     * 
     * @param in the compressed input stream
     * @param out the output stream for uncompressed data
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs during reading or writing
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        BitInputStream bin = new BitInputStream(in); 
        BitOutputStream bout = new BitOutputStream(out); 
        Uncompressor uncompressor = new Uncompressor(bin, bout); // Handles uncompression
        return uncompressor.uncompress(); 
    }

    /**
     * Sets the viewer for displaying progress or results.
     * 
     * @param viewer the IHuffViewer instance to update
     */
    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    /**
     * Displays a message on the viewer, if available.
     * 
     * @param s the message to display
     */
    private void showString(String s) {
        if (myViewer != null) {
            myViewer.update(s);
        }
    }

    /**
     * Counts number of leaf nodes in tree using its associated array
     * 
     * @param arr the array of codes associated with a huffman tree
     */
    private int countLeafNodes(String[] arr) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null) {
                count++;
            }
        }
        return count;
    }
}
