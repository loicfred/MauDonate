package mau.donate.objects;

import org.solarframework.db.spring.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

public class Payment_Method extends DatabaseObject.ID_OBJ<Long, Payment_Method> {
    public long UserID;
    public String Provider;
    public String Provider_token;
    public LocalDateTime CreatedAt;

    public Payment_Method() {}
    public Payment_Method(String provider_token, String provider, long userID) {
        ID = Instant.now().toEpochMilli();
        Provider_token = provider_token;
        Provider = provider;
        UserID = userID;
        CreatedAt = LocalDateTime.now();
        Write();
    }

}