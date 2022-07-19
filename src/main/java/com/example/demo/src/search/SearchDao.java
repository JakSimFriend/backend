package com.example.demo.src.search;

import com.example.demo.src.search.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SearchDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetChallengeSearch> getChallengeSearch(){
        String getTestQuery = "select now()";
        String getRecruitQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       ca.categoryName,\n" +
                "       DATE_FORMAT(c.startDate, '%c월 %e일') as startDate,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루에 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일에 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일에 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       accept\n" +
                "from Category ca, Challenge c\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as accept\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "where c.categoryIdx = ca.categoryIdx\n" +
                "and c.status = 1\n" +
                "and startDate > now()\n" +
                "and accept < 6\n" +
                "order by startDate;";
        String getTagsQuery = "select tag from ChallengeTag t, Challenge ch where ch.challengeIdx = t.challengeIdx and ch.status = 1 and ch.challengeIdx = ? order by tag";
        String getEndQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       ca.categoryName,\n" +
                "       DATE_FORMAT(c.startDate, '종료') as endStatus,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루에 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일에 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일에 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       accept\n" +
                "from Category ca, Challenge c\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as accept\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "where c.categoryIdx = ca.categoryIdx\n" +
                "and c.status = 1\n" +
                "and (startDate < now() or accept = 6)\n" +
                "order by startDate desc ;\n";
        return this.jdbcTemplate.query(getTestQuery,
                (rs, rowNum) -> new GetChallengeSearch(
                        this.jdbcTemplate.query(getRecruitQuery, (rs1, rowNum1) -> new GetRecruitment(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                rs1.getString("categoryName"),
                                rs1.getString("startDate"),
                                rs1.getString("certification"),
                                rs1.getInt("accept"),
                                this.jdbcTemplate.query(getTagsQuery, (rs2, rowNum2) -> new String(rs2.getString("tag")), rs1.getInt("challengeIdx"))
                        )),
                        this.jdbcTemplate.query(getEndQuery, (rs1, rowNum1) -> new GetEnd(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                rs1.getString("categoryName"),
                                rs1.getString("endStatus"),
                                rs1.getString("certification"),
                                rs1.getInt("accept"),
                                this.jdbcTemplate.query(getTagsQuery, (rs2, rowNum2) -> new String(rs2.getString("tag")), rs1.getInt("challengeIdx"))
                        ))
                ));
    }

    public List<GetChallengeSearch> getCategorySearch(int categoryIdx){
        String getTestQuery = "select now()";
        String getRecruitQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       ca.categoryName,\n" +
                "       DATE_FORMAT(c.startDate, '%c월 %e일') as startDate,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루에 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일에 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일에 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       accept\n" +
                "from Category ca, Challenge c\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as accept\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "where c.categoryIdx = ca.categoryIdx\n" +
                "and c.status = 1\n" +
                "and startDate > now()\n" +
                "and accept < 6\n" +
                "and c.categoryIdx = ?\n" +
                "order by startDate;";
        String getTagsQuery = "select tag from ChallengeTag t, Challenge ch where ch.challengeIdx = t.challengeIdx and ch.status = 1 and ch.challengeIdx = ? order by tag;";
        String getEndQuery = "select c.challengeIdx,\n" +
                "       c.title,\n" +
                "       ca.categoryName,\n" +
                "       DATE_FORMAT(c.startDate, '종료') as endStatus,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루에 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일에 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일에 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       accept\n" +
                "from Category ca, Challenge c\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as accept\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "where c.categoryIdx = ca.categoryIdx\n" +
                "and c.status = 1\n" +
                "and (startDate < now() or accept = 6)\n" +
                "and c.categoryIdx = ?\n" +
                "order by startDate desc ;";
        return this.jdbcTemplate.query(getTestQuery,
                (rs, rowNum) -> new GetChallengeSearch(
                        this.jdbcTemplate.query(getRecruitQuery, (rs1, rowNum1) -> new GetRecruitment(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                rs1.getString("categoryName"),
                                rs1.getString("startDate"),
                                rs1.getString("certification"),
                                rs1.getInt("accept"),
                                this.jdbcTemplate.query(getTagsQuery, (rs2, rowNum2) -> new String(rs2.getString("tag")), rs1.getInt("challengeIdx"))
                        ), categoryIdx),
                        this.jdbcTemplate.query(getEndQuery, (rs1, rowNum1) -> new GetEnd(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                rs1.getString("categoryName"),
                                rs1.getString("endStatus"),
                                rs1.getString("certification"),
                                rs1.getInt("accept"),
                                this.jdbcTemplate.query(getTagsQuery, (rs2, rowNum2) -> new String(rs2.getString("tag")), rs1.getInt("challengeIdx"))
                        ), categoryIdx)
                ));
    }

    public List<GetChallengeSearch> getSearchKeyword(String keyword){
        String getTestQuery = "select now()";
        String getRecruitQuery = "select distinct c.challengeIdx,\n" +
                "       c.title,\n" +
                "       ca.categoryName,\n" +
                "       DATE_FORMAT(c.startDate, '%c월 %e일') as startDate,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루에 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일에 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일에 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       accept\n" +
                "from Category ca, ChallengeTag ct, Challenge c\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as accept\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "where c.categoryIdx = ca.categoryIdx\n" +
                "and c.status = 1\n" +
                "and startDate > now()\n" +
                "and accept < 6\n" +
                "and ((ct.tag like concat('%', ?, '%') and ct.challengeIdx = c.challengeIdx ) or c.title like concat('%', ?, '%'))\n" +
                "order by startDate;";
        String getTagsQuery = "select tag from ChallengeTag t, Challenge ch where ch.challengeIdx = t.challengeIdx and ch.status = 1 and ch.challengeIdx = ? order by tag;\n";
        String getEndQuery = "select distinct c.challengeIdx,\n" +
                "       c.title,\n" +
                "       ca.categoryName,\n" +
                "       DATE_FORMAT(c.startDate, '종료') as endStatus,\n" +
                "       case\n" +
                "           when c.cycle = '1' then concat('하루에 ', c.count, '회')\n" +
                "           when c.cycle = '7' then concat('1주일에 ', c.count, '회')\n" +
                "           when c.cycle = '14' then concat('2주일에 ', c.count, '회')\n" +
                "               end as certification,\n" +
                "       accept\n" +
                "from Category ca, ChallengeTag ct, Challenge c\n" +
                "left join (\n" +
                "    select challengeIdx, count(userIdx) as accept\n" +
                "    from ChallengeWaiting\n" +
                "    where status = 1 and accept = 1\n" +
                "    group by challengeIdx\n" +
                "    ) as x on c.challengeIdx = x.challengeIdx\n" +
                "where c.categoryIdx = ca.categoryIdx\n" +
                "and c.status = 1\n" +
                "and (startDate < now() or accept = 6)\n" +
                "and ((ct.tag like concat('%', ?, '%') and ct.challengeIdx = c.challengeIdx ) or c.title like concat('%', ?, '%'))\n" +
                "order by startDate desc;";
        return this.jdbcTemplate.query(getTestQuery,
                (rs, rowNum) -> new GetChallengeSearch(
                        this.jdbcTemplate.query(getRecruitQuery, (rs1, rowNum1) -> new GetRecruitment(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                rs1.getString("categoryName"),
                                rs1.getString("startDate"),
                                rs1.getString("certification"),
                                rs1.getInt("accept"),
                                this.jdbcTemplate.query(getTagsQuery, (rs2, rowNum2) -> new String(rs2.getString("tag")), rs1.getInt("challengeIdx"))
                        ), keyword, keyword),
                        this.jdbcTemplate.query(getEndQuery, (rs1, rowNum1) -> new GetEnd(
                                rs1.getInt("challengeIdx"),
                                rs1.getString("title"),
                                rs1.getString("categoryName"),
                                rs1.getString("endStatus"),
                                rs1.getString("certification"),
                                rs1.getInt("accept"),
                                this.jdbcTemplate.query(getTagsQuery, (rs2, rowNum2) -> new String(rs2.getString("tag")), rs1.getInt("challengeIdx"))
                        ), keyword, keyword)
                ));
    }

}
