package cho.ym.webhook.result;

import java.time.LocalDateTime;

public record Field (
        String name,
        String value,
        boolean inline
){
}
