package cz.muni.physics.pdr.app.spring;

import com.thoughtworks.xstream.XStream;
import cz.muni.physics.pdr.app.MainApp;
import cz.muni.physics.pdr.app.updater.UpdaterService;
import cz.muni.physics.pdr.app.updater.UpdaterStatus;
import cz.muni.physics.pdr.app.updater.UpdaterUnavailableException;
import cz.muni.physics.pdr.app.updater.WindowsUpdaterService;
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
import cz.muni.physics.pdr.backend.utils.BackendConfig;
import org.apache.commons.lang3.SystemUtils;
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
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
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
        preferences.setUserTreePath(Preferences.userNodeForPackage(MainApp.class).absolutePath());
        preferences.afterPropertiesSet();
        return preferences;
    }

    @Bean
    public AppInitializer appInitializer(File appDataDir,
                                         @Value("${config.file.name}") String configFileName,
                                         Executor executor) {
        AppInitializerImpl initializer = new AppInitializerImpl(appDataDir,
                new File(appDataDir, configFileName));
        initializer.setExecutor(executor);
        return initializer;
    }

    @Bean
    public File appDataDir(@Value("${app.data.dir.path}") String appDataDirPath) {
        return new File(appDataDirPath);
    }

    @Bean
    public File pluginsDir(@Value("${plugins.dir.path}") String pluginDirPath) {
        return new File(pluginDirPath);
    }

    @Bean
    public ConfigurationHolder configurationHolder(XStream xStream,
                                                   File appDataDir,
                                                   @Value("${config.file.name}") String configFileName) {
        return new ConfigurationHolderImpl(xStream, new File(appDataDir, configFileName));
    }

    @Bean
    public PluginRepository pluginRepository(File pluginsDir) {
        return new PluginRepositoryImpl(pluginsDir);
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
    public PhotometricDataRetrieverManager photometricDataRetrieverManager(StarSurveyManager starSurveyManager,
                                                                           Executor executor,
                                                                           @Value("${threshold.min}") Double minThreshold,
                                                                           @Value("${threshold.max}") Double maxThreshold) {
        return new PhotometricDataRetrieverManagerImpl(starSurveyManager, executor, minThreshold, maxThreshold);
    }

    @Bean
    public ThreadPoolTaskExecutor searchServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.prefersShortLivedTasks();
        executor.setCorePoolSize(16);
        executor.setDaemon(true);
        executor.setThreadNamePrefix("Backend Thread-");
        return executor;
    }

    @Bean
    public ResourceBundle resources() {
        return ResourceBundle.getBundle("i18n/bundle");
    }

    @Bean
    public Preferences preferences() {
        return Preferences.userNodeForPackage(MainApp.class);
    }

    @Bean
    public UpdaterService updaterService() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return new WindowsUpdaterService();
        } else
            return new UpdaterService() {
                @Override
                public File downloadUpdate(UpdaterStatus status) throws UpdaterUnavailableException {
                    return null;
                }

                @Override
                public boolean applyUpdate(File update) {
                    return false;
                }

                @Override
                public UpdaterStatus status() throws UpdaterUnavailableException {
                    return null;
                }

                @Override
                public String getCurrentVersion() {
                    return null;
                }

                @Override
                public File downloadUpdate(UpdaterStatus serverStatus, BiConsumer<Long, Long> consumer) throws UpdaterUnavailableException {
                    return null;
                }
            };
    }

}
