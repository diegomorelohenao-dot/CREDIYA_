package co.com.crediya.autenticacion.api.security;

import co.com.crediya.autenticacion.model.usuario.RolUsuario;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.TokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProviderAdapter implements TokenGenerator {
    private static final String ISSUER = "crediya-auth";
    private final SecretKey signingKey;
    private final long expirationSeconds;

    public JwtTokenProviderAdapter(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String generate(Usuario usuario) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(usuario.getId().toString())
                .claim("email", usuario.getCorreoElectronico())
                .claim("role", usuario.getRol().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(signingKey)
                .compact();
    }

    public JwtAuthenticatedUser validate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new JwtAuthenticatedUser(
                Long.valueOf(claims.getSubject()),
                claims.get("email", String.class),
                RolUsuario.valueOf(claims.get("role", String.class))
        );
    }

    public boolean isInvalidTokenException(Exception exception) {
        return exception instanceof JwtException || exception instanceof IllegalArgumentException;
    }
}