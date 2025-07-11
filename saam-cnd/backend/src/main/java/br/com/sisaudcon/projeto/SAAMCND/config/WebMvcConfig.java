package br.com.sisaudcon.projeto.SAAMCND.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
}
