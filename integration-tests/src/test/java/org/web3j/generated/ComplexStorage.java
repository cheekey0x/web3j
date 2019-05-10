package org.web3j.generated;


import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.CompositeDataHolderType;
import org.web3j.abi.datatypes.DynamicStructType;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version none.
 */
public class ComplexStorage extends Contract {
    private static final String BINARY = "60806040523480156200001157600080fd5b506040516080806200117d8339810180604052620000339190810190620002c1565b8160008082015181600001908051906020019062000053929190620000ad565b50602082015181600101908051906020019062000072929190620000ad565b50905050806002600082015181600001908051906020019062000097929190620000ad565b50602082015181600101559050505050620003cf565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620000f057805160ff191683800117855562000121565b8280016001018555821562000121579182015b828111156200012057825182559160200191906001019062000103565b5b50905062000130919062000134565b5090565b6200015991905b80821115620001555760008160009055506001016200013b565b5090565b90565b600082601f83011215156200017057600080fd5b815162000187620001818262000362565b62000334565b91508082526020830160208301858383011115620001a457600080fd5b620001b183828462000399565b50505092915050565b600060408284031215620001cd57600080fd5b620001d9604062000334565b9050600082015167ffffffffffffffff811115620001f657600080fd5b62000204848285016200015c565b60008301525060206200021a84828501620002ab565b60208301525092915050565b6000604082840312156200023957600080fd5b62000245604062000334565b9050600082015167ffffffffffffffff8111156200026257600080fd5b62000270848285016200015c565b600083015250602082015167ffffffffffffffff8111156200029157600080fd5b6200029f848285016200015c565b60208301525092915050565b6000620002b982516200038f565b905092915050565b60008060408385031215620002d557600080fd5b600083015167ffffffffffffffff811115620002f057600080fd5b620002fe8582860162000226565b925050602083015167ffffffffffffffff8111156200031c57600080fd5b6200032a85828601620001ba565b9150509250929050565b6000604051905081810181811067ffffffffffffffff821117156200035857600080fd5b8060405250919050565b600067ffffffffffffffff8211156200037a57600080fd5b601f19601f8301169050602081019050919050565b6000819050919050565b60005b83811015620003b95780820151818401526020810190506200039c565b83811115620003c9576000848401525b50505050565b610d9e80620003df6000396000f300608060405260043610610078576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063243dc8da1461007d5780632cf07395146100a857806367a23d13146100d15780638fc965bd146100fc578063c225651314610128578063cfd91f2b14610151575b600080fd5b34801561008957600080fd5b5061009261017d565b60405161009f9190610c40565b60405180910390f35b3480156100b457600080fd5b506100cf60048036036100ca9190810190610b17565b6102db565b005b3480156100dd57600080fd5b506100e661031d565b6040516100f39190610c1e565b60405180910390f35b34801561010857600080fd5b506101116103e3565b60405161011f929190610c62565b60405180910390f35b34801561013457600080fd5b5061014f600480360361014a9190810190610ad6565b61065b565b005b34801561015d57600080fd5b5061016661068b565b604051610174929190610c62565b60405180910390f35b6101856108b3565b6000604080519081016040529081600082018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561022b5780601f106102005761010080835404028352916020019161022b565b820191906000526020600020905b81548152906001019060200180831161020e57829003601f168201915b50505050508152602001600182018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102cd5780601f106102a2576101008083540402835291602001916102cd565b820191906000526020600020905b8154815290600101906020018083116102b057829003601f168201915b505050505081525050905090565b806000808201518160000190805190602001906102f99291906108cd565b5060208201518160010190805190602001906103169291906108cd565b5090505050565b61032561094d565b6002604080519081016040529081600082018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156103cb5780601f106103a0576101008083540402835291602001916103cb565b820191906000526020600020905b8154815290600101906020018083116103ae57829003601f168201915b50505050508152602001600182015481525050905090565b6103eb6108b3565b6103f361094d565b3373ffffffffffffffffffffffffffffffffffffffff167fed780f1dfaf928f77dd066a615b98641a01d34bf8b139c6f87ae25365fdc9a1f838360405161043b929190610c62565b60405180910390a26000600281604080519081016040529081600082018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104ec5780601f106104c1576101008083540402835291602001916104ec565b820191906000526020600020905b8154815290600101906020018083116104cf57829003601f168201915b50505050508152602001600182018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561058e5780601f106105635761010080835404028352916020019161058e565b820191906000526020600020905b81548152906001019060200180831161057157829003601f168201915b505050505081525050915080604080519081016040529081600082018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561063e5780601f106106135761010080835404028352916020019161063e565b820191906000526020600020905b81548152906001019060200180831161062157829003601f168201915b505050505081526020016001820154815250509050915091509091565b806002600082015181600001908051906020019061067a9291906108cd565b506020820151816001015590505050565b6106936108b3565b61069b61094d565b6000600281604080519081016040529081600082018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107445780601f1061071957610100808354040283529160200191610744565b820191906000526020600020905b81548152906001019060200180831161072757829003601f168201915b50505050508152602001600182018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107e65780601f106107bb576101008083540402835291602001916107e6565b820191906000526020600020905b8154815290600101906020018083116107c957829003601f168201915b505050505081525050915080604080519081016040529081600082018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108965780601f1061086b57610100808354040283529160200191610896565b820191906000526020600020905b81548152906001019060200180831161087957829003601f168201915b505050505081526020016001820154815250509050915091509091565b604080519081016040528060608152602001606081525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061090e57805160ff191683800117855561093c565b8280016001018555821561093c579182015b8281111561093b578251825591602001919060010190610920565b5b5090506109499190610967565b5090565b604080519081016040528060608152602001600081525090565b61098991905b8082111561098557600081600090555060010161096d565b5090565b90565b600082601f830112151561099f57600080fd5b81356109b26109ad82610cc6565b610c99565b915080825260208301602083018583830111156109ce57600080fd5b6109d9838284610d11565b50505092915050565b6000604082840312156109f457600080fd5b6109fe6040610c99565b9050600082013567ffffffffffffffff811115610a1a57600080fd5b610a268482850161098c565b6000830152506020610a3a84828501610ac2565b60208301525092915050565b600060408284031215610a5857600080fd5b610a626040610c99565b9050600082013567ffffffffffffffff811115610a7e57600080fd5b610a8a8482850161098c565b600083015250602082013567ffffffffffffffff811115610aaa57600080fd5b610ab68482850161098c565b60208301525092915050565b6000610ace8235610d07565b905092915050565b600060208284031215610ae857600080fd5b600082013567ffffffffffffffff811115610b0257600080fd5b610b0e848285016109e2565b91505092915050565b600060208284031215610b2957600080fd5b600082013567ffffffffffffffff811115610b4357600080fd5b610b4f84828501610a46565b91505092915050565b6000610b6382610cf2565b808452610b77816020860160208601610d20565b610b8081610d53565b602085010191505092915050565b60006040830160008301518482036000860152610bab8282610b58565b9150506020830151610bc06020860182610c0f565b508091505092915050565b60006040830160008301518482036000860152610be88282610b58565b91505060208301518482036020860152610c028282610b58565b9150508091505092915050565b610c1881610cfd565b82525050565b60006020820190508181036000830152610c388184610b8e565b905092915050565b60006020820190508181036000830152610c5a8184610bcb565b905092915050565b60006040820190508181036000830152610c7c8185610bcb565b90508181036020830152610c908184610b8e565b90509392505050565b6000604051905081810181811067ffffffffffffffff82111715610cbc57600080fd5b8060405250919050565b600067ffffffffffffffff821115610cdd57600080fd5b601f19601f8301169050602081019050919050565b600081519050919050565b6000819050919050565b6000819050919050565b82818337600083830152505050565b60005b83811015610d3e578082015181840152602081019050610d23565b83811115610d4d576000848401525b50505050565b6000601f19601f83011690509190505600a265627a7a72305820955df472bb3e56951d37542fc653be819ea54bc90370890c55ee60a870c644216c6578706572696d656e74616cf50037";

