package org.development.wide.world.spring.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(@NonNull final HttpSecurity http) throws Exception {
        return http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(this::setUpAuthorizeHttpRequestsConfigurer)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    /* Private methods */
    private void setUpAuthorizeHttpRequestsConfigurer(@NonNull final AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requestAuthorizationConfigurer) {
        requestAuthorizationConfigurer.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
        requestAuthorizationConfigurer.requestMatchers(HttpMethod.GET,
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/swagger-ui/**"
        ).permitAll();
        requestAuthorizationConfigurer.anyRequest().authenticated();
    }

}
