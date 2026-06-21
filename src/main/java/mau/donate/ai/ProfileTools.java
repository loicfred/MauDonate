package mau.donate.ai;

import org.solarframework.web.auth.obj.Account_User;
import org.springframework.ai.tool.annotation.Tool;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;
import static org.solarframework.core.util.ClassUtils.copyObject;

public class ProfileTools {
    private Long UserID;

    public ProfileTools(long userID) {
        this.UserID = userID;
    }

    @Tool(description = "Get the currently logged in user information")
    public Object getProfileInformation() {
        if (UserID == null) return "You are not logged in.";
        Account_User inCache = SolarDBManager.getByIdWithJoins(Account_User.class, UserID).orElse(null);
        Account_User u = new Account_User();
        if (inCache == null) return "No information found about you.";
        copyObject(u, inCache);
        u.setAvatar(null);
        return u;
    }

}
