package mau.donate.ai;

import org.springframework.ai.tool.annotation.Tool;

public class AboutUsTools {

    @Tool(description = "Get information about who we are")
    public Object getAboutUs() {
        return "We are a group of students who wanted to help people, that's why we made MauDonate.";
    }

    @Tool(description = "Get the creators of the project")
    public Object getAboutUsUsers() {
        return "We are Loïc Fred Cheerkoot, Ilhaan Hoolash Jasir and Jeconia Botteveaux";
    }

    @Tool(description = "Get our email address")
    public Object getAboutUsEmail() {
        return "maudonate@gmail.com";
    }

}
