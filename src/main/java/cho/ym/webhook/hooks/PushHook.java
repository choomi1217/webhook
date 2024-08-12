package cho.ym.webhook.hooks;

import cho.ym.webhook.Const;
import cho.ym.webhook.comment.Commit;
import cho.ym.webhook.comment.Modified;
import cho.ym.webhook.comment.PushComment;
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

public class PushHook implements Hook {

    private ObjectMapper objectMapper = new ObjectMapper();

    public PushHook() {
        super();
    }

    @Override
    public void execute(String json) throws JsonProcessingException {

        objectMapper.registerModule(new JavaTimeModule());
        JsonNode jsonNode = objectMapper.readTree(json);

        String projectName = jsonNode.path("project").path("name").asText();
        String projectUrl = jsonNode.path("project").path("web_url").asText();
        JsonNode commits = jsonNode.path("commits");
        String userName = jsonNode.path("user_name").asText();
        String userAvatar = jsonNode.path("user_avatar").asText();

        List<Commit> commitList = new ArrayList<>();
        for (JsonNode commit : commits) {
            String message = commit.path("message").asText();
            String title = commit.path("title").asText();
            String author = commit.path("author").path("name").asText();
            JsonNode modified = commit.path("modified");
            String timestamp = commit.path("timestamp").asText();
            List<Modified> modifiedList = new ArrayList<>();
            for (JsonNode mod : modified) {
                String modText = mod.asText();
                Modified modifiedObj = new Modified(modText);
                modifiedList.add(modifiedObj);
            }

            Commit commitObj = new Commit(title, message, author, timestamp, modifiedList);
            commitList.add(commitObj);
        }

        PushComment pushComment = new PushComment(projectName, commitList);

        Author author = new Author(
                userName + " 님이 " + projectName + "에 Push 했습니다.",
                projectUrl,
                userAvatar
        );

        List<Field> fields = new ArrayList<>();
        pushComment.commit().forEach(commit -> {
            Field field = new Field(
                    "Title : " + commit.title(),
                    "Message : " + commit.message() + "\n" + " Time : " + commit.timeStamp() + "\n" + " Author : " + commit.author(),
                    true
            );
            fields.add(field);
        });

        List<Embed> embeds = new ArrayList<>();
        Embed embed = new Embed(
                0x0000FF,
                author,
                "Commit List ",
                "",
                "",
                fields,
                ""
        );
        embeds.add(embed);

        HookData data = new HookData(
                "https://gitlab.com/favicon.png",
                embeds
        );
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
        return header.equals("Push Hook");
    }
}

