package control;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClickjackingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            // Imposta l'header X-Frame-Options
            httpResponse.setHeader("X-Frame-Options", "DENY");
            // Imposta l'header Content-Security-Policy
            httpResponse.setHeader("Content-Security-Policy", "frame-ancestors 'self'");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
