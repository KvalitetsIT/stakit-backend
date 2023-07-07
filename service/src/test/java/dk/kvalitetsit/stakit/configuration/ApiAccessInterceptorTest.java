package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.controller.exception.UnauthorizedException;
import dk.kvalitetsit.stakit.controller.interceptor.ApiAccessInterceptor;
import dk.kvalitetsit.stakit.session.ApiKey;
import dk.kvalitetsit.stakit.session.PublicApi;
import dk.kvalitetsit.stakit.session.UserContextService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.method.HandlerMethod;


import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApiAccessInterceptorTest {
    private UserContextService userContextService;
    private ApiAccessInterceptor apiAccessInterceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HandlerMethod handlerMethod;
    private String apiKey;

    @Before
    public void setUp() {
        apiKey = "API_KEY";
        userContextService = Mockito.mock(UserContextService.class);
        apiAccessInterceptor = new ApiAccessInterceptor(userContextService, apiKey);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        handlerMethod = Mockito.mock(HandlerMethod.class);
    }

    @Test
    public void testPreHandleWithoutPublicApiReturnsThrowsException() throws NoSuchMethodException {
        Mockito.when(handlerMethod.getMethod()).thenReturn(this.getClass().getMethod("methodWithoutAnnotations"));
        Mockito.doReturn(this.getClass()).when(handlerMethod).getBeanType();
        Mockito.when(userContextService.hasValidAuthorizationToken()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> apiAccessInterceptor.preHandle(request, response, handlerMethod));
    }

    @Test
    public void testPreHandlePublicApiReturnsTrue() throws Exception {
        Mockito.when(handlerMethod.getMethod()).thenReturn(this.getClass().getMethod("methodWithPublicEndpointAnnotation"));
        Mockito.doReturn(this.getClass()).when(handlerMethod).getBeanType();
        Mockito.when(userContextService.hasValidAuthorizationToken()).thenReturn(false);

        boolean result = apiAccessInterceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
        Mockito.verifyNoInteractions(response);
    }

    @Test
    public void testPreHandleApiKeyReturnsTrue() throws Exception {
        Mockito.when(handlerMethod.getMethod()).thenReturn(this.getClass().getMethod("methodWithPublicEndpointAnnotation"));
        Mockito.doReturn(this.getClass()).when(handlerMethod).getBeanType();
        Mockito.when(userContextService.hasValidAuthorizationToken()).thenReturn(false);
        Mockito.when(request.getHeader("X-API-KEY")).thenReturn(apiKey);

        boolean result = apiAccessInterceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
        Mockito.verifyNoInteractions(response);
    }

    @Test
    public void testPreHandleApiKeyInvalidKeyReturnsFalse() throws Exception {
        Mockito.when(handlerMethod.getMethod()).thenReturn(this.getClass().getMethod("methodWithApiKey"));
        Mockito.doReturn(this.getClass()).when(handlerMethod).getBeanType();
        Mockito.when(userContextService.hasValidAuthorizationToken()).thenReturn(false);
        Mockito.when(request.getHeader("X-API-KEY")).thenReturn(apiKey);

        boolean result = apiAccessInterceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
        Mockito.verifyNoInteractions(response);
    }

    @Test
    public void testPreHandleApiKeyMissingKeyReturnsFalse() throws Exception {
        Mockito.when(handlerMethod.getMethod()).thenReturn(this.getClass().getMethod("methodWithApiKey"));
        Mockito.doReturn(this.getClass()).when(handlerMethod).getBeanType();
        Mockito.when(userContextService.hasValidAuthorizationToken()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> apiAccessInterceptor.preHandle(request, response, handlerMethod));

        Mockito.verifyNoInteractions(response);
    }

    @Test
    public void preHandle_WithoutPublicAnnotationValidTokenAndKnownUser_ReturnsTrue() throws Exception {
        Mockito.when(handlerMethod.getMethod()).thenReturn(this.getClass().getMethod("methodWithApiKey"));
        Mockito.doReturn(this.getClass()).when(handlerMethod).getBeanType();
        Mockito.when(userContextService.hasValidAuthorizationToken()).thenReturn(true);
        Mockito.when(request.getHeader("X-API-KEY")).thenReturn("INVALID");

        assertThrows(UnauthorizedException.class, () -> apiAccessInterceptor.preHandle(request, response, handlerMethod));

        Mockito.verifyNoInteractions(response);
    }

    public void methodWithoutAnnotations() {
        // Empty
    }

    @PublicApi
    public void methodWithPublicEndpointAnnotation() {
        // Empty
    }

    @ApiKey
    public void methodWithApiKey() {

    }
}