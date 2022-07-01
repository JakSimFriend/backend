package com.example.demo.src.profile;

import com.example.demo.src.profile.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProfileDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int modifyPromise(PostPromise postPromise){
        String modifyPromiseQuery = "update User set promise = ? where userIdx = ?;";
        Object[] modifyPromiseParams = new Object[]{postPromise.getPromise(), postPromise.getUserIdx()};

        return this.jdbcTemplate.update(modifyPromiseQuery, modifyPromiseParams);
    }

    public GetProfileEdit getProfileEdit(int userIdx) {
        String getProfileEditQuery = "select userIdx, profile, promise from User where userIdx = ? ";
        int getProfileEditParams = userIdx;

        return this.jdbcTemplate.queryForObject(getProfileEditQuery,
                (rs, rowNum) -> new GetProfileEdit(
                        rs.getInt("userIdx"),
                        rs.getString("profile"),
                        rs.getString("promise")
                ),
                getProfileEditParams
        );

    }
}
