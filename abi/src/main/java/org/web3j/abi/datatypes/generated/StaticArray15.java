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
public class StaticArray15<T extends Type> extends StaticArray<T> {
    @Deprecated
    public StaticArray15(final List<T> values) {
        super(15, values);
    }

    @Deprecated
    @SafeVarargs
    public StaticArray15(final T... values) {
        super(15, values);
    }

    public StaticArray15(final Class<T> type, final List<T> values) {
        super(type, 15, values);
    }

    @SafeVarargs
    public StaticArray15(final Class<T> type, final T... values) {
        super(type, 15, values);
    }
}
