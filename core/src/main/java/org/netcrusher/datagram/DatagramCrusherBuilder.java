package org.netcrusher.datagram;

import org.netcrusher.core.buffer.BufferOptions;
import org.netcrusher.core.filter.PassFilter;
import org.netcrusher.core.filter.PassFilterFactory;
import org.netcrusher.core.filter.TransformFilter;
import org.netcrusher.core.filter.TransformFilterFactory;
import org.netcrusher.core.reactor.NioReactor;
import org.netcrusher.core.throttle.Throttler;
import org.netcrusher.core.throttle.ThrottlerFactory;
import org.netcrusher.datagram.callback.DatagramClientCreation;
import org.netcrusher.datagram.callback.DatagramClientDeletion;

import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;

/**
 * Builder for DatagramCrusher instance
 */
public final class DatagramCrusherBuilder {

    public static final int DEFAULT_BUFFER_COUNT = 1024;

    public static final int DEFAULT_BUFFER_SIZE = 8192;

    private InetSocketAddress bindAddress;

    private InetSocketAddress connectAddress;

    private NioReactor reactor;

    private DatagramCrusherSocketOptions socketOptions;

    private DatagramClientCreation creationListener;

    private DatagramClientDeletion deletionListener;

    private boolean deferredListeners;

    private TransformFilterFactory incomingTransformFilterFactory;

    private TransformFilterFactory outgoingTransformFilterFactory;

    private PassFilterFactory incomingPassFilterFactory;

    private PassFilterFactory outgoingPassFilterFactory;

    private Throttler incomingThrottler;

    private ThrottlerFactory outgoingThrottlerFactory;

    private BufferOptions bufferOptions;

    private DatagramCrusherBuilder() {
        this.socketOptions = new DatagramCrusherSocketOptions();

        this.bufferOptions = new BufferOptions();
        this.bufferOptions.setCount(DEFAULT_BUFFER_COUNT);
        this.bufferOptions.setSize(DEFAULT_BUFFER_SIZE);
        this.bufferOptions.setDirect(true);

        this.deferredListeners = true;
    }

    /**
     * Create a new builder
     * @return Builder instance
     */
    public static DatagramCrusherBuilder builder() {
        return new DatagramCrusherBuilder();
    }

    /**
     * Set local address for proxy (where to bind a listening socket)
     * @param address Inet address
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withBindAddress(InetSocketAddress address) {
        this.bindAddress = address;
        return this;
    }

    /**
     * Set local address for proxy (where to bind a listening socket)
     * @param hostname Host name or interface address
     * @param port Port number
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withBindAddress(String hostname, int port) {
        this.bindAddress = new InetSocketAddress(hostname, port);
        return this;
    }

    /**
     * Set remote address for proxy (where to connect)
     * @param address Inet address
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withConnectAddress(InetSocketAddress address) {
        this.connectAddress = address;
        return this;
    }

    /**
     * Set remote address for proxy (where to connect)
     * @param hostname Remote host name or IP address of remote host
     * @param port Port number
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withConnectAddress(String hostname, int port) {
        this.connectAddress = new InetSocketAddress(hostname, port);
        return this;
    }

    /**
     * Set reactor instance for this proxy
     * @param reactor Reactor
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withReactor(NioReactor reactor) {
        this.reactor = reactor;
        return this;
    }

    /**
     * Set broadcast flag for both sockets
     * @param broadcast Broadcast flag
     * @see StandardSocketOptions#SO_BROADCAST
     * @see StandardProtocolFamily#INET
     * @see StandardProtocolFamily#INET6
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withBroadcast(boolean broadcast) {
        this.socketOptions.setBroadcast(broadcast);
        return this;
    }

    /**
     * Set protocol family for both sockets
     * @param protocolFamily Protocol family
     * @see StandardProtocolFamily
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withProtocolFamily(ProtocolFamily protocolFamily) {
        this.socketOptions.setProtocolFamily(protocolFamily);
        return this;
    }

    /**
     * Set socket buffer size for receiving, If not set the default size will be used.
     * @param bufferSize Size in bytes
     * @see StandardSocketOptions#SO_RCVBUF
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withRcvBufferSize(int bufferSize) {
        this.socketOptions.setRcvBufferSize(bufferSize);
        return this;
    }

    /**
     * Set socket buffer size for sending, If not set the default size will be used.
     * @param bufferSize Size in bytes
     * @see StandardSocketOptions#SO_SNDBUF
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withSndBufferSize(int bufferSize) {
        this.socketOptions.setSndBufferSize(bufferSize);
        return this;
    }

    /**
     * Set how many buffer instances will be in queue between two sockets in a proxy pair
     * @param bufferCount Count of buffer
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withBufferCount(int bufferCount) {
        this.bufferOptions.setCount(bufferCount);
        return this;
    }

    /**
     * Set the size of each buffer in queue between two sockets in a proxy pair
     * @param bufferSize Size of buffer in bytes. Should not be less than the maximum size of datagram
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withBufferSize(int bufferSize) {
        this.bufferOptions.setSize(bufferSize);
        return this;
    }

    /**
     * Set buffer allocation method
     * @param direct Set true if ByteBuffer should be allocated as direct
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withBufferDirect(boolean direct) {
        this.bufferOptions.setDirect(direct);
        return this;
    }

    /**
     * Set outgoing (from the inner to the outer) transform filter factory
     * @param filterFactory Filter factory
     * @return This builder instance to chain with other methods
     * @see TransformFilter
     */
    public DatagramCrusherBuilder withOutgoingTransformFilterFactory(TransformFilterFactory filterFactory) {
        this.outgoingTransformFilterFactory = filterFactory;
        return this;
    }

