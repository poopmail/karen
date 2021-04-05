package pm.poopmail.karen.receiver;

import com.github.jezza.TomlTable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class HttpReceiver extends Receiver {

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

        connection.setDoInput(true);
        if (this.rawPayload != null) {
            // Send payload
            connection.setDoOutput(true);
            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(this.replacePlaceholders(this.rawPayload, incident).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }

        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (final Exception e) {
            inputStream = connection.getErrorStream();
        }

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            System.out.println(s);
        }

        // Wont work without this for some reason
        final int responseCode = connection.getResponseCode();
        System.out.println("response: " + responseCode);
    }

    @Override
    public void loadFromConfig(final TomlTable tomlTable) throws ConfigurationException {
        super.loadFromConfig(tomlTable);

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
