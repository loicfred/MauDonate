package mau.donate.ai;

import mau.donate.objects.User;
import org.springframework.ai.tool.annotation.Tool;

import static org.solarframework.db.spring.DatabaseService.dbService;
import static org.solarframework.core.util.ClassUtils.copyObject;

public class ProfileTools {
    private Long UserID;

    public ProfileTools(long userID) {
        this.UserID = userID;
    }

    @Tool(description = "Get the currently logged in user information")
    public Object getProfileInformation() {
        if (UserID == null) return "You are not logged in.";
        User inCache = dbService.getByIdWithJoins(User.class, UserID).orElse(null);
        User u = new User();
        if (inCache == null) return "No information found about you.";
        copyObject(u, inCache);
        u.Image = null;
        return u;
    }

}
