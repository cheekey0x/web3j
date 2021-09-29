/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.tx;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StructType;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.ens.EnsResolver;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.ConstructorTransaction;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

/**
 * Solidity contract type abstraction for interacting with smart contracts via native Java types.
 */
@SuppressWarnings("WeakerAccess")
public abstract class Contract extends ManagedTransaction {

    public static final String BIN_NOT_PROVIDED = "Bin file was not provided";
    public static final String FUNC_DEPLOY = "deploy";
    public static final String METADATA_INDEX = "a165627a7a72305820";

    protected final String contractBinary;
    protected String contractAddress;
    protected ContractGasProvider gasProvider;
    protected TransactionReceipt transactionReceipt;
    protected Map<String, String> deployedAddresses;
    protected DefaultBlockParameter defaultBlockParameter = DefaultBlockParameterName.LATEST;

    protected Contract(
            final String contractBinary,
            final String contractAddress,
            final Web3j web3j,
            final TransactionManager transactionManager,
            final ContractGasProvider gasProvider) {
        this(
                new EnsResolver(web3j),
                contractBinary,
                contractAddress,
                web3j,
                transactionManager,
                gasProvider);
    }

    protected Contract(
            final EnsResolver ensResolver,
            final String contractBinary,
            final String contractAddress,
            final Web3j web3j,
            final TransactionManager transactionManager,
            final ContractGasProvider gasProvider) {

        super(ensResolver, web3j, transactionManager);
        this.contractAddress = resolveContractAddress(contractAddress);
        this.contractBinary = contractBinary;
        this.gasProvider = gasProvider;
    }

