package com.website.chatapp.service;

import com.website.chatapp.dto.requests.AuthenticationRequest;
import com.website.chatapp.dto.requests.TokenRequest;
import com.website.chatapp.dto.responses.AuthenticationResponse;
import com.website.chatapp.dto.responses.IntrospectionResponse;

public interface IAuthService {

    IntrospectionResponse introspect(TokenRequest tokenRequest);

    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

    AuthenticationResponse logout(TokenRequest tokenRequest);



}
