package utils;

import java.util.Arrays;

// Stores arrays of bits by using an array of long and manipulating long values with bit operators
// <insert credits to libGDX for implementation details on bitset>

public class BitVector {

	long[] bits = {0};
	static int WORD_SIZE  = 6;
	static int LONG_BITMASK = 0x3f;
	
	public BitVector() {}
	
	public void set(int index) {
		int wordIndex = index >> WORD_SIZE;
		checkCapacity(wordIndex);
		bits[wordIndex] |= 1L << ( index & LONG_BITMASK);
	}
	
	public boolean get(int index) { //////
		int wordIndex = index >> WORD_SIZE;
		if (wordIndex >= bits.length) return false;
		return ( bits[wordIndex] & ( 1L << ( index & LONG_BITMASK) ) ) != 0;
	}
	
	public void clear(int index) {
		int wordIndex = index >> WORD_SIZE;
		checkCapacity(wordIndex);
		bits[wordIndex] &= ~( 1L << ( index & LONG_BITMASK) );
	}
	
	public void flip(int index) {
		int wordIndex = index >> WORD_SIZE;
		checkCapacity(wordIndex);
		bits[wordIndex] ^= 1L << ( index & LONG_BITMASK);
	}
	
	public boolean getAndSet(int index) {
		int wordIndex = index >> WORD_SIZE;
		checkCapacity(wordIndex);
		long oldBit = bits[wordIndex];
		bits[wordIndex] |= 1L << ( index & LONG_BITMASK);
		return bits[wordIndex] == oldBit;
	}
	
	public boolean getAndClear(int index) {
		int wordIndex = index >> WORD_SIZE;
		if (wordIndex >= bits.length) return false;
		long oldBit = bits[wordIndex];
		bits[wordIndex] &= ~( 1L << ( index & LONG_BITMASK) );
		return bits[wordIndex] != oldBit;
	}

	public void clearAll() {
		Arrays.fill(bits, 0);
	}
	
	public void and(BitVector other) {
		int minWordLength = Math.min(bits.length, other.bits.length);
		for( int i = 0; i < minWordLength; i++) {
			bits[i] &= other.bits[i];
		}
		if (bits.length > minWordLength) {
			for(int i = minWordLength; i < bits.length; i++) {
				bits[i] = 0L;
			}
		}
	}
	
	public void andNot(BitVector other) {
		int minWordLength = Math.min(bits.length, other.bits.length);
		for( int i = 0; i < minWordLength; i++) {
			bits[i] &= ~other.bits[i];
		}
	}
	
	public void or(BitVector other) {
		int minWordLength = Math.min(bits.length, other.bits.length);
		for( int i = 0; i < minWordLength; i++) {
			bits[i] |= other.bits[i];
		}
		if (other.bits.length > minWordLength) {
			checkCapacity(other.bits.length);
			for(int i = minWordLength; i < other.bits.length; i++) {
				bits[i] = other.bits[i];
			}
		}
	}
	
	public void xor(BitVector other) {
		int minWordLength = Math.min(bits.length, other.bits.length);
		for( int i = 0; i < minWordLength; i++) {
			bits[i] ^= other.bits[i];
		}
		if (other.bits.length > minWordLength) {
			checkCapacity(other.bits.length);
			for(int i = minWordLength; i < other.bits.length; i++) {
				bits[i] = other.bits[i];
			}
		}
	}
	
	public int nextSetBit(int fromIndex) {
		
		return -1;
	}
	
	public int nextClearBit(int fromIndex) {
		return -1;
	}
	
	public int previousSetBit(int fromIndex) {
		return -1;
	}
	
	public int previousClearBit(int fromIndex) {
		return -1;
	}
	
	public boolean containsAll(BitVector other) {
		long[] thisBits = this.bits;
		long[] otherBits = other.bits;
		int thisLen = thisBits.length;
		int otherLen = otherBits.length;
		
		for (int i = thisLen; i < otherLen; i++) { // checks first if any words in wordIndices of otherBits is not zero. this happens when length of otherBits > thisBits.
			if (otherBits[i] != 0)
				return false;
		}
		
		for (int i = Math.min(thisLen, otherLen); i >= 0; i--) { //checks from minWordLength towards zero, if thisBits contains all bits of otherBits.
			if ((thisBits[i] & otherBits[i]) != otherBits[i] )
				return false;
		}
		
		return true;
	}
	
	public boolean intersects(BitVector other) {
		long[] thisBits = this.bits;
		long[] otherBits = other.bits;
		int minWordLength = Math.min(thisBits.length, otherBits.length);
		for (int i = minWordLength; i >= 0; i--) {
			if ((thisBits[i] & otherBits[i]) != 0) {
				return true;
			}
		}
		return false;
	}
	
	protected void checkCapacity(int len) {
		if (bits.length <= len) {
			long[] newBits = new long[len + 1];
			System.arraycopy(bits, 0, newBits, 0, bits.length);
			bits = newBits;
		}
	}
	
	//sizes: bit count, bit length, array size 
	//isEmpty
	//
	
}
