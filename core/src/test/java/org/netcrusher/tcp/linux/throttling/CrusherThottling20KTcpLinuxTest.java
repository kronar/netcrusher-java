package org.netcrusher.tcp.linux.throttling;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.netcrusher.core.reactor.NioReactor;
import org.netcrusher.core.throttle.rate.ByteRateThrottler;
import org.netcrusher.tcp.TcpCrusher;
import org.netcrusher.tcp.TcpCrusherBuilder;
import org.netcrusher.tcp.linux.AbstractTcpLinuxTest;
import org.netcrusher.test.process.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CrusherThottling20KTcpLinuxTest extends AbstractTcpLinuxTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrusherThottling20KTcpLinuxTest.class);

    private NioReactor reactor;

    private TcpCrusher crusher;

    @Before
    public void setUp() throws Exception {
        reactor = new NioReactor(10);

        crusher = TcpCrusherBuilder.builder()
            .withReactor(reactor)
            .withBindAddress(ADDR_LOOPBACK4, PORT_DIRECT)
            .withConnectAddress(ADDR_LOOPBACK4, PORT_PROXY)
            .withBufferSize(1_000)
            .withBufferCount(32)
            .withRcvBufferSize(20_000)
            .withSndBufferSize(20_000)
            .withOutgoingThrottlerFactory((addr) -> new ByteRateThrottler(20_000, 1, TimeUnit.SECONDS))
            .withCreationListener((addr) -> LOGGER.info("Client is created <{}>", addr))
            .withDeletionListener((addr, byteMeters) -> LOGGER.info("Client is deleted <{}>", addr))
            .buildAndOpen();
    }

    @After
    public void tearDown() throws Exception {
        if (crusher != null) {
            crusher.close();
            Assert.assertFalse(crusher.isOpen());
        }

        if (reactor != null) {
            reactor.close();
            Assert.assertFalse(reactor.isOpen());
        }
    }

    @Test
    public void direct() throws Exception {
        ProcessResult result = direct(SOCAT4_PRODUCER, SOCAT4_CONSUMER_PROXIED, 100_000, FULL_THROUGHPUT);
    }

}
