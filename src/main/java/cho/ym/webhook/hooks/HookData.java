package cho.ym.webhook.hooks;

import cho.ym.webhook.result.Embed;

import java.util.List;

public record HookData(
        String avatarUrl,
        List<Embed> embeds
) {
}


