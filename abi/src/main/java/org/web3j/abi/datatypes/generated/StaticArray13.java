package org.web3j.abi.datatypes.generated;

import java.util.List;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.Type;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use org.web3j.codegen.AbiTypesGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class StaticArray13<T extends Type> extends StaticArray<T> {
    @Deprecated
    public StaticArray13(final List<T> values) {
        super(13, values);
    }

    @Deprecated
    @SafeVarargs
    public StaticArray13(final T... values) {
        super(13, values);
    }

    public StaticArray13(final Class<T> type, final List<T> values) {
        super(type, 13, values);
    }

    @SafeVarargs
    public StaticArray13(final Class<T> type, final T... values) {
        super(type, 13, values);
    }
}
