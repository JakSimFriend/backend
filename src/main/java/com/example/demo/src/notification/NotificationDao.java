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

    public int deleteNotification(int alertIdx, int userIdx){
        String deleteNotificationQuery = "update Alert set status = 0 where alertIdx = ? and userIdx = ?; ";
        Object[] deleteNotificationParams = new Object[]{alertIdx, userIdx};
        return this.jdbcTemplate.update(deleteNotificationQuery, deleteNotificationParams);
    }

    public int checkNotification(int alertIdx) {
        String checkNotificationQuery = "select exists(select alertIdx from Alert where alertIdx = ? and status = 1);";
        int checkNotificationParams = alertIdx;
        return this.jdbcTemplate.queryForObject(checkNotificationQuery, int.class, checkNotificationParams);
    }

    public int checkAlert(int alertIdx) {
        String checkNotificationQuery = "select exists(select alertIdx from ChallengeAlert where alertIdx = ? and status = 1);";
        int checkNotificationParams = alertIdx;
        return this.jdbcTemplate.queryForObject(checkNotificationQuery, int.class, checkNotificationParams);
    }

    public int checkUser(int alertIdx, int userIdx) {
        String checkNotificationQuery = "select exists(select alertIdx from Alert where alertIdx = ? and userIdx = ? and status = 1);";
        Object[] checkNotificationParams = new Object[]{alertIdx, userIdx};
        return this.jdbcTemplate.queryForObject(checkNotificationQuery, int.class, checkNotificationParams);
    }

    public int deleteNotificationAll(int userIdx){
        String deleteNotificationQuery = "update Alert set status = 0 where userIdx = ?; ";
        Object[] deleteNotificationParams = new Object[]{userIdx};
        return this.jdbcTemplate.update(deleteNotificationQuery, deleteNotificationParams);
    }

    public List<GetChallengeAlert> getChallengeAlert(int challengeIdx, int userIdx) {
        String getChallengeAlertQuery = "select date_format(createAt, '%c월 %e일') date\n" +
                "from ChallengeAlert\n" +
                "where userIdx = ? and challengeIdx = ? and status = 1 group by date order by date desc ;";
        String getAlertQuery = "select ca.alertIdx,\n" +
                "       ca.image,\n" +
                "       ca.alert,\n" +
                "       ifnull(x.certificationIdx, 0) certificationIdx,\n" +
                "       ifnull(certificationPhoto, '인증알림아님') certificationPhoto,\n" +
                "       date_format(ca.createAt, '%p %l:%i') time,\n" +
                "       ifnull(reportIdx, 0) reportStatus\n" +
                "from ChallengeAlert ca\n" +
                " left join (\n" +
                "     select ce.certificationIdx, certificationPhoto\n" +
                "     from Certification ce where ce.status = 1)\n" +
                " as x on x.certificationIdx = ca.certificationIdx\n" +
                "left join (\n" +
                "    select certificationIdx, reportIdx\n" +
                "    from Report where status = 1\n" +
                "    ) as y on y.certificationIdx = ca.certificationIdx\n" +
                "where ca.userIdx = ? and ca.challengeIdx = ?\n" +
                "and ca.status = 1 and date_format(ca.createAt, '%c월 %e일') = ?\n" +
                "order by ca.createAt desc;";

        return this.jdbcTemplate.query(getChallengeAlertQuery,
                (rs, rowNum) -> new GetChallengeAlert(
                        rs.getString("date"),
                        this.jdbcTemplate.query(getAlertQuery, (rs1, rowNum1) -> new GetAlert(
                                rs1.getInt("alertIdx"),
                                rs1.getString("image"),
                                rs1.getString("alert"),
                                rs1.getInt("certificationIdx"),
                                rs1.getString("certificationPhoto"),
                                rs1.getString("time"),
                                rs1.getInt("reportStatus")
                        ), userIdx, challengeIdx, rs.getString("date"))
                ), userIdx, challengeIdx);
    }

    public int deleteAlert(int alertIdx, int userIdx){
        String deleteNotificationQuery = "update ChallengeAlert set status = 0 where alertIdx = ? and userIdx = ?; ";
        Object[] deleteNotificationParams = new Object[]{alertIdx, userIdx};
        return this.jdbcTemplate.update(deleteNotificationQuery, deleteNotificationParams);
    }

}