    protected Contract(
            final String contractBinary,
            final String contractAddress,
            final Web3j web3j,
            final Credentials credentials,
            final ContractGasProvider gasProvider) {
        this(
                new EnsResolver(web3j),
                contractBinary,
                contractAddress,
                web3j,
                new RawTransactionManager(web3j, credentials),
                gasProvider);
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setTransactionReceipt(final TransactionReceipt transactionReceipt) {
        this.transactionReceipt = transactionReceipt;
    }

    public String getContractBinary() {
        return contractBinary;
    }

    public void setGasProvider(final ContractGasProvider gasProvider) {
        this.gasProvider = gasProvider;
    }

    /**
     * Allow {@code gasPrice} to be set.
     *
     * @param newPrice gas price to use for subsequent transactions
     * @deprecated use ContractGasProvider
     */
    public void setGasPrice(final BigInteger newPrice) {
        this.gasProvider = new StaticGasProvider(newPrice, gasProvider.getGasLimit());
    }

    /**
     * Get the current {@code gasPrice} value this contract uses when executing transactions.
     *
     * @return the gas price set on this contract
     * @deprecated use ContractGasProvider
     */
    public BigInteger getGasPrice() {
        return gasProvider.getGasPrice();
    }

    /**
     * Check that the contract deployed at the address associated with this smart contract wrapper
     * is in fact the contract you believe it is.
     *
     * <p>This method uses the <a
     * href="https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getcode">eth_getCode</a> method to
     * get the contract byte code and validates it against the byte code stored in this smart
     * contract wrapper.
     *
     * @return true if the contract is valid
     * @throws IOException if unable to connect to web3j node
     */
    public boolean isValid() throws IOException {
        if (contractBinary.equals(BIN_NOT_PROVIDED)) {
            throw new UnsupportedOperationException(
                    "Contract binary not present in contract wrapper, "
                            + "please generate your wrapper using -abiFile=<file>");
        }

        if (contractAddress.equals("")) {
            throw new UnsupportedOperationException(
                    "Contract binary not present, you will need to regenerate your smart "
                            + "contract wrapper with web3j v2.2.0+");
        }

        final EthGetCode ethGetCode =
                transactionManager.getCode(contractAddress, DefaultBlockParameterName.LATEST);
        if (ethGetCode.hasError()) {
            return false;
        }

        String code = Numeric.cleanHexPrefix(ethGetCode.getCode());
        final int metadataIndex = code.indexOf(METADATA_INDEX);
        if (metadataIndex != -1) {
            code = code.substring(0, metadataIndex);
        }
        // There may be multiple contracts in the Solidity bytecode, hence we only check for a
        // match with a subset
        return !code.isEmpty() && contractBinary.contains(code);
    }

    /**
     * If this Contract instance was created at deployment, the TransactionReceipt associated with
     * the initial creation will be provided, e.g. via a <em>deploy</em> method. This will not
     * persist for Contracts instances constructed via a <em>load</em> method.
     *
     * @return the TransactionReceipt generated at contract deployment
     */
    public Optional<TransactionReceipt> getTransactionReceipt() {
        return Optional.ofNullable(transactionReceipt);
    }

    /**
     * Sets the default block parameter. This use useful if one wants to query historical state of a
     * contract.
     *
     * @param defaultBlockParameter the default block parameter
     * @deprecated Set the block parameter on a per-call basis
     */
    @Deprecated
    public void setDefaultBlockParameter(final DefaultBlockParameter defaultBlockParameter) {
        this.defaultBlockParameter = defaultBlockParameter;
    }

    protected static <T extends Contract> RemoteCall<T> deployRemoteCall(
            final Class<T> type,
            final Web3j web3j,
            final Credentials credentials,
            final ContractGasProvider contractGasProvider,
            final String binary,
            final String encodedConstructor,
            final BigInteger value) {
        return new ConstructorTransaction<>(
                web3j,
                type,
                credentials,
                null,
                contractGasProvider,
                binary,
                encodedConstructor,
                value);
    }

    protected static <T extends Contract> RemoteCall<T> deployRemoteCall(
            final Class<T> type,
            final Web3j web3j,
            final Credentials credentials,
            final ContractGasProvider contractGasProvider,
            final String binary,
            final String encodedConstructor) {
        return new ConstructorTransaction<>(
                web3j,
                type,
                credentials,
                null,
                contractGasProvider,
                binary,
                encodedConstructor,
                BigInteger.ZERO);
    }

    protected static <T extends Contract> RemoteCall<T> deployRemoteCall(
            final Class<T> type,
            final Web3j web3j,
            final TransactionManager transactionManager,
            final ContractGasProvider contractGasProvider,
            final String binary,
            final String encodedConstructor,
            final BigInteger value) {
        return new ConstructorTransaction<>(
                web3j,
                type,
                null,
                transactionManager,
                contractGasProvider,
                binary,
                encodedConstructor,
                value);
    }

    protected static <T extends Contract> RemoteCall<T> deployRemoteCall(
            final Class<T> type,
            final Web3j web3j,
            final TransactionManager transactionManager,
            final ContractGasProvider contractGasProvider,
            final String binary,
            final String encodedConstructor) {
        return new ConstructorTransaction<>(
                web3j,
                type,
                null,
                transactionManager,
                contractGasProvider,
                binary,
                encodedConstructor,
                BigInteger.ZERO);
    }

    public static EventValues staticExtractEventParameters(final Event event, final Log log) {
        final List<String> topics = log.getTopics();
        final String encodedEventSignature = EventEncoder.encode(event);
        if (topics == null || topics.size() == 0 || !topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        final List<Type<?>> indexedValues = new ArrayList<>();
        final List<Type<?>> nonIndexedValues =
                FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());

        final List<TypeReference<Type<?>>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            final Type<?> value =
                    FunctionReturnDecoder.decodeIndexedValue(
                            topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

    protected String resolveContractAddress(final String contractAddress) {
        return ensResolver.resolve(contractAddress);
    }

    protected EventValues extractEventParameters(final Event event, final Log log) {
        return staticExtractEventParameters(event, log);
    }

    protected List<EventValues> extractEventParameters(
            final Event event, final TransactionReceipt transactionReceipt) {
        return transactionReceipt.getLogs().stream()
                .map(log -> extractEventParameters(event, log))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected EventValuesWithLog extractEventParametersWithLog(final Event event, final Log log) {
        return staticExtractEventParametersWithLog(event, log);
    }

    protected static EventValuesWithLog staticExtractEventParametersWithLog(
            final Event event, final Log log) {
        final EventValues eventValues = staticExtractEventParameters(event, log);
        return (eventValues == null) ? null : new EventValuesWithLog(eventValues, log);
    }

    protected List<EventValuesWithLog> extractEventParametersWithLog(
            final Event event, final TransactionReceipt transactionReceipt) {
        return transactionReceipt.getLogs().stream()
                .map(log -> extractEventParametersWithLog(event, log))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Subclasses should implement this method to return pre-existing addresses for deployed
     * contracts.
     *
     * @param networkId the network id, for example "1" for the main-net, "3" for ropsten, etc.
     * @return the deployed address of the contract, if known, and null otherwise.
     */
    protected String getStaticDeployedAddress(final String networkId) {
        return null;
    }

    public final void setDeployedAddress(final String networkId, final String address) {
        if (deployedAddresses == null) {
            deployedAddresses = new HashMap<>();
        }
        deployedAddresses.put(networkId, address);
    }

    public final String getDeployedAddress(final String networkId) {
        String addr = null;
        if (deployedAddresses != null) {
            addr = deployedAddresses.get(networkId);
        }
        return addr == null ? getStaticDeployedAddress(networkId) : addr;
    }

    /** Adds a log field to {@link EventValues}. */
    public static class EventValuesWithLog {
        private final EventValues eventValues;
        private final Log log;

        private EventValuesWithLog(final EventValues eventValues, final Log log) {
            this.eventValues = eventValues;
            this.log = log;
        }

        public List<Type<?>> getIndexedValues() {
            return eventValues.getIndexedValues();
        }

        public List<Type<?>> getNonIndexedValues() {
            return eventValues.getNonIndexedValues();
        }

        public Log getLog() {
            return log;
        }
    }
}
