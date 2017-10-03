package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Fixed;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Fixed136x64 extends Fixed {
    public static final Fixed136x64 DEFAULT = new Fixed136x64(BigInteger.ZERO);

    public Fixed136x64(BigInteger value) {
        super(136, 64, value);
    }

    public Fixed136x64(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
        super(136, 64, m, n);
    }
}
