package org.web3j.protocol.core;

import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import rx.Observable;
import rx.Subscription;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.web3j.protocol.core.TestParameters.isInfuraTestRinkebyUrl;

/**
 * Observable callback tests.
 */
public class ObservableIT {

    @Rule
    public Timeout globalTimeout = new Timeout(10 * 60_000);

    private static final int EVENT_COUNT = 1;
    private static final int TIMEOUT_MINUTES = 5;

    private Web3j web3j;

    @Before
    public void setUp() {
        HttpService httpService = new HttpService(TestParameters.TEST_RINKEBY_URL);
        if (TestParameters.hasRinkebyCredentials()) {
            httpService.addHeader("Authorization", TestParameters.getRinkebyAuthorization());
        }
        this.web3j = Web3j.build(httpService);
    }

    @Test
    public void testBlockObservable() throws Throwable {
        assumeFalse("Infura does NOT support eth_newBlockFilter - "
                        + "https://github.com/INFURA/infura/blob/master/docs/source/index.html.md"
                        + "#supported-json-rpc-methods",
                isInfuraTestRinkebyUrl());

        run(web3j.blockObservable(false));
    }

    @Test
    public void testPendingTransactionObservable() throws Throwable {
        assumeFalse("Infura does NOT support eth_newPendingTransactionFilter - "
                        + "https://github.com/INFURA/infura/blob/master/docs/source/index.html.md"
                        + "#supported-json-rpc-methods",
                isInfuraTestRinkebyUrl());

        run(web3j.pendingTransactionObservable());
    }

    @Test
    public void testTransactionObservable() throws Throwable {
        assumeFalse("Infura does NOT support eth_newBlockFilter - "
                        + "https://github.com/INFURA/infura/blob/master/docs/source/index.html.md"
                        + "#supported-json-rpc-methods",
                isInfuraTestRinkebyUrl());

        run(web3j.transactionObservable());
    }

    @Test
    public void testLogObservable() throws Throwable {
        assumeFalse("Infura does NOT support eth_newFilter - "
                        + "https://github.com/INFURA/infura/blob/master/docs/source/index.html.md"
                        + "#supported-json-rpc-methods",
                isInfuraTestRinkebyUrl());

        run(web3j.ethLogObservable(new EthFilter()));
    }

    @Test
    public void testReplayObservable() throws Throwable {
        run(web3j.replayBlocksObservable(
                new DefaultBlockParameterNumber(0),
                new DefaultBlockParameterNumber(EVENT_COUNT), true));
    }

    @Test
    public void testCatchUpToLatestAndSubscribeToNewBlocksObservable() throws Throwable {
        assumeFalse("Infura does NOT support eth_newBlockFilter - "
                        + "https://github.com/INFURA/infura/blob/master/docs/source/index.html.md"
                        + "#supported-json-rpc-methods",
                isInfuraTestRinkebyUrl());

        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                .send();
        BigInteger latestBlockNumber = ethBlock.getBlock().getNumber();
        run(web3j.catchUpToLatestAndSubscribeToNewBlocksObservable(
                new DefaultBlockParameterNumber(latestBlockNumber.subtract(BigInteger.ONE)),
                false));
    }

    private <T> void run(Observable<T> observable) throws Throwable {
        CountDownLatch countDownLatch = new CountDownLatch(EVENT_COUNT);
        CountDownLatch completedLatch = new CountDownLatch(EVENT_COUNT);

        AtomicReference<Throwable> exceptionThrown = new AtomicReference<>();

        Subscription subscription = observable.subscribe(
                x -> countDownLatch.countDown(),
                e -> {
                    exceptionThrown.set(e);
                    countDownLatch.countDown();
                },
                completedLatch::countDown
        );

        countDownLatch.await(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        subscription.unsubscribe();

        if (exceptionThrown.get() != null) {
            throw exceptionThrown.get();
        }

        completedLatch.await(1, TimeUnit.SECONDS);
        assertTrue(subscription.isUnsubscribed());
    }
}
