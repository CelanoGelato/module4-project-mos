package com.codegym.mos.module4projectmos.config.security;

import com.codegym.mos.module4projectmos.config.security_customization.*;
import com.codegym.mos.module4projectmos.config.security_filter.JwtAuthenticationFilter;
import com.codegym.mos.module4projectmos.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] CSRF_IGNORE = {"/api/login", "/api/register"};

    @Autowired
    CustomRestAuthenticationSuccessHandler customRestAuthenticationSuccessHandler;

    @Autowired
    CustomRestAuthenticationFailureHandler customRestAuthenticationFailureHandler;

    @Autowired
    CustomRestAccessDeniedHandler customRestAccessDeniedHandler;

    @Autowired
    CustomRestAuthenticationEntryPoint customRestAuthenticationEntryPoint;

    @Autowired
    CustomRestLogoutSuccessHandler customRestLogoutSuccessHandler;

    @Autowired
    UserDetailServiceImpl userDetailServiceImpl;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Password encoder, để Spring Security sử dụng mã hóa mật khẩu người dùng
        return new BCryptPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
            throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailServiceImpl) // Cung cáp userservice cho spring security
                .passwordEncoder(passwordEncoder()); // cung cấp password encoder
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/login", "/api/register", "/api/song/download/**", "/api/song/upload").permitAll()
                .antMatchers("/api/admin/**").access("hasRole('ADMIN')")
                .antMatchers("/api/profile").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers("/api/**").permitAll()
                .and().formLogin()
//                .loginPage("/login")
//                .loginProcessingUrl("/appLogin")
                .successHandler(customRestAuthenticationSuccessHandler)
                .failureHandler(customRestAuthenticationFailureHandler)
                .usernameParameter("ssoId").passwordParameter("password")
                .and().csrf().disable()
//                .ignoringAntMatchers(CSRF_IGNORE)
//                .csrfTokenRepository(csrfTokenRepository()) // defines a repository where tokens are stored
//                .and()
//                .addFilterAfter(new CustomCsrfFilter(), CsrfFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(customRestAuthenticationEntryPoint)
                .accessDeniedHandler(customRestAccessDeniedHandler)
//                .accessDeniedPage("/accessDenied")
        //             .and()
        //             .logout().logoutSuccessHandler(customRestLogoutSuccessHandler)
//                .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
        ;
        // Thêm một lớp Filter kiểm tra jwt
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors();
    }
}