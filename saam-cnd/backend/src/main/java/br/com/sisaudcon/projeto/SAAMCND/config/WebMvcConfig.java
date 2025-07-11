package br.com.sisaudcon.projeto.SAAMCND.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; // Adicionado
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") // Aplica a todas as rotas sob /api
                .excludePathPatterns("/api/public/**"); // Exclui rotas públicas, se houver
                // Adicionar outras exclusões se necessário (ex: /api/auth/login)
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Permite CORS para todos os endpoints /api
                .allowedOrigins("http://localhost:3000") // URL do frontend em desenvolvimento
                // .allowedOrigins("*") // Alternativamente, para desenvolvimento irrestrito
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
