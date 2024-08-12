package cho.ym.webhook.result;

import java.util.List;

public record WebhookPayload(
        String avatarUrl,
        List<Embed> embeds
) {
}

