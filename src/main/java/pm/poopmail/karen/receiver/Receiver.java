package pm.poopmail.karen.receiver;

import com.github.jezza.TomlTable;
import pm.poopmail.karen.exception.ConfigurationException;
import pm.poopmail.karen.incident.Incident;
import pm.poopmail.karen.incident.IncidentType;

/**
 * Superclass for incident receivers
 *
 * @author Maximilian Dorn (Cerus)
 */
public abstract class Receiver {

    protected int lowestPrio = -1;
    protected int highestPrio = -1;

    /**
     * Reports the incident to this receiver
     *
     * @param incident The incident
     *
     * @throws Exception If something goes wrong
     */
    public abstract void report(Incident incident) throws Exception;

    /**
     * Replaces the placeholders in the provided string
     *
     * @param str      The string
     * @param incident The incident
     *
     * @return The new string
     */
    protected String replacePlaceholders(final String str, final Incident incident) {
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
    public void loadFromConfig(final TomlTable tomlTable) throws ConfigurationException {
        this.loadTrigger(tomlTable);
    }

    protected void loadTrigger(final TomlTable tomlTable) {
        final String prioRange = (String) tomlTable.getOrDefault("trigger", "0-5");
        final String[] split = prioRange.split("-");
        if (split.length == 2) {
            try {
                this.lowestPrio = Integer.parseInt(split[0]);
                this.highestPrio = Integer.parseInt(split[1]);
            } catch (final NumberFormatException ignored) {
                this.lowestPrio = 0;
                this.highestPrio = 5;
            }
        }
    }

    public boolean willTrigger(final IncidentType type) {
        return type.getPriority() >= this.lowestPrio && type.getPriority() <= this.highestPrio;
    }

    /**
     * Whether this receiver is able to handle incidents or not
     *
     * @return the above
     */
    public abstract boolean isOperational();

}
