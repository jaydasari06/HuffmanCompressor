import java.io.IOException;

public class Compressor {
    private int[] freq;
    private String[] codes;
    private HuffmanTree tree;
    private int formatNumber;
    private int count;
    private int bitsSaved;

    public Compressor(int[] f, String[] c, HuffmanTree t, int fn, int co, int b) {
        freq = f;
        codes = c;
        tree = t;
        formatNumber = fn;
        count = co;
        bitsSaved = b;
    }

    public int compress(BitInputStream bin, BitOutputStream bout) throws IOException {
        int numBitsWritten = 0;
        numBitsWritten = writeHeader(bout, numBitsWritten);
        int num = bin.read();
        while (num != -1) {
            for (int i = 0; i < codes[num].length(); i++) {
                bout.writeBits(1, codes[num].charAt(i) == '0' ? 0 : 1);
                numBitsWritten++;
            }
            num = bin.read();
        }
        // explicitly add psuedo EOF code to end of compressed file
        for (int i = 0; i < codes[IHuffConstants.PSEUDO_EOF].length(); i++) {
            bout.writeBits(1, codes[IHuffConstants.PSEUDO_EOF].charAt(i) == '0' ? 0 : 1);
            numBitsWritten++;
        }
        bin.close();
        bout.close();
        return numBitsWritten;
    }

    public int getBitsSaved() {
        return bitsSaved;
    }

        private int writeHeader(BitOutputStream bout, int numBits) throws IOException{
        bout.writeBits(32, IHuffConstants.MAGIC_NUMBER);
        bout.writeBits(32, formatNumber);
        numBits += 64;
        if (formatNumber == IHuffConstants.STORE_COUNTS) {
            for (int i = 0; i < freq.length; i++) {
                bout.writeBits(32, freq[i]);
                numBits += 32;
            }
        } else if (formatNumber == IHuffConstants.STORE_TREE) {
            bout.writeBits(32, count * 9 + tree.size());
            numBits += 32;
            String treeOutput = tree.printTreeHeader();
            for (int i = 0; i < treeOutput.length(); i++) {
                bout.writeBits(1, treeOutput.charAt(i) == '0' ? 0 : 1);
                numBits++;
            }
        }
        return numBits;
    }
}
