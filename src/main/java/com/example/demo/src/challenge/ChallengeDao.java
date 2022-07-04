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

    public int deleteChallenge(int challengeIdx, int userIdx){
        String deleteChallengeQuery = "update Challenge set status = 0 where challengeIdx = ? and userIdx = ?; ";
        Object[] deleteChallengeParams = new Object[]{challengeIdx, userIdx};
        return this.jdbcTemplate.update(deleteChallengeQuery, deleteChallengeParams);
    }

    public int checkChallenge(int challengeIdx) {
        String checkChallengeQuery = "select exists(select challengeIdx from Challenge where challengeIdx = ? and status = 1)";
        int checkChallengeParams = challengeIdx;
        return this.jdbcTemplate.queryForObject(checkChallengeQuery, int.class, checkChallengeParams);
    }

    public List<GetChallengeHome> getChallengeHome(int categoryIdx){
        String getChallengeHomeQuery = "select c.categoryName,\n" +
                "       ch.challengeIdx,\n" +
                "       ch.title,\n" +
                "       DATE_FORMAT(ch.startDate, '%c월 %e일') as startDate,\n" +
                "       case\n" +
                "           when ch.cycle = '1' then concat('하루에 ', ch.count, '회')\n" +
                "           when ch.cycle = '7' then concat('1주일에 ', ch.count, '회')\n" +
                "           when ch.cycle = '14' then concat('2주일에 ', ch.count, '회')\n" +
                "               end as certification,\n" +
                "       waiting\n" +
                "from Category c, Challenge ch\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as waiting\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on ch.challengeIdx = x.challengeIdx\n" +
                "where c.categoryIdx = ch.categoryIdx\n" +
                "and ch.status = 1\n" +
                "and ch.categoryIdx = ?\n" +
                "and ch.startDate > now()\n" +
                "and waiting > 0\n" +
                "order by waiting desc limit 4; ";
        String getTagsQuery = "select tag\n" +
                "from ChallengeTag t, Challenge ch\n" +
                "where ch.challengeIdx = t.challengeIdx\n" +
                "and ch.status = 1\n" +
                "and ch.challengeIdx = ?;";
        int getChallengeHomeParams = categoryIdx;

        return this.jdbcTemplate.query(getChallengeHomeQuery,
                (rs, rowNum) -> new GetChallengeHome(
                        rs.getString("categoryName"),
                        rs.getInt("challengeIdx"),
                        rs.getString("title"),
                        rs.getString("startDate"),
                        rs.getString("certification"),
                        rs.getInt("waiting"),
                        this.jdbcTemplate.query(getTagsQuery, (rs1, rowNum1) -> new String(
                                rs1.getString("tag")
                        ), rs.getInt("challengeIdx"))
                ), getChallengeHomeParams);
    }

    public int getUserIdx(int challengeIdx) {
        String getUserIdxQuery = "select userIdx from Challenge where challengeIdx = ?;";
        int getUserIdxParams = challengeIdx;

        return this.jdbcTemplate.queryForObject(getUserIdxQuery,
                (rs, rowNum) -> new Integer(rs.getInt("userIdx")), getUserIdxParams);
    }

    public int checkJoin(int challengeIdx, int userIdx){
        String checkNickNameQuery = "select exists(select waitingIdx from ChallengeWaiting where challengeIdx = ? and userIdx = ? and status = 1)";
        return this.jdbcTemplate.queryForObject(checkNickNameQuery, int.class, challengeIdx, userIdx);
    }

    public int joinChallenge(PostChallengeJoin postChallengeJoin) {
        int founderIdx = getUserIdx(postChallengeJoin.getChallengeIdx());

        String joinChallengeQuery = "insert into ChallengeWaiting(challengeIdx, userIdx, founderIdx) values (?, ?, ?);";
        Object[] joinChallengeParams = new Object[]{postChallengeJoin.getChallengeIdx(), postChallengeJoin.getUserIdx(), founderIdx};
        this.jdbcTemplate.update(joinChallengeQuery, joinChallengeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int checkWaiting(int waitingIdx){
        String checkWaitingQuery = "select exists(select waitingIdx from ChallengeWaiting where waitingIdx = ? and status = 1)";
        return this.jdbcTemplate.queryForObject(checkWaitingQuery, int.class, waitingIdx);
    }

    public int deleteWaiting(int waitingIdx, int userIdx){
        String deleteWaitingQuery = "update ChallengeWaiting set status = 0 where waitingIdx = ? and userIdx = ?;";
        Object[] deleteWaitingParams = new Object[]{waitingIdx, userIdx};
        return this.jdbcTemplate.update(deleteWaitingQuery, deleteWaitingParams);
    }
}
