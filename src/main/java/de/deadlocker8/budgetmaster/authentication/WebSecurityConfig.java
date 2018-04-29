package de.deadlocker8.budgetmaster.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private final UserDetailsService userDetailsService;

	@Autowired
	public WebSecurityConfig(UserDetailsService userDetailsService)
	{
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf()
			.and()

			.authorizeRequests()
			.antMatchers("/css/**", "/js/**", "/font-awesome-5.0.10/**", "/images/**", "/jquery/**", "/materialize-0.100.2/**").permitAll()
			.antMatchers("/**").authenticated()
			.antMatchers("/login").permitAll()
			.and()

			.formLogin()
			.loginPage("/login")
			.successHandler((req, res, auth) -> {
				String preLoginURL = req.getSession().getAttribute("preLoginURL").toString();
				if(preLoginURL.contains("login"))
				{
					preLoginURL = "/";
				}
				redirectStrategy.sendRedirect(req, res, preLoginURL);
			})
			.permitAll()
			.and()

			.logout()
			.permitAll();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}
}