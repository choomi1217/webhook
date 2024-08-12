package cho.ym.webhook.comment;

import java.util.List;

public record PushComment(
        String projectName,
        List<Commit> commit
) {
}