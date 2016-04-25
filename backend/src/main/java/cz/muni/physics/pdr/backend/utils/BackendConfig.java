package cz.muni.physics.pdr.backend.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.pdr.backend.entity.Configuration;
import cz.muni.physics.pdr.backend.entity.PhotometricData;
import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.resolver.plugin.PhotometricDataProcessStarter;
import cz.muni.physics.pdr.backend.resolver.plugin.ProcessStarter;
import cz.muni.physics.pdr.backend.resolver.sesame.SesameNameResolver;
import cz.muni.physics.pdr.backend.resolver.sesame.SesameNameResolverImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Lazy
@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = {"cz.muni.physics.pdr.backend.*"}, lazyInit = true)
@PropertySource("classpath:backend.properties")
public class BackendConfig {

    @Bean
    @Scope("prototype")
    public ProcessStarter<PhotometricData> photometricDataPluginStarter() {
        return new PhotometricDataProcessStarter();
    }

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(new Class[]{Configuration.class,
                StarSurvey.class, Plugin.class});
        return xStream;
    }

    @Bean
    public RestOperations restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SesameNameResolver sesameNameResolver(RestOperations restTemplate,
                                                     @Value("${sesame.resolver.url}") String resolverUrl,
                                                     @Value("${sesame.test.url}") String testUrl){
        return new SesameNameResolverImpl(restTemplate, resolverUrl, testUrl);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
        properties.setIgnoreUnresolvablePlaceholders(true);
        return properties;
    }

}
