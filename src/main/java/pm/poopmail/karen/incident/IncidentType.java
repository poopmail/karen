package pm.poopmail.karen.incident;

/**
 * Simple enum for incident types
 *
 * @author Maximilian Dorn (Cerus)
 */
public enum IncidentType {

    ERROR(15158332),
    INFO(1752220),
    SUCCESS(3066993),
    DEBUG(10181046);

    private final int color;

    IncidentType(final int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

}
