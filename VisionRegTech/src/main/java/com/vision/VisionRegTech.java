package com.vision;

import java.util.Arrays;
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableScheduling
@EnableEncryptableProperties
@EnableTransactionManagement
@EnableRetry
public class VisionRegTech {
	
	@Autowired
	public static ApplicationContext appContext;
	@Autowired
	DataSource datasource;
	 @Value("${app.cors.allowed-origins}")
	    private String allowedOrigins;
	
	public static void main(String[] args) {
		appContext = SpringApplication.run(VisionRegTech.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext appContext) {
		this.appContext = appContext;
		
		/*long ut1 = Instant.now().getEpochSecond();
        Date dNow = new Date(ut1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dNow);
        cal.add(Calendar.MINUTE, 5);
        dNow = cal.getTime();*/

        
		return args -> {
//			String[] beans = appContext.getBeanDefinitionNames();
//			Arrays.stream(beans).sorted().forEach(System.out::println);
		};
	}
	
	// imports you may need
	
	 
	// add this bean to your VisionRegTech class
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilterRegistration(
	        @Value("${app.cors.allowed-origins:http://10.16.1.60:8000}") String allowedOrigins
	) {
	    CorsConfiguration config = new CorsConfiguration();
	 
	    // split and trim allowed origins, fall back to wildcard if empty
	    if (allowedOrigins != null && !allowedOrigins.isBlank()) {
	        String[] arr = Arrays.stream(allowedOrigins.split(","))
	                             .map(String::trim)
	                             .filter(s -> !s.isEmpty())
	                             .toArray(String[]::new);
	        for (String o : arr) {
	            config.addAllowedOrigin(o);            // exact origins (safe)
	            config.addAllowedOriginPattern(o);     // pattern support (Spring 5.3+)
	        }
	    } else {
	        config.addAllowedOriginPattern("*");
	    }
	 
	    config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","HEAD"));
	    config.setAllowedHeaders(Arrays.asList("*"));
	    config.setAllowCredentials(true);  // set false if you do NOT use cookies/auth from browser
	    config.setMaxAge(3600L);
	 
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	 
	    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
	    bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // run this filter as early as possible
	    return bean;
	}
	 
	 
	   @Bean
	   public LocaleResolver localeResolver() {
	       SessionLocaleResolver slr = new SessionLocaleResolver();
	       slr.setDefaultLocale(Locale.US);
	       return slr;
	   }
	   
	 @Bean 
	 public ResourceBundleMessageSource messageSource() {
		 ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
		 rs.setBasename("messages");
		 rs.setUseCodeAsDefaultMessage(true);
		 return rs;
		 
	 }
	 
		@RequestMapping(path="/getmethod" , method=RequestMethod.GET)
		public String getLanguages(@RequestHeader("Accept-Language") String locale){
         return messageSource().getMessage("greeting",null,new Locale(locale));		
		}
}

