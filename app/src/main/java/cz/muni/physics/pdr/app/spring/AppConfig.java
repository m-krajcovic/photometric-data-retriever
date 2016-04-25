package cz.muni.physics.pdr.app.spring;

import com.thoughtworks.xstream.XStream;
import cz.muni.physics.pdr.app.utils.AppInitializer;
import cz.muni.physics.pdr.app.utils.AppInitializerImpl;
import cz.muni.physics.pdr.backend.manager.PluginManager;
import cz.muni.physics.pdr.backend.manager.PluginManagerImpl;
import cz.muni.physics.pdr.backend.manager.StarSurveyManager;
import cz.muni.physics.pdr.backend.manager.StarSurveyManagerImpl;
import cz.muni.physics.pdr.backend.repository.config.ConfigurationHolder;
import cz.muni.physics.pdr.backend.repository.config.ConfigurationHolderImpl;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepository;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepositoryImpl;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepository;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepositoryConfigImpl;
import cz.muni.physics.pdr.backend.resolver.plugin.PhotometricDataRetrieverManager;
import cz.muni.physics.pdr.backend.resolver.plugin.PhotometricDataRetrieverManagerImpl;
import cz.muni.physics.pdr.backend.resolver.vsx.VSXStarResolver;
import cz.muni.physics.pdr.backend.resolver.vsx.VSXStarResolverImpl;
import cz.muni.physics.pdr.backend.utils.BackendConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.prefs.Preferences;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Configuration
@Lazy
@ComponentScan(basePackages = {"cz.muni.physics.pdr.app.*"}, lazyInit = true)
@Import(BackendConfig.class)
public class AppConfig {

    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() throws IOException {
        PreferencesPlaceholderConfigurer preferences = new PreferencesPlaceholderConfigurer();
        preferences.setLocations(resourcePatternResolver.getResources("classpath*:application.properties"));
        preferences.setFileEncoding("UTF-8");
        preferences.setUserTreePath(AppConfig.class.getName());
        return preferences;
    }

    @Bean
    public AppInitializer appInitializer(@Value("${app.data.dir.path}") String appDataDirPath,
                                         @Value("${plugins.dir.path}") String pluginDirPath,
                                         @Value("${config.file.name}") String configFileName,
                                         @Value("${vsx.dat.file.name}") String vsxDatFileName,
                                         @Value("${vsx.ftp.url}") String vsxFtpUrl,
                                         @Value("${vsx.check.outdated}") boolean checkOutdated,
                                         Executor executor) {
        AppInitializerImpl initializer = new AppInitializerImpl(new File(appDataDirPath),
                new File(pluginDirPath),
                new File(appDataDirPath, configFileName),
                new File(appDataDirPath, vsxDatFileName),
                vsxFtpUrl,
                checkOutdated);
        initializer.setExecutor(executor);
        return initializer;
    }

    @Bean
    public ConfigurationHolder configurationHolder(XStream xStream,
                                                   @Value("${app.data.dir.path}") String appDataDirPath,
                                                   @Value("${config.file.name}") String configFileName) {
        return new ConfigurationHolderImpl(xStream, new File(appDataDirPath, configFileName));
    }

    @Bean
    public PluginRepository pluginRepository(@Value("${plugins.dir.path}") String pluginDirPath) {
        return new PluginRepositoryImpl(new File(pluginDirPath));
    }

    @Bean
    public StarSurveyRepository starSurveyRepository(ConfigurationHolder configurationHolder) {
        return new StarSurveyRepositoryConfigImpl(configurationHolder);
    }

    @Bean
    public PluginManager pluginManager(PluginRepository pluginRepository) {
        return new PluginManagerImpl(pluginRepository);
    }

    @Bean
    public StarSurveyManager starSurveyManager(PluginRepository pluginRepository,
                                               StarSurveyRepository starSurveyRepository) {
        return new StarSurveyManagerImpl(pluginRepository, starSurveyRepository);
    }

    @Bean
    public VSXStarResolver vsxStarResolver(@Value("${app.data.dir.path}") String appDataDirPath,
                                           @Value("${vsx.dat.file.name}") String vsxDatFileName) {
        return new VSXStarResolverImpl(new File(appDataDirPath, vsxDatFileName));
    }

    @Bean
    public PhotometricDataRetrieverManager photometricDataRetrieverManager(StarSurveyManager starSurveyManager,
                                                                           Executor executor) {
        return new PhotometricDataRetrieverManagerImpl(starSurveyManager, executor);
    }

    @Bean
    public ThreadPoolTaskExecutor searchServiceExecutor(@Value("${core.pool.size}") int corePoolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.prefersShortLivedTasks();
        executor.setCorePoolSize(corePoolSize); // min 2 -> max ?
        executor.setDaemon(true);
        executor.setThreadNamePrefix("Backend Thread-");
        return executor;
    }

    @Bean
    public Preferences preferences() {
        return Preferences.userRoot().node(AppConfig.class.getName());
    }

}
