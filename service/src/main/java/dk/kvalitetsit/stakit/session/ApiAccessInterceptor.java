package dk.kvalitetsit.stakit.session;

import dk.kvalitetsit.stakit.session.exception.InvalidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiAccessInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(ApiAccessInterceptor.class);
    private final UserContextService userContextService;

    public ApiAccessInterceptor(UserContextService userContextService) {
        this.userContextService = userContextService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("Validating access for endpoint {}", request.getServletPath());
//        if (!(handler instanceof HandlerMethod handlerMethod)) { // TODO Hvorfor?
//            return true; // Results in a 404 not found
//        }

        if (isPublicEndpoint((HandlerMethod) handler)) {
            return true;
        }

        validateAuthorizationToken();

        logger.debug("User context is present and valid.");
        return true;
    }

    private void validateAuthorizationToken() {
        if (!userContextService.hasValidAuthorizationToken()) {
            throw new InvalidTokenException("Invalid authorization token");
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
