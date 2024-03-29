package com.example.demo.src.challenge;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.challenge.model.*;
import com.example.demo.src.mychallenge.model.GetToken;
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
                "       date_format(c.deadline, '%H시 %m분 마감') deadline,\n" +
                "       ifnull(accept, 0) accept,\n" +
                "       ifnull(waiting, 0) waiting,\n" +
                "       if(floor(avg(ifnull(ach, 0))) = 0, '달성률 정보 없음', concat('상위 ', floor(avg(ifnull(ach, 0))), '%')) tier,\n" +
                "       (select sum(point) from Point where userIdx = ?) as myPoint,\n" +
                "       exists(select userIdx from ChallengeWaiting w where w.userIdx = ? and w.status = 1 and w.challengeIdx = c.challengeIdx) as existStatus,\n" +
                "       exists(select userIdx from Member m where m.userIdx = ? and m.status = 1 and m.challengeIdx = ?) as memberStatus\n" +
                "from Member m\n" +
                "   left join (\n" +
                "       select a.userIdx, rank() over (order by achievement desc ) as ranking, avg(achievement) ach\n" +
                "       from AchievementRate a where a.status = 1\n" +
                "    ) as w on w.userIdx = m.userIdx\n" +
                "   , Challenge c\n" +
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
                "where c.status = 1 and c.challengeIdx = ? and c.challengeIdx = m.challengeIdx;";

        return this.jdbcTemplate.queryForObject(getChallengeDetailQuery,
                (rs, rowNum) -> new GetChallengeDetail(
                        rs.getInt("challengeIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("pee"),
                        rs.getString("date"),
                        rs.getString("certification"),
                        rs.getString("deadline"),
                        rs.getInt("accept"),
                        rs.getInt("waiting"),
                        rs.getString("tier"),
                        rs.getInt("myPoint"),
                        rs.getInt("existStatus"),
                        rs.getInt("memberStatus")
                        ),
                userIdx, userIdx, userIdx, challengeIdx, challengeIdx
        );

    }

    public int checkUser(int userIdx){
        String checkUserQuery = "select exists(select userIdx from User where userIdx = ? and status = 1)";
        return this.jdbcTemplate.queryForObject(checkUserQuery, int.class, userIdx);
    }

    public int checkProceeding(int challengeIdx) {
        String checkProceedingQuery = "select exists(select challengeIdx from Challenge where challengeIdx = ? and status = 1 and proceeding = 1)";
        int checkChallengeParams = challengeIdx;
        return this.jdbcTemplate.queryForObject(checkProceedingQuery, int.class, checkChallengeParams);
    }

    public int getPoint(int userIdx) {
        String getPointQuery = "select sum(point) point from Point where userIdx = ?;";
        return this.jdbcTemplate.queryForObject(getPointQuery, (rs, rowNum) -> new Integer(rs.getInt("point")), userIdx);
    }

    public ClosingCondition closingCondition(int challengeIdx) {
        String closingConditionQuery = "select datediff(startDate, now()) startDate,\n" +
                "       (select count(m.userIdx) from Member m where c.challengeIdx = m.challengeIdx) people\n" +
                "from Challenge c\n" +
                "where c.status = 1\n" +
                "and c.challengeIdx = ?;";
        return this.jdbcTemplate.queryForObject(closingConditionQuery,
                (rs, rowNum) -> new ClosingCondition(
                        rs.getInt("startDate"),
                        rs.getInt("people")
                        ), challengeIdx);
    }

    public int createAlert(String alert, int userIdx, String image) {
        String creatAlertQuery = "insert into Alert(alert, userIdx, image) values (?, ?, ?);\n";
        Object[] createAlertParams = new Object[]{alert, userIdx, image};
        this.jdbcTemplate.update(creatAlertQuery, createAlertParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public String getNickName(int userIdx) {
        String getNickNameQuery = "select nickName from User where userIdx = ? and status = 1;\n";
        return this.jdbcTemplate.queryForObject(getNickNameQuery, (rs, rowNum) -> new String(rs.getString("nickName")), userIdx);
    }

    public String getToken(int userIdx) {
        String getTokenQuery = "select token from UserDevice where userIdx = ? and status = 1;\n";
        String token = this.jdbcTemplate.queryForObject(getTokenQuery, (rs, rowNum) -> new String(rs.getString("token")), userIdx);
        return token;
    }

    public String getTitle(int challengeIdx) {
        String getTitleQuery = "select title from Challenge where challengeIdx = ?;\n";
        return this.jdbcTemplate.queryForObject(getTitleQuery, (rs, rowNum) -> new String(rs.getString("title")), challengeIdx);
    }

    public Integer getFounder(int challengeIdx) {
        String getFounderQuery = "select userIdx from Challenge where challengeIdx = ? and status = 1;\n";
        return this.jdbcTemplate.queryForObject(getFounderQuery, (rs, rowNum) -> new Integer(rs.getInt("userIdx")), challengeIdx);
    }

    public Integer getChallenge(int waitingIdx) {
        String getchallengeQuery = "select challengeIdx from ChallengeWaiting where waitingIdx = ? and status = 1;\n";
        return this.jdbcTemplate.queryForObject(getchallengeQuery, (rs, rowNum) -> new Integer(rs.getInt("challengeIdx")), waitingIdx);
    }

    public Integer getUser(int waitingIdx) {
        String getUserQuery = "select userIdx from ChallengeWaiting where waitingIdx = ? and status = 1;\n";
        return this.jdbcTemplate.queryForObject(getUserQuery, (rs, rowNum) -> new Integer(rs.getInt("userIdx")), waitingIdx);
    }

    public List<Integer> checkStart() {
        String checkStartQuery = "select c.challengeIdx from Challenge c\n" +
                "left join (select m.challengeIdx, count(memberIdx) as member\n" +
                "from Member m\n" +
                "where m.status = 1\n" +
                "    group by challengeIdx) as x on x.challengeIdx = c.challengeIdx\n" +
                "where c.status = 1\n" +
                "and member > 3 and datediff(now(), startDate) = 0;";

        return this.jdbcTemplate.query(checkStartQuery,
                (rs, rowNum) -> new Integer(rs.getInt("challengeIdx")));
    }

    public List<Integer> checkEnd() {
        String checkEndQuery = "select c.challengeIdx from Challenge c\n" +
                "left join (select m.challengeIdx, count(memberIdx) as member\n" +
                "from Member m\n" +
                "where m.status = 1\n" +
                "    group by challengeIdx) as x on x.challengeIdx = c.challengeIdx\n" +
                "where c.status = 1 and proceeding = 1\n" +
                "and member > 3  and date_add(startDate, interval 14 day) <= curdate()";

        return this.jdbcTemplate.query(checkEndQuery,
                (rs, rowNum) -> new Integer(rs.getInt("challengeIdx")));
    }

    public int startChallenge(int challengeIdx){
        String startChallengeQuery = "update Challenge set proceeding = 1 where challengeIdx = ? and status = 1;";
        System.out.println("dao: start");
        return this.jdbcTemplate.update(startChallengeQuery, challengeIdx);
    }

    public int endChallenge(int challengeIdx){
        String endChallengeQuery = "update Challenge set proceeding = 2 where challengeIdx = ? and status = 1;\n";
        System.out.println("dao: end");
        return this.jdbcTemplate.update(endChallengeQuery, challengeIdx);
    }

    public List<Integer> checkAbolition() {
        String checkAbolitionQuery = "select c.challengeIdx from Challenge c\n" +
                "left join (select m.challengeIdx, count(memberIdx) as member\n" +
                "from Member m\n" +
                "where m.status = 1\n" +
                "    group by challengeIdx) as x on x.challengeIdx = c.challengeIdx\n" +
                "where c.status = 1\n" +
                "and member < 4 and datediff(now(), startDate) = 0;";
        return this.jdbcTemplate.query(checkAbolitionQuery,
                (rs, rowNum) -> new Integer(rs.getInt("challengeIdx")));
    }

    public int abolitionChallenge(int challengeIdx){
        String abolitionChallengeQuery = "update Challenge set status = 0 where challengeIdx = ? and status = 1;\n";
        System.out.println("dao: abolition");
        return this.jdbcTemplate.update(abolitionChallengeQuery, challengeIdx);
    }

    public List<Integer> getMember(int challengeIdx) {
        String getMemberQuery = "select userIdx from Member where challengeIdx = ?;";
        return this.jdbcTemplate.query(getMemberQuery,
                (rs, rowNum) -> new Integer(rs.getInt("userIdx")), challengeIdx);
    }




}
