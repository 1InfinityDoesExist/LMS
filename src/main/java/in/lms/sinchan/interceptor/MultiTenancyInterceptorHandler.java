package in.lms.sinchan.interceptor;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MultiTenancyInterceptorHandler extends HandlerInterceptorAdapter {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
//                    Object handler) throws Exception {
//        log.info("-----Inside MultiTenancyInterceptorHandler Class, preHandle Method-----");
//        Map uri = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//        String method = request.getMethod();
//        String authDetails = request.getHeader("authorization");
//        log.info(":::::::" + uri);
//        log.info(":::::QueryParam::: " + request.getQueryString());
//        log.info(":::::Method::::" + method);
//        log.info(":::::Header Authorization::::" + authDetails);
//        log.info(":::::URL :::: " + request.getRequestURL());
//        log.info(":::::Protocol::::" + request.getProtocol());
//        log.info(":::::LocalName:::::" + request.getLocalName());
//        log.info(":::::Port:::::" + request.getLocalPort());
//        log.info("::::Address ::::" + request.getLocalAddr() + " " + request.getRemoteAddr());
//
//
//        // Remove Bearer
//        log.info(":::::authDetails before split : {}", authDetails);
//        authDetails = authDetails.split(" ")[1];
//        log.info("::::authDetails after split:  {}", authDetails);
//
//        // split authDetails with .
//        String[] splitedAuthDetails = authDetails.split("\\.");
//        log.info("::::splitedAuthDetails : {}", splitedAuthDetails[2]);
//
//        // Decode the 3 token parts
//        if (splitedAuthDetails.length < 3) {
//            log.info(":::::Token splited size must be 3");
//            return false;
//        }
//        Base64.Decoder decoder = Base64.getDecoder();
//        String tokenDetails = new String(decoder.decode(splitedAuthDetails[1].getBytes()));
//        log.info("::::Decoded token details :{}", tokenDetails);
//
//
//        return true;
//    }


    private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";
    private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                    final Object handler) throws Exception {
        final String correlationId = getCorrelationIdFromHeader(request);
        MDC.put(CORRELATION_ID_LOG_VAR_NAME, correlationId);
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request,
                    final HttpServletResponse response,
                    final Object handler, final Exception ex) throws Exception {
        MDC.remove(CORRELATION_ID_LOG_VAR_NAME);
    }

    private String getCorrelationIdFromHeader(final HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME);
        if (StringUtils.isEmpty(correlationId)) {
            correlationId = generateUniqueCorrelationId();
        }
        return correlationId;
    }

    private String generateUniqueCorrelationId() {
        return UUID.randomUUID().toString();
    }

}
