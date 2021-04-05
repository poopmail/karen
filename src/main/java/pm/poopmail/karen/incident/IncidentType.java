package pm.poopmail.karen.incident;

/**
 * Simple enum for incident types
 *
 * @author Maximilian Dorn (Cerus)
 */
public enum IncidentType {

    PANIC(5, 0XFF0000),
    ERROR(4, 15158332),
    WARNING(3, 16776960),
    INFO(2, 3447003),
    SUCCESS(1, 3066993),
    DEBUG(0, 10181046);

    private final int priority;
    private final int color;

    IncidentType(final int priority, final int color) {
        this.priority = priority;
        this.color = color;
    }

    public int getPriority() {
        return this.priority;
    }

    public int getColor() {
        return this.color;
    }

}
