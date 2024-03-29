package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers() {
        String getUsersQuery = "select * from UserInfo";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password"))
        );
    }

    public List<GetUserRes> getUsersByEmail(String email) {
        String getUsersByEmailQuery = "select * from UserInfo where email =?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                getUsersByEmailParams);
    }

    public GetUserRes getUser(int userIdx) {
        String getUserQuery = "select * from UserInfo where userIdx = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                getUserParams);
    }


    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into UserInfo (userName, ID, password, email) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getId(), postUserReq.getPassword(), postUserReq.getEmail()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int checkToken(int userIdx) {
        String checkQuery = "select exists(select token from UserDevice where userIdx = ?);";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, userIdx);

    }

    public int modifyUserName(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update UserInfo set userName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams);
    }

    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select userIdx, password,email,userName,ID from UserInfo where ID = ?";
        String getPwdParams = postLoginReq.getId();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("ID"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("email")
                ),
                getPwdParams
        );

    }

    public GetSocial getIdx(String email) {
        String getSocialQuery = "select userIdx from User where email = ? ";
        String getSocialParams = email;

        return this.jdbcTemplate.queryForObject(getSocialQuery,
                (rs, rowNum) -> new GetSocial(rs.getInt("userIdx")), getSocialParams);
    }

    public int postEmail(String email) {
        String postEmailQuery = "insert into User (email) values (?);";
        Object[] postEmailParams = new Object[]{email};

        this.jdbcTemplate.update(postEmailQuery, postEmailParams);

        String lastInserIdQuery = "select last_insert_id()";
        int userIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
        addUserPoint(userIdx);
        return userIdx;
    }

    public int postDeviceToken(int userIdx, String deviceToken) {
        String postDeviceQuery = "insert into UserDevice(token, userIdx) values(?, ?);";
        Object[] postDeviceParams = new Object[]{deviceToken, userIdx};

        this.jdbcTemplate.update(postDeviceQuery, postDeviceParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int updateDeviceToken(int userIdx, String deviceToken) {
        String updateDeviceQuery = "update UserDevice set token = ? where userIdx = ?;";
        Object[] updateDeviceParams = new Object[]{deviceToken, userIdx};

        return this.jdbcTemplate.update(updateDeviceQuery, updateDeviceParams);
    }

    public int checkNickName(String nickName) {
        String checkNickNameQuery = "select exists(select nickName from User where nickName = ?)";
        String checkNickNameParams = nickName;
        return this.jdbcTemplate.queryForObject(checkNickNameQuery, int.class, checkNickNameParams);
    }

    public int getUserIdx(String nickName){
        String getUserIdxQuery = "select userIdx from User where nickName = ? and status = 1";
        String getUserIdxParams = nickName;
        return this.jdbcTemplate.queryForObject(getUserIdxQuery,
                (rs, rowNum) -> new Integer(rs.getInt("userIdx")), getUserIdxParams);
    }

    public int createNickName(PostUserInfo postUserInfo){
        String postUserInfoQuery = "update User set nickName = ? where userIdx = ?;";
        Object[] postUserInfoParams = new Object[]{postUserInfo.getNickName(), postUserInfo.getUserIdx()};

        return this.jdbcTemplate.update(postUserInfoQuery, postUserInfoParams);
    }

    public int createBirth(PostBirth postBirth){
        String postUserInfoQuery = "update User set birth = ? where userIdx = ?;";
        Object[] postUserInfoParams = new Object[]{postBirth.getBirth(), postBirth.getUserIdx()};

        return this.jdbcTemplate.update(postUserInfoQuery, postUserInfoParams);
    }

    public int createRecommender(PostUserInfo postUserInfo) {
        String createRecommenderQuery = "insert into Recommend (recommenderIdx, userIdx) VALUES (?,?)";
        Object[] createRecommenderParams = new Object[]{postUserInfo.getRecommenderIdx(), postUserInfo.getUserIdx()};
        this.jdbcTemplate.update(createRecommenderQuery, createRecommenderParams);

        updateIdxPoint(postUserInfo.getUserIdx(), 1500);
        updateIdxPoint(postUserInfo.getRecommenderIdx(), 1500);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public void updateIdxPoint(int userIdx, int point){
        String updatePointQuery = "insert into Point (point, userIdx, categoryIdx) values (?, ?, 1);";
        Object[] updatePointParams = new Object[]{point, userIdx};
        this.jdbcTemplate.update(updatePointQuery, updatePointParams);
    }

    public int deleteUser(int userIdx) {
        String deleteUserQuery = "update User set email = concat(email, '-'), status = 0, nickName = '알수없음' where userIdx = ? and status = 1;";
        Object[] deleteUserParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(deleteUserQuery, deleteUserParams);
    }

    public int checkUser(int userIdx) {
        String checkUserQuery = "select exists(select userIdx from User where userIdx = ? and status = 1);";
        return this.jdbcTemplate.queryForObject(checkUserQuery, int.class, userIdx);
    }

    public int addUserPoint(int userIdx){
        String addUserPointQuery = "insert into Point(point, userIdx, categoryIdx) values (1000, ?, 6);";
        Object[] addUserPointParams = new Object[]{userIdx};
        return this.jdbcTemplate.update(addUserPointQuery, addUserPointParams);
    }

    public int deleteDeviceToken(int userIdx) {
        String deleteUserQuery = "delete from UserDevice where userIdx = ?;";
        Object[] deleteUserParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(deleteUserQuery, deleteUserParams);
    }
}
