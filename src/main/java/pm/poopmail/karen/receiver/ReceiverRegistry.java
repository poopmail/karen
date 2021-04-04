package pm.poopmail.karen.receiver;

import com.github.jezza.TomlTable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import pm.poopmail.karen.exception.ConfigurationException;

/**
 * Simple registry for available receivers
 *
 * @author Maximilian Dorn (Cerus)
 */
public class ReceiverRegistry {

    private static final Map<String, Class<? extends Receiver>> receiverMap = new HashMap<>() {
        {
            this.put("http", HttpReceiver.class);
            this.put("discord", DiscordReceiver.class);
        }
    };

    private ReceiverRegistry() {
    }

    /**
     * Attempts to load a receiver from a toml table
     *
     * @param tomlTable The toml table
     *
     * @return The loaded receiver
     *
     * @throws ConfigurationException If the receiver is wrongly configured
     */
    public static Receiver loadReceiver(final TomlTable tomlTable) throws ConfigurationException {
        final String type = (String) tomlTable.getOrDefault("type", null);
        if (type == null) {
            throw new ConfigurationException("Missing required field: type");
        }

        if (!receiverMap.containsKey(type)) {
            throw new ConfigurationException("Unknown receiver type: " + type);
        }

        final Receiver receiver;
        try {
            final Class<? extends Receiver> receiverClass = receiverMap.get(type);
            final Constructor<? extends Receiver> constructor = receiverClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            receiver = constructor.newInstance();
        } catch (final InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        receiver.loadFromConfig(tomlTable);
        return receiver;
    }

}
