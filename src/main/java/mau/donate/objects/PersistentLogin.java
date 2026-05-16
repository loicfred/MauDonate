package mau.donate.objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.solarframework.db.spring.DatabaseObject;

import java.time.Instant;

@Entity
@Table(name = "persistent_logins")
public class PersistentLogin extends DatabaseObject<PersistentLogin> {
    @Id
    @Column(name = "series", nullable = false, length = 64)
    private String series;

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Column(name = "token", nullable = false, length = 64)
    private String token;

    @Column(name = "last_used", nullable = false)
    private Instant lastUsed;

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Instant lastUsed) {
        this.lastUsed = lastUsed;
    }

}