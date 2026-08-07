package app.mendnook.hub.material;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class ServiceTokenIssuer {

    private final SecretKey key;

    public ServiceTokenIssuer(@Value("${mendnook.jwt-secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String issue() {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject("mendnook-main")
                .claim("scope", "MATERIAL_SYNC")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(120)))
                .signWith(key)
                .compact();
    }
}
