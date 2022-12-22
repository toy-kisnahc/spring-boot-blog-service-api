package com.kisnahc.blogservice.auth.provider;

import com.kisnahc.blogservice.auth.CustomUserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    private static final long JWT_EXPIRATION = 1000L * 60 * 30;

    private final CustomUserDetailService customUserDetailService;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String generateJwt(String email) {
        Date now = new Date();
        Date accessExpiration = new Date(now.getTime() + JWT_EXPIRATION);
        Claims claims = Jwts.claims().setSubject(email);

        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessExpiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetail = customUserDetailService.loadUserByUsername(getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetail, "", userDetail.getAuthorities());
    }

    public Date getExpiration(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();

    }

    public boolean jwtValidation(String token) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        ValueOperations<String, String> logoutOperation = redisTemplate.opsForValue();
        if (logoutOperation.get(token) != null) {
            throw new RuntimeException("logged out member.");
        }
        return !claimsJws.getBody().getExpiration().before(new Date());
    }

}
