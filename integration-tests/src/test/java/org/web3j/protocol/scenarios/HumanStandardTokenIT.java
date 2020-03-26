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
package org.web3j.protocol.scenarios;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.web3j.EVMTest;
import org.web3j.NodeType;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration test demonstrating integration with <a
 * href="https://github.com/ethereum/EIPs/issues/20">EIP-20</a>. Solidity implementation is taken
 * from <a href="https://github.com/ConsenSys/Tokens">ConsenSys Tokens</a>.
 */
@EVMTest(type = NodeType.BESU)
public class HumanStandardTokenIT extends Scenario {

    @BeforeAll
    public static void setUp(Web3j web3j) {
        Scenario.web3j = web3j;
    }

    @Test
    public void testContract() throws Exception {

        // deploy contract
        BigInteger aliceQty = BigInteger.valueOf(1_000_000);
        String contractAddress = createContract(aliceQty);

        assertEquals(getTotalSupply(contractAddress), aliceQty);

        confirmBalance(ALICE.getAddress(), contractAddress, aliceQty);

        // transfer tokens
        BigInteger transferQuantity = BigInteger.valueOf(100_000);

        sendTransferTokensTransaction(BOB.getAddress(), contractAddress, transferQuantity);

        aliceQty = aliceQty.subtract(transferQuantity);

        BigInteger bobQty = BigInteger.ZERO;
        bobQty = bobQty.add(transferQuantity);

        confirmBalance(ALICE.getAddress(), contractAddress, aliceQty);
        confirmBalance(BOB.getAddress(), contractAddress, bobQty);

        // set an allowance
        confirmAllowance(ALICE.getAddress(), BOB.getAddress(), contractAddress, BigInteger.ZERO);

        transferQuantity = BigInteger.valueOf(50);
        sendApproveTransaction(BOB.getAddress(), transferQuantity, contractAddress);

        confirmAllowance(ALICE.getAddress(), BOB.getAddress(), contractAddress, transferQuantity);

        // perform a transfer
        transferQuantity = BigInteger.valueOf(25);

        sendTransferFromTransaction(
                BOB, ALICE.getAddress(), BOB.getAddress(), transferQuantity, contractAddress);

        aliceQty = aliceQty.subtract(transferQuantity);
        bobQty = bobQty.add(transferQuantity);

        confirmBalance(ALICE.getAddress(), contractAddress, aliceQty);
        confirmBalance(BOB.getAddress(), contractAddress, bobQty);
    }

    private BigInteger getTotalSupply(final String contractAddress) throws Exception {
        final Function function = totalSupply();
        final String responseValue = callSmartContractFunction(function, contractAddress);

        final List<Type<?>> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());

