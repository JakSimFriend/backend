package com.example.demo.src.notification;

import com.example.demo.src.notification.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class NotificationDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetHomeNotification> getHomeNotification(int userIdx) {
        String getHomeNotificationQuery = "select date_format(createAt, '%c월 %e일') date from Alert where userIdx = ? and status = 1 group by date_format(createAt, '%c %e') order by createAt desc;\n";
        String getNotificationQuery = "select alertIdx,\n" +
                "       image,\n" +
                "       alert,\n" +
                "       date_format(createAt, '%p %h:%i') time\n" +
                "from Alert\n" +
                "where userIdx = ?\n" +
                "and status = 1\n" +
                "and date_format(createAt, '%c월 %e일') = ?\n" +
                "order by createAt desc;";
        int getNotificationParams = userIdx;

        return this.jdbcTemplate.query(getHomeNotificationQuery,
                (rs, rowNum) -> new GetHomeNotification(
                        rs.getString("date"),
                        this.jdbcTemplate.query(getNotificationQuery, (rs1, rowNum1) -> new GetNotification(
                                rs1.getInt("alertIdx"),
                                rs1.getString("image"),
                                rs1.getString("alert"),
                                rs1.getString("time")
                        ), getNotificationParams, rs.getString("date"))
                ), getNotificationParams);
    }
}
