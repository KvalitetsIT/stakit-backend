package dk.kvalitetsit.stakit.controller.interceptor;

import dk.kvalitetsit.stakit.controller.exception.UnauthorizedException;
import dk.kvalitetsit.stakit.session.ApiKey;
import dk.kvalitetsit.stakit.session.PublicApi;
import dk.kvalitetsit.stakit.session.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiAccessInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ApiAccessInterceptor.class);
    private final UserContextService userContextService;
    private final String apiKey;

    public ApiAccessInterceptor(UserContextService userContextService, String apiKey) {
        this.userContextService = userContextService;
        this.apiKey = apiKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("Validating access for endpoint {}", request.getServletPath());
        if (!(handler instanceof HandlerMethod handlerMethod) || handlerMethod.getBeanType().equals(BasicErrorController.class)) {
            return true; // Show 404 instead of 403 on URL's that does exist.
        }

        if (isPublicEndpoint(handlerMethod)) {
            return true;
        }

        if(isApiKeyAndValidApiKey(handlerMethod, request)) {
            return true;
        }

        validateAuthorizationToken();

        logger.debug("User context is present and valid.");
        return true;
    }

    private boolean isApiKeyAndValidApiKey(HandlerMethod handlerMethod, HttpServletRequest request) {
        var isApiKeyEndpoint =  handlerMethod.getMethod().getAnnotation(ApiKey.class) != null;

        if (isApiKeyEndpoint) {
            var header = request.getHeader("X-API-KEY");

            if(header == null || !header.equals(apiKey)) {
                throw new UnauthorizedException("Invalid or missing API key.");
            }

            return true;
        }

        return false;
    }

    private void validateAuthorizationToken() {
        if (!userContextService.hasValidAuthorizationToken()) {
            throw new UnauthorizedException("Invalid authorization token");
        }
    }

    private boolean isPublicEndpoint(HandlerMethod handlerMethod) {
        var isPublicEndpoint =  handlerMethod.getMethod().getAnnotation(PublicApi.class) != null;

        if (isPublicEndpoint) {
            logger.debug("Endpoint is public");
        }

        return isPublicEndpoint;
    }
}