    /**
     * Set incoming (from the outer to the inner) transform filter factory
     * @param filterFactory Filter factory
     * @return This builder instance to chain with other methods
     * @see TransformFilter
     */
    public DatagramCrusherBuilder withIncomingTransformFilterFactory(TransformFilterFactory filterFactory) {
        this.incomingTransformFilterFactory = filterFactory;
        return this;
    }

    /**
     * Set outgoing (from the inner to the outer) pass filter factory
     * @param filterFactory Filter factory
     * @return This builder instance to chain with other methods
     * @see PassFilter
     */
    public DatagramCrusherBuilder withOutgoingPassFilterFactory(PassFilterFactory filterFactory) {
        this.outgoingPassFilterFactory = filterFactory;
        return this;
    }

    /**
     * Set incoming (from the outer to the inner) pass filter factory
     * @param filterFactory Filter factory
     * @return This builder instance to chain with other methods
     * @see PassFilter
     */
    public DatagramCrusherBuilder withIncomingPassFilterFactory(PassFilterFactory filterFactory) {
        this.incomingPassFilterFactory = filterFactory;
        return this;
    }

    /**
     * Set outgoing (from the inner to the outer) throttling factory
     * @param throttlerFactory Throttler factory
     * @return This builder instance to chain with other methods
     * @see Throttler
     */
    public DatagramCrusherBuilder withOutgoingThrottlerFactory(ThrottlerFactory throttlerFactory) {
        this.outgoingThrottlerFactory = throttlerFactory;
        return this;
    }

    /**
     * Set incoming (from the outer to the inner) throttler. For datagram crusher incoming throttler works on
     * the total incoming packet stream not the individual channel streams.
     * @param throttler Throttler strategy
     * @return This builder instance to chain with other methods
     * @see Throttler
     */
    public DatagramCrusherBuilder withIncomingThrottler(Throttler throttler) {
        this.incomingThrottler = throttler;
        return this;
    }

    /**
     * Set a listener for a new proxy connection
     * @param creationListener Listener implementation
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withCreationListener(DatagramClientCreation creationListener) {
        this.creationListener = creationListener;
        return this;
    }

    /**
     * Set a listener for a proxy connection to be deleted
     * @param deletionListener Listener implementation
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withDeletionListener(DatagramClientDeletion deletionListener) {
        this.deletionListener = deletionListener;
        return this;
    }

    /**
     * Set listeners call method
     * @param deferredListeners Set true if listeners should be called from separate thread
     * @return This builder instance to chain with other methods
     */
    public DatagramCrusherBuilder withDeferredListeners(boolean deferredListeners) {
        this.deferredListeners = deferredListeners;
        return this;
    }

    /**
     * Builds a new DatagramCrusher instance
     * @return DatagramCrusher instance
     */
    public DatagramCrusher build() {
        if (bindAddress == null) {
            throw new IllegalArgumentException("Bind address is not set");
        }

        if (connectAddress == null) {
            throw new IllegalArgumentException("Connect address is not set");
        }

        if (reactor == null) {
            throw new IllegalArgumentException("Reactor is not set");
        }

        DatagramFilters filters = new DatagramFilters(
            incomingTransformFilterFactory,
            outgoingTransformFilterFactory,
            incomingPassFilterFactory,
            outgoingPassFilterFactory,
            incomingThrottler,
            outgoingThrottlerFactory
        );

        return new DatagramCrusher(
            reactor,
            bindAddress,
            connectAddress,
            socketOptions.copy(),
            filters,
            creationListener,
            deletionListener,
            deferredListeners,
            bufferOptions.copy()
        );
    }

    /**
     * Builds a new DatagramCrusher instance and opens it for incoming packets
     * @return DatagramCrusher instance
     */
    public DatagramCrusher buildAndOpen() {
        DatagramCrusher crusher = build();
        crusher.open();
        return crusher;
    }

}
