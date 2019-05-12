package org.web3j.test;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStructType;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.unittests.java.ComplexStorage.TupleClass1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version none.
 */
public class SimpleStruct3 extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50604051604080610819833981018060405261002f91908101906101d4565b8060008082015181600001556020820151816001019080519060200190610057929190610061565b50905050506102ab565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a257805160ff19168380011785556100d0565b828001600101855582156100d0579182015b828111156100cf5782518255916020019190600101906100b4565b5b5090506100dd91906100e1565b5090565b61010391905b808211156100ff5760008160009055506001016100e7565b5090565b90565b600082601f830112151561011957600080fd5b815161012c61012782610242565b610215565b9150808252602083016020830185838301111561014857600080fd5b610153838284610278565b50505092915050565b60006040828403121561016e57600080fd5b6101786040610215565b90506000610188848285016101c0565b600083015250602082015167ffffffffffffffff8111156101a857600080fd5b6101b484828501610106565b60208301525092915050565b60006101cc825161026e565b905092915050565b6000602082840312156101e657600080fd5b600082015167ffffffffffffffff81111561020057600080fd5b61020c8482850161015c565b91505092915050565b6000604051905081810181811067ffffffffffffffff8211171561023857600080fd5b8060405250919050565b600067ffffffffffffffff82111561025957600080fd5b601f19601f8301169050602081019050919050565b6000819050919050565b60005b8381101561029657808201518184015260208101905061027b565b838111156102a5576000848401525b50505050565b61055f806102ba6000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806337ec42c31461005157806367a23d131461007a575b600080fd5b34801561005d57600080fd5b5061007860048036036100739190810190610375565b6100a5565b005b34801561008657600080fd5b5061008f6100d4565b60405161009c9190610438565b60405180910390f35b80600080820151816000015560208201518160010190805190602001906100cd9291906101e8565b5090505050565b6100dc610268565b3373ffffffffffffffffffffffffffffffffffffffff167f6c29f7da837889ffde19797304bda101433c6509d7871822cf846c4b2b586032826040516101229190610438565b60405180910390a2600060408051908101604052908160008201548152602001600182018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101da5780601f106101af576101008083540402835291602001916101da565b820191906000526020600020905b8154815290600101906020018083116101bd57829003601f168201915b505050505081525050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061022957805160ff1916838001178555610257565b82800160010185558215610257579182015b8281111561025657825182559160200191906001019061023b565b5b5090506102649190610282565b5090565b604080519081016040528060008152602001606081525090565b6102a491905b808211156102a0576000816000905550600101610288565b5090565b90565b600082601f83011215156102ba57600080fd5b81356102cd6102c882610487565b61045a565b915080825260208301602083018583830111156102e957600080fd5b6102f48382846104d2565b50505092915050565b60006040828403121561030f57600080fd5b610319604061045a565b9050600061032984828501610361565b600083015250602082013567ffffffffffffffff81111561034957600080fd5b610355848285016102a7565b60208301525092915050565b600061036d82356104c8565b905092915050565b60006020828403121561038757600080fd5b600082013567ffffffffffffffff8111156103a157600080fd5b6103ad848285016102fd565b91505092915050565b60006103c1826104b3565b8084526103d58160208601602086016104e1565b6103de81610514565b602085010191505092915050565b60006040830160008301516104046000860182610429565b506020830151848203602086015261041c82826103b6565b9150508091505092915050565b610432816104be565b82525050565b6000602082019050818103600083015261045281846103ec565b905092915050565b6000604051905081810181811067ffffffffffffffff8211171561047d57600080fd5b8060405250919050565b600067ffffffffffffffff82111561049e57600080fd5b601f19601f8301169050602081019050919050565b600081519050919050565b6000819050919050565b6000819050919050565b82818337600083830152505050565b60005b838110156104ff5780820151818401526020810190506104e4565b8381111561050e576000848401525b50505050565b6000601f19601f83011690509190505600a265627a7a72305820f111fbcd9a3eda7a012506fbd88c56b71212822fa01cca30fcf02f245d548b2a6c6578706572696d656e74616cf50037";

    public static final String FUNC_SETBAR = "setBar";

    public static final String FUNC_GETBAR = "getBar";

    public static final Event ACCESS_EVENT = new Event("Access", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<ComplexStorage.TupleClass1>() {}));
    ;

    @Deprecated
    protected SimpleStruct3(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SimpleStruct3(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SimpleStruct3(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SimpleStruct3(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> setBar(ComplexStorage.TupleClass1 _toSet) {
        final Function function = new Function(
                FUNC_SETBAR, 
                Arrays.<Type>asList(new org.web3j.unittests.java.ComplexStorage.TupleClass1(_toSet)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> getBar() {
        final Function function = new Function(
                FUNC_GETBAR, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<AccessEventResponse> getAccessEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ACCESS_EVENT, transactionReceipt);
        ArrayList<AccessEventResponse> responses = new ArrayList<AccessEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AccessEventResponse typedResponse = new AccessEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._bar = (ComplexStorage.TupleClass1) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<AccessEventResponse> accessEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, AccessEventResponse>() {
            @Override
            public AccessEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ACCESS_EVENT, log);
                AccessEventResponse typedResponse = new AccessEventResponse();
                typedResponse.log = log;
                typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._bar = (ComplexStorage.TupleClass1) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<AccessEventResponse> accessEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ACCESS_EVENT));
        return accessEventFlowable(filter);
    }

    @Deprecated
    public static SimpleStruct3 load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SimpleStruct3(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SimpleStruct3 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SimpleStruct3(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SimpleStruct3 load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SimpleStruct3(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SimpleStruct3 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SimpleStruct3(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SimpleStruct3> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, ComplexStorage.TupleClass1 _bar) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.unittests.java.ComplexStorage.TupleClass1(_bar)));
        return deployRemoteCall(SimpleStruct3.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<SimpleStruct3> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, ComplexStorage.TupleClass1 _bar) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.unittests.java.ComplexStorage.TupleClass1(_bar)));
        return deployRemoteCall(SimpleStruct3.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SimpleStruct3> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, ComplexStorage.TupleClass1 _bar) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.unittests.java.ComplexStorage.TupleClass1(_bar)));
        return deployRemoteCall(SimpleStruct3.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SimpleStruct3> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, ComplexStorage.TupleClass1 _bar) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.unittests.java.ComplexStorage.TupleClass1(_bar)));
        return deployRemoteCall(SimpleStruct3.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public class TupleClass1 extends StaticStructType {
        public BigInteger intval;

        public String data;
    }

    public static class AccessEventResponse {
        public Log log;

        public String _address;

        public ComplexStorage.TupleClass1 _bar;
    }
}
