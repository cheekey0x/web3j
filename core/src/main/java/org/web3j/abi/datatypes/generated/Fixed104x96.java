package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed104x96 extends Fixed {
    public static final Fixed104x96 DEFAULT = new Fixed104x96(BigInteger.ZERO);

    public Fixed104x96(BigInteger value) {
        super(104, 96, value);
    }

    public Fixed104x96(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(104, 96, m, n);
    }
}