        assertEquals(1, response.size());
        return (BigInteger) response.get(0).getValue();
    }

    private void confirmBalance(
            final String address, final String contractAddress, final BigInteger expected)
            throws Exception {
        final Function function = balanceOf(address);
        final String responseValue = callSmartContractFunction(function, contractAddress);

        final List<Type<?>> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());
        assertEquals(response.size(), (1));
        assertEquals(response.get(0), (new Uint256(expected)));
    }

    private void confirmAllowance(
            final String owner,
            final String spender,
            final String contractAddress,
            final BigInteger expected)
            throws Exception {
        final Function function = allowance(owner, spender);
        final String responseValue = callSmartContractFunction(function, contractAddress);

        final List<Type<?>> response =
                FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());

        assertEquals(response.size(), (function.getOutputParameters().size()));
        assertEquals(response.get(0), (new Uint256(expected)));
    }

    private String createContract(BigInteger initialSupply) throws Exception {
        String createTransactionHash = sendCreateContractTransaction(initialSupply);
        assertFalse(createTransactionHash.isEmpty());

        final TransactionReceipt createTransactionReceipt =
                waitForTransactionReceipt(createTransactionHash);

        assertEquals(createTransactionReceipt.getTransactionHash(), (createTransactionHash));

        assertFalse(createTransactionReceipt.getGasUsed().equals(GAS_LIMIT));

        final String contractAddress = createTransactionReceipt.getContractAddress();

        assertNotNull(contractAddress);
        return contractAddress;
    }

    private String sendCreateContractTransaction(BigInteger initialSupply) throws Exception {
        BigInteger nonce = getNonce(ALICE.getAddress());

        final String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.asList(
                                new Uint256(initialSupply),
                                new Utf8String("web3j tokens"),
                                new Uint8(BigInteger.TEN),
                                new Utf8String("w3j$")));

        final RawTransaction rawTransaction =
                RawTransaction.createContractTransaction(
                        nonce,
                        GAS_PRICE,
                        GAS_LIMIT,
                        BigInteger.ZERO,
                        getHumanStandardTokenBinary() + encodedConstructor);

        final byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, ALICE);
        final String hexValue = Numeric.toHexString(signedMessage);

        final EthSendTransaction transactionResponse =
                web3j.ethSendRawTransaction(hexValue).sendAsync().get();

        return transactionResponse.getTransactionHash();
    }

    private void sendTransferTokensTransaction(String to, String contractAddress, BigInteger qty)
            throws Exception {

        final Function function = transfer(to, qty);
        final String functionHash = execute(Scenario.ALICE, function, contractAddress);

        final TransactionReceipt transferTransactionReceipt =
                waitForTransactionReceipt(functionHash);
        assertEquals(transferTransactionReceipt.getTransactionHash(), (functionHash));

        final List<Log> logs = transferTransactionReceipt.getLogs();
        assertFalse(logs.isEmpty());
        final Log log = logs.get(0);

        // verify the event was called with the function parameters
        final List<String> topics = log.getTopics();
        assertEquals(topics.size(), (3));

        final Event transferEvent = transferEvent();

        // check function signature - we only have a single topic our event signature,
        // there are no indexed parameters in this example
        final String encodedEventSignature = EventEncoder.encode(transferEvent);
        assertEquals(topics.get(0), (encodedEventSignature));
        assertEquals(new Address(topics.get(1)), (new Address(Scenario.ALICE.getAddress())));
        assertEquals(new Address(topics.get(2)), (new Address(to)));

        // verify qty transferred
        final List<Type<?>> results =
                FunctionReturnDecoder.decode(
                        log.getData(), transferEvent.getNonIndexedParameters());
        assertEquals(results, (Collections.singletonList(new Uint256(qty))));
    }

    private void sendApproveTransaction(String spender, BigInteger value, String contractAddress)
            throws Exception {
        final Function function = approve(spender, value);
        final String functionHash = execute(Scenario.ALICE, function, contractAddress);

        final TransactionReceipt transferTransactionReceipt =
                waitForTransactionReceipt(functionHash);
        assertEquals(transferTransactionReceipt.getTransactionHash(), (functionHash));

        final List<Log> logs = transferTransactionReceipt.getLogs();
        assertFalse(logs.isEmpty());
        final Log log = logs.get(0);

        // verify the event was called with the function parameters
        final List<String> topics = log.getTopics();
        assertEquals(topics.size(), (3));

        // event Transfer(address indexed _from, address indexed _to, uint256 _value);
        final Event event = approvalEvent();

        // check function signature - we only have a single topic our event signature,
        // there are no indexed parameters in this example
        final String encodedEventSignature = EventEncoder.encode(event);
        assertEquals(topics.get(0), (encodedEventSignature));
        assertEquals(new Address(topics.get(1)), (new Address(Scenario.ALICE.getAddress())));
        assertEquals(new Address(topics.get(2)), (new Address(spender)));

        // verify our two event parameters
        final List<Type<?>> results =
                FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());
        assertEquals(results, (Collections.singletonList(new Uint256(value))));
    }

    public void sendTransferFromTransaction(
            final Credentials credentials,
            final String from,
            final String to,
            final BigInteger value,
            final String contractAddress)
            throws Exception {

        final Function function = transferFrom(from, to, value);
        final String functionHash = execute(credentials, function, contractAddress);

        final TransactionReceipt transferTransactionReceipt =
                waitForTransactionReceipt(functionHash);
        assertEquals(transferTransactionReceipt.getTransactionHash(), (functionHash));

        final List<Log> logs = transferTransactionReceipt.getLogs();
        assertFalse(logs.isEmpty());
        final Log log = logs.get(0);

        final Event transferEvent = transferEvent();
        final List<String> topics = log.getTopics();

        // check function signature - we only have a single topic our event signature,
        // there are no indexed parameters in this example
        final String encodedEventSignature = EventEncoder.encode(transferEvent);
        assertEquals(topics.get(0), (encodedEventSignature));
        assertEquals(new Address(topics.get(1)), (new Address(from)));
        assertEquals(new Address(topics.get(2)), (new Address(to)));

        // verify qty transferred
        final List<Type<?>> results =
                FunctionReturnDecoder.decode(
                        log.getData(), transferEvent.getNonIndexedParameters());
        assertEquals(results, (Collections.singletonList(new Uint256(value))));
    }

    private String execute(
            final Credentials credentials, final Function function, final String contractAddress)
            throws Exception {
        final BigInteger nonce = getNonce(credentials.getAddress());

        final String encodedFunction = FunctionEncoder.encode(function);

        final RawTransaction rawTransaction =
                RawTransaction.createTransaction(
                        nonce, GAS_PRICE, GAS_LIMIT, contractAddress, encodedFunction);

        final byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        final String hexValue = Numeric.toHexString(signedMessage);

        final EthSendTransaction transactionResponse =
                web3j.ethSendRawTransaction(hexValue).sendAsync().get();

        return transactionResponse.getTransactionHash();
    }

    private String callSmartContractFunction(final Function function, final String contractAddress)
            throws Exception {
        final String encodedFunction = FunctionEncoder.encode(function);

        final org.web3j.protocol.core.methods.response.EthCall response =
                web3j.ethCall(
                                Transaction.createEthCallTransaction(
                                        ALICE.getAddress(), contractAddress, encodedFunction),
                                DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();

        return response.getValue();
    }

    private Function totalSupply() {
        return new Function(
                "totalSupply",
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {}));
    }

    private Function balanceOf(final String owner) {
        return new Function(
                "balanceOf",
                Collections.singletonList(new Address(owner)),
                Collections.singletonList(new TypeReference<Uint256>() {}));
    }

    private Function transfer(final String to, final BigInteger value) {
        return new Function(
                "transfer",
                Arrays.asList(new Address(to), new Uint256(value)),
                Collections.singletonList(new TypeReference<Bool>() {}));
    }

    private Function allowance(final String owner, final String spender) {
        return new Function(
                "allowance",
                Arrays.asList(new Address(owner), new Address(spender)),
                Collections.singletonList(new TypeReference<Uint256>() {}));
    }

    private Function approve(final String spender, final BigInteger value) {
        return new Function(
                "approve",
                Arrays.asList(new Address(spender), new Uint256(value)),
                Collections.singletonList(new TypeReference<Bool>() {}));
    }

    private Function transferFrom(final String from, final String to, final BigInteger value) {
        return new Function(
                "transferFrom",
                Arrays.asList(new Address(from), new Address(to), new Uint256(value)),
                Collections.singletonList(new TypeReference<Bool>() {}));
    }

    private Event transferEvent() {
        return new Event(
                "Transfer",
                Arrays.asList(
                        new TypeReference<Address>(true) {},
                        new TypeReference<Address>(true) {},
                        new TypeReference<Uint256>() {}));
    }

    private Event approvalEvent() {
        return new Event(
                "Approval",
                Arrays.asList(
                        new TypeReference<Address>(true) {},
                        new TypeReference<Address>(true) {},
                        new TypeReference<Uint256>() {}));
    }

    private static String getHumanStandardTokenBinary() throws Exception {
        return load("/EIP20.bin");
    }
}
