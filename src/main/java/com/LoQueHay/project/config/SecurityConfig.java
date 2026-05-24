package com.LoQueHay.project.config;


import com.LoQueHay.project.security.JwtAuthenticationFilter;
import com.LoQueHay.project.security.JwtService;
import com.LoQueHay.project.security.MyUserEntityDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {


    private final MyUserEntityDetailService myUserEntityDetailService;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(MyUserEntityDetailService myUserEntityDetailService, JwtAuthenticationFilter jwtFilter) {
        this.myUserEntityDetailService = myUserEntityDetailService;
        this.jwtFilter = jwtFilter;
    }


    // Definimos el codificador de contraseñas para Spring Security
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Spring usa este AuthenticationManager para procesar login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();

    }


    // Este AuthenticationProvider conecta nuestro UserDetailsService y codificador
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myUserEntityDetailService); // usamos nuestro servicio personalizado
        provider.setPasswordEncoder(passwordEncoder());// y el codificador definido
        return provider;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Desactivar protección CSRF (no se usa en APIs JWT)
        http.csrf(csrf -> csrf.disable());

        // 2. No usar sesiones (API sin estado)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 3. Definir reglas de acceso según las rutas
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/login", "/auth/register-owner", "/auth/forgot-password", "/auth/reset-password", "/auth/reset-password/validate").permitAll()      // /auth/** es público
                .requestMatchers("/admin/**").hasAuthority("admin:access")
                .anyRequest().authenticated()                  // cualquier otra ruta necesita token válido
        );

        // 4. Establecer el proveedor de autenticación (tu servicio + codificador)
        http.authenticationProvider(authenticationProvider());

        // 5. Agregar el filtro JWT antes del filtro por defecto
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 6. Devolver el filtro de seguridad construido
        return http.build();
    }





}
