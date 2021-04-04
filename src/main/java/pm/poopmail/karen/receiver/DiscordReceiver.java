package pm.poopmail.karen.receiver;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.github.jezza.TomlTable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import pm.poopmail.karen.exception.ConfigurationException;
import pm.poopmail.karen.incident.Incident;

/**
 * Makes a Discord api request based on incidents
 *
 * @author Maximilian Dorn (Cerus)
 */
public class DiscordReceiver extends HttpReceiver {

    private Map<String, String> serviceIconMap = new HashMap<>();
    private String content;

    public DiscordReceiver() {
    }

    @Override
    public void report(final Incident incident) throws Exception {
        // Build payload
        this.rawPayload = "{" + (this.content == null ? "" : "\"content\": \"" + this.content + "\", ")
                + "\"embeds\": [" + new WebhookEmbedBuilder()
                .setThumbnailUrl(this.serviceIconMap.getOrDefault(incident.getServiceName(), null))
                .setTitle(new WebhookEmbed.EmbedTitle(incident.getType() + " @ " + incident.getServiceName(), null))
                .setDescription("```\n" + incident.getDescription() + "\n```")
                .addField(new WebhookEmbed.EmbedField(false, "Topic", incident.getTopic()))
                .setFooter(new WebhookEmbed.EmbedFooter("Poopmail Karen", null))
                .setTimestamp(Instant.now())
                .setColor(incident.getType().getColor())
                .build()
                .toJSONString() + "]}";
        // Send report
        super.report(incident);
    }

    @Override
    public void loadFromConfig(final TomlTable tomlTable) throws ConfigurationException {
        this.urlStr = (String) tomlTable.get("url");
        this.method = "POST";
        this.headerMap = new HashMap<>() {
            {
                this.put("User-Agent", "github.com/poopmail");
                this.put("Content-Type", "application/json");
            }
        };
        this.serviceIconMap = ((TomlTable) tomlTable.getOrDefault("icons", new TomlTable()))
                .asMap().entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), (String) entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.content = (String) tomlTable.getOrDefault("content", null);
        this.rawPayload = "";

        // Check if the configuration was valid
        if (this.urlStr == null) {
            throw new ConfigurationException("Missing required field: url");
        }
    }

}
