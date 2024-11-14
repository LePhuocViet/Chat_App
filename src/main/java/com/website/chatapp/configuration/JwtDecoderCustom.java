package com.website.chatapp.configuration;

import com.website.chatapp.dto.requests.TokenRequest;
import com.website.chatapp.enums.ErrorCode;
import com.website.chatapp.exception.WebException;
import com.website.chatapp.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.util.Introspection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class JwtDecoderCustom implements JwtDecoder {

    @Value("${secret_key.key}")
    String secret_key;

    final IAuthService iAuthService;

    NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var introspect = iAuthService.introspect(new TokenRequest(token));
            if (!introspect.isResult()) throw new WebException(ErrorCode.TOKEN_INVALID);
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKey secretKey = new SecretKeySpec(secret_key.getBytes(), "HS256");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
