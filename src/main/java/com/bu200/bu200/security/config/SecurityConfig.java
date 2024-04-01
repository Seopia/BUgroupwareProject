package com.bu200.bu200.security.config;

import com.bu200.bu200.security.jwt.JWTUtil;
import com.bu200.bu200.security.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  //스프링 설정 파일이다.
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    @Bean   //빈이기 때문에 주입 가능하다.
    public BCryptPasswordEncoder bCryptPasswordEncoder(){   //패스워드 인코드
        return new BCryptPasswordEncoder();
    }
    @Bean   //매니저 생성하여 리턴하는 메서드
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //csrf 를 disable 하기
        http
                .csrf(AbstractHttpConfigurer::disable);
        //form 로그인 방식 disable : JWT를 사용하기 때문에 이 부분을 커스텀해야함
        http
                .formLogin(AbstractHttpConfigurer::disable);
        //http basic 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth)-> auth
                        .requestMatchers("/login", "/", "/join").permitAll()   //"login", "/", "/join" 경로는 모든 권한을 허용함
                        .requestMatchers("/admin").hasRole("ADMIN")             // "/admin" 경로는 ADMIN 권한이 있는지 확인함
                        .anyRequest().authenticated());     //나머지 경로는 모두 권한을 확인함

        http
                //필터를 중간에 추가한다. LoginFilter 는 내가 만든거다.
                //인자로 매니저를 넣어주는데 스프링에서 제공하는 설정을 넣어주면 매니저가 완성된다.
                //매니저는 UserDetailsService 을 구현한 클래스를 찾아 그 쪽의 로직을 실행하게 된다. 그게 CustomUserDetailsService 이다.
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));   //세션을 stateless 상태로 설정함

        return http.build();
    }
}
