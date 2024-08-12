package cho.ym.webhook.hooks;

import cho.ym.webhook.Const;
import cho.ym.webhook.result.Author;
import cho.ym.webhook.result.Embed;
import cho.ym.webhook.result.Field;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PipelineHook implements Hook {

    private ObjectMapper objectMapper = new ObjectMapper();

    public PipelineHook() {
        super();
    }

    @Override
    public void execute(String json) throws JsonProcessingException {

        objectMapper.registerModule(new JavaTimeModule());
        JsonNode jsonNode = objectMapper.readTree(json);

        String projectName = jsonNode.path("project").path("name").asText();
        String projectUrl = jsonNode.path("project").path("web_url").asText();
        String status = jsonNode.path("object_attributes").path("status").asText();
        String pipelineUrl = jsonNode.path("object_attributes").path("url").asText();
        String userName = jsonNode.path("user").path("name").asText();
        String userAvatar = jsonNode.path("user").path("avatar_url").asText();

        Author author = new Author(userName + " 님이 " + projectName + "에서 파이프라인을 실행했습니다."
                , projectUrl
                , userAvatar);

        List<Field> fields = new ArrayList<>();
        Field field = new Field(
                "Pipeline Status"
                , "Status : " + status + "\n" + "URL : " + pipelineUrl, true);
        fields.add(field);

        List<Embed> embeds = new ArrayList<>();
        if (status.equals("failed")) {
            String failureMessage = jsonNode.path("builds").get(0).path("failure_reason").asText();
            Field failureField = new Field("Failure Reason", failureMessage, true);
            fields.add(failureField);
            Embed embed = new Embed(
                    16711680
                    , author
                    , "Pipeline Result"
                    , ""
                    , ""
                    , fields
                    , "");
            embeds.add(embed);
        } else if (status.equals("success")) {
            String duration = jsonNode.path("object_attributes").path("duration").asText();
            Field durationField = new Field("Duration", duration + " seconds", true);
            fields.add(durationField);
            Embed embed = new Embed(
                    65280
                    , author
                    , "Pipeline Result"
                    , ""
                    , ""
                    , fields
                    , "");
            embeds.add(embed);
        } else {
            Field durationField = new Field("Duration", "Not available", true);
            fields.add(durationField);
            Embed embed = new Embed(
                    16776960
                    , author
                    , "Pipeline Result"
                    , ""
                    , ""
                    , fields
                    , "");
            embeds.add(embed);
        }


        HookData data = new HookData("https://gitlab.com/favicon.png", embeds);
        String payload = objectMapper.writeValueAsString(data);
        System.out.println(payload);

        try {
            URL url = new URL(Const.URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message successfully sent to Discord.");
            } else {
                System.out.println("Failed to send message to Discord. Response code: " + responseCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSupport(String header) {
        return header.equals("Pipeline Hook");
    }
}
