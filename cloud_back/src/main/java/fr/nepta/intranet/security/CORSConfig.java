package fr.nepta.intranet.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**")
//		.allowedMethods("*")
//		.allowedOrigins("*")
////		.allowedOrigins("https://intranet.tracroute.lan/")
////		.allowedMethods("GET", "POST")
//		;

		registry
		// Enable cross-origin request handling for the specified path pattern. 
        // Exact path mapping URIs (such as "/admin") are supported as well as Ant-style path patterns (such as "/admin/**"). 
        .addMapping("/*")
        .allowedOrigins("*")
        // .allowedOriginPatterns("")
        .allowCredentials(false)
        .allowedHeaders("*")
        .exposedHeaders("*")
        .maxAge(60 *30)
        .allowedMethods("*")
        ;
	}
}
