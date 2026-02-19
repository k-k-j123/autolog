package com.Kaushik.Autolog.Security;

import com.Kaushik.Autolog.Entity.Users;
import com.Kaushik.Autolog.Repository.UsersRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
//used after login to validate the jwt token in every request
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UsersRepo usersRepo;
    private final AuthUtils authUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("request :{}", request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(7);
        String email = authUtils.extractEmail(jwt); //extract email from the token using auth utils
        //check if email is not null and there is no authentication in the security context and if so
        // fetch the user from the database using the email and set the authentication in the security context
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            Users user = usersRepo.findByEmail(email).orElse(null);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(token);
            log.info("Authenticated user: {}", email);
        }
        filterChain.doFilter(request, response);
    }
}
