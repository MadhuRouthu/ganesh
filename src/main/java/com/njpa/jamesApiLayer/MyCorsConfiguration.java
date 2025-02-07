package com.njpa.jamesApiLayer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class MyCorsConfiguration {
	@Value("${cors.allowedOrigins}")
	private String origin;
	@Value("${cors.allowedMethods}")
	private String allowMethod;
	@Value("${cors.allowedHeaders}")
	private String header;
	@Value("${cors.allowCredentials}")
	private Boolean allowCredentials;
	@Value("${cors.Configuration}")
	private String corsConfiguration;
	
	
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(allowCredentials);
        config.addAllowedOrigin(origin); // Add specific allowed origins here
        config.addAllowedHeader(header);
        config.addAllowedMethod(allowMethod);
        source.registerCorsConfiguration(corsConfiguration, config);
        return new CorsFilter(source);
    }
}
