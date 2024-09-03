package com.bit.springboard.oauth;

import com.bit.springboard.entity.CustomUserDetails;
import com.bit.springboard.entity.Member;
import com.bit.springboard.oauth.provider.KakaoUserInfo;
import com.bit.springboard.oauth.provider.OAuth2UserInfo;
import com.bit.springboard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    /*
    *  소셜 로그인 버튼 클릭 -> 인증서버로 요청 -> 인증서버에서 인증코드 발급
    *  -> 발급받은 인증코드로 다시 한번 인증서버로 요청 -> 인증서버는 인증코드의 유효성 검사 후 토큰 발급
    *  -> 발급받은 토큰으로 자원서버에 요청 -> 자원서버는 토큰의 유효성을 검사 후 사용자 정보를 리턴
    * */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);

        String nickname = "";
        String providerId = "";

        OAuth2UserInfo oAuth2UserInfo = null;

        if(request.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

            providerId = oAuth2UserInfo.getProviderId();
            nickname = oAuth2UserInfo.getName();

        } else if (request.getClientRegistration().getRegistrationId().equals("naver")) {

        } else if (request.getClientRegistration().getRegistrationId().equals("google")) {

        } else {
            return null;
        }

        String provider = oAuth2UserInfo.getProvider();
        // ex: kakako_123123
        String username = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();
        String password = passwordEncoder.encode(username);
        String email = oAuth2UserInfo.getEmail();

        Member member;

        if(memberRepository.findByUsername(username).isPresent()) {
            member = memberRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("member not exist"));

        } else {
            member = Member.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .nickname(nickname)
                    .build();

            memberRepository.save(member);
        }

        return CustomUserDetails.builder()
                .member(member)
                .attributes(oAuth2User.getAttributes())
                .build();
    }


}
