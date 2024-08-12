package cho.ym.webhook.result;

import java.util.List;

public record Embed (
        int color,
        Author author,
        String title,
        String url,
        String description,
        List<Field> fields,
        String timestamp

){
}
