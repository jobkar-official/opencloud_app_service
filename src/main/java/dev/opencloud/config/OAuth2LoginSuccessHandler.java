package dev.opencloud.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        response.setContentType("text/html");
        response.getWriter().write("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>GitHub Connected</title>
                </head>
                <body>
                    <script>
                        if (window.opener) {
                            window.opener.postMessage(
                                { type: 'GITHUB_CONNECTED' },
                                window.location.origin
                            );
                            window.close();
                        } else {
                            window.location.href = '/deployments/new';
                        }
                    </script>
                </body>
                </html>
                """);
        response.getWriter().flush();
    }
}