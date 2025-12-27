package by.vppolkhovsky.tests_app.config;

import by.vppolkhovsky.tests_app.controller.argresolver.JwtTokenResolver;
import by.vppolkhovsky.tests_app.services.DataSigner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    private final DataSigner dataSigner;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new JwtTokenResolver(objectMapper, dataSigner));
    }
}
