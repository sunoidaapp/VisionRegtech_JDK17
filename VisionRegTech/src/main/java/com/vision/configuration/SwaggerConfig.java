//package com.vision.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import io.swagger.v3.oas.models.info.Contact;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//
//
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//	@Bean
//	public Docket product//Api() {
//		return new Docket(DocumentationType.SWAGGER_2).select()
//				.apis(RequestHandlerSelectors.basePackage("com.vision.controller")).paths(Predicates.not(PathSelectors.regex("/options-controller.*"))).build()
//				.apiInfo(metaData());
//		
////		.paths(Predicates.not(PathSelectors.regex("/OptionsController.*"))).build().apiInfo(metaData());
//	}
//
//	private ApiInfo metaData() {
//		ApiInfo apiInfo = new ApiInfo("Spring Boot REST API", "Spring Boot REST API for Online Store", "1.0",
//				"Terms of service", new Contact("Sunoida", "", "sunoidaSupport@sunoida.com"),
//				"Apache License Version 2.0", "https://www.apache.org/licenses/LICENSE-2.0");
//		return apiInfo;
//	}
//}