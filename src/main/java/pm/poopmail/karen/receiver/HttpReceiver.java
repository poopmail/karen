package pm.poopmail.karen.receiver;

import com.github.jezza.TomlTable;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import pm.poopmail.karen.exception.ConfigurationException;
import pm.poopmail.karen.incident.Incident;

/**
 * Makes http requests based on incidents
 *
 * @author Maximilian Dorn (Cerus)
 */
public class HttpReceiver implements Receiver {

    protected String urlStr;
    protected String method;
    protected Map<String, String> headerMap;
    protected String rawPayload;

    public HttpReceiver() {
    }

    @Override
    public void report(final Incident incident) throws Exception {
        // Create url and open connection
        final URL url = new URL(this.urlStr);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure connection
        connection.setRequestMethod(this.method);
        this.headerMap.forEach(connection::setRequestProperty);

        if (this.rawPayload != null) {
            // Send payload
            connection.setDoOutput(true);
            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(this.replacePlaceholders(this.rawPayload, incident).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }

        // Wont work without this for some reason
        connection.getResponseCode();
    }

    @Override
    public void loadFromConfig(final TomlTable tomlTable) throws ConfigurationException {
        this.urlStr = (String) tomlTable.get("url");
        this.method = (String) tomlTable.get("method");
        this.headerMap = ((TomlTable) tomlTable.getOrDefault("header", new TomlTable()))
                .asMap().entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), (String) entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.rawPayload = (String) tomlTable.getOrDefault("payload", null);

        // Check if the configuration was valid
        if (this.urlStr == null || this.method == null) {
            throw new ConfigurationException("Missing required field: " + (this.urlStr == null ? "url" : "method"));
        }
    }

    @Override
    public boolean isOperational() {
        return this.urlStr != null && this.method != null && this.headerMap != null;
    }

}
