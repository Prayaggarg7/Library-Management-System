package com.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS is handled by CorsFilter in CorsConfig.java — nothing needed here.
    // Keeping this class empty avoids the wildcard+credentials conflict.
}