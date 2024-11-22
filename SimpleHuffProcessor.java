/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, <NAME1> and <NAME2), this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose Canvas account is being used)
 *  UTEID:
 *  email address:
 *  Grader name:
 *
 *  Student 2
 *  UTEID:
 *  email address:
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private Compressor compressor;

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
        // magic number and header format ints
        int totalCompressedBits = BITS_PER_INT * 2;
        int formatNumber = headerFormat;
        boolean bitsExist = true;
        while (bitsExist) {
            int num = bin.read();
            if (num == -1) {
                bitsExist = false;
            } else {
                freq[num]++;
                totalUncompressedBits += BITS_PER_WORD;          
            }
        }
        bin.close();
        HuffmanTree tree = new HuffmanTree(freq);
        String[] huffCodes = tree.getHuffCodes();
        int count = 0;
        if (formatNumber == STORE_COUNTS) {
            // store: int freqs of all indices (not including PEOF)
            totalCompressedBits += BITS_PER_INT * ALPH_SIZE;
        } else if (formatNumber == STORE_TREE) {
            // tree: tree size (32 bits) + all 1 bit nodes
            totalCompressedBits += BITS_PER_INT + tree.size();
            for (int i = 0; i < huffCodes.length; i++) {
                if (huffCodes[i] != null) {
                    count++;
                }
            }
            totalCompressedBits += count * 9;
        }
        for (int i = 0; i < huffCodes.length - 1; i++) {
            if (freq[i] > 0) {
                // all: length of total compressed file (non-header code)
                totalCompressedBits += huffCodes[i].length() * freq[i];
            }
        }
        totalCompressedBits += huffCodes[PSEUDO_EOF].length();
        int totalBitsSaved = totalUncompressedBits - totalCompressedBits;
        compressor = new Compressor(freq, huffCodes, tree, formatNumber, count, totalBitsSaved);
        return totalBitsSaved;
    }

    /**
	 * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     * @param in is the stream being compressed (NOT a BitInputStream)
     * @param out is bound to a file/stream to which bits are written
     * for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than the input file.
     * If this is false do not create the output file if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        int numBitsWritten = 0;        
        if (compressor.getBitsSaved() >= 0 || force) {
            BitInputStream bin = new BitInputStream(in);
            BitOutputStream bout = new BitOutputStream(out);
            numBitsWritten = compressor.compress(bin, bout);
        }
        showString(String.valueOf(numBitsWritten));
        return numBitsWritten;
    }

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * @param in is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        BitInputStream bin = new BitInputStream(in);
        BitOutputStream bout = new BitOutputStream(out);
        Uncompressor uncompressor = new Uncompressor(bin, bout);
	    return uncompressor.uncompress();
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s){
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}
