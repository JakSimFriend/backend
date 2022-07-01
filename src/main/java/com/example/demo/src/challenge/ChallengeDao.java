package com.example.demo.src.challenge;

import com.example.demo.src.challenge.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChallengeDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createChallenge(PostChallenge postChallenge) {
        String createChallengeQuery = "insert into Challenge (title, content, startDate, cycle, count, deadline, categoryIdx, userIdx) values (?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] createChallengeParams = new Object[]{postChallenge.getTitle(), postChallenge.getContent(), postChallenge.getStartDate(),
        postChallenge.getCycle(), postChallenge.getCount(), postChallenge.getDeadline(), postChallenge.getCategoryIdx(), postChallenge.getUserIdx()};
        this.jdbcTemplate.update(createChallengeQuery, createChallengeParams);

        String lastInserIdQuery = "select last_insert_id()";
        int challengeIdx =  this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);

        if(postChallenge.getTags() == null){
            return challengeIdx;
        }

        for(int i = 0; i < postChallenge.getTags().size(); i++){
            String createTagQuery = "insert into ChallengeTag (tag, challengeIdx, userIdx) values (?, ?, ?);";
            Object[] createTagParams = new Object[]{postChallenge.getTags().get(i), challengeIdx, postChallenge.getUserIdx()};
            this.jdbcTemplate.update(createTagQuery, createTagParams);
        }

        return challengeIdx;
    }
}
