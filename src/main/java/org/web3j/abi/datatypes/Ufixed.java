package org.web3j.abi.datatypes;

import java.math.BigInteger;

/**
 * Signed fixed type.
 */
public class Ufixed extends FixedPointType {

    public Ufixed(int mBitSize, int nBitSize, BigInteger value) {
        super("ufixed", mBitSize, nBitSize, value);
    }

    public Ufixed(BigInteger value) {
        this(DEFAULT_BIT_LENGTH, DEFAULT_BIT_LENGTH, value);
    }

    public Ufixed(BigInteger m, BigInteger n) {
        this(convert(m, n));
    }

    public Ufixed(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        this(convert(mBitSize, nBitSize, m, n));
    }

    @Override
    boolean valid(int mBitSize, int nBitSize, BigInteger value) {
        return super.valid(mBitSize, nBitSize, value)
                && value.signum() != -1;
    }
}
