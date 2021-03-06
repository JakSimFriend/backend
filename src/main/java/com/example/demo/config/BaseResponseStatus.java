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
    INVALID_USER_JWT(false, 2003, "권한이 없는 유저의 접근입니다."),
    EMPTY_ACCESS_TOKEN(false, 2004, "ACCESS TOKEN을 입력하세요."),
    EMPTY_DEVICE_TOKEN(false,2005,"DEVICE TOKEN을 입력하세요."),
    EMPTY_ID_TOKEN(false, 2006, "ID TOKEN을 입력하세요."),
    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false, 2017, "중복된 이메일입니다."),
    POST_USERS_EXISTS_NICKNAME(false, 2018, "중복된 닉네임입니다."),
    POST_USERS_EMPTY_NICKNAME(false, 2019, "닉네임을 입력해주세요."),
    POST_USERS_INVALID_NICKNAME(false, 2020, "닉네임 형식을 확인해주세요."),
    POST_USERS_INVALID_RECOMMENDER(false, 2021, "추천인을 확인해주세요."),
    POST_USERS_EMPTY_BIRTH(false, 2022, "생일을 입력해주세요."),

    // [POST] /challenges
    POST_CHALLENGES_EMPTY_TITLE(false, 2100, "챌린지 제목을 입력해주세요."),
    POST_CHALLENGES_INVALID_TITLE(false, 2101, "챌린지 제목 형식을 확인해주세요."),
    POST_CHALLENGES_EMPTY_CONTENT(false, 2102, "챌린지 내용을 입력해주세요."),
    POST_CHALLENGES_INVALID_CONTENT(false, 2103, "챌린지 내용 형식을 확인해주세요."),
    POST_CHALLENGES_EMPTY_START(false, 2104, "챌린지 시작 날짜를 입력해주세요."),
    POST_CHALLENGES_INVALID_START(false, 2105, "챌린지 시작 날짜 형식을 확인해주세요."),
    POST_CHALLENGES_EMPTY_CYCLE(false, 2106, "챌린지 인증 주기를 입력해주세요."),
    POST_CHALLENGES_INVALID_CYCLE(false, 2107, "챌린지 인증 주기 형식을 확인해주세요."),
    POST_CHALLENGES_EMPTY_COUNT(false, 2108, "챌린지 인증 횟수를 입력해주세요."),
    POST_CHALLENGES_INVALID_COUNT(false, 2109, "챌린지 인증 횟수 형식을 확인해주세요."),
    POST_CHALLENGES_EMPTY_DEADLINE(false, 2110, "챌린지 인증 시간을 입력해주세요."),
    POST_CHALLENGES_INVALID_DEADLINE(false, 2111, "챌린지 인증 시간 형식을 확인해주세요."),
    POST_CHALLENGES_EMPTY_CATEGORY(false, 2112, "챌린지 카테고리를 입력해주세요."),
    POST_CHALLENGES_INVALID_CATEGORY(false, 2113, "챌린지 카테고리 형식을 확인해주세요."),
    POST_CHALLENGES_EMPTY_USER(false, 2114, "사용자를 입력해주세요."),
    POST_CHALLENGES_INVALID_TAG(false, 2115, "태그 형식을 확인해주세요."),

    // [POST] /profiles
    POST_PROFILES_EMPTY_PROMISE(false, 2200, "작심 다짐하기를 입력해주세요."),
    POST_PROFILES_INVALID_PROMISE(false, 2201, "작심 다짐하기 형식을 확인해주세요."),

    GET_SEARCH_EMPTY_KEYWORD(false, 2202, "검색어를 입력해주세요."),
    GET_SEARCH_INVALID_KEYWORD(false, 2203, "검색어 형식을 확인해주세요"),

    POST_REWARDS_EMPTY_USERIDX(false, 2204, "사용자 인덱스를 입력해주세요."),
    POST_REWARDS_EMPTY_CHALLENGEIDX(false, 2205, "챌린지 인덱스를 입력해주세요."),
    POST_REWARDS_EMPTY_POINT(false, 2206, "포인트를 입력해주세요."),
    POST_REWARDS_EMPTY_EXPERIENCE(false, 2207, "경험치를 입력해주세요."),
    POST_REWARDS_EMPTY_ACHIEVEMENT(false, 2208, "달성률을 입력해주세요."),

    POST_REWARDS_INVALID_POINT(false, 2209, "포인트를 확인해주세요."),
    POST_REWARDS_INVALID_EXPERIENCE(false, 2210, "경험치를 확인해주세요."),
    POST_REWARDS_INVALID_ACHIEVEMENT(false, 2211, "달성률을 확인해주세요."),

    POST_REPORT_EMPTY_USER(false, 2212, "사용자 인덱스를 입력해주세요."),
    POST_REPORT_EMPTY_CHALLENGE(false, 2213, "챌린지 인덱스를 입력해주세요."),
    POST_REPORT_EMPTY_CERTIFICATION(false, 2214, "인증 인덱스를 입력해주세요."),

    POST_INQUIRE_EMPTY_TITLE(false, 2215, "문의 제목을 입력해주세요."),
    POST_INQUIRE_EMPTY_CONTENT(false, 2216, "문의 내용을 입력해주세요."),
    POST_INQUIRE_INVALID_TITLE(false, 2217, "문의 제목 형식을 확인해주세요."),
    POST_INQUIRE_INVALID_CONTENT(false, 2218, "문의 내용 형식을 확인해주세요."),
    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false, 3014, "없는 아이디거나 비밀번호가 틀렸습니다."),
    DUPLICATED_NICKNAME(false, 3015, "중복된 닉네임입니다."),
    NOT_EXIST_USER(false, 3016, "존재하지 않는 유저입니다."),
    POST_FAIL_NICKNAME(false, 3017, "닉네임 설정에 실패하였습니다."),
    POST_FAIL_RECOMMENDER(false, 3018, "추천인 등록에 실패하였습니다."),
    POST_FAIL_CHALLENGE(false, 3019, "챌린지 개설에 실패하였습니다."),
    POST_FAIL_PROMISE(false, 3020, "작심 다짐하기 설정에 실패하였습니다."),

    NOT_EXIST_CHALLENGE(false, 3021, "존재하지 않는 챌린지입니다."),
    NOT_EXIST_RECOMMENDATION(false, 3022, "추천 챌린지가 존재하지 않습니다."),

    POST_FAIL_JOIN(false, 3023, "챌린지 가입에 실패하였습니다."),
    EXIST_JOIN(false, 3024, "이미 가입 신청이 되어있습니다."),

    NOT_EXIST_JOIN(false, 3025, "가입 신청이 존재하지 않습니다."),
    EXIST_REFUSE(false, 3026, "이미 거절하였습니다."),
    EXIST_ACCEPT(false, 3027, "이미 수락하였습니다."),
    NOT_EXIST_FOUNDER(false, 3028, "챌린지를 개설한 사용자가 아닙니다."),

    NOT_EXIST_SEARCH(false, 3029, "챌린지가 존재하지 않습니다."),
    NOT_EXIST_DETAIL(false, 3030, "챌린지 정보가 존재하지 않습니다."),

    POST_FAIL_PROFILE(false, 3031, "프로필 사진 수정에 실패하였습니다."),

    NOT_PROCEEDING_CHALLENGE(false, 3032, "진행중인 챌린지가 아닙니다."),
    NOT_EXIST_MEMBER(false, 3033, "챌린지 멤버가 아닙니다."),
    EXIST_CERTIFICATION(false, 3034, "오늘은 이미 인증하였습니다."),
    DEADLINE_END(false,3035, "오늘의 인증 마감시간이 지났습니다."),
    PROCEEDING_CHALLENGE(false, 3036, "진행 중인 챌린지는 삭제할 수 없습니다."),
    NOT_EXIST_POINT(false, 3037, "포인트가 부족합니다."),
    CLOSED_CHALLENGE(false, 3038, "마감된 챌린지입니다."),
    PROCEEDING_CHALLENGE_JOIN(false, 3039, "진행 중인 챌린지는 신청할 수 없습니다."),

    POST_FAIL_BIRTH(false, 3040, "생일 등록에 실패하였습니다."),

    NOT_EXIST_NOTIFICATION(false, 3041, "존재하지 않는 알림입니다."),
    NOTHING_NOTIFICATION(false, 3042, "알림이 존재하지 않습니다."),
    NO_ACCESS_USER(false, 3043, "접근이 불가능한 사용자입니다."),
    EXITS_SETTING(false, 3044, "이미 설정이 되어있습니다."),
    EXITS_CANCEL(false, 3045, "이미 해제가 되어있습니다."),

    EXIST_REWARD(false, 3046, "이미 보상을 받았습니다."),
    NOT_END_CHALLENGE(false, 3047, "종료된 챌린지가 아닙니다."),
    POST_FAIL_REWARD(false, 3048, "보상받기에 실패하였습니다."),
    NOTHING_STATUS(false, 3049, "현황 정보가 존재하지 않습니다."),
    NOT_EXIST_TOKEN(false, 3050, "DEVICE TOKEN이 존재하지 않습니다."),
    NOT_EXIST_CERTIFICATION(false, 3051, "존재하지 않는 인증입니다."),
    EXIST_REPORT(false, 3052, "이미 신고하셨습니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false, 4014, "유저네임 수정 실패"),

    //[PATCH] /challenges/
    DELETE_FAIL_CHALLENGE(false, 4015, "챌린지 삭제 실패"),
    DELETE_FAIL_JOIN(false, 4016, "가입 신청 취소 실패"),
    REFUSE_FAIL_JOIN(false, 4017, "가입 거절 실패"),
    ACCEPT_FAIL_jOIN(false, 4018, "가입 수락 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    CERTIFICATION_FAIL(false, 4019, "챌린지 인증에 실패하였습니다."),
    DELETE_FAIL_USER(false, 4020, "탈퇴에 실패하였습니다."),

    DELETE_FAIL_NOTIFICATION(false, 4021, "알림 삭제에 실패하였습니다."),
    DELETE_FAIL_ALL(false, 4022, "알림 전체 삭제에 실패하였습니다."),

    GET_FAIL_REWARD(false, 4023, "광고 보상 받기에 실패하였습니다."),
    SETTING_FAIL_ALERT(false, 4024, "알림 설정에 실패하였습니다."),
    SETTING_FAIL_ALERT_CANCEL(false, 4025, "알림 해제에 실패하였습니다."),
    SAVE_FAIL_DEVICE(false, 4026, "DEVICE TOKEN 저장에 실패하였습니다."),
    LOGOUT_FAIL_USER(false, 4027, "로그아웃에 실패하였습니다."),
    FAILED_TO_REPORT(false, 4028, "신고하기에 실패하였습니다."),
    FAILED_TO_INQUIRE(false, 4029, "문의하기에 실패하였습니다."),

    // 5000 : 기타 오류
    WRONG_URL(false, 5001, "잘못된 URL 정보입니다."),
    FAILED_TO_CONNECT(false, 5002, "URL 연결에 실패했습니다."),
    FAILED_TO_READ_RESPONSE(false, 5003, "로그인 정보 조회에 실패했습니다."),
    FAILED_TO_PARSE(false, 5004, "파싱에 실패했습니다."),
    FORBIDDEN_ACCESS(false, 5005, "접근 권한이 없습니다."),
    FAILED_TO_KAKAO_SIGN_UP(false, 5006, "카카오 회원가입에 실패하였습니다."),
    FAILED_TO_NAVER_SIGN_UP(false, 5007, "네이버 회원가입에 실패하였습니다."),
    FAILED_TO_KAKAO_SIGN_IN(false, 5008, "카카오 로그인에 실패하였습니다."),
    FAILED_TO_NAVER_SIGN_IN(false, 5009, "네이버 로그인에 실패하였습니다."),
    EXIST_USER(false, 5010, "존재하는 회원입니다. 로그인을 시도하세요"),
    FORBIDDEN_USER(false, 5011, "해당 회원에 접근할 수 없습니다."),
    FAILED_TO_APPLE_SIGN_UP(false, 5006, "애플 회원가입에 실패하였습니다."),


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
