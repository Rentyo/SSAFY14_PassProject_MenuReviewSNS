package com.ssafy.user.service;

import com.ssafy.user.dto.*;
import com.ssafy.user.exception.*;
import com.ssafy.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final com.ssafy.util.JwtUtil jwtUtil;
    private final com.ssafy.local.Service.S3Service s3Service;

    @Override
    @Transactional

    /*
     * 회원가입에 대한 구현체
     * 1. 이메일 중복체크
     * 2. 닉네임 중복체크
     * 3. 역할 설정
     */
    public Long signup(Signup signup) {
        // 1. 이메일 중복 체크
        if (userMapper.existsByEmail(signup.getUserEmail()) > 0) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        // 2. 닉네임 중복 체크
        if (userMapper.existsByNickname(signup.getNickname()) > 0) {
            throw new DuplicateNicknameException("이미 사용 중인 이메일입니다.");
        }

        // 3. role 기본값 설정
        if (signup.getRole() == null || signup.getRole().isEmpty()) {
            signup.setRole("USER");
        }

        // 4. DB insert
        userMapper.insertUser(signup);

        // 5. 생성된 userNo 반환 (useGeneratedKeys로 자동 설정됨)
        return signup.getUserNo();
    }

    @Override
    public LoginResp login(LoginReq loginReq) {
        // 1. 이메일로 사용자 조회
        User user = userMapper.selectUserByEmail(loginReq.getUserEmail());
        if (user == null) {
            throw new LoginFailedException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 2. 비밀번호 확인
        if (!user.getUserPw().equals(loginReq.getUserPw())) {
            throw new LoginFailedException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. LoginResp 생성 (User -> LoginResp 변환)
        LoginResp loginResp = new LoginResp();
        loginResp.setUserNo(user.getUserNo());
        loginResp.setUserEmail(user.getUserEmail());
        loginResp.setNickname(user.getNickname());
        loginResp.setUserImg(user.getUserImg());
        loginResp.setCreatedAt(user.getCreatedAt());
        loginResp.setUpdatedAt(user.getUpdatedAt());
        loginResp.setGender(user.getGender());
        loginResp.setRole(user.getRole());
        loginResp.setPhoneNumber(user.getPhoneNumber());

        // CEO라면 식당을 소유하고 있는 지
        if (user.getRole().equals("CEO")) {
            loginResp.setOwner(userMapper.isOwner(user.getUserNo()));
        }
        // 4. JWT 토큰 생성 및 설정
        String accessToken = jwtUtil.createToken(user.getUserNo(), user.getUserEmail(), user.getRole());
        loginResp.setAccessToken(accessToken);

        return loginResp;
    }

    @Override
    public LoginResp getUserInfo(Long userNo) {
        // 1. userNo로 사용자 조회
        User user = userMapper.selectUserByUserNo(userNo);
        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 사용자입니다.");
        }

        // 2. LoginResp 생성 (User -> LoginResp 변환)
        LoginResp loginResp = new LoginResp();
        loginResp.setUserNo(user.getUserNo());
        loginResp.setUserEmail(user.getUserEmail());
        loginResp.setNickname(user.getNickname());
        loginResp.setUserImg(user.getUserImg());
        loginResp.setCreatedAt(user.getCreatedAt());
        loginResp.setUpdatedAt(user.getUpdatedAt());
        loginResp.setGender(user.getGender());
        loginResp.setRole(user.getRole());
        loginResp.setPhoneNumber(user.getPhoneNumber());

        // CEO라면 식당을 소유하고 있는 지
        if (user.getRole().equals("CEO")) {
            loginResp.setOwner(userMapper.isOwner(user.getUserNo()));
        }

        return loginResp;
    }

    @Override
    @Transactional
    public void deleteUser(Long userNo) {
    	// 유저에 해당하는 게시글 메뉴 평점 수정
    	userMapper.deleteChangeMenuRatingByUser(userNo);
    	// 유저에 해당하는 게시글 식당 평점 수정
    	userMapper.deleteChangeRestaurantRatingByUser(userNo);
    	
        userMapper.deleteUser(userNo);
    }

    @Override
    @Transactional
    public void updateUser(Long userNo, Update update) {
        // 1. 사용자 존재 확인
        User user = userMapper.selectUserByUserNo(userNo);
        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 사용자입니다.");
        }
        // 2. 닉네임 변경 시 중복 체크 (본인 제외)
        if (update.getNickname() != null && !update.getNickname().equals(user.getNickname())) {
            if (userMapper.existsByNicknameExcludingUser(update.getNickname(), userNo) > 0) {
                throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
            }
        }

        // 3. 전화번호 변경 시 중복 체크 (본인 제외)
        if (update.getPhoneNumber() != null && !update.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userMapper.existsByPhoneNumberExcludingUser(update.getPhoneNumber(), userNo) > 0) {
                throw new DuplicatePhoneNumberException("이미 사용 중인 전화번호입니다.");
            }
        }

        // 4. 업데이트할 User 객체 생성
        User updateUser = new User();
        updateUser.setUserNo(userNo);
        updateUser.setUserPw(update.getUserPw() != null ? update.getUserPw() : user.getUserPw());
        updateUser.setNickname(update.getNickname() != null ? update.getNickname() : user.getNickname());
        updateUser.setPhoneNumber(update.getPhoneNumber() != null ? update.getPhoneNumber() : user.getPhoneNumber());

        // 5. DB 업데이트
        userMapper.updateUser(updateUser);

        if (update.getNickname() != null && user.getRole().equals("CEO")) {
            if (userMapper.isOwner(userNo)) {
                userMapper.updateRestaurantName(update.getNickname(),update.getUserImg() ,userNo);
            }
        }
    }

    @Override
    public UserProfile getUserProfile(Long userNo, Long myUserNo) {
        UserProfile profile = userMapper.selectUserProfile(userNo, myUserNo);
        if (profile == null) {
            throw new UserNotFoundException("존재하지 않는 사용자입니다.");
        }
        return profile;
    }

    @Override
    public List<UserRole> getUserRole() throws Exception {
        return userMapper.getUserRole();
    }

    @Override
    public int updateRole(Long userNo, String role) throws Exception {
        if (!"U".equals(role) && !"A".equals(role) && !"C".equals(role)) {
            throw new IllegalArgumentException("role 값은 U, A, C 만 가능합니다.");
        }
        switch (role) {
            case "U" -> role = "USER";
            case "A" -> role = "ADMIN";
            case "C" -> role = "CEO";
        }
        return userMapper.updateRole(userNo, role);
    }

    @Override
    public List<LoginResp> getRandomUsers(Long myUserNo, int limit) {
        return userMapper.selectRandomUsers(myUserNo, limit);
    }

    @Override
    @Transactional
    public String uploadProfileImage(Long userNo, org.springframework.web.multipart.MultipartFile file) {
        // 1. 사용자 존재 확인
        User user = userMapper.selectUserByUserNo(userNo);
        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 사용자입니다.");
        }

        // 2. 기존 이미지가 있으면 S3에서 삭제
        if (user.getUserImg() != null && !user.getUserImg().isEmpty()) {
            try {
                s3Service.deleteImage(user.getUserImg());
            } catch (Exception e) {
                // 기존 이미지 삭제 실패해도 계속 진행
                System.out.println("기존 이미지 삭제 실패: " + e.getMessage());
            }
        }

        // 3. 새 이미지 S3에 업로드
        String imageUrl = s3Service.uploadImage(file);

        // 4. DB에 이미지 URL 업데이트
        User updateUser = new User();
        updateUser.setUserNo(userNo);
        updateUser.setUserImg(imageUrl);
        userMapper.updateUserImage(updateUser);

        // 5. CEO이고 식당을 소유한 경우, 식당 메인 이미지도 동기화
        if (user.getRole().equals("CEO") && userMapper.isOwner(userNo)) {
            userMapper.updateRestaurantImage(imageUrl, userNo);
        }

        return imageUrl;
    }

}
