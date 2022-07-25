package com.example.demo.src.setting;

import com.example.demo.src.setting.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SettingDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int settingAlert(int userIdx){
        String modifyAlertQuery = "update User set alert = 1 where userIdx = ? and status = 1;\n";
        Object[] modifyAlertParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(modifyAlertQuery, modifyAlertParams);
    }

    public int getAlert(int userIdx) {
        String getAlertQuery = "select alert from User where userIdx = ? and status = 1; ";
        int getAlertParams = userIdx;

        return this.jdbcTemplate.queryForObject(getAlertQuery,
                (rs, rowNum) -> new Integer(rs.getInt("alert")), getAlertParams);
    }

    public int cancelAlert(int userIdx){
        String modifyAlertQuery = "update User set alert = 0 where userIdx = ? and status = 1;\n";
        Object[] modifyAlertParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(modifyAlertQuery, modifyAlertParams);
    }

    public int postReport(PostReport postReport) {
        String postReportrQuery = "insert into Report(userIdx, challengeIdx, certificationIdx) values (?, ?, ?);\n";
        Object[] postReportParams = new Object[]{postReport.getUserIdx(), postReport.getChallengeIdx(), postReport.getCertificationIdx()};
        this.jdbcTemplate.update(postReportrQuery, postReportParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int checkCertification(int certificationIdx) {
        String checkQuery = "select exists(select certificationIdx from Certification where certificationIdx = ?);\n";
        Object[] checkParams = new Object[]{certificationIdx};
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, checkParams);
    }

    public int checkMember(int challengeIdx, int userIdx) {
        String checkMemberQuery = "select exists(select userIdx from Member m where m.status = 1 and challengeIdx = ? and userIdx = ?);\n";
        return this.jdbcTemplate.queryForObject(checkMemberQuery, int.class, challengeIdx, userIdx);
    }

    public int checkChallenge(int challengeIdx) {
        String checkChallengeQuery = "select exists(select challengeIdx from Challenge where challengeIdx = ? and status = 1)";
        int checkChallengeParams = challengeIdx;
        return this.jdbcTemplate.queryForObject(checkChallengeQuery, int.class, checkChallengeParams);
    }

    public int checkReport(int certificationIdx, int userIdx) {
        String checkReportQuery = "select exists(select reportIdx from Report where certificationIdx = ? and userIdx = ?);\n";
        return this.jdbcTemplate.queryForObject(checkReportQuery, int.class, certificationIdx, userIdx);
    }

    public int postInquire(PostInquire postInquire) {
        String postInquireQuery = "insert into Inquire(title, content, userIdx) values (?, ?, ?);\n";
        Object[] postInquireParams = new Object[]{postInquire.getTitle(), postInquire.getContent(), postInquire.getUserIdx()};
        this.jdbcTemplate.update(postInquireQuery, postInquireParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public List<GetNotice> getNotice(){
        String getNoticeQuery = "select noticeIdx,\n" +
                "       title,\n" +
                "       date_format(createAt, '%Y/%m/%d %H:%i') date\n" +
                "from Notice order by createAt desc ;";

        return this.jdbcTemplate.query(getNoticeQuery,
                (rs, rowNum) -> new GetNotice(
                        rs.getInt("noticeIdx"),
                        rs.getString("title"),
                        rs.getString("date")
                ));
    }

    public GetNoticeDetail getNoticeDetail(int noticeIdx){
        String getNoticeQuery = "select noticeIdx,\n" +
                "       title,\n" +
                "       date_format(createAt, '%Y/%m/%d %H:%i') date,\n" +
                "       content\n" +
                "from Notice where noticeIdx = ? order by createAt desc ;";
        return this.jdbcTemplate.queryForObject(getNoticeQuery,
                (rs, rowNum) -> new GetNoticeDetail(
                        rs.getInt("noticeIdx"),
                        rs.getString("title"),
                        rs.getString("date"),
                        rs.getString("content")
                ), noticeIdx);
    }
}
