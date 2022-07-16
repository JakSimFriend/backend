package com.example.demo.src.setting;

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
}
