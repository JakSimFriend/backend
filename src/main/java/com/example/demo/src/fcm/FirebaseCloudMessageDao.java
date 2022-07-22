package com.example.demo.src.fcm;

import com.example.demo.src.fcm.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.challenge.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FirebaseCloudMessageDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public int createReaction(PostReaction postReaction) {
        String createReactionQuery = "insert into ReactionHistory(senderIdx, receiverIdx, reactionIdx) values (?, ?, ?);\n";
        Object[] createChallengeParams = new Object[]{postReaction.getSenderIdx(), postReaction.getReceiverIdx(), postReaction.getReactionIdx()};
        this.jdbcTemplate.update(createReactionQuery, createChallengeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int createAlert(String alert, int userIdx, int challengeIdx) {
        String createReactionQuery = "insert into ChallengeAlert(alert, userIdx, challengeIdx) values (?, ?, ?);\n";
        Object[] createChallengeParams = new Object[]{alert, userIdx, challengeIdx};
        this.jdbcTemplate.update(createReactionQuery, createChallengeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public String getNickName(int userIdx) {
        String getNickNameQuery = "select nickName from User where userIdx = ? and status = 1;\n";
        return this.jdbcTemplate.queryForObject(getNickNameQuery, (rs, rowNum) -> new String(rs.getString("nickName")), userIdx);
    }

    public String getReaction(int reactionIdx) {
        String getReactionQuery = "select reactionName from Reaction where reactionIdx = ?;\n";
        return this.jdbcTemplate.queryForObject(getReactionQuery, (rs, rowNum) -> new String(rs.getString("reactionName")), reactionIdx);
    }

    public String getToken(int userIdx) {
        String getTokenQuery = "select token from UserDevice where userIdx = ? and status = 1;\n";
        String token = this.jdbcTemplate.queryForObject(getTokenQuery, (rs, rowNum) -> new String(rs.getString("token")), userIdx);
        return token;
    }

    public String getImage(int reactionIdx) {
        String getReactionQuery = "select reactionPhoto from Reaction where reactionIdx = ?;\n";
        return this.jdbcTemplate.queryForObject(getReactionQuery, (rs, rowNum) -> new String(rs.getString("reactionPhoto")), reactionIdx);
    }


}
