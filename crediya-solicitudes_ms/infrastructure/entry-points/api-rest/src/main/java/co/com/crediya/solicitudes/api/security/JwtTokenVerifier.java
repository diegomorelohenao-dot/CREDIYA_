package co.com.crediya.solicitudes.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtTokenVerifier {
    private static final String ISSUER = "crediya-auth";
    private final SecretKey verificationKey;

    public JwtTokenVerifier(@Value("${security.jwt.secret}") String base64Secret) {
        this.verificationKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    public JwtAuthenticatedUser validate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(verificationKey)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new JwtAuthenticatedUser(
                Long.valueOf(claims.getSubject()),
                claims.get("email", String.class),
                claims.get("role", String.class)
        );
    }
}