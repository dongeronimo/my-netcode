package net.dongeronimo.netcode.setup;
import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthenticationFilter extends OncePerRequestFilter{
    private JwtService jwtService;
    public AuthenticationFilter(JwtService _JwtService){
        jwtService = _JwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String jws = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(jws!=null){
            String user = jwtService.getAuthUser(request);
            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, 
                Collections.emptyList());
            SecurityContextHolder.getContext()
            .setAuthentication(auth);
            
        }
        filterChain.doFilter(request, response);
    }
    
}
