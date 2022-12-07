package dk.kvalitetsit.stakit.configuration;

import dk.kvalitetsit.stakit.controller.exception.ForbiddenException;
import dk.kvalitetsit.stakit.controller.interceptor.ApiAccessInterceptor;
import dk.kvalitetsit.stakit.session.PublicApi;
import dk.kvalitetsit.stakit.session.UserContextService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApiAccessInterceptorTest {
    private UserContextService userContextService;
    private ApiAccessInterceptor apiAccessInterceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HandlerMethod handlerMethod;

    @Before
    public void setUp() {
        userContextService = Mockito.mock(UserContextService.class);
        apiAccessInterceptor = new ApiAccessInterceptor(userContextService);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        handlerMethod = Mockito.mock(HandlerMethod.class);
    }

    @Test
    public void testPreHandleWithoutPublicApiReturnsThrowsException() throws NoSuchMethodException {
        Mockito.when(handlerMethod.getMethod()).thenReturn(this.getClass().getMethod("methodWithoutAnnotations"));
        Mockito.doReturn(this.getClass()).when(handlerMethod).getBeanType();
        Mockito.when(userContextService.hasValidAuthorizationToken()).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> apiAccessInterceptor.preHandle(request, response, handlerMethod));
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
    public void preHandle_WithoutPublicAnnotationValidTokenAndKnownUser_ReturnsTrue() throws Exception {
        Mockito.when(handlerMethod.getMethod()).thenReturn(this.getClass().getMethod("methodWithoutAnnotations"));
        Mockito.doReturn(this.getClass()).when(handlerMethod).getBeanType();
        Mockito.when(userContextService.hasValidAuthorizationToken()).thenReturn(true);

        boolean result = apiAccessInterceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
        Mockito.verifyNoInteractions(response);
    }

    public void methodWithoutAnnotations() {
        // Empty
    }

    @PublicApi
    public void methodWithPublicEndpointAnnotation() {
        // Empty
    }
}