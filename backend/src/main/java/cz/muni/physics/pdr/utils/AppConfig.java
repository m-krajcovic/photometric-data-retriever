package cz.muni.physics.pdr.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.pdr.entity.PhotometricData;
import cz.muni.physics.pdr.entity.Plugin;
import cz.muni.physics.pdr.entity.StarSurvey;
import cz.muni.physics.pdr.plugin.PhotometricDataProcessStarter;
import cz.muni.physics.pdr.plugin.ProcessStarter;
import cz.muni.physics.pdr.resolver.StarName;
import cz.muni.physics.pdr.resolver.StarResolver;
import cz.muni.physics.pdr.resolver.name.SesameNameResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Configuration
@EnableAsync
@ComponentScan(basePackages = {"cz.muni.physics.pdr.*"})
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    @Scope("prototype")
    public ProcessStarter<PhotometricData> photometricDataPluginStarter() {
        return new PhotometricDataProcessStarter();
    }

    @Bean
    public ThreadPoolTaskExecutor searchServiceExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(300);
        return executor;
    }

    @Bean
    public StarResolver<StarName> sesameNameResolver(@Value("${sesame.resolver.url}") String resolverUrl,
                                                     @Value("${sesame.test.url}") String testUrl) {
        return new SesameNameResolver(new RestTemplate(), resolverUrl, testUrl);
    }

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(new Class[]{StarSurvey.class, Plugin.class});
        return xStream;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
