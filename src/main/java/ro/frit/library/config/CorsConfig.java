package ro.frit.library.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final LibraryProperties libraryProperties;

    public CorsConfig(LibraryProperties libraryProperties) {
        this.libraryProperties = libraryProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> allowedOrigins = libraryProperties.cors().allowedOriginList();

        if (allowedOrigins.isEmpty()) return;

        registry.addMapping("/**")
            .allowedOrigins(allowedOrigins.toArray(String[]::new))
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("Content-Type", "Accept");
    }
}
