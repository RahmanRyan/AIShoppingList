package org.example.scraper.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatGPTService {
    @Value("${openai.api.key}")
    private String apiKey;

    public String extractProductInfo(String html) {
        OpenAiService service = new OpenAiService(apiKey);

        String prompt = "Extract the product name and price from this HTML. Return only a JSON object with 'name' and 'price' fields. HTML: " + html;

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(new ChatMessage("user", prompt)))
                .build();

        return service.createChatCompletion(request)
                .getChoices().get(0)
                .getMessage()
                .getContent();
    }
}
