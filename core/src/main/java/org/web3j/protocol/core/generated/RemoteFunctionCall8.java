package org.web3j.protocol.core.generated;

import java.util.List;

import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.TransactionManager;

public class RemoteFunctionCall8<T1, T2, T3, T4, T5, T6, T7, T8>
        extends RemoteFunctionCall<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> {

    public RemoteFunctionCall8(
            final Function function,
            final String contractAddress,
            final TransactionManager transactionManager,
            final DefaultBlockParameter defaultBlockParameter) {
        super(function, contractAddress, transactionManager, defaultBlockParameter);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> convert(final List<Type<?>> values) {
        return new Tuple8<>(
                (T1) values.get(0),
                (T2) values.get(1),
                (T3) values.get(2),
                (T4) values.get(3),
                (T5) values.get(4),
                (T6) values.get(5),
                (T7) values.get(6),
                (T8) values.get(7)
        );
    }
}
