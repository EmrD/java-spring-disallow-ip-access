package com.example.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Disable IP Access", version = "1.0"))
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

@RestController
@RequestMapping
class Test{
	@GetMapping("/")
	public String test(HttpServletRequest request){
		return IpChecker.getConnectionType(request);
	}
}

@Configuration
class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/**").permitAll()
				)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable);

		return http.build();
	}
}

class IpChecker {
	public static String getConnectionType(HttpServletRequest request) {
		String remoteAddr = request.getRemoteAddr();
		if (isValidIPv4(remoteAddr)) {
			return "You cannot use API with IP connection!";
		} else {
			return "Welcome to API";
		}
	}

	public static boolean isValidIPv4(String str) {
		String[] parts = str.split("\\.");
		if (parts.length != 4) {
			return false;
		}

		for (String part : parts) {
			if (!isNumeric(part)) {
				return false;
			}

			int num = Integer.parseInt(part);
			if (num < 0 || num > 255) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}