package in.lms.sinchan.interceptor;

import java.util.Base64;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MultiTenancyInterceptorHandler extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                    Object handler) throws Exception {
        log.info("-----Inside MultiTenancyInterceptorHandler Class, preHandle Method-----");
        Map uri = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String method = request.getMethod();
        String authDetails = request.getHeader("authorization");
        log.info(":::::::" + uri);
        log.info(":::::QueryParam::: " + request.getQueryString());
        log.info(":::::Method::::" + method);
        log.info(":::::Header Authorization::::" + authDetails);
        log.info(":::::URL :::: " + request.getRequestURL());
        log.info(":::::Protocol::::" + request.getProtocol());
        log.info(":::::LocalName:::::" + request.getLocalName());
        log.info(":::::Port:::::" + request.getLocalPort());
        log.info("::::Address ::::" + request.getLocalAddr() + " " + request.getRemoteAddr());


        // Remove Bearer
        log.info(":::::authDetails before split : {}", authDetails);
        authDetails = authDetails.split(" ")[1];
        log.info("::::authDetails after split:  {}", authDetails);

        // split authDetails with .
        String[] splitedAuthDetails = authDetails.split("\\.");
        log.info("::::splitedAuthDetails : {}", splitedAuthDetails[2]);

        // Decode the 3 splited partes
        if (splitedAuthDetails.length < 3) {
            log.info(":::::Token splited size must be 3");
            return false;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        String tokenDetails = new String(decoder.decode(splitedAuthDetails[1].getBytes()));
        log.info("::::Decoded token details :{}", tokenDetails);

        return true;
    }

}
