package com.example.demo.src.mychallenge;

import com.example.demo.src.mychallenge.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class MyChallengeDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetMyChallengeProgress> getMyChallengeProgress(int userIdx) {
        String getCountQuery = "select count(case when startDate < now() and proceeding = 1 then 1 end) as proceedingCount,\n" +
                "       count(case when startDate > now() and acceptCount > 3 then 1 end) as beforeCount\n" +
                "from Member m, Challenge c\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as acceptCount\n" +
                "    from Member m\n" +
                "    where status = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "where m.userIdx = ?\n" +
                "and c.status = 1\n" +
                "and m.challengeIdx = c.challengeIdx;";
        String getTitleQuery = "select c.challengeIdx, c.title, exists(select userIdx from Certification ce where ce.status and (DATEDIFF(now(), createAt)) = 0 and ce.challengeIdx = m.challengeIdx) as certification\n" +
                "from Challenge c, Member m\n" +
                "where c.challengeIdx = m.challengeIdx\n" +
                "and c.status = 1\n" +
                "and proceeding = 1\n" +
                "and startDate < now()\n" +
                "and m.userIdx = ?\n" +
                "order by startDate, title;";
        String getMemberQuery = "select u.userIdx,\n" +
                "       u.profile,\n" +
                "       u.nickName,\n" +
                "       case\n" +
                "           when c.cycle = '1' then round((nowCount/14) * 100)\n" +
                "           when c.cycle = '7' then round((nowCount/(count * 2)) * 100)\n" +
                "           when c.cycle = '14' then round((nowCount/count) * 100)\n" +
                "               end as percent\n" +
                "from User u, Challenge c,  Member m\n" +
                "left join ( select userIdx, challengeIdx, count(certificationIdx) nowCount\n" +
                "    from Certification\n" +
                "    where challengeIdx = ? and status = 1\n" +
                "    group by userIdx\n" +
                "    ) as x on x.userIdx = m.userIdx\n" +
                "where c.status = 1\n" +
                "and c.challengeIdx = m.challengeIdx\n" +
                "and u.userIdx = m.userIdx\n" +
                "and m.challengeIdx = ?\n" +
                "order by percent desc, nickName;";
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
                "and accept > 3\n" +
                "and m.userIdx = ?\n" +
                "order by remainingDay, title;";
        String getTagsQuery = "select tag\n" +
                "from ChallengeTag t, Challenge ch\n" +
                "where ch.challengeIdx = t.challengeIdx\n" +
                "and ch.status = 1\n" +
                "and ch.challengeIdx = ? order by tag;";

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
                                ), rs1.getInt("challengeIdx"), rs1.getInt("challengeIdx"))
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

    public int checkMember(int challengeIdx, int userIdx) {
        String checkMemberQuery = "select exists(select userIdx from Member m where m.status = 1 and challengeIdx = ? and userIdx = ?);\n";
        return this.jdbcTemplate.queryForObject(checkMemberQuery, int.class, challengeIdx, userIdx);
    }

    public int checkCertification(int challengeIdx, int userIdx) {
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

    public String getNickName(int userIdx) {
        String getNickNameQuery = "select nickName from User where userIdx = ?;";

        return this.jdbcTemplate.queryForObject(getNickNameQuery, (rs, rowNum) -> new String(rs.getString("nickName")), userIdx);
    }

    public int getPercent(int challengeIdx, int userIdx) {
        String getPercentQuery = "select case\n" +
                "           when c.cycle = '1' then round((nowCount/14) * 100)\n" +
                "           when c.cycle = '7' then round((nowCount/(count * 2)) * 100)\n" +
                "           when c.cycle = '14' then round((nowCount/count) * 100)\n" +
                "           end as percent\n" +
                "from Challenge c\n" +
                " inner join ( select challengeIdx, userIdx, count(certificationIdx) nowCount\n" +
                "    from Certification ce\n" +
                "    where ce.status = 1\n" +
                "    and ce.challengeIdx = ?\n" +
                "    and ce.userIdx = ?\n" +
                "    group by challengeIdx\n" +
                "    ) as x on x.challengeIdx = c.challengeIdx\n" +
                "where c.status = 1;";

        return this.jdbcTemplate.queryForObject(getPercentQuery, (rs, rowNum) -> new Integer(rs.getInt("percent")), challengeIdx, userIdx);
    }

    public int existCertification(int challengeIdx, int userIdx) {
        String checkCertificationQuery = "select exists(select userIdx from Certification ce where ce.status = 1 and challengeIdx = ? and userIdx = ?);\n";
        return this.jdbcTemplate.queryForObject(checkCertificationQuery, int.class, challengeIdx, userIdx);
    }

    public List<GetProgressInfo> getProgressInfo(int challengeIdx, int userIdx) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String getProgressInfoQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       exists(select userIdx from Certification ce where ce.status = 1 and (DATEDIFF(now(), createAt)) = 0 and challengeIdx = c.challengeIdx and userIdx = ?) certificationStatus,\n" +
                "       datediff(date_add(c.startDate, interval 14 day), curdate()) remainingDay,\n" +
                "       case\n" +
                "           when c.cycle = '1' then 14 - nowCount\n" +
                "           when c.cycle = '7' then (count * 2) - nowCount\n" +
                "           when c.cycle = '14' then count - nowCount\n" +
                "           end as remainingCount,\n" +
                "       concat(DATE_FORMAT(c.startDate, '%c월 %e일'), ' ~ ', DATE_FORMAT(ADDDATE(c.startDate, 14), '%c월 %e일')) as date,\n" +
                "       c.limited,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일 ', c.count, '회')\n" +
                "               end as certificationInfo,\n" +
                "       date_format(c.deadline, '%H시 %m분 마감') deadline,\n" +
                "       memberCount\n" +
                "from Challenge c\n" +
                "left join (select challengeIdx, userIdx, count(certificationIdx) nowCount\n" +
                "    from Certification ce\n" +
                "    where ce.status = 1\n" +
                "    and ce.challengeIdx = ?\n" +
                "    and ce.userIdx = ?\n" +
                "    group by challengeIdx\n" +
                "    ) as x on x.challengeIdx = c.challengeIdx\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as memberCount\n" +
                "    from Member\n" +
                "    where status = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as y on c.challengeIdx = y.challengeIdx\n" +
                "where c.challengeIdx = ?\n" +
                "and c.status = 1;";
        String getDateListQuery = "select DATE_FORMAT(createAt, '%Y-%m-%d') certificationDate from Certification where challengeIdx = ? and userIdx = ? and status = 1;";
        String getMemberQuery = "select u.userIdx,\n" +
                "       u.profile,\n" +
                "       u.nickName,\n" +
                "       u.promise,\n" +
                "       case\n" +
                "           when c.cycle = '1' then round((nowCount/14) * 100)\n" +
                "           when c.cycle = '7' then round((nowCount/(count * 2)) * 100)\n" +
                "           when c.cycle = '14' then round((nowCount/count) * 100)\n" +
                "               end as percent,\n" +
                "       ifnull((select\n" +
                "               case\n" +
                "                   when datediff(curdate(), ce.createAt) = 0 then '오늘 인증' else concat((datediff(curdate(), ce.createAt)), '일 전 인증') end\n" +
                "       from Certification ce where ce.userIdx = u.userIdx order by createAt desc limit 1), '인증 내역 없음') as certification\n" +
                "from User u, Challenge c,  Member m\n" +
                "left join ( select userIdx, challengeIdx, count(certificationIdx) nowCount\n" +
                "    from Certification\n" +
                "    where challengeIdx = ? and status = 1\n" +
                "    group by userIdx\n" +
                "    ) as x on x.userIdx = m.userIdx\n" +
                "where c.status = 1\n" +
                "and c.challengeIdx = m.challengeIdx\n" +
                "and u.userIdx = m.userIdx\n" +
                "and m.challengeIdx = ?\n" +
                "order by percent desc, nickName;\n";

        return this.jdbcTemplate.query(getProgressInfoQuery,
                (rs, rowNum) -> new GetProgressInfo(
                        rs.getInt("challengeIdx"),
                        rs.getString("title"),
                        this.jdbcTemplate.query(getDateListQuery, (rs1, rowNum1) -> new GetDateList(rs1.getDate("certificationDate")), challengeIdx, userIdx),
                        rs.getInt("certificationStatus"),
                        rs.getInt("remainingDay"),
                        rs.getInt("remainingCount"),
                        rs.getString("date"),
                        rs.getInt("limited"),
                        rs.getString("certificationInfo"),
                        rs.getString("deadline"),
                        rs.getInt("memberCount"),
                        this.jdbcTemplate.query(getMemberQuery, (rs1, rowNum1) -> new GetProgressMember(
                                rs1.getInt("userIdx"),
                                rs1.getString("profile"),
                                rs1.getString("nickName"),
                                rs1.getString("promise"),
                                rs1.getInt("percent"),
                                rs1.getString("certification")
                        ), challengeIdx, challengeIdx)
                ), userIdx, challengeIdx, userIdx, challengeIdx);
    }

    public List<GetBeforeInfo> getBeforeInfo(int challengeIdx) {
        String getBeforeInfoQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       c.startDate,\n" +
                "       concat(datediff(startDate, now()), '일 후에 시작해요') remainingDay,\n" +
                "       concat(DATE_FORMAT(c.startDate, '%c월 %e일'), '~', DATE_FORMAT(ADDDATE(c.startDate, 14), '%c월 %e일')) as date,\n" +
                "       c.limited,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       date_format(c.deadline, '%H시 %m분 마감') deadline,\n" +
                "       (select count(userIdx) from Member m where c.challengeIdx = m.challengeIdx and status = 1) memberCount\n" +
                "from Challenge c\n" +
                "where c.status = 1\n" +
                "and c.proceeding = 0\n" +
                "and c.challengeIdx = ?\n" +
                "and startDate > now();";
        String getMemberInfoQuery = "select u.userIdx,\n" +
                "       u.nickName,\n" +
                "       u.profile,\n" +
                "       u.promise\n" +
                "from User u, Member m\n" +
                "where u.userIdx = m.userIdx\n" +
                "and m.status = 1\n" +
                "and m.challengeIdx = ?\n" +
                "order by nickName;";

        return this.jdbcTemplate.query(getBeforeInfoQuery,
                (rs, rowNum) -> new GetBeforeInfo(
                        rs.getInt("challengeIdx"),
                        rs.getString("title"),
                        rs.getDate("startDate"),
                        rs.getString("remainingDay"),
                        rs.getString("date"),
                        rs.getInt("limited"),
                        rs.getString("certification"),
                        rs.getString("deadline"),
                        rs.getInt("memberCount"),
                        this.jdbcTemplate.query(getMemberInfoQuery, (rs1, rowNum1) -> new GetMemberInfo(
                                rs1.getInt("userIdx"),
                                rs1.getString("nickName"),
                                rs1.getString("profile"),
                                rs1.getString("promise")
                                ), challengeIdx)
                ), challengeIdx);
    }

}
