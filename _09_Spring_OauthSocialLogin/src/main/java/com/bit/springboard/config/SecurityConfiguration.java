package com.bit.springboard.config;

import com.bit.springboard.handler.LoginFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// @EnableWebSecurity: SpringSecurity의 FilterChain을 구성하기 위한 어노테이션
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final LoginFailureHandler loginFailureHandler;
    // 비밀번호 암호화 객체를 Bean 객체로 등록
    // 비밀번호 암호화 객체의 역할은 로그인할 때 입력된 암호와 DB에 저장되어 있는 암호화된 비밀번호가
    // 일치하는 지 비교. 비밀번호 암호화 객체에 matches메소드(암호화되지 않은 비밀번호, 암호화된 비밀번호)
    // 를 통해서 비교를 진행. 일치하면 true, 일치하지 않으면 false를 리턴한다.
    // 회원가입 시 비밀번호 저장하기 전에 비밀번호를 암호화
    // 비밀번호는 한 번 암호화되면 다시는 복호화(디코딩)할 수 없다.
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // Spring Security의 FilterChain 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // csrf공격에 대한 방지
                // csrf(Cross Site Request Forgery): 사이트간 위조 요청을 하는 공격
//                               정상적인 사용자가 의도치 않게 위조 요청을 보낼 수 있어서 꺼두면 안된다
//                               논 브라우저 클라이언트(REST API 역할만 하는 어플리케이션)에서는 꺼둬도 무방하다.
                //               REST API의 stateless(무속성) 특성 때문에 사용자에 대한 정보가 서버에 저장되기 않기 때문이다.
                .csrf(AbstractHttpConfigurer::disable)
                // 인가 처리
                // 요청 주소(엔드포인트)에 대한 권한 처리
                .authorizeHttpRequests((authorizeRequests) ->  {
                    // static resorces에 대한 요청은 모든 사용자가 접속가능하도록 설정
                    authorizeRequests.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
                    // /요청은 모든 사용자가 접속가능하도록 설정
                    authorizeRequests.requestMatchers("/").permitAll();
                    // member의 모든 하위 요청은 모든 사용자가 접속 가능하도록 설정
                    authorizeRequests.requestMatchers("/member/**").permitAll();
                    // boards의 모든 하위 요청은 ROLE_USER, ROLE_ADMIN만 접근할 수 있도록 설정
                    authorizeRequests.requestMatchers("/boards/**").hasAnyRole("USER", "ADMIN");
                    // 위에 설정한 요청주소를 제외한 모든 요청은 인증(로그인, 권한이 있는)된 사용자만 접근할 수 있도록 설정
                    authorizeRequests.anyRequest().authenticated();
                })
                // 인증(로그인) 처리
                .formLogin((formLogin) -> {
                    // 로그인 페이지 지정
                    formLogin.loginPage("/member/login");
                    // 사용자가 입력하는 아이디와 비밀번호의 키 값 설정
                    // Spring Security에서는 기본값으로 username과 password로 설정되어 있다.
                    formLogin.usernameParameter("username");
                    formLogin.passwordParameter("password");
                    // 로그인을 처리(Spring Security에서 인터셉트)할 url 지정
                    // 지정된 주소로 요청이 오면 Spring Security에서 요청을 가로채서
                    // 인증처리를 진행
                    formLogin.loginProcessingUrl("/member/loginProc");
                    // 로그인 실패 시 동작할 핸들러 클래스 등록
                    formLogin.failureHandler(loginFailureHandler);
                    // 로그인 성공시 리다이렉트할 요청 주소 지정
                    formLogin.defaultSuccessUrl("/");
                })
                // 로그아웃 처리
                .logout((logout) -> {
                  // 로그아웃 요청 주소 지정
                  logout.logoutUrl("/member/logout");

                  // 사용자의 인증정보가 저장된 Security Context 가
                    // HttpSession 에 저장되기 때문에 만료시켜서 세션의 Security Context 제거
                    logout.invalidateHttpSession(true);

                    // 로그아웃 후 리다이렉트 주소
                    logout.logoutSuccessUrl("/member/login");
                })
                .build();


    }















}
