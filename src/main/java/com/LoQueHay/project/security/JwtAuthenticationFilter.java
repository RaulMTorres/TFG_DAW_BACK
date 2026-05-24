package com.LoQueHay.project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserEntityDetailService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, MyUserEntityDetailService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userEmail = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                userEmail = jwtService.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                sendErrorResponse("Token expirado", e, response);
                return; // detener el filtro
            } catch (Exception e) {
                sendErrorResponse("Token inválido", e, response);
                return;
            }
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                Claims claims = jwtService.extractAllClaims(jwt);

                @SuppressWarnings("unchecked")
                List<String> permisos = (List<String>) claims.get("permissions");

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (permisos != null) {
                    for (String permiso : permisos) {
                        authorities.add(new SimpleGrantedAuthority(permiso));
                    }
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(String mensaje, Exception ex, HttpServletResponse response) throws IOException {
        String json = String.format(
                "{\"httpStatus\": %d, \"message\": \"%s\", \"code\": \"%s\", \"backendMessage\": \"%s\"}",
                HttpServletResponse.SC_UNAUTHORIZED,
                mensaje,
                "AUTH_ERROR",
                ex.getMessage()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
