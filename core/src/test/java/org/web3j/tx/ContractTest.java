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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.SampleKeys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteArrayCall;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteTransactionCall;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.generated.RemoteCall1;
import org.web3j.protocol.core.generated.RemoteTransactionCall0;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Async;
import org.web3j.utils.Numeric;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.web3j.tx.gas.DefaultGasProvider.GAS_LIMIT;
import static org.web3j.tx.gas.DefaultGasProvider.GAS_PRICE;

@SuppressWarnings("deprecation")
public class ContractTest extends ManagedTransactionTester {

    private static final String TEST_CONTRACT_BINARY = "12345";

    private static final String TXN_SUCCESS_STATUS = "0x1";

    private static final String TXN_FAIL_STATUS = "0x0";

    private static final String TXN_GAS_USED = "0x1";

    private static final String TXN_BLOCK_NUM = "0x2";

    private static final String OWNER_REVERT_MSG_STR =
            "Only the contract owner can perform this action";

    private static final String OWNER_REVERT_MSG_HASH =
            "0x08c379a"
                    + "00000000000000000000000000000000000000000000000000000000000000020"
                    + "000000000000000000000000000000000000000000000000000000000000002f4"
                    + "f6e6c792074686520636f6e7472616374206f776e65722063616e20706572666f"
                    + "726d207468697320616374696f6e0000000000000000000000000000000000";

