package pm.poopmail.karen.receiver;

import com.github.jezza.TomlTable;
import pm.poopmail.karen.exception.ConfigurationException;
import pm.poopmail.karen.incident.Incident;

/**
 * Superclass for incident receivers
 *
 * @author Maximilian Dorn (Cerus)
 */
public interface Receiver {

    /**
     * Reports the incident to this receiver
     *
     * @param incident The incident
     *
     * @throws Exception If something goes wrong
     */
    void report(Incident incident) throws Exception;

    /**
     * Replaces the placeholders in the provided string
     *
     * @param str      The string
     * @param incident The incident
     *
     * @return The new string
     */
    default String replacePlaceholders(final String str, final Incident incident) {
        return str.replace("{SERVICE_NAME}", incident.getServiceName())
                .replace("{TOPIC}", incident.getTopic())
                .replace("{DESCRIPTION}", incident.getDescription());
    }

    /**
     * Attempts to load this receiver from a configuration
     *
     * @param tomlTable The toml table
     *
     * @throws ConfigurationException If this receiver is wrongly configured
     */
    void loadFromConfig(TomlTable tomlTable) throws ConfigurationException;

    /**
     * Whether this receiver is able to handle incidents or not
     *
     * @return the above
     */
    boolean isOperational();

}
