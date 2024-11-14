package com.website.chatapp.enity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String content;

    LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "users_id")
    Users users;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    Conversation conversation;

}