    private TestContract contract;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        contract =
                new TestContract(
                        ADDRESS,
                        web3j,
                        getVerifiedTransactionManager(SampleKeys.CREDENTIALS),
                        new DefaultGasProvider(),
                        null);
    }

    @Test
    public void testGetContractAddress() {
        assertEquals(ADDRESS, contract.getContractAddress());
    }

    @Test
    public void testGetContractTransactionReceipt() {
        assertFalse(contract.getTransactionReceipt().isPresent());
    }

    @Test
    public void testDeploy() throws Exception {
        final TransactionReceipt transactionReceipt = createTransactionReceipt();
        final Contract deployedContract = deployContract(transactionReceipt);

        assertEquals(ADDRESS, deployedContract.getContractAddress());
        assertTrue(deployedContract.getTransactionReceipt().isPresent());
        assertEquals(transactionReceipt, deployedContract.getTransactionReceipt().get());
    }

    @Test
    public void testContractDeployFails() throws IOException {
        final TransactionReceipt transactionReceipt = createFailedTransactionReceipt();
        prepareCall(null);
        assertThrows(IOException.class, () -> deployContract(transactionReceipt));
    }

    @Test
    public void testContractDeployWithNullStatusSucceeds() throws Exception {
        final TransactionReceipt transactionReceipt = createTransactionReceiptWithStatus(null);
        final Contract deployedContract = deployContract(transactionReceipt);

        assertEquals(ADDRESS, deployedContract.getContractAddress());
        assertTrue(deployedContract.getTransactionReceipt().isPresent());
        assertEquals(transactionReceipt, deployedContract.getTransactionReceipt().get());
    }

    @Test
    public void testIsValid() throws Exception {
        prepareEthGetCode(TEST_CONTRACT_BINARY);

        final Contract contract = deployContract(createTransactionReceipt());
        assertTrue(contract.isValid());
    }

    @Test
    public void testIsValidSkipMetadata() throws Exception {
        prepareEthGetCode(
                TEST_CONTRACT_BINARY
                        + Contract.METADATA_INDEX
                        + "a9bc86938894dc250f6ea25dd823d4472fad6087edcda429a3504e3713a9fc880029");

        final Contract contract = deployContract(createTransactionReceipt());
        assertTrue(contract.isValid());
    }

    @Test
    public void testIsValidDifferentCode() throws Exception {
        prepareEthGetCode(TEST_CONTRACT_BINARY + "0");

        final Contract contract = deployContract(createTransactionReceipt());
        assertFalse(contract.isValid());
    }

    @Test
    public void testIsValidEmptyCode() throws Exception {
        prepareEthGetCode("");

        final Contract contract = deployContract(createTransactionReceipt());
        assertFalse(contract.isValid());
    }

    @Test
    public void testIsValidNoBinThrows() {
        final TransactionManager txManager = mock(TransactionManager.class);
        final TestContract contract =
                new TestContract(
                        Contract.BIN_NOT_PROVIDED,
                        ADDRESS,
                        web3j,
                        txManager,
                        new DefaultGasProvider(),
                        null);
        assertThrows(UnsupportedOperationException.class, contract::isValid);
    }

    @Test
    public void testDeployInvalidContractAddress() throws Throwable {
        final TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setTransactionHash(TRANSACTION_HASH);

        prepareTransaction(transactionReceipt);
        final ContractGasProvider contractGasProvider = new DefaultGasProvider();
        final String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Collections.singletonList(new Uint256(BigInteger.TEN)));
        assertThrows(
                RuntimeException.class,
                () ->
                        TestContract.deployRemoteCall(
                                        TestContract.class,
                                        web3j,
                                        SampleKeys.CREDENTIALS,
                                        contractGasProvider,
                                        TEST_CONTRACT_BINARY,
                                        encodedConstructor,
                                        ZERO)
                                .send());
    }

    @Test
    public void testCallSingleValue() throws Exception {
        // Example taken from FunctionReturnDecoderTest

        prepareCall(
                "0x0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000000");

        assertEquals(new Utf8String(""), contract.callSingleValue().call());
    }

    @Test
    public void testCallSingleValueEmpty() throws Exception {
        // Example taken from FunctionReturnDecoderTest

        prepareCall("0x");

        assertNull(contract.callSingleValue().call());
    }

    @Test
    public void testCallSingleValueReverted() throws Exception {
        prepareCall(OWNER_REVERT_MSG_HASH);
        final ContractCallException thrown =
                assertThrows(ContractCallException.class, () -> contract.callSingleValue().call());

        assertEquals(
                String.format(TransactionManager.REVERT_ERR_STR, OWNER_REVERT_MSG_STR),
                thrown.getMessage());
    }

    @Test
    public void testCallMultipleValue() throws Exception {
        prepareCall(
                "0x0000000000000000000000000000000000000000000000000000000000000037"
                        + "0000000000000000000000000000000000000000000000000000000000000007");

        assertEquals(
                Arrays.asList(
                        new Uint256(BigInteger.valueOf(55)), new Uint256(BigInteger.valueOf(7))),
                contract.callMultipleValue().call());
    }

    @Test
    public void testCallMultipleValueEmpty() throws Exception {
        prepareCall("0x");

        assertEquals(emptyList(), contract.callMultipleValue().call());
    }

    @SuppressWarnings("unchecked")
    private void prepareCall(final String result) throws IOException {
        final EthCall ethCall = new EthCall();
        ethCall.setResult(result);

        final Request<?, EthCall> request = mock(Request.class);
        when(request.send()).thenReturn(ethCall);

        when(web3j.ethCall(any(Transaction.class), any(DefaultBlockParameter.class)))
                .thenReturn((Request) request);
    }

    @Test
    public void testTransaction() throws Exception {
        final TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setTransactionHash(TRANSACTION_HASH);
        transactionReceipt.setStatus(TXN_SUCCESS_STATUS);

        prepareTransaction(transactionReceipt);

        assertEquals(
                transactionReceipt,
                contract.performTransaction(new Address(BigInteger.TEN), new Uint256(ONE)).send());
    }

    @Test
    public void testTransactionFailed() throws IOException {
        final TransactionReceipt transactionReceipt = createFailedTransactionReceipt();
        prepareCall(null);

        assertThrows(
                TransactionException.class,
                () -> {
                    prepareTransaction(transactionReceipt);

                    contract.performTransaction(new Address(BigInteger.TEN), new Uint256(ONE))
                            .send();
                });
    }

    @Test
    public void testTransactionFailedWithRevertReason() throws Exception {
        final TransactionReceipt transactionReceipt = createFailedTransactionReceipt();
        prepareCall(OWNER_REVERT_MSG_HASH);

        final TransactionException thrown =
                assertThrows(
                        TransactionException.class,
                        () -> {
                            prepareTransaction(transactionReceipt);

                            contract.performTransaction(
                                            new Address(BigInteger.TEN), new Uint256(ONE))
                                    .send();
                        });

        assertEquals(
                String.format(
                        "Transaction %s has failed with status: %s. Gas used: 1. Revert reason: '%s'.",
                        TRANSACTION_HASH, TXN_FAIL_STATUS, OWNER_REVERT_MSG_STR),
                thrown.getMessage());
        assertEquals(transactionReceipt, thrown.getTransactionReceipt().get());
    }

    @Test
    public void testProcessEvent() {
        final TransactionReceipt transactionReceipt = new TransactionReceipt();
        final Log log = new Log();
        log.setTopics(
                Arrays.asList(
                        // encoded function
                        "0xfceb437c298f40d64702ac26411b2316e79f3c28ffa60edfc891ad4fc8ab82ca",
                        // indexed value
                        "0000000000000000000000003d6cb163f7c72d20b0fcd6baae5889329d138a4a"));
        // non-indexed value
        log.setData("0000000000000000000000000000000000000000000000000000000000000001");

        transactionReceipt.setLogs(singletonList(log));

        final EventValues eventValues = contract.processEvent(transactionReceipt).get(0);

        assertEquals(
                singletonList(new Address("0x3d6cb163f7c72d20b0fcd6baae5889329d138a4a")),
                eventValues.getIndexedValues());
        assertEquals(singletonList(new Uint256(ONE)), eventValues.getNonIndexedValues());
    }

    @Test
    public void testProcessEventForLogWithoutTopics() {
        final TransactionReceipt transactionReceipt = new TransactionReceipt();
        final Log log = new Log();
        log.setTopics(Collections.emptyList());
        // non-indexed value
        log.setData("0000000000000000000000000000000000000000000000000000000000000001");
        transactionReceipt.setLogs(singletonList(log));

        final List<EventValues> eventValues = contract.processEvent(transactionReceipt);
        assertTrue(eventValues.isEmpty(), "No events expected");
    }

    @Test
    public void testTimeout() throws IOException {
        prepareTransaction(null);

        final TransactionManager transactionManager =
                getVerifiedTransactionManager(SampleKeys.CREDENTIALS, 1, 1);

        contract =
                new TestContract(
                        ADDRESS, web3j, transactionManager, new DefaultGasProvider(), null);
        assertThrows(TransactionException.class, this::testErrorScenario);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidTransactionResponse() throws IOException {
        prepareNonceRequest();

        final EthSendTransaction ethSendTransaction = new EthSendTransaction();
        ethSendTransaction.setError(new Response.Error(1, "Invalid transaction"));

        final Request<?, EthSendTransaction> rawTransactionRequest = mock(Request.class);
        when(rawTransactionRequest.sendAsync()).thenReturn(Async.run(() -> ethSendTransaction));
        when(web3j.ethSendRawTransaction(any(String.class)))
                .thenReturn((Request) rawTransactionRequest);
        assertThrows(RuntimeException.class, this::testErrorScenario);
    }

    @Test
    public void testSetGetAddresses() {
        assertNull(contract.getDeployedAddress("1"));
        contract.setDeployedAddress("1", "0x000000000000add0e00000000000");
        assertNotNull(contract.getDeployedAddress("1"));
        contract.setDeployedAddress("2", "0x000000000000add0e00000000000");
        assertNotNull(contract.getDeployedAddress("2"));
    }

    @Test
    public void testSetGetGasPrice() {
        assertEquals(GAS_PRICE, contract.getGasProvider().getGasPrice(null));
        final BigInteger newPrice = GAS_PRICE.multiply(BigInteger.valueOf(2));
        contract.setGasProvider(new StaticGasProvider(newPrice, GAS_LIMIT));
        assertEquals(newPrice, contract.getGasProvider().getGasPrice(null));
    }

    @Test
    public void testStaticGasProvider() throws IOException, TransactionException {
        final StaticGasProvider gasProvider = new StaticGasProvider(BigInteger.TEN, ONE);
        final TransactionManager txManager = mock(TransactionManager.class);

        when(txManager.executeTransaction(
                        any(BigInteger.class),
                        any(BigInteger.class),
                        anyString(),
                        anyString(),
                        any(BigInteger.class),
                        anyBoolean()))
                .thenReturn(new TransactionReceipt());

        contract = new TestContract(ADDRESS, web3j, txManager, gasProvider, null);

        final Function func =
                new Function("test", Collections.emptyList(), Collections.emptyList());

        new RemoteTransactionCall0(
                        web3j,
                        func,
                        contract.contractAddress,
                        true,
                        contract.transactionManager,
                        contract.defaultBlockParameter,
                        FunctionEncoder.encode(func),
                        ZERO,
                        false,
                        contract.gasProvider)
                .send();

        verify(txManager)
                .executeTransaction(
                        eq(BigInteger.TEN),
                        eq(ONE),
                        anyString(),
                        anyString(),
                        any(BigInteger.class),
                        anyBoolean());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvalidTransactionReceipt() throws Throwable {
        prepareNonceRequest();
        prepareTransactionRequest();

        final EthGetTransactionReceipt ethGetTransactionReceipt = new EthGetTransactionReceipt();
        ethGetTransactionReceipt.setError(new Response.Error(1, "Invalid transaction receipt"));

        final Request<?, EthGetTransactionReceipt> getTransactionReceiptRequest =
                mock(Request.class);
        when(getTransactionReceiptRequest.sendAsync())
                .thenReturn(Async.run(() -> ethGetTransactionReceipt));
        when(web3j.ethGetTransactionReceipt(TRANSACTION_HASH))
                .thenReturn((Request) getTransactionReceiptRequest);
        assertThrows(RuntimeException.class, this::testErrorScenario);
    }

    @Test
    public void testExtractEventParametersWithLogGivenATransactionReceipt() {

        final java.util.function.Function<String, Event> eventFactory =
                name -> new Event(name, emptyList());

        final BiFunction<Integer, Event, Log> logFactory =
                (logIndex, event) ->
                        new Log(
                                false,
                                "" + logIndex,
                                "0",
                                "0x0",
                                "0x0",
                                "0",
                                "0x" + logIndex,
                                "",
                                "",
                                singletonList(EventEncoder.encode(event)));

        final Event testEvent1 = eventFactory.apply("TestEvent1");
        final Event testEvent2 = eventFactory.apply("TestEvent2");

        final List<Log> logs =
                Arrays.asList(logFactory.apply(0, testEvent1), logFactory.apply(1, testEvent2));

        final TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setLogs(logs);

        final List<Contract.EventValuesWithLog> eventValuesWithLogs1 =
                contract.extractEventParametersWithLog(testEvent1, transactionReceipt);

        assertEquals(eventValuesWithLogs1.size(), 1);
        assertEquals(eventValuesWithLogs1.get(0).getLog(), logs.get(0));

        final List<Contract.EventValuesWithLog> eventValuesWithLogs2 =
                contract.extractEventParametersWithLog(testEvent2, transactionReceipt);

        assertEquals(eventValuesWithLogs2.size(), 1);
        assertEquals(eventValuesWithLogs2.get(0).getLog(), logs.get(1));
    }

    void testErrorScenario() throws Throwable {
        contract.performTransaction(new Address(BigInteger.TEN), new Uint256(ONE)).send();
    }

    private TransactionReceipt createTransactionReceipt() {
        return createTransactionReceiptWithStatus(TXN_SUCCESS_STATUS);
    }

    private TransactionReceipt createFailedTransactionReceipt() {
        return createTransactionReceiptWithStatus(TXN_FAIL_STATUS);
    }

    private TransactionReceipt createTransactionReceiptWithStatus(final String status) {
        final TransactionReceipt transactionReceipt = new TransactionReceipt();
        transactionReceipt.setTransactionHash(TRANSACTION_HASH);
        transactionReceipt.setContractAddress(ADDRESS);
        transactionReceipt.setStatus(status);
        transactionReceipt.setGasUsed(TXN_GAS_USED);
        transactionReceipt.setBlockNumber(TXN_BLOCK_NUM);
        return transactionReceipt;
    }

    private Contract deployContract(final TransactionReceipt transactionReceipt) throws Exception {

        prepareTransaction(transactionReceipt);

        final String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Collections.singletonList(new Uint256(BigInteger.TEN)));
        final ContractGasProvider contractGasProvider = new DefaultGasProvider();
        return TestContract.deployRemoteCall(
                        TestContract.class,
                        web3j,
                        getVerifiedTransactionManager(SampleKeys.CREDENTIALS),
                        contractGasProvider,
                        TEST_CONTRACT_BINARY,
                        encodedConstructor,
                        ZERO)
                .send();
    }

    @SuppressWarnings("unchecked")
    private void prepareEthGetCode(final String binary) throws IOException {
        final EthGetCode ethGetCode = new EthGetCode();
        ethGetCode.setResult(Numeric.prependHexPrefix(binary));

        final Request<?, EthGetCode> ethGetCodeRequest = mock(Request.class);
        when(ethGetCodeRequest.send()).thenReturn(ethGetCode);
        when(web3j.ethGetCode(ADDRESS, DefaultBlockParameterName.LATEST))
                .thenReturn((Request) ethGetCodeRequest);
    }

    private static class TestContract extends Contract {
        public TestContract(
                final String contractAddress,
                final Web3j web3j,
                final Credentials credentials,
                final ContractGasProvider contractGasProvider,
                final TransactionReceipt transactionReceipt) {
            super(
                    TEST_CONTRACT_BINARY,
                    contractAddress,
                    web3j,
                    credentials,
                    contractGasProvider,
                    transactionReceipt);
        }

        public TestContract(
                final String contractAddress,
                final Web3j web3j,
                final TransactionManager transactionManager,
                final ContractGasProvider gasProvider,
                final TransactionReceipt transactionReceipt) {
            this(
                    TEST_CONTRACT_BINARY,
                    contractAddress,
                    web3j,
                    transactionManager,
                    gasProvider,
                    transactionReceipt);
        }

        public TestContract(
                final String binary,
                final String contractAddress,
                final Web3j web3j,
                final TransactionManager transactionManager,
                final ContractGasProvider gasProvider,
                final TransactionReceipt transactionReceipt) {
            super(
                    binary,
                    contractAddress,
                    web3j,
                    transactionManager,
                    gasProvider,
                    transactionReceipt);
        }

        public RemoteCall<Utf8String> callSingleValue() {
            final Function function =
                    new Function(
                            "call",
                            Collections.emptyList(),
                            Collections.singletonList(new TypeReference<Utf8String>() {}));

            return new RemoteCall1<>(
                    function, contractAddress, false, transactionManager, defaultBlockParameter);
        }

        public RemoteCall<List<Type<?>>> callMultipleValue() {
            final Function function =
                    new Function(
                            "call",
                            Collections.emptyList(),
                            Arrays.asList(
                                    new TypeReference<Uint256>() {},
                                    new TypeReference<Uint256>() {}));
            return new RemoteArrayCall<>(
                    function, contractAddress, false, transactionManager, defaultBlockParameter);
        }

        public RemoteTransactionCall<Void> performTransaction(
                final Address address, final Uint256 amount) {
            final Function function =
                    new Function(
                            "approve", Arrays.asList(address, amount), Collections.emptyList());

            return new RemoteTransactionCall0(
                    web3j,
                    function,
                    contractAddress,
                    true,
                    transactionManager,
                    defaultBlockParameter,
                    FunctionEncoder.encode(function),
                    ZERO,
                    false,
                    gasProvider);
        }

        public List<EventValues> processEvent(final TransactionReceipt transactionReceipt) {
            final Event event =
                    new Event(
                            "Event",
                            Arrays.asList(
                                    new TypeReference<Address>(true) {},
                                    new TypeReference<Uint256>() {}));
            return extractEventParameters(event, transactionReceipt);
        }
    }
}
