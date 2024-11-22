import java.io.IOException;

public class Uncompressor {

    private BitInputStream bin;
    private BitOutputStream bout;

    public Uncompressor(BitInputStream bi, BitOutputStream bo) {
        bin = bi;
        bout = bo;
    }

    public int uncompress() throws IOException {
        int bitsWritten = 0;
        if (bin.readBits(IHuffConstants.BITS_PER_INT) == IHuffConstants.MAGIC_NUMBER) {
            HuffmanTree newTree = null;            
            int num = bin.readBits(IHuffConstants.BITS_PER_INT);
            if (num == IHuffConstants.STORE_COUNTS) {
                int[] freq = new int[IHuffConstants.ALPH_SIZE];
                for (int i = 0; i < IHuffConstants.ALPH_SIZE; i++) {
                    freq[i] = bin.readBits(IHuffConstants.BITS_PER_INT);
                }
                newTree = new HuffmanTree(freq);
            } else if (num == IHuffConstants.STORE_TREE) {
                newTree = new HuffmanTree(bin);
            }
            bitsWritten = newTree.traverseTree(bin, bout, bitsWritten);
        }
        return bitsWritten;
    }
}
