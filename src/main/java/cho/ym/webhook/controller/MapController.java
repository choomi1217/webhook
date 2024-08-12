package cho.ym.webhook.controller;

import cho.ym.webhook.hooks.HookFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class MapController {

    @PostMapping("/webhook/map")
    public String handleWebhook(
            @RequestHeader("X-Gitlab-Event") String event,
            @RequestBody String json) throws JsonProcessingException {
        HookFactory.getInstance().getHook(event).execute(json);
        return "Webhook received successfully!";
    }

    @GetMapping("/webhook/map")
    public String handleWebhook2(HttpServletRequest request) {
        System.out.println("GET : " + request.getHeader("X-Gitlab-Event"));
        return "Webhook received successfully!";
    }
}
