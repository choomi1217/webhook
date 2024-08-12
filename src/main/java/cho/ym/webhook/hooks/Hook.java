package cho.ym.webhook.hooks;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Hook {
    void execute(String json) throws JsonProcessingException;
    boolean isSupport(String header);
}
