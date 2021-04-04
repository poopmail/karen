package pm.poopmail.karen.exception;

/**
 * Thrown if something is wrongly configured
 *
 * @author Maximilian Dorn (Cerus)
 */
public class ConfigurationException extends Exception {

    public ConfigurationException() {
    }

    public ConfigurationException(final String message) {
        super(message);
    }

}
