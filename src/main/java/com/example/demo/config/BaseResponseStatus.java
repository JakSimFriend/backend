package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    EMPTY_ACCESS_TOKEN(false,2004,"ACCESS TOKEN을 입력하세요."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_EXISTS_NICKNAME(false, 2018, "중복된 닉네임입니다."),
    POST_USERS_EMPTY_NICKNAME(false, 2019, "닉네임을 입력해주세요."),
    POST_USERS_INVALID_NICKNAME(false, 2020, "닉네임 형식을 확인해주세요."),



    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),


    // 5000 : 기타 오류
    WRONG_URL(false, 5001, "잘못된 URL 정보입니다."),
    FAILED_TO_CONNECT(false,5002,"URL 연결에 실패했습니다."),
    FAILED_TO_READ_RESPONSE(false,5003,"로그인 정보 조회에 실패했습니다."),
    FAILED_TO_PARSE(false,5004,"파싱에 실패했습니다."),
    FORBIDDEN_ACCESS(false, 5005, "접근 권한이 없습니다."),
    FAILED_TO_KAKAO_SIGN_UP(false, 5006, "카카오 회원가입에 실패하였습니다."),
    FAILED_TO_NAVER_SIGN_UP(false, 5007, "네이버 회원가입에 실패하였습니다."),
    FAILED_TO_KAKAO_SIGN_IN(false, 5008, "카카오 로그인에 실패하였습니다."),
    FAILED_TO_NAVER_SIGN_IN(false, 5009, "네이버 로그인에 실패하였습니다."),
    EXIST_USER(false, 5010, "존재하는 회원입니다. 로그인을 시도하세요"),
    FORBIDDEN_USER(false, 5011, "해당 회원에 접근할 수 없습니다."),

            ;


    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
