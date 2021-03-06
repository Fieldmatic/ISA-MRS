package mrsisa.project.config;

import mrsisa.project.security.auth.RestAuthenticationEntryPoint;
import mrsisa.project.security.auth.TokenAuthenticationFilter;
import mrsisa.project.security.auth.TokenUtils;
import mrsisa.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Autowired
	private UserService userService;

	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userService)
			.passwordEncoder(new BCryptPasswordEncoder());
	}

	@Autowired
	private TokenUtils tokenUtils;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

			.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()

			.authorizeRequests()
								.antMatchers("/api/auth/login").permitAll()
								.antMatchers("/api/auth/ownerRegistration").permitAll()
								.antMatchers("/api/auth/clientRegistration").permitAll()
								.antMatchers("/api/auth/instructorRegistration").permitAll()
								.antMatchers("/h2-console/**").permitAll()	// /h2-console/** ako se koristi H2 baza)
								.antMatchers("/api/foo").permitAll()
								.antMatchers("/api/cottage/all").permitAll()
								.antMatchers("/api/boat/all").permitAll()
								.antMatchers("/api/adventure/all").permitAll()
								.antMatchers("/api/cottage/getProfilePicture/{id}").permitAll()
								.antMatchers("/api/boat/getProfilePicture/{id}").permitAll()
								.antMatchers("/api/adventure/getProfilePicture/{id}").permitAll()
								.antMatchers("/api/cottage/reviewsNumber/{id}").permitAll()
								.antMatchers("/api/boat/reviewsNumber/{id}").permitAll()
								.antMatchers("/api/adventure/reviewsNumber/{id}").permitAll()
								.antMatchers("/api/bookable/getProfilePicture/{id}").permitAll()

				//proveri da li moze i instruktor da vidi vikendice
								.antMatchers("/api/cottage/get/{id}").permitAll()
								.antMatchers("/api/boat/get/{id}").permitAll()
								.antMatchers("/api/adventure/get/{id}").permitAll()
								.antMatchers("/api/action/getActions/{id}").permitAll()
				.antMatchers("/api/cottage/allAvailableByCity/{startDate}/{endDate}/{city}/{capacity}").permitAll()
				.antMatchers("/api/cottage/allAvailable/{startDate}/{endDate}/{capacity}").permitAll()

				.antMatchers("/api/boat/allAvailableByCityAndCapacity/{startDate}/{endDate}/{city}/{capacity}").permitAll()
				.antMatchers("/api/boat/allAvailable/{startDate}/{endDate}/{capacity}").permitAll()

				.antMatchers("/api/adventure/allAvailableByCityAndCapacity/{startDate}/{endDate}/{city}/{capacity}").permitAll()
				.antMatchers("/api/adventure/allAvailable/{startDate}/{endDate}/{capacity}").permitAll()
				.antMatchers("/api/review/getBookableReviews/{id}").permitAll()
				.antMatchers("/api/owner/getBookableOwner/{id}").permitAll()
				.antMatchers("/api/clients/verify/{username}").permitAll()


								.antMatchers("/api/instructor/get").permitAll()

				.anyRequest().authenticated().and()

			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll()
				.and()

			.cors().and()
			.addFilterBefore(new TokenAuthenticationFilter(tokenUtils, userService), BasicAuthenticationFilter.class);
		
		// zbog jednostavnosti primera ne koristimo Anti-CSRF token (https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
		http.csrf().disable();
	}

}
