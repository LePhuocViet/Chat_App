package com.website.chatapp.repository;

import com.website.chatapp.enity.TokenInvalid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenInvalidRepository extends JpaRepository<TokenInvalid, String> {

    boolean existsById(String id);

}