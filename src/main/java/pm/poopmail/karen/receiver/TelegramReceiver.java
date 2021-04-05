package pm.poopmail.karen.receiver;

import com.github.jezza.TomlTable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import pm.poopmail.karen.exception.ConfigurationException;
import pm.poopmail.karen.incident.Incident;

public class TelegramReceiver extends HttpReceiver {

    private boolean operational = false;

    public TelegramReceiver() {
    }

    @Override
    public void report(final Incident incident) throws Exception {
        final String message = incident.getType() + " @ " + incident.getServiceName() + "\n\n"
                + "Description:\n------------------\n" + incident.getDescription()
                + "\n------------------\n\nTopic: '" + incident.getTopic() + "'";

        final String temp = this.urlStr;
        this.urlStr += "&text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        super.report(incident);
        this.urlStr = temp;
    }

    @Override
    public void loadFromConfig(final TomlTable tomlTable) throws ConfigurationException {
        this.loadTrigger(tomlTable);

        final String botToken = (String) tomlTable.get("bottoken");
        final String chatId = (String) tomlTable.get("chatid");
        this.urlStr = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId;
        this.method = "POST";
        this.headerMap = new HashMap<>();
        this.rawPayload = null;

        this.operational = botToken != null && chatId != null;
        if (!this.operational) {
            throw new ConfigurationException("Missing required field: " + (botToken == null ? "bottoken" : "chatid"));
        }
    }

    @Override
    public boolean isOperational() {
        return super.isOperational() && this.operational;
    }

}
