package com.example.demo.src.fcm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostReaction {
    private int senderIdx;
    private int receiverIdx;
    private int reactionIdx;
    private int challengeIdx;
}
