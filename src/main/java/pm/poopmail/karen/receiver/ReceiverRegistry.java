package pm.poopmail.karen.receiver;

import com.github.jezza.TomlTable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import pm.poopmail.karen.exception.ConfigurationException;

/**
 * Simple registry for available receivers
 *
 * @author Maximilian Dorn (Cerus)
 */
public class ReceiverRegistry {

    private static final Map<String, Supplier<Receiver>> receiverMap = new HashMap<>() {
        {
            this.put("http", HttpReceiver::new);
            this.put("discord", DiscordReceiver::new);
            this.put("telegram", TelegramReceiver::new);
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

        // Retrieve receiver
        final Receiver receiver = receiverMap.get(type).get();
        receiver.loadFromConfig(tomlTable);
        return receiver;
    }

}
