import java.util.BitSet;

/**
 * Created by glacier on 14-9-1.
 */
public class BloomFilter {
    private int BloomFilter_Size = 2 << 24;
    private int[] HashSeeds;
    private BitSet bitset;
    private HashFunction[] HFS;

    public BloomFilter( ) {
        this.BloomFilter_Size = 2 << 24;
        this.HashSeeds  = new int[] { 3, 5, 7, 11, 13, 31, 37, 61 };
        this.bitset = new BitSet( this.BloomFilter_Size );
        this.HFS = new HashFunction[ this.HashSeeds.length ];
        for ( int i = 0 ; i < HFS.length; i ++ ) {
            HFS[i] = new HashFunction( this.BloomFilter_Size, HashSeeds[i] );
        }
    }

    public boolean addValue( String Value ) {
        if ( Value == null )
            return false;
        for ( HashFunction HF : HFS ) {
            bitset.set( HF.Hash( Value ), true );
        }
        return true;
    }
    public boolean isUniqueValue( String Value ) {
        if ( Value == null )
            return false;
        boolean Result = true;
        for ( HashFunction HF : HFS ) {
            Result = Result && bitset.get( HF.Hash( Value ) );
            if ( Result == false ) {
                return false;
            }
        }
        return true;
    }
    public void clearBitset() {
        this.bitset.clear();
    }
}


class HashFunction {
    private int BloomFilter_Size, HashSeed;

    public HashFunction ( int BloomFilter_Size, int HashSeed ) {
        this.BloomFilter_Size = BloomFilter_Size;
        this.HashSeed = HashSeed;
    }
    public int Hash( String Value ) {
        int Result = 0;
        int len = Value.length();
        for ( int i = 0; i < len; i ++ ) {
            Result = HashSeed * Result + Value.charAt(i);
        }
        return Result & ( BloomFilter_Size - 1 );
    }
}