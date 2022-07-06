package com.example.demo.src.mychallenge;

import com.example.demo.src.challenge.model.PostChallenge;
import com.example.demo.src.mychallenge.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MyChallengeDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetMyChallengeProgress> getMyChallengeProgress(int userIdx){
        String getCountQuery = "select count(case when proceeding = 1 then 1 end) as proceedingCount,\n" +
                "       count(case when proceeding = 0 then 1 end) as beforeCount\n" +
                "from Member m, Challenge c\n" +
                "where m.userIdx = ?\n" +
                "and c.status = 1\n" +
                "and m.challengeIdx = c.challengeIdx;";
        String getTitleQuery = "select c.challengeIdx, c.title, exists(select userIdx from Certification ce where ce.status and (DATEDIFF(now(), createAt)) = 0 and ce.challengeIdx = m.challengeIdx) as certification\n" +
                "from Challenge c, Member m\n" +
                "where c.challengeIdx = m.challengeIdx\n" +
                "and c.status = 1\n" +
                "and proceeding = 1\n" +
                "and m.userIdx = ?;";
        String getMemberQuery = "select u.userIdx,\n" +
                "       u.profile,\n" +
                "       u.nickName,\n" +
                "       case\n" +
                "           when c.cycle = '1' then round((nowCount/14) * 100)\n" +
                "           when c.cycle = '7' then round((nowCount/(count * 2)) * 100)\n" +
                "           when c.cycle = '14' then round((nowCount/count) * 100)\n" +
                "               end as percent\n" +
                "from User u, Challenge c, Member m\n" +
                "left join ( select userIdx, count(certificationIdx) nowCount\n" +
                "    from Certification\n" +
                "    where status = 1\n" +
                "    ) as x on x.userIdx = m.userIdx\n" +
                "where u.status = 1\n" +
                "and u.userIdx = m.userIdx\n" +
                "and c.challengeIdx = m.challengeIdx\n" +
                "and c.status = 1\n" +
                "and m.challengeIdx = ?\n" +
                "order by percent desc;";
        String getBeforeQuery = "select ch.challengeIdx,\n" +
                "       c.categoryName,\n" +
                "       ch.title,\n" +
                "       concat('D', DATEDIFF(now(), startDate)) as remainingDay,\n" +
                "       case\n" +
                "           when ch.cycle = '1' then concat('하루에 ', ch.count, '회')\n" +
                "           when ch.cycle = '7' then concat('1주일에 ', ch.count, '회')\n" +
                "           when ch.cycle = '14' then concat('2주일에 ', ch.count, '회')\n" +
                "               end as certification,\n" +
                "       accept\n" +
                "from Member m, Category c, Challenge ch\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as accept\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on ch.challengeIdx = x.challengeIdx\n" +
                "where c.categoryIdx = ch.categoryIdx\n" +
                "and ch.challengeIdx = m.challengeIdx\n" +
                "and ch.status = 1\n" +
                "and ch.startDate > now()\n" +
                "and proceeding = 0\n" +
                "and m.userIdx = ?\n" +
                "order by remainingDay;";
        String getTagsQuery = "select tag\n" +
                "from ChallengeTag t, Challenge ch\n" +
                "where ch.challengeIdx = t.challengeIdx\n" +
                "and ch.status = 1\n" +
                "and ch.challengeIdx = ?;";

        return this.jdbcTemplate.query(getCountQuery,
                (rs, rowNum) -> new GetMyChallengeProgress(
                        rs.getInt("proceedingCount"),
                        this.jdbcTemplate.query(getTitleQuery, (rs1, rowNum1) -> new GetProceeding(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                rs1.getInt("certification"),
                                this.jdbcTemplate.query(getMemberQuery, (rs2, rowNum2) -> new GetMember(
                                        rs2.getInt("userIdx"),
                                        rs2.getString("profile"),
                                        rs2.getString("nickName"),
                                        rs2.getInt("percent")
                                ), rs1.getInt("challengeIdx"))
                        ), userIdx),
                        rs.getInt("beforeCount"),
                        this.jdbcTemplate.query(getBeforeQuery, (rs1, rowNum1) -> new GetBefore(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("categoryName"),
                                rs1.getString("title"),
                                rs1.getString("remainingDay"),
                                rs1.getString("certification"),
                                rs1.getInt("accept"),
                                this.jdbcTemplate.query(getTagsQuery, (rs2, rowNum2) -> new String(rs2.getString("tag")), rs1.getInt("challengeIdx"))
                        ), userIdx)
                ), userIdx);
    }

    public int checkChallenge(int challengeIdx) {
        String checkChallengeQuery = "select exists(select challengeIdx from Challenge where challengeIdx = ? and status = 1)";
        int checkChallengeParams = challengeIdx;
        return this.jdbcTemplate.queryForObject(checkChallengeQuery, int.class, checkChallengeParams);
    }

    public int checkProceeding(int challengeIdx) {
        String checkProceedingQuery = "select exists(select challengeIdx from Challenge where challengeIdx = ? and status = 1 and proceeding = 1)";
        int checkChallengeParams = challengeIdx;
        return this.jdbcTemplate.queryForObject(checkProceedingQuery, int.class, checkChallengeParams);
    }

    public int checkMember(int challengeIdx,  int userIdx) {
        String checkMemberQuery = "select exists(select userIdx from Member m where m.status = 1 and challengeIdx = ? and userIdx = ?);\n";
        return this.jdbcTemplate.queryForObject(checkMemberQuery, int.class, challengeIdx, userIdx);
    }

    public int checkCertification(int challengeIdx,  int userIdx) {
        String checkCertificationQuery = "select exists(select userIdx from Certification ce where ce.status = 1 and (DATEDIFF(now(), createAt)) = 0 and challengeIdx = ? and userIdx = ?);\n";
        return this.jdbcTemplate.queryForObject(checkCertificationQuery, int.class, challengeIdx, userIdx);
    }

    public int checkDeadline(int challengeIdx) {
        String checkDeadlineQuery = "select\n" +
                "case\n" +
                "when(timediff(deadline, curtime()) < 0) then 1\n" +
                "when(timediff(deadline, curtime()) > 1) then 0\n" +
                "end as deadlineCheck\n" +
                "from Challenge\n" +
                "where challengeIdx = ?;";
        int checkChallengeParams = challengeIdx;
        return this.jdbcTemplate.queryForObject(checkDeadlineQuery, int.class, checkChallengeParams);
    }

    public int certification(int challengeIdx, int userIdx, String imageUrl) {
        String certificationQuery = "insert into Certification(challengeIdx, userIdx, certificationPhoto) values (?, ?, ?);\n";
        Object[] certificationParams = new Object[]{challengeIdx, userIdx, imageUrl};
        this.jdbcTemplate.update(certificationQuery, certificationParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public String getNickName(int userIdx){
        String getNickNameQuery = "select nickName from User where userIdx = ?;";

        return this.jdbcTemplate.queryForObject(getNickNameQuery, (rs, rowNum) -> new String(rs.getString("nickName")), userIdx);
    }

    public int getPercent(int challengeIdx, int userIdx){
        String getPercentQuery = "select case\n" +
                "           when c.cycle = '1' then round((nowCount/14) * 100)\n" +
                "           when c.cycle = '7' then round((nowCount/(count * 2)) * 100)\n" +
                "           when c.cycle = '14' then round((nowCount/count) * 100)\n" +
                "               end as percent\n" +
                "from Challenge c, Member m\n" +
                "left join ( select userIdx, count(certificationIdx) nowCount\n" +
                "    from Certification\n" +
                "    where status = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on x.userIdx = m.userIdx\n" +
                "where c.status = 1\n" +
                "and m.status = 1\n" +
                "and c.challengeIdx = m.challengeIdx\n" +
                "and m.challengeIdx = ?\n" +
                "and m.userIdx = ?";

        return this.jdbcTemplate.queryForObject(getPercentQuery, (rs, rowNum) -> new Integer(rs.getInt("percent")), challengeIdx, userIdx);
    }

}
