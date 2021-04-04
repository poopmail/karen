package pm.poopmail.karen.incident;

/**
 * Simple enum for incident types
 *
 * @author Maximilian Dorn (Cerus)
 */
public enum IncidentType {

    PANIC(0XFF0000),
    ERROR(15158332),
    WARNING(16776960),
    INFO(3447003),
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
