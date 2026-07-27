package top.yuhanpeng.musiccard.module.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import top.yuhanpeng.musiccard.module.domain.UserDTO;

import java.util.Date;

public class JwtUtil {
    private final static String SECRET = System.getenv("JWT_SECRET");

    public static Long getUserId(String token) throws JsonProcessingException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        String json = claims.getSubject();
        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDTO = objectMapper.readValue(json, UserDTO.class);
        return userDTO.getUserId();
    }

    public static String createToken(String user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}