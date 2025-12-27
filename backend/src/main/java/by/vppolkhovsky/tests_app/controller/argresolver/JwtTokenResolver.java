package by.vppolkhovsky.tests_app.controller.argresolver;

import by.vppolkhovsky.tests_app.services.DataSigner;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtTokenResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper mapper;
    private final DataSigner dataSigner;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JwtToken.class);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        Optional<JsonNode> jwt = Optional.ofNullable(request.getCookies())
            .stream()
            .flatMap(Arrays::stream)
            .filter(cookie -> cookie.getName().equals("JWT"))
            .map(Cookie::getValue)
            .map(value -> Base64.getDecoder().decode(value))
            .map(mapper::readTree)
            .findAny();

        if (jwt.isEmpty()) {
            return null;
        }

        JsonNode jwtNode = jwt.get();

        String userId = jwtNode.get("userid").asStringOpt()
            .orElseThrow(() -> new IllegalArgumentException("No JWT userid"));

        String signature = jwtNode.get("signature").asStringOpt()
            .orElseThrow(() -> new IllegalArgumentException("No JWT signature"));

        if (dataSigner.verifySignature(userId, signature)) {
            return UUID.fromString(userId);
        }

        return null;
    }
}
