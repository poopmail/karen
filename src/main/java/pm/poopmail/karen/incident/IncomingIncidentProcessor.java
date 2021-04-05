package pm.poopmail.karen.incident;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pm.poopmail.karen.config.Config;

/**
 * Processor for raw incidents
 *
 * @author Maximilian Dorn (Cerus)
 */
public class IncomingIncidentProcessor {

    private final Gson gson = new Gson();
    private final Config config;

    public IncomingIncidentProcessor(final Config config) {
        this.config = config;
    }

    /**
     * Processes a raw incident
     *
     * @param jsonObject The raw incident
     */
    public void process(final JsonObject jsonObject) {
        // Parse to incident object
        final Incident incident = this.gson.fromJson(jsonObject, Incident.class);

        // Loop through configured receivers
        this.config.configuredReceivers.forEach(receiver -> {
            if (receiver.isOperational() && receiver.willTrigger(incident.getType())) {
                // Report the incident
                try {
                    receiver.report(incident);
                } catch (final Exception e) {
                    e.printStackTrace();
                    System.err.println("Failed to report incident to receiver " + receiver.getClass().getName());
                }
            }
        });
    }

}
