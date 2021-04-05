package pm.poopmail.karen;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import java.io.IOException;
import java.util.Base64;
import pm.poopmail.karen.config.Config;
import pm.poopmail.karen.exception.ConfigurationException;
import pm.poopmail.karen.incident.IncomingIncidentProcessor;
import pm.poopmail.karen.redis.RedisEventSubscriber;

/**
 * Application launcher
 *
 * @author Maximilian Dorn (Cerus)
 */
public class Launcher {

    public static void main(final String[] args) {
        // Retrieve configuration
        final String redisUri = System.getenv("KAREN_REDIS_URI");
        final String redisKey = System.getenv("KAREN_REDIS_KEY");
        final String configFilePath = System.getenv("KAREN_CONFIG_PATH");
        final boolean debug = System.getenv("KAREN_DEBUG") != null;

        // Load report config
        final Config config = new Config();
        try {
            config.load(configFilePath);
        } catch (final ConfigurationException | IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load config");
            return;
        }

        // Init incident processor
        final IncomingIncidentProcessor incidentProcessor = new IncomingIncidentProcessor(config);

        if (debug) {
            // Do debug stuff
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("service", "Test");
            jsonObject.addProperty("type", "INFO");
            jsonObject.addProperty("topic", "This is the topic");
            jsonObject.addProperty("description", "THis is the desc");
            incidentProcessor.process(jsonObject);
            return;
        }

        // Init redis
        final RedisClient redisClient = RedisClient.create(redisUri);
        final RedisEventSubscriber redisEventSubscriber = new RedisEventSubscriber(incidentProcessor);
        redisClient.getResources().eventBus().get().subscribe(redisEventSubscriber);
        redisClient.addListener(redisEventSubscriber);
        final RedisPubSubAsyncCommands<String, String> redisSub = redisClient.connectPubSub().async();
        redisSub.subscribe(redisKey);
        final StatefulRedisPubSubConnection<String, String> statefulConnection = redisSub.getStatefulConnection();
        statefulConnection.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(final String channel, final String message) {
                // Check if this is the correct channel
                if (!channel.equals(redisKey)) {
                    return;
                }

                // Parse json and forward to incident processor
                final JsonObject jsonObject = JsonParser.parseString(new String(Base64.getDecoder().decode(message))).getAsJsonObject();
                incidentProcessor.process(jsonObject);
            }
        });

        // Cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(redisClient::shutdown));
    }

}
