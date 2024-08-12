package cho.ym.webhook.comment;

import java.time.LocalDateTime;
import java.util.List;

public record Commit(
        String title,
        String message,
        String author,
        String timeStamp,
        List<Modified> modified
) {

}

