package com.example.demo.src.challenge;

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

        int userIdx = postChallenge.getUserIdx();
        String addWaitingQuery = "insert into ChallengeWaiting(challengeIdx, userIdx, founderIdx, accept) values (?, ?, ?, ?);";
        Object[] addWaitingParams = new Object[]{challengeIdx, userIdx, userIdx, 1};
        this.jdbcTemplate.update(addWaitingQuery, addWaitingParams);

        // 챌린지 멤버에 추가
        String addMemberQuery = "insert into Member(challengeIdx, userIdx) values (?, ?);";
        Object[] addMemberParams = new Object[]{challengeIdx, userIdx};
        this.jdbcTemplate.update(addMemberQuery, addMemberParams);

        subtractUserPoint(userIdx);
        addChallengePoint(challengeIdx, userIdx);

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
                "       accept\n" +
                "from Category c, Challenge ch\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as accept\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on ch.challengeIdx = x.challengeIdx\n" +
                "where c.categoryIdx = ch.categoryIdx\n" +
                "and ch.status = 1\n" +
                "and ch.categoryIdx = ?\n" +
                "and ch.startDate > now()\n" +
                "and accept < 6\n" +
                "and accept > 0\n" +
                "order by accept desc, title limit 4;";
        String getTagsQuery = "select tag\n" +
                "from ChallengeTag t, Challenge ch\n" +
                "where ch.challengeIdx = t.challengeIdx\n" +
                "and ch.status = 1\n" +
                "and ch.challengeIdx = ? order by tag";
        int getChallengeHomeParams = categoryIdx;

        return this.jdbcTemplate.query(getChallengeHomeQuery,
                (rs, rowNum) -> new GetChallengeHome(
                        rs.getString("categoryName"),
                        rs.getInt("challengeIdx"),
                        rs.getString("title"),
                        rs.getString("startDate"),
                        rs.getString("certification"),
                        rs.getInt("accept"),
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

    public int refuseWaiting(int waitingIdx, int founderIdx){
        String refuseWaitingQuery = "update ChallengeWaiting set accept = 2 where waitingIdx = ? and founderIdx = ? and status = 1;";
        Object[] refuseWaitingParams = new Object[]{waitingIdx, founderIdx};
        return this.jdbcTemplate.update(refuseWaitingQuery, refuseWaitingParams);
    }

    public int checkRefuse(int waitingIdx){
        String checkRefuseQuery = "select exists(select waitingIdx from ChallengeWaiting where waitingIdx = ? and accept = 2)";
        return this.jdbcTemplate.queryForObject(checkRefuseQuery, int.class, waitingIdx);
    }

    public int checkAccept(int waitingIdx){
        String checkRefuseQuery = "select exists(select waitingIdx from ChallengeWaiting where waitingIdx = ? and accept = 1)";
        return this.jdbcTemplate.queryForObject(checkRefuseQuery, int.class, waitingIdx);
    }

    public int getChallengeIdx(int waitingIdx) {
        String getChallengeIdxQuery = "select challengeIdx from ChallengeWaiting where waitingIdx = ?";
        int getChallengeIdxIdxParams = waitingIdx;

        return this.jdbcTemplate.queryForObject(getChallengeIdxQuery,
                (rs, rowNum) -> new Integer(rs.getInt("challengeIdx")), getChallengeIdxIdxParams);
    }

    @Transactional
    public int acceptWaiting(int waitingIdx, int founderIdx) throws Exception{

        // 챌린지 인덱스 받아오기
        int challengeIdx = getChallengeIdx(waitingIdx);

        // 신청한 유저 인덱스 받아오기
        int userIdx = getWaitingUser(waitingIdx);

        int check = checkUser(userIdx);
        if(check == 0) throw new BaseException(BaseResponseStatus.NOT_EXIST_USER);

        // 챌린지 멤버에 추가
        String addMemberQuery = "insert into Member(challengeIdx, userIdx) values (?, ?);";
        Object[] addMemberParams = new Object[]{challengeIdx, userIdx};
        this.jdbcTemplate.update(addMemberQuery, addMemberParams);

        subtractUserPoint(userIdx);
        addChallengePoint(challengeIdx, userIdx);

        // accept 상태 변경
        String changeAcceptQuery = "update ChallengeWaiting set accept = 1 where waitingIdx = ? and founderIdx = ? and status = 1;";
        Object[] changeAcceptParams = new Object[]{waitingIdx, founderIdx};
        return this.jdbcTemplate.update(changeAcceptQuery, changeAcceptParams);
    }

    public int getWaitingUser(int waitingIdx) {
        String getWaitingUserQuery = "select userIdx from ChallengeWaiting where waitingIdx = ?;";
        int getWaitingUserParams = waitingIdx;

        return this.jdbcTemplate.queryForObject(getWaitingUserQuery,
                (rs, rowNum) -> new Integer(rs.getInt("userIdx")), getWaitingUserParams);
    }

    public int subtractUserPoint(int userIdx){
        String subtractUserPointQuery = "insert into Point(point, userIdx, categoryIdx) values (-1000, ?, 3);";
        Object[] subtractUserPointParams = new Object[]{userIdx};
        return this.jdbcTemplate.update(subtractUserPointQuery, subtractUserPointParams);
    }

    public int addChallengePoint(int challengeIdx, int userIdx){
        String addChallengePointQuery = "insert into ChallengePoint(point, challengeIdx, userIdx) values (1000, ?, ?)";
        Object[] addChallengePointParams = new Object[]{challengeIdx, userIdx};
        return this.jdbcTemplate.update(addChallengePointQuery, addChallengePointParams);
    }

    public int checkFounder(int waitingIdx, int founderIdx){
        String checkFounderQuery = "select exists(select waitingIdx from ChallengeWaiting where waitingIdx = ? and founderIdx = ?)";
        return this.jdbcTemplate.queryForObject(checkFounderQuery, int.class, waitingIdx, founderIdx);
    }

    public GetChallengeDetail getChallengeDetail(int challengeIdx, int userIdx) {
        String getChallengeDetailQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       c.content,\n" +
                "       c.pee,\n" +
                "       concat(DATE_FORMAT(c.startDate, '%c월 %e일'), ' ~ ', DATE_FORMAT(ADDDATE(c.startDate, 14), '%c월 %e일')) as date,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루에 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일에 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일에 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       accept,\n" +
                "       waiting,\n" +
                "       c.tier,\n" +
                "       (select sum(point) from Point where userIdx = ?) as myPoint,\n" +
                "       exists(select userIdx from ChallengeWaiting where userIdx = ?) as existStatus\n" +
                "from Challenge c\n" +
                "    left join (\n" +
                "    select challengeIdx, count(userIdx) as accept\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as waiting\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 0\n" +
                "    group by challengeIdx\n" +
                "    ) as y on c.challengeIdx = y.challengeIdx\n" +
                "where status = 1 and c.challengeIdx = ?";

        return this.jdbcTemplate.queryForObject(getChallengeDetailQuery,
                (rs, rowNum) -> new GetChallengeDetail(
                        rs.getInt("challengeIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("pee"),
                        rs.getString("date"),
                        rs.getString("certification"),
                        rs.getInt("accept"),
                        rs.getInt("waiting"),
                        rs.getString("tier"),
                        rs.getInt("myPoint"),
                        rs.getInt("existStatus")),
                userIdx, userIdx, challengeIdx
        );

    }

    public int checkUser(int userIdx){
        String checkUserQuery = "select exists(select userIdx from User where userIdx = ? and status = 1)";
        return this.jdbcTemplate.queryForObject(checkUserQuery, int.class, userIdx);
    }
}