    public static final String FUNC_GETFOO = "getFoo";

    public static final String FUNC_SETFOO = "setFoo";

    public static final String FUNC_GETBAR = "getBar";

    public static final String FUNC_GETFOOBAREVENT = "getFooBarEvent";

    public static final String FUNC_SETBAR = "setBar";

    public static final String FUNC_GETFOOBAR = "getFooBar";

    public static final Event ACCESS_EVENT = new Event("Access",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }, new TypeReference<ComplexStorage.TupleClass1>() {
            }, new TypeReference<ComplexStorage.TupleClass2>() {
            }));
    ;

    @Deprecated
    protected ComplexStorage(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ComplexStorage(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ComplexStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ComplexStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<DynamicStructType> getFoo() {
        final Function function = new Function(FUNC_GETFOO,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<ComplexStorage.TupleClass1>() {
                }));
        return executeRemoteCallSingleValueReturn(function, DynamicStructType.class);
    }

    public RemoteCall<TransactionReceipt> setFoo(ComplexStorage.TupleClass1 _toSet) {
        final Function function = new Function(
                FUNC_SETFOO,
                Arrays.<Type>asList(new org.web3j.generated.ComplexStorage.TupleClass1(_toSet)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<DynamicStructType> getBar() {
        final Function function = new Function(FUNC_GETBAR,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<ComplexStorage.TupleClass2>() {
                }));
        return executeRemoteCallSingleValueReturn(function, DynamicStructType.class);
    }

    public RemoteCall<TransactionReceipt> getFooBarEvent() {
        final Function function = new Function(
                FUNC_GETFOOBAREVENT,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setBar(ComplexStorage.TupleClass2 _toSet) {
        final Function function = new Function(
                FUNC_SETBAR,
                Arrays.<Type>asList(new org.web3j.generated.ComplexStorage.TupleClass2(_toSet)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple2<ComplexStorage.TupleClass1, ComplexStorage.TupleClass2>> getFooBar() {
        final Function function = new Function(FUNC_GETFOOBAR,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<ComplexStorage.TupleClass1>() {
                }, new TypeReference<ComplexStorage.TupleClass2>() {
                }));
        return new RemoteCall<Tuple2<ComplexStorage.TupleClass1, ComplexStorage.TupleClass2>>(
                new Callable<Tuple2<ComplexStorage.TupleClass1, ComplexStorage.TupleClass2>>() {
                    @Override
                    public Tuple2<ComplexStorage.TupleClass1, ComplexStorage.TupleClass2> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<ComplexStorage.TupleClass1, ComplexStorage.TupleClass2>(
                                (ComplexStorage.TupleClass1) results.get(0).getValue(),
                                (ComplexStorage.TupleClass2) results.get(1).getValue());
                    }
                });
    }

    public List<AccessEventResponse> getAccessEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ACCESS_EVENT, transactionReceipt);
        ArrayList<AccessEventResponse> responses = new ArrayList<AccessEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AccessEventResponse typedResponse = new AccessEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._foo = (ComplexStorage.TupleClass1) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._bar = (ComplexStorage.TupleClass2) eventValues.getNonIndexedValues().get(1).getValue();
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
                typedResponse._foo = (ComplexStorage.TupleClass1) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._bar = (ComplexStorage.TupleClass2) eventValues.getNonIndexedValues().get(1).getValue();
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
    public static ComplexStorage load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ComplexStorage(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ComplexStorage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ComplexStorage(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ComplexStorage load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ComplexStorage(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ComplexStorage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ComplexStorage(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ComplexStorage> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, ComplexStorage.TupleClass1 _foo, ComplexStorage.TupleClass2 _bar) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(
                Arrays.<Type>asList(
                        new CompositeDataHolderType(new org.web3j.generated.ComplexStorage.TupleClass1(_foo),
                                new org.web3j.generated.ComplexStorage.TupleClass2(_bar))
                )
        );
        return deployRemoteCall(ComplexStorage.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<ComplexStorage> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, ComplexStorage.TupleClass1 _foo, ComplexStorage.TupleClass2 _bar) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(
                Arrays.<Type>asList(
                        new CompositeDataHolderType(new org.web3j.generated.ComplexStorage.TupleClass1(_foo),
                                new org.web3j.generated.ComplexStorage.TupleClass2(_bar))
                )
        );

        return deployRemoteCall(ComplexStorage.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    public static class TupleClass1 extends DynamicStructType {
        public String id;

        public String name;

        public TupleClass1(String id, String name) {
            super(Type.class, Arrays.asList(new Utf8String(id), new Utf8String(name)));
            this.id = id;
            this.name = name;
        }

        public TupleClass1(TupleClass1 foo) {
            this(foo.id, foo.name);
        }
    }

    public static class TupleClass2 extends DynamicStructType {
        public String id;

        public BigInteger data;

        public TupleClass2(String id, BigInteger data) {
            super(Type.class, Arrays.asList(new Utf8String(id), new Uint256(data)));
            this.id = id;
            this.data = data;
        }

        public TupleClass2(TupleClass2 bar) {
            this(bar.id, bar.data);
        }
    }

    public static class AccessEventResponse {
        public Log log;

        public String _address;

        public ComplexStorage.TupleClass1 _foo;

        public ComplexStorage.TupleClass2 _bar;
    }
}

