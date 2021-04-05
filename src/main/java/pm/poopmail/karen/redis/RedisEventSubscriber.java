package pm.poopmail.karen.redis;

import io.lettuce.core.RedisChannelHandler;
import io.lettuce.core.RedisConnectionStateListener;
import io.lettuce.core.event.Event;
import io.lettuce.core.event.connection.ReconnectFailedEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import pm.poopmail.karen.incident.Incident;
import pm.poopmail.karen.incident.IncidentType;
import pm.poopmail.karen.incident.IncomingIncidentProcessor;

/**
 * Simple subscriber for Redis events
 */
public class RedisEventSubscriber implements Consumer<Event>, RedisConnectionStateListener {

    private final IncomingIncidentProcessor processor;
    private long lastIncident = 0;

    public RedisEventSubscriber(final IncomingIncidentProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void accept(final Event event) {
        if (event instanceof ReconnectFailedEvent) {
            if (System.currentTimeMillis() - this.lastIncident < TimeUnit.MINUTES.toMillis(5)) {
                return;
            }

            this.lastIncident = System.currentTimeMillis();
            final ReconnectFailedEvent reconnectFailedEvent = (ReconnectFailedEvent) event;
            this.processor.process(new Incident(
                    "karen",
                    IncidentType.ERROR,
                    "Failed to reconnect to Redis (" + reconnectFailedEvent.getAttempt() + " attempts)",
                    this.getStacktrace(reconnectFailedEvent.getCause())
            ));
        }
    }

    @Override
    public void onRedisExceptionCaught(final RedisChannelHandler<?, ?> connection, final Throwable cause) {
        if (System.currentTimeMillis() - this.lastIncident < TimeUnit.MINUTES.toMillis(5)) {
            return;
        }

        this.lastIncident = System.currentTimeMillis();
        this.processor.process(new Incident(
                "karen",
                IncidentType.ERROR,
                "Redis caught exception",
                this.getStacktrace(cause)
        ));
    }

    @Override
    public void onRedisConnected(final RedisChannelHandler<?, ?> connection, final SocketAddress socketAddress) {
    }

    @Override
    public void onRedisDisconnected(final RedisChannelHandler<?, ?> connection) {
    }

    private String getStacktrace(final Throwable t) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(outputStream);
        t.printStackTrace(printStream);
        printStream.close();
        return outputStream.toString();
    }

}
