package com.website.chatapp.service.implement;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.website.chatapp.dto.requests.AuthenticationRequest;
import com.website.chatapp.dto.requests.TokenRequest;
import com.website.chatapp.dto.responses.AuthenticationResponse;
import com.website.chatapp.dto.responses.IntrospectionResponse;
import com.website.chatapp.enity.TokenInvalid;
import com.website.chatapp.enity.Users;
import com.website.chatapp.enums.ErrorCode;
import com.website.chatapp.exception.WebException;
import com.website.chatapp.repository.TokenInvalidRepository;
import com.website.chatapp.repository.UsersRepository;
import com.website.chatapp.service.IAuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthService implements IAuthService {


    private final UsersRepository usersRepository;
    @Value("${secret_key.key}")
    String SECRET_KEY;


    final TokenInvalidRepository tokenInvalidRepository;
    final UsersRepository UsersRepository;


    @Override
    public IntrospectionResponse introspect(TokenRequest tokenRequest) {
        boolean result = true;
        try {
            SignedJWT signedJWT = verify(tokenRequest.getToken());

        } catch (Exception e) {
            result = false;
        }

        return IntrospectionResponse.builder()
                .result(result)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        Users users = usersRepository.findUsersByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean verified = passwordEncoder.matches(authenticationRequest.getPassword(), users.getPassword());
        if (!verified) throw new WebException(ErrorCode.PASSWORD_IS_INCORRECT);
        String token = generateToken(users);
        return AuthenticationResponse.builder()
                .result(true)
                .token(token)
                .build();
    }

    @Override
    public AuthenticationResponse logout(TokenRequest tokenRequest) {
        boolean result = true;
        try {
            SignedJWT signedJWT = verify(tokenRequest.getToken());
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            TokenInvalid tokenInvalid = TokenInvalid.builder()
                    .id(jwtId)
                    .token(tokenRequest.getToken())
                    .date_created(LocalDate.now())
                    .build();
            tokenInvalidRepository.save(tokenInvalid);
        }catch (Exception e) {
            result = false;
            throw new WebException(ErrorCode.TOKEN_INVALID);
        }


        return AuthenticationResponse.builder()
                .result(result)
                .token(tokenRequest.getToken())
                .build();
    }


    SignedJWT verify(String token) {
        try {
            JWSVerifier jwsVerifier = new MACVerifier(SECRET_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date dateExp = signedJWT.getJWTClaimsSet().getExpirationTime();
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            var verifier = signedJWT.verify(jwsVerifier);
            if (!(verifier) || !(dateExp.after(new Date())))
                throw new WebException(ErrorCode.TOKEN_INVALID);
            if (tokenInvalidRepository.existsById(jwtId)) {
                throw new WebException(ErrorCode.TOKEN_INVALID);
            }
            return signedJWT;
        } catch (Exception e) {
            throw new WebException(ErrorCode.TOKEN_INVALID);
        }

    }

    public String generateToken(Users users) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(users.getUsername())
                .issuer("ChatApp.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(30, ChronoUnit.MINUTES).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", build_scope(users))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
        } catch (JOSEException e) {
        }
        return jwsObject.serialize();
    }

    public String build_scope(Users users) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(users.getRoles())) {
            users.getRoles().forEach(role -> stringJoiner.add(role.getName()));
        }

        return stringJoiner.toString();

    }

}
