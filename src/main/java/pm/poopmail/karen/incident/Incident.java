package pm.poopmail.karen.incident;

import com.google.gson.annotations.SerializedName;

/**
 * Simple incident model
 *
 * @author Maximilian Dorn (Cerus)
 */
public class Incident {

    @SerializedName("service")
    private final String serviceName;
    @SerializedName("type")
    private final IncidentType type;
    @SerializedName("topic")
    private final String topic;
    @SerializedName("description")
    private final String description;

    public Incident(final String serviceName, final IncidentType type, final String topic, final String description) {
        this.serviceName = serviceName;
        this.type = type;
        this.topic = topic;
        this.description = description;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public IncidentType getType() {
        return this.type;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getDescription() {
        return this.description;
    }

}
