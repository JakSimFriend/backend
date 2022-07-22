package com.example.demo.src.status;

import com.example.demo.src.status.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StatusDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetStatus> getStatus(int userIdx) {
        String getStatusQuery = "select userIdx, floor(avg(achievement)) achievement,\n" +
                "       (select sum(experience) from ExperienceRate where userIdx = ? and status  = 1) experience\n" +
                "from AchievementRate where userIdx = ? and status = 1;";
        String getStatusCategoryQuery = "select ca.categoryIdx,\n" +
                "       ca.categoryPhoto,\n" +
                "       ca.categoryName,\n" +
                "       sum(experience) categoryExp\n" +
                "from Challenge c, ExperienceRate e, Category ca\n" +
                "where e.userIdx = ?\n" +
                "and c.categoryIdx = ca.categoryIdx\n" +
                "and e.challengeIdx = c.challengeIdx\n" +
                "and e.status = 1\n" +
                "group by c.categoryIdx order by c.categoryIdx;";

        return this.jdbcTemplate.query(getStatusQuery,
                (rs, rowNum) -> new GetStatus(
                        rs.getInt("userIdx"),
                        rs.getInt("achievement"),
                        rs.getInt("experience"),
                        this.jdbcTemplate.query(getStatusCategoryQuery, (rs1, rowNum1) -> new GetStatusCategory(
                                rs1.getInt("categoryIdx"),
                                rs1.getString("categoryPhoto"),
                                rs1.getString("categoryName"),
                                rs1.getInt("categoryExp")
                        ), userIdx)
                ), userIdx, userIdx);
    }


}
