package org.web3j.abi.datatypes.generated;

import java.math.BigInteger;
import org.web3j.abi.datatypes.Ufixed;

/**
 * <p>Auto generated code.<br>
 * <strong>Do not modifiy!</strong><br>
 * Please use {@link org.web3j.codegen.AbiTypesGenerator} to update.</p>
 */
public class Ufixed56x128 extends Ufixed {
  public static final Ufixed56x128 DEFAULT = new Ufixed56x128(BigInteger.ZERO);

  public Ufixed56x128(BigInteger value) {
    super(56, 128, value);
  }

  public Ufixed56x128(int mBitSize, int nBitSize, BigInteger m, BigInteger n) {
    super(56, 128, m, n);
  }
}
