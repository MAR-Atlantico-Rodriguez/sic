package sic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import sic.service.impl.AfipWebServiceSOAPClient;

@SpringBootApplication
@EnableScheduling
public class App extends WebMvcConfigurerAdapter {
    
    @Bean    
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }
    
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in pom.xml        
        marshaller.setContextPaths("afip.wsaa.wsdl", "afip.wsfe.wsdl");
        return marshaller;
    }
    
    @Bean
    public AfipWebServiceSOAPClient afipWebServiceSOAPClient(Jaxb2Marshaller marshaller) {
        AfipWebServiceSOAPClient afipClient = new AfipWebServiceSOAPClient();
        afipClient.setMarshaller(marshaller);
        afipClient.setUnmarshaller(marshaller);
        return afipClient;
    }   
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/*/login");
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }    
}