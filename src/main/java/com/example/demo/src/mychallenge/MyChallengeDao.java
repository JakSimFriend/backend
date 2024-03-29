package com.example.demo.src.mychallenge;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.mychallenge.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                "       c.startDate,\n" +
                "       date_add(c.startDate, interval 14 day ) endDate,\n" +
                "       exists(select userIdx from Certification ce where ce.status = 1 and (DATEDIFF(now(), createAt)) = 0 and challengeIdx = c.challengeIdx and userIdx = ?) certificationStatus,\n" +
                "       datediff(date_add(c.startDate, interval 14 day), curdate()) remainingDay,\n" +
                "       ifnull(case\n" +
                "           when c.cycle = '1' then 14 - nowCount\n" +
                "           when c.cycle = '7' then (count * 2) - nowCount\n" +
                "           when c.cycle = '14' then count - nowCount\n" +
                "           end, 0) as remainingCount,\n" +
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
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
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

    public List<GetMyChallengeApplication> getMyChallengeApplication(int userIdx) {
        String getCountQuery = "select count(challengeIdx) recruitmentCount,\n" +
                "       (select count(ce.challengeIdx) from ChallengeWaiting ce, Challenge c where c.proceeding = 0 and ce.userIdx = ? and c.status = 1 and ce.status = 1 and c.startDate > now() and ce.userIdx != ce.founderIdx and c.challengeIdx = ce.challengeIdx) applyingCount\n" +
                "from Challenge c\n" +
                "where c.status = 1\n" +
                "and startDate > now()\n" +
                "and proceeding = 0\n" +
                "and c.userIdx = ?;";
        String getRecruitQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       concat('D', DATEDIFF(now(), startDate)) as remainingDay,\n" +
                "       ifnull(memberCount, 0) memberCount,\n" +
                "       (select count(w.userIdx) from ChallengeWaiting w where c.challengeIdx = w.challengeIdx and accept = 0 and status = 1) waiting\n" +
                "from Challenge c\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as memberCount\n" +
                "    from Member\n" +
                "    where status = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "where c.userIdx = ?\n" +
                "and c.status = 1\n" +
                "and c.proceeding = 0\n" +
                "and startDate > now()\n" +
                "order by startDate, title;";
        String getApplyingQuery = "select w.accept acceptStatus,\n" +
                "       w.waitingIdx,\n" +
                "       c.challengeIdx,\n" +
                "       c.title,\n" +
                "       concat('D', DATEDIFF(now(), startDate)) as remainingDay,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루에 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일에 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일에 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       ifnull(memberCount, 0) memberCount,\n" +
                "       ifnull((4 - memberCount), 4) needCount\n" +
                "from ChallengeWaiting w, Challenge c\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as memberCount\n" +
                "    from Member m\n" +
                "    where status = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "where c.status = 1\n" +
                "and w.challengeIdx = c.challengeIdx\n" +
                "and w.userIdx = ?\n" +
                "and c.startDate > now()\n" +
                "and proceeding = 0\n" +
                "and w.userIdx != w.founderIdx\n" +
                "and w.status = 1\n" +
                "order by startDate, title;\n";
        String getTagsQuery = "select tag\n" +
                "from ChallengeTag t, Challenge ch\n" +
                "where ch.challengeIdx = t.challengeIdx\n" +
                "and ch.status = 1\n" +
                "and ch.challengeIdx = ? order by tag;";

        return this.jdbcTemplate.query(getCountQuery,
                (rs, rowNum) -> new GetMyChallengeApplication(
                        rs.getInt("recruitmentCount"),
                        this.jdbcTemplate.query(getRecruitQuery, (rs1, rowNum1) -> new GetRecruitment(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                rs1.getString("remainingDay"),
                                rs1.getInt("memberCount"),
                                rs1.getInt("waiting")
                        ), userIdx),
                        rs.getInt("applyingCount"),
                        this.jdbcTemplate.query(getApplyingQuery, (rs1, rowNum1) -> new GetApplying(
                                rs1.getInt("acceptStatus"),
                                rs1.getInt("waitingIdx"),
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                this.jdbcTemplate.query(getTagsQuery, (rs2, rowNum2) -> new String(rs2.getString("tag")), rs1.getInt("challengeIdx")),
                                rs1.getString("remainingDay"),
                                rs1.getString("certification"),
                                rs1.getInt("memberCount"),
                                rs1.getInt("needCount")
                        ), userIdx)
                ), userIdx, userIdx);

    }

    public List<GetRecruitmentInfo> getRecruitmentInfo(int challengeIdx) {
        String getRecruitInfoQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       c.startDate,\n" +
                "       datediff(startDate, curdate()) remainingDay,\n" +
                "       concat(DATE_FORMAT(c.startDate, '%c월 %e일'), '~', DATE_FORMAT(ADDDATE(c.startDate, 14), '%c월 %e일')) as date,\n" +
                "       c.limited,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       date_format(c.deadline, '%H시 %m분 마감') deadline,\n" +
                "       (select count(w.userIdx) from ChallengeWaiting w where c.challengeIdx = w.challengeIdx and accept = 0 and status = 1) waiting,\n" +
                "       (select count(userIdx) from Member m where c.challengeIdx = m.challengeIdx and status = 1) memberCount\n" +
                "from Challenge c\n" +
                "where c.status = 1\n" +
                "and c.proceeding = 0\n" +
                "and c.challengeIdx = ?\n" +
                "and startDate > now();";
        String getWaitingQuery = "select waitingIdx,\n" +
                "       u.userIdx,\n" +
                "       ifnull(concat('달성률 ', floor((select avg(achievement) from AchievementRate a where u.userIdx = a.userIdx)), '%'), '달성률 정보없음') as achievement,\n" +
                "       u.nickName,\n" +
                "       u.profile,\n" +
                "       u.promise\n" +
                "from User u, ChallengeWaiting w\n" +
                "where accept = 0\n" +
                "  and u.userIdx = w.userIdx\n" +
                "  and w.status = 1\n" +
                "and challengeIdx = ?\n" +
                "order by w.createAt;";
        String getMemberQuery = "select u.userIdx,\n" +
                "       u.nickName,\n" +
                "       u.profile,\n" +
                "       u.promise\n" +
                "from User u, Member m\n" +
                "where u.userIdx = m.userIdx\n" +
                "and m.status = 1\n" +
                "and m.challengeIdx = ?\n" +
                "order by nickName;";

        return this.jdbcTemplate.query(getRecruitInfoQuery,
                (rs, rowNum) -> new GetRecruitmentInfo(
                        rs.getInt("challengeIdx"),
                        rs.getString("title"),
                        rs.getDate("startDate"),
                        rs.getInt("remainingDay"),
                        rs.getString("date"),
                        rs.getInt("limited"),
                        rs.getString("certification"),
                        rs.getString("deadline"),
                        rs.getInt("waiting"),
                        this.jdbcTemplate.query(getWaitingQuery, (rs1, rowNum1) -> new GetWaiting(
                                rs1.getInt("waitingIdx"),
                                rs1.getInt("userIdx"),
                                rs1.getString("achievement"),
                                rs1.getString("nickName"),
                                rs1.getString("profile"),
                                rs1.getString("promise")
                        ), challengeIdx),
                        rs.getInt("memberCount"),
                        this.jdbcTemplate.query(getMemberQuery, (rs1, rowNum1) -> new GetMemberInfo(
                                rs1.getInt("userIdx"),
                                rs1.getString("nickName"),
                                rs1.getString("profile"),
                                rs1.getString("promise")
                                ), challengeIdx)
                ), challengeIdx);

    }

    public int checkFounder(int challengeIdx, int userIdx){
        String checkFounderQuery = "select exists(select userIdx from Challenge where challengeIdx = ? and userIdx = ?)";
        return this.jdbcTemplate.queryForObject(checkFounderQuery, int.class, challengeIdx, userIdx);
    }

    public List<GetMyChallengeHistory> getMyChallengeHistory(int userIdx) {
        String getMyChallengeHistoryQuery = "select date_format(startDate, '%Y') year,\n" +
                "       count(c.challengeIdx) count\n" +
                "from Member m, Challenge c\n" +
                "where m.userIdx\n" +
                "and m.challengeIdx = c.challengeIdx\n" +
                "and c.status = 1\n" +
                "and proceeding = 2\n" +
                "and m.userIdx = ?\n" +
                "group by date_format(startDate, '%Y')\n" +
                "order by year desc ;";
        String getHistoryQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       ca.categoryName,\n" +
                "       date_add(startDate, INTERVAL 14 DAY) endDate,\n" +
                "       case\n" +
                "           when c.cycle = '1' then round((nowCount/14) * 100)\n" +
                "           when c.cycle = '7' then round((nowCount/(count * 2)) * 100)\n" +
                "           when c.cycle = '14' then round((nowCount/count) * 100)\n" +
                "               end as percent,\n" +
                "       (exists(select userIdx from ExperienceRate e where e.challengeIdx = c.challengeIdx and m.userIdx = e.userIdx)) rewardStatus\n" +
                "from Category ca, Challenge c, Member m\n" +
                "left join ( select userIdx, challengeIdx, count(certificationIdx) nowCount\n" +
                "    from Certification ce\n" +
                "    where status = 1\n" +
                "    group by challengeIdx, userIdx\n" +
                "    ) as x on x.challengeIdx = m.challengeIdx and m.userIdx = x.userIdx\n" +
                "where ca.categoryIdx = c.categoryIdx\n" +
                "and c.challengeIdx = m.challengeIdx\n" +
                "and c.status = 1\n" +
                "and proceeding = 2\n" +
                "and m.userIdx = ?\n" +
                "and date_format(startDate, '%Y') = ?\n" +
                "order by endDate desc, title;";

        return this.jdbcTemplate.query(getMyChallengeHistoryQuery,
                (rs, rowNum) -> new GetMyChallengeHistory(
                        rs.getInt("year"),
                        rs.getInt("count"),
                        this.jdbcTemplate.query(getHistoryQuery, (rs1, rowNum1) -> new GetHistory(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                rs1.getString("categoryName"),
                                rs1.getDate("endDate"),
                                rs1.getInt("percent"),
                                rs1.getInt("rewardStatus")
                                ), userIdx, rs.getInt("year"))
                ), userIdx);
    }

    public GetDetail getDetail(int challengeIdx) {
        String getChallengeDetailQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       c.content,\n" +
                "       concat(DATE_FORMAT(c.startDate, '%c월 %e일'), ' ~ ', DATE_FORMAT(ADDDATE(c.startDate, 14), '%c월 %e일')) as date,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루에 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일에 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일에 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       date_format(c.deadline, '%H시 %m분 마감') deadline,\n" +
                "       ifnull(accept, 0) accept,\n" +
                "       ifnull(waiting, 0) waiting,\n" +
                "       if(floor(avg(ifnull(ach, 0))) = 0, '달성률 정보 없음', concat('상위 ', floor(avg(ifnull(ach, 0))), '%')) tier\n" +
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
                (rs, rowNum) -> new GetDetail(
                        rs.getInt("challengeIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("date"),
                        rs.getString("certification"),
                        rs.getString("deadline"),
                        rs.getInt("accept"),
                        rs.getInt("waiting"),
                        rs.getString("tier")
                ), challengeIdx
        );
    }


    public GetCalculation getCalculation(int challengeIdx, int userIdx) {
        String getCalculationQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       date_format(now(), '%Y/%m/%d일 %H:%i 기준') nowTime,\n" +
                "       case\n" +
                "           when c.cycle = '1' then round((certificationCount/14) * 100)\n" +
                "           when c.cycle = '7' then round((certificationCount/(c.count * 2)) * 100)\n" +
                "           when c.cycle = '14' then round((certificationCount/c.count) * 100)\n" +
                "               end as achievement,\n" +
                "       (memberCount * 1000) totalCash,\n" +
                "       if(date_add(c.startDate, interval 14 day) < now(),\n" +
                "           floor((\n" +
                "               case\n" +
                "                   when c.cycle = '1' then round((certificationCount/14) * 100)\n" +
                "                   when c.cycle = '7' then round((certificationCount/(c.count * 2)) * 100)\n" +
                "                   when c.cycle = '14' then round((certificationCount/c.count) * 100)\n" +
                "                   end) / 100 * 1000),\n" +
                "           if(\n" +
                "               case when c.cycle = '1' then round((certificationCount/14) * 100)\n" +
                "                   when c.cycle = '7' then round((certificationCount/(c.count * 2)) * 100)\n" +
                "                   when c.cycle = '14' then round((certificationCount/c.count) * 100)\n" +
                "                   end <= 90,\n" +
                "               floor((case\n" +
                "                   when c.cycle = '1' then round((certificationCount/14) * 100)\n" +
                "                   when c.cycle = '7' then round((certificationCount/(c.count * 2)) * 100)\n" +
                "                   when c.cycle = '14' then round((certificationCount/c.count) * 100)\n" +
                "                   end) / 100 * 1000),\n" +
                "               1000)) refundCash,\n" +
                "       (certificationCount * 100) + (floor(friendCount / (memberCount - 1))) * (floor(std((ifnull(ach, 0))))) / 100 experience,\n" +
                "       certificationCount * 100 individual,\n" +
                "       floor(friendCount / (memberCount - 1)) friend,\n" +
                "       floor(std((ifnull(ach, 0)))) bonus,\n" +
                "       exists(select experienceIdx from ExperienceRate where challengeIdx = ? and userIdx = ? and status = 1) rewardStatus,\n" +
                "       if(now() > date_add(startDate, interval 14 day), 1, 0) exitStatus\n" +
                "from Challenge c\n" +
                "left join(select ce.userIdx u, ce.challengeIdx, ce.certificationIdx cc, count(certificationIdx) certificationCount\n" +
                "    from Certification ce\n" +
                "    where ce.status = 1\n" +
                "    group by challengeIdx, userIdx having ce.userIdx = ?\n" +
                ") as x on x.challengeIdx = c.challengeIdx\n" +
                "left join (\n" +
                "    select m.challengeIdx, count(m.userIdx) memberCount\n" +
                "    from Member m where m.status = 1 and m.challengeIdx = ?\n" +
                "    ) as y on y.challengeIdx = c.challengeIdx\n" +
                "left join (\n" +
                "    select m.challengeIdx, 100 * ifnull(count(certificationIdx), 0) friendCount\n" +
                "    from Member m, Certification ce\n" +
                "    where m.challengeIdx = ce.challengeIdx and m.userIdx = ce.userIdx and m.userIdx not in(?) and m.challengeIdx = ?\n" +
                "    group by ce.challengeIdx, ce.userIdx\n" +
                "    ) as w on w.challengeIdx = c.challengeIdx,\n" +
                "Member m\n" +
                "left join (\n" +
                "    select a.userIdx, case\n" +
                "                when avg(achievement) >= 0 && avg(achievement) < 10 then 0\n" +
                "                when avg(achievement) >= 10 && avg(achievement) < 30 then 20\n" +
                "                when avg(achievement) >= 30 && avg(achievement) < 50 then 40\n" +
                "                when avg(achievement) >= 50 && avg(achievement) < 70 then 60\n" +
                "                when avg(achievement) >= 70 && avg(achievement) < 90 then 80\n" +
                "                when avg(achievement) >= 90 && avg(achievement) < 100 then 100\n" +
                "            end as ach\n" +
                "    from AchievementRate a\n" +
                "    group by a.userIdx\n" +
                "    ) as z on z.userIdx = m.userIdx and m.challengeIdx = ? and m.status = 1\n" +
                "where c.challengeIdx = ?\n" +
                "and c.challengeIdx = m.challengeIdx\n" +
                "and c.status = 1;";

        return this.jdbcTemplate.queryForObject(getCalculationQuery,
                (rs, rowNum) -> new GetCalculation(
                        rs.getInt("challengeIdx"),
                        rs.getString("title"),
                        rs.getString("nowTime"),
                        rs.getInt("achievement"),
                        rs.getInt("totalCash"),
                        rs.getInt("refundCash"),
                        rs.getInt("experience"),
                        rs.getInt("individual"),
                        rs.getInt("friend"),
                        rs.getInt("bonus"),
                        rs.getInt("rewardStatus"),
                        rs.getInt("exitStatus")
                        ),challengeIdx, userIdx, userIdx, challengeIdx, userIdx, challengeIdx, challengeIdx, challengeIdx
        );
    }

    @Transactional
    public int postReward(PostReward postReward) throws Exception{
        int challengeIdx = postReward.getChallengeIdx();
        int userIdx = postReward.getUserIdx();

        String point = "insert into Point(point, userIdx, categoryIdx) values (?, ?, 4);\n";
        Object[] pointParams = new Object[]{postReward.getPoint(), userIdx};
        this.jdbcTemplate.update(point, pointParams);

        String achievement = "insert into AchievementRate(achievement, userIdx, challengeIdx) values (?, ?, ?);\n";
        Object[] achievementParams = new Object[]{postReward.getAchievement(), userIdx, challengeIdx};
        this.jdbcTemplate.update(achievement, achievementParams);

        String experience = "insert into ExperienceRate(experience, userIdx, challengeIdx) values (?, ?, ?);\n";
        Object[] experienceParams = new Object[]{postReward.getExperience(), userIdx, challengeIdx};
        this.jdbcTemplate.update(experience, experienceParams);

        return 1;
    }

    public int getEnd(int challengeIdx) {
        String getEndQuery = "select if(date_add(startDate, interval 14 day ) < now(), 1, 0) status from Challenge where challengeIdx = ? and status = 1;\n";
        return this.jdbcTemplate.queryForObject(getEndQuery, (rs, rowNum) -> new Integer(rs.getInt("status")), challengeIdx);
    }

    public int checkAchievement(int challengeIdx, int userIdx) {
        String checkQuery = "select exists(select achievementIdx from AchievementRate where challengeIdx = ? and userIdx = ? and status = 1)\n";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, challengeIdx, userIdx);
    }

    public int checkExperience(int challengeIdx, int userIdx) {
        String checkQuery = "select exists(select experienceIdx from ExperienceRate where challengeIdx = ? and userIdx = ? and status = 1)\n";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, challengeIdx, userIdx);
    }

    public int createAlert(String alert, String image, int certificationIdx, int userIdx, int challengeIdx) {
        String createReactionQuery = "insert into ChallengeAlert(alert, image, certificationIdx, userIdx, challengeIdx) values (?, ?, ?, ?, ?);\n";
        Object[] createChallengeParams = new Object[]{alert, image, certificationIdx, userIdx, challengeIdx};
        this.jdbcTemplate.update(createReactionQuery, createChallengeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public String getToken(int userIdx) {
        String getTokenQuery = "select token from UserDevice where userIdx = ? and status = 1;\n";
        String token = this.jdbcTemplate.queryForObject(getTokenQuery, (rs, rowNum) -> new String(rs.getString("token")), userIdx);
        return token;
    }

    public String getTitle(int challengeIdx) {
        String getTitleQuery = "select title from Challenge where challengeIdx = ? and status = 1;\n";
        return this.jdbcTemplate.queryForObject(getTitleQuery, (rs, rowNum) -> new String(rs.getString("title")), challengeIdx);
    }


    public List<GetToken> getMember(int challengeIdx, int userIdx) {
        String getMemberQuery = "select m.userIdx, ud.token from Member m, UserDevice ud where m.userIdx = ud.userIdx and m.challengeIdx = ? and m.userIdx != ?;";

        return this.jdbcTemplate.query(getMemberQuery,
                (rs, rowNum) -> new GetToken(rs.getInt("userIdx"),
                        rs.getString("token")), challengeIdx, userIdx);
    }

}

