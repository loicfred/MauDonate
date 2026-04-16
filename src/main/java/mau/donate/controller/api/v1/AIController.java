package mau.donate.controller.api.v1;

import mau.donate.ai.AboutUsTools;
import mau.donate.ai.CampaignTools;
import mau.donate.ai.ProfileTools;
import mau.donate.objects.User;
import org.solarframework.ai.spring.Conversation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.solarframework.ai.spring.AIService.aiService;

@RestController
@RequestMapping("/api/v1/ai")
public class AIController {

    @PostMapping("/chat")
    public Msg receiveMessage(Principal p, @RequestBody ChatRequest messages) {
        List<String> infoType = aiService.chooseBetween("What kind of information is the user currently asking for?",
                List.of("Campaign", "Personal Information", "About Us", "Other"));
        System.err.println(infoType);
        Long userId = p != null ? User.getByAuthentication(p).getID() : null;

        SystemMessage SYSMSG = SystemMessage.builder().text("""
            You are an AI assistant that helps users to answer questions about the MauDonate donation app.
            You should answer questions related to donations, donation requests, campaigns, personal information, and about us.
            The user is currently asking about %s.
            You should answer in short paragraph.
            %s.
            """.formatted(infoType.stream().map(String::toString).collect(Collectors.joining(", ")),
                p != null ? "The current user's ID is " + userId + ". YOU SHOULD NOT SHARE THIS ID, nor use the ID of someone else." : "No user logged in.")).build();

        List<Object> toolClasses = new ArrayList<>();
        if (infoType.contains("Campaign")) toolClasses.add(new CampaignTools());
        if (infoType.contains("Personal Information")) toolClasses.add(new ProfileTools(userId));
        if (infoType.contains("About Us")) toolClasses.add(new AboutUsTools());

        Conversation C = aiService.startConversation(SYSMSG, toolClasses.toArray());



        for (Msg m : messages.messages) {
            if (Objects.equals(m.role, "bot")) {
                C.add(AssistantMessage.builder().content(m.content).build());
            } else if (m != messages.messages.getLast()) {
                C.add(UserMessage.builder().text(m.content).build());
            }
        }

        String answer = C.talk(messages.messages.getLast().content);
        System.err.println(answer);
        return new Msg("bot", answer);
    }


    public static class Msg {
        String role;
        String content;

        public Msg(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    public static class ChatRequest {
        private List<Msg> messages;

        public List<Msg> getMessages() { return messages; }
        public void setMessages(List<Msg> messages) { this.messages = messages; }
    }
}
