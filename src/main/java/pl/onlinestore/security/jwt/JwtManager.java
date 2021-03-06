package pl.onlinestore.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Profile("jwt")
public class JwtManager {

    private final long ACCESS_TOKEN_VALIDATION_TIME;
    private final long REFRESH_TOKEN_VALIDATION_TIME;
    private final SecretKey SECRET_KEY;
    private final JwtParser parser;

    @Autowired
    public JwtManager(@Value("${jwt.access-token.validation-time}") long accessValidationTime,
                      @Value("${jwt.refresh-token.validation-time}") long refreshValidationTime) {
        this.ACCESS_TOKEN_VALIDATION_TIME = accessValidationTime;
        this.REFRESH_TOKEN_VALIDATION_TIME = refreshValidationTime;
        this.SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.parser = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build();
    }

    public String generateAccessToken(Authentication authentication) {
        Date expirationTime = Date.from(LocalDateTime.now()
                                                     .plusMinutes(ACCESS_TOKEN_VALIDATION_TIME)
                                                     .atZone(ZoneId.systemDefault())
                                                     .toInstant());

        List<String> roles = authentication.getAuthorities()
                                           .stream()
                                           .map(GrantedAuthority::getAuthority)
                                           .collect(Collectors.toList());

        return Jwts.builder()
                   .setSubject(authentication.getName())
                   .claim("roles", roles)
                   .setExpiration(expirationTime)
                   .signWith(SECRET_KEY)
                   .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        Date expirationTime = Date.from(LocalDateTime.now()
                                                     .plusMinutes(REFRESH_TOKEN_VALIDATION_TIME)
                                                     .atZone(ZoneId.systemDefault())
                                                     .toInstant());

        return Jwts.builder()
                   .setSubject(authentication.getName())
                   .setExpiration(expirationTime)
                   .signWith(SECRET_KEY)
                   .compact();
    }

    public String generateSwaggerToken(Authentication authentication) {
        return Jwts.builder()
                   .setSubject(authentication.getName())
                   .claim("swagger", true)
                   .signWith(SECRET_KEY)
                   .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            parser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException e) {
            return false;
        }
    }

    public boolean isSwaggerCookie(String cookie) {
        try {
            return parser
                .parseClaimsJws(cookie)
                .getBody()
                .get("swagger") != null;
        } catch (SignatureException | MalformedJwtException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            return parser
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims;

        try {
            claims = parser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }

        List<String> authorities = claims.get("roles", List.class);

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "",
                                                       authorities.stream().map(SimpleGrantedAuthority::new)
                                                                           .collect(Collectors.toList()));
    }
}