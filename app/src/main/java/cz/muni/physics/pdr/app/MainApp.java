package cz.muni.physics.pdr.app;

import com.sun.javafx.application.LauncherImpl;
import cz.muni.physics.pdr.app.utils.AppInitializer;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.app.utils.ScreenConfig;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.prefs.Preferences;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class MainApp extends Application {

    private final static Logger logger = LogManager.getLogger(MainApp.class);

    private ScreenConfig app;
    private AppInitializer initializer;

    public MainApp() {

        Preferences prefs = Preferences.userRoot();
        prefs.putInt("core.pool.size", 8);
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainApp.class, MainPreloader.class, args);

//        Map<String, String> params = new HashMap<>();
//        params.put("vsx.id", "123123");
//        params.put("nsvs.id", "9897");
//        params.put("nsvs.full", "NSVS 9897");
//
//        String url = "http://www.google.com?query={vsx.id}/{nsvs.id}/{nsvs.full}";
//        String string = "${vsx.id}/${nsvs.id}/${nsvs.full}";
//
//        UriTemplate template = new UriTemplate(url);
//        System.out.println(template.expand(params));
//        System.out.println(StrSubstitutor.replace(string, params));
//
//        XStream xStream = new XStream();
//        List<String> regexes = new ArrayList<>();
//        regexes.add("(?<nsvs>NSVS\\s(?<nsvsID>\\d*))");
//        regexes.add("(?<nsv>NSV\\s(?<nsvID>\\d*))");
//        regexes.add("(?<crts>CRTS\\s(?<crtsID>\\d*))");
//        regexes.add("(?<hip>HIP\\s(?<hipID>\\d*))");
//        regexes.add("(?<hic>HIC\\s(?<hicID>\\d*))");
//        regexes.add("(?<wolf>Wolf\\s(?<wolfID>\\d*))");
//        regexes.add("(?<sbc9>SBC9\\s(?<sbc9ID>\\d*))");
//        regexes.add("(?<tyc>TYC\\s(?<tycID>.*))");
//        regexes.add("(?<2mass>2MASS\\s(?<2massID>.*))");
//        regexes.add("(?<gcvs>V\\*\\s(?<gcvsID>.*))");
//        regexes.add("(?<rotse1>ROTSE1\\s(?<rotse1ID>.*))");
//        regexes.add("(?<rx>RX\\s(?<rxID>.*))");
//        regexes.add("(?<sdss>SDSS\\s(?<sdssID>.*))");
//        regexes.add("(?<an>AN\\s(?<anID>.*))");
//        regexes.add("(?<ngp>NGP\\s(?<ngpID>.*))");
//        regexes.add("(?<orxs>1RXS\\s(?<orxsID>.*))");
//        regexes.add("(?<tpbc>2PBC\\\\s(?<tpbcID>.*))");
//        regexes.add("(?<pbc>PBC\\s(?<pbcID>.*))");
//        regexes.add("(?<saxwfc>SAXWFC\\s(?<saxwfcID>.*))");
//        regexes.add("(?<krl2007>\\[KRL2007\\]\\s(?<krl2007ID>.*))");
//        regexes.add("(?<fs2003>\\[FS2003\\]\\s(?<fs2003ID>.*))");
//        regexes.add("(?<tes2010>\\[TES2010\\]\\s(?<tes2010ID>.*))");
//        regexes.add("(?<btm2013>\\[BTM2013\\]\\s(?<btm2013ID>.*))");
//        regexes.add("(?<ta>3A\\s(?<taID>.*))");
//        regexes.add("(?<als>ALS\\s(?<alsID>.*))");
//        regexes.add("(?<bd42>BD+42\\s(?<bd42ID>.*))");
//        regexes.add("(?<bd40>BD+40\\s(?<bd40ID>.*))");
//        regexes.add("(?<csi42>CSI+42\\s(?<csi42ID>.*))");
//        regexes.add("(?<oe>1E\\s(?<oeID>.*))");
//        regexes.add("(?<te>2E\\s(?<teID>.*))");
//        regexes.add("(?<ems>EM\\*\\s(?<emsID>.*))");
//        regexes.add("(?<vs>V\\*\\s(?<vsID>.*))");
//        regexes.add("(?<teuve>2EUVE\\s(?<teuveID>.*))");
//        regexes.add("(?<euve>EUVE\\s(?<euveID>.*))");
//        regexes.add("(?<gcrv>GCRV\\s(?<gcrvID>.*))");
//        regexes.add("(?<gen>GEN#\\s(?<genID>.*))");
//        regexes.add("(?<oh>1H\\s(?<ohID>.*))");
//        regexes.add("(?<hd>HD\\s(?<hdID>.*))");
//        regexes.add("(?<integral1>INTEGRAL1\\s(?<integral1ID>.*))");
//        regexes.add("(?<kpd>KPD\\s(?<kpdID>.*))");
//        regexes.add("(?<pbc>PBC\\s(?<pbcID>.*))");
//        regexes.add("(?<plx>PLX\\s(?<plxID>.*))");
//        regexes.add("(?<pm>PM\\s(?<pmID>.*))");
//        regexes.add("(?<re>2RE\\s(?<reID>.*))");
//        regexes.add("(?<re>RE\\s(?<reID>.*))");
//        regexes.add("(?<rxs>1RXS\\s(?<rxsID>.*))");
//        regexes.add("(?<sbc7>SBC7\\s(?<sbc7ID>.*))");
//        regexes.add("(?<sbc9>SBC9\\s(?<sbc9ID>.*))");
//        regexes.add("(?<svs>SV\\*\\s(?<svsID>.*))");
//        regexes.add("(?<swift>SWIFT\\s(?<swiftID>.*))");
//        regexes.add("(?<ubv>UBV\\s(?<ubvID>.*))");
//        regexes.add("(?<ucac2>UCAC2\\s(?<ucac2ID>.*))");
//        regexes.add("(?<ucac3>UCAC3\\s(?<ucac3ID>.*))");
//        regexes.add("(?<usno>USNO\\s(?<usnoID>.*))");
//        regexes.add("(?<bm83>\\[BM83\\]\\s(?<bm83ID>.*))");
//        regexes.add("(?<fs2003>\\[FS2003\\]\\s(?<fs2003ID>.*))");
//        regexes.add("(?<krl2007b>\\[KRL2007b\\]\\s(?<krl2007bID>.*))");
//        regexes.add("(?<kw97>\\[KW97\\]\\s(?<kw97ID>.*))");
//        regexes.add("(?<aavso>AAVSO\\s(?<aavsoID>.*))");
//        regexes.add("(?<spb96>\\[SPB96\\]\\s(?<spb96ID>.*))");
//        regexes.add("(?<gsc>GSC\\s(?<gscID>.*))");
//        regexes.add("(?<swasp>1SWASP\\s(?<swaspID>.*))");
//        regexes.add("(?<asas>ASAS\\s(?<asasID>.*))");
//        regexes.add("(?<iomc>IOMC\\s(?<iomcID>.*))");
//        regexes.add("(?<gsc>GSC\\s(?<gscID>.*))");
//        regexes.add("(?<linear>LINEAR\\s(?<linearID>.*))");
//        xStream.toXML(regexes, System.out);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            e = e.getCause().getCause();
            logger.error(e.getMessage(), e);
            FXMLUtils.showExceptionAlert("Something went terribly wrong!", "Your best bet is to restart this application", e.getMessage(), e);
        });
        primaryStage.setTitle(app.getName());
        primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream(app.getIconPath())));
        app.setPrimaryStage(primaryStage);
        app.initRootLayout();
        app.showSearch();

        initializer.showInitExceptions();
        initializer.showInitErrors();
    }

    @Override
    public void init() throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(ScreenConfig.class);
        app = context.getBean(ScreenConfig.class);
        initializer = context.getBean(AppInitializer.class);
        initializer.initialize(this);
    }
}
