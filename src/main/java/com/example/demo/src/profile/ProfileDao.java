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

    public List<GetProfile> getProfile(int userIdx){
        String getProfileQuery = "select u.userIdx userIdx,\n" +
                "       u.profile profile,\n" +
                "       u.nickName nickName,\n" +
                "       u.promise promise,\n" +
                "       sum(p.point) point\n" +
                "from User u, Point p\n" +
                "where u.userIdx = p.userIdx\n" +
                "and u.status = 1\n" +
                "and u.userIdx = ? ";
        String getPointQuery = "select pc.categoryName categoryName,\n" +
                "       pc.image image,\n" +
                "       DATE_FORMAT(p.createAt, '%Y/%m/%d') as createAt,\n" +
                "       p.point point,\n" +
                "       sum(point) over(order by p.createAt, pointIdx) as balance\n" +
                "from PointCategory pc, Point p\n" +
                "where pc.categoryIdx = p.categoryIdx\n" +
                "and p.userIdx = ?\n" +
                "order by createAt desc; ";
        int getProfileParams = userIdx;

        return this.jdbcTemplate.query(getProfileQuery,
                (rs, rowNum) -> new GetProfile(
                        rs.getInt("userIdx"),
                        rs.getString("profile"),
                        rs.getString("nickName"),
                        rs.getString("promise"),
                        rs.getInt("point"),
                        this.jdbcTemplate.query(getPointQuery, (rs1, rowNum1) -> new GetPoints(
                                rs1.getString("categoryName"),
                                rs1.getString("image"),
                                rs1.getString("createAt"),
                                rs1.getInt("point"),
                                rs1.getInt("balance")
                                ), getProfileParams)
                ), getProfileParams);
    }

    public int modifyProfileImage(int userIdx, String imageUrl){
        String modifyProfileImageQuery = "update User set profile = ? where userIdx = ? and status = 1;\n";
        Object[] modifyProfileImageParams = new Object[]{imageUrl, userIdx};

        return this.jdbcTemplate.update(modifyProfileImageQuery, modifyProfileImageParams);
    }

    public int getReward(int userIdx){
        String getRewardQuery = "insert into Point(point, userIdx, categoryIdx) values (500, ?, 2);";
        Object[] getRewardParams = new Object[]{userIdx};
        return this.jdbcTemplate.update(getRewardQuery, getRewardParams);
    }

    public int checkUser(int userIdx) {
        String checkUserQuery = "select exists(select userIdx from User where userIdx = ? and status = 1);";
        return this.jdbcTemplate.queryForObject(checkUserQuery, int.class, userIdx);
    }
}
