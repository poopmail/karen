package pm.poopmail.karen.config;

import com.github.jezza.Toml;
import com.github.jezza.TomlTable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import pm.poopmail.karen.exception.ConfigurationException;
import pm.poopmail.karen.receiver.Receiver;
import pm.poopmail.karen.receiver.ReceiverRegistry;

/**
 * Helper class for the report configuration
 *
 * @author Maximilian Dorn (Cerus)
 */
public class Config {

    public List<Receiver> configuredReceivers;

    public Config() {
    }

    /**
     * Attempts to load the configuration
     *
     * @param filePath The path to the config file
     *
     * @throws ConfigurationException If something is wrongly configured
     * @throws IOException            If something goes wrong with i/o
     */
    public void load(final String filePath) throws ConfigurationException, IOException {
        final File file = new File(filePath);
        if (file.getParentFile() != null) {
            // Create parent directories
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            // Create file
            file.createNewFile();
            // Copy defaults
            try (final FileOutputStream outputStream = new FileOutputStream(file);
                 final InputStream inputStream = this.getClass().getResourceAsStream("/config.toml")) {
                int i;
                while ((i = inputStream.read()) != -1) {
                    outputStream.write(i);
                }
                outputStream.flush();
            }
        }

        // Actually load config
        this.load(Toml.from(new FileInputStream(file)));
    }

    /**
     * Attempts to read the configuration
     *
     * @param tomlTable The toml table
     *
     * @throws ConfigurationException If something is wrongly configured
     */
    public void load(final TomlTable tomlTable) throws ConfigurationException {
        // Init fields
        this.configuredReceivers = new ArrayList<>();

        // Get report table
        final TomlTable reportsTable = (TomlTable) tomlTable.getOrDefault("reports", new TomlTable());
        // Loop report table
        for (final String key : reportsTable.keySet()) {
            // Load receiver
            final TomlTable section = (TomlTable) reportsTable.get(key);
            final Receiver receiver = ReceiverRegistry.loadReceiver(section);
            this.configuredReceivers.add(receiver);
        }
    }

}
