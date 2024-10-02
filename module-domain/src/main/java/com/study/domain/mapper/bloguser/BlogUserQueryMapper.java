package com.study.domain.mapper.bloguser;


import com.study.domain.blogUser.entity.BlogUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface BlogUserQueryMapper {

    /**
     * 주어진 사용자 ID로 블로그 사용자 엔티티를 찾습니다.
     *
     * @param userId 찾을 사용자 ID
     * @return 찾은 블로그 사용자 엔티티의 Optional 객체
     */
    Optional<BlogUser> findBlogUserByUserId(String userId);


    /**
     * 주어진 사용자 ID가 존재하는지 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 사용자 ID 존재 여부
     */
    boolean existsByUserId(String userId);

    /**
     * 주어진 닉네임이 존재하는지 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임 존재 여부
     */
    boolean existsByNickname(String nickname);

    /**
     * 주어진 이메일로 BlogUser를 찾습니다.
     *
     * @param email 찾을 이메일
     * @return 유저 존재 여부
     */
    boolean existsBlogUserByEmail(String email);

}
