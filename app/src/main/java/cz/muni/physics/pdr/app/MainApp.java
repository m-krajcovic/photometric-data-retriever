package cz.muni.physics.pdr.app;

import com.sun.javafx.application.LauncherImpl;
import cz.muni.physics.pdr.app.spring.AppConfig;
import cz.muni.physics.pdr.app.utils.AppInitializer;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class MainApp extends Application {

    private final static Logger logger = LogManager.getLogger(MainApp.class);

    private AppConfig app;
    private AppInitializer initializer;

    public MainApp() {

    }

    public static void main(String[] args) {

        try {
            Preferences.userRoot().clear();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

//        launch(MainApp.class);
        LauncherImpl.launchApplication(MainApp.class, MainPreloader.class, args);

//
//        XStream xStream = new XStream();
//        Map<String, Pattern> regexes = new HashMap<>();
//        regexes.put("NSVS",Pattern.compile("(?<nsvs>NSVS\\s(?<nsvsID>\\d*))"));
//        regexes.put("NSV",Pattern.compile("(?<nsv>NSV\\s(?<nsvID>\\d*))"));
//        regexes.put("CRTS",Pattern.compile("(?<crts>CRTS\\s(?<crtsID>\\d*))"));
//        regexes.put("HIP",Pattern.compile("(?<hip>HIP\\s(?<hipID>\\d*))"));
//        regexes.put("HIC",Pattern.compile("(?<hic>HIC\\s(?<hicID>\\d*))"));
//        regexes.put("Wolf",Pattern.compile("(?<wolf>Wolf\\s(?<wolfID>\\d*))"));
//        regexes.put("SBC9",Pattern.compile("(?<sbc9>SBC9\\s(?<sbc9ID>\\d*))"));
//        regexes.put("TYC",Pattern.compile("(?<tyc>TYC\\s(?<tycID>.*))"));
//        regexes.put("2MASS",Pattern.compile("(?<tmass>2MASS\\s(?<tmassID>.*))"));
//        regexes.put("GCVS",Pattern.compile("(?<gcvs>V\\*\\s(?<gcvsID>.*))"));
//        regexes.put("ROTSE1",Pattern.compile("(?<rotse1>ROTSE1\\s(?<rotse1ID>.*))"));
//        regexes.put("RX",Pattern.compile("(?<rx>RX\\s(?<rxID>.*))"));
//        regexes.put("SDSS",Pattern.compile("(?<sdss>SDSS\\s(?<sdssID>.*))"));
//        regexes.put("AN",Pattern.compile("(?<an>AN\\s(?<anID>.*))"));
//        regexes.put("NGP",Pattern.compile("(?<ngp>NGP\\s(?<ngpID>.*))"));
//        regexes.put("1RXS",Pattern.compile("(?<orxs>1RXS\\s(?<orxsID>.*))"));
//        regexes.put("2PBC",Pattern.compile("(?<tpbc>2PBC\\\\s(?<tpbcID>.*))"));
//        regexes.put("PBC",Pattern.compile("(?<pbc>PBC\\s(?<pbcID>.*))"));
//        regexes.put("SAXWFC",Pattern.compile("(?<saxwfc>SAXWFC\\s(?<saxwfcID>.*))"));
//        regexes.put("[KRL2007]",Pattern.compile("(?<krl2007>\\[KRL2007\\]\\s(?<krl2007ID>.*))"));
//        regexes.put("[FS2003]",Pattern.compile("(?<fs2003>\\[FS2003\\]\\s(?<fs2003ID>.*))"));
//        regexes.put("[TES2010]",Pattern.compile("(?<tes2010>\\[TES2010\\]\\s(?<tes2010ID>.*))"));
//        regexes.put("[BTM2013]",Pattern.compile("(?<btm2013>\\[BTM2013\\]\\s(?<btm2013ID>.*))"));
//        regexes.put("3A",Pattern.compile("(?<ta>3A\\s(?<taID>.*))"));
//        regexes.put("ALS",Pattern.compile("(?<als>ALS\\s(?<alsID>.*))"));
//        regexes.put("BD+42",Pattern.compile("(?<bd42>BD+42\\s(?<bd42ID>.*))"));
//        regexes.put("BD+40",Pattern.compile("(?<bd40>BD+40\\s(?<bd40ID>.*))"));
//        regexes.put("CSI+42",Pattern.compile("(?<csi42>CSI+42\\s(?<csi42ID>.*))"));
//        regexes.put("1E",Pattern.compile("(?<oe>1E\\s(?<oeID>.*))"));
//        regexes.put("2E",Pattern.compile("(?<te>2E\\s(?<teID>.*))"));
//        regexes.put("EM*",Pattern.compile("(?<ems>EM\\*\\s(?<emsID>.*))"));
//        regexes.put("V*",Pattern.compile("(?<vs>V\\*\\s(?<vsID>.*))"));
//        regexes.put("2EUVE",Pattern.compile("(?<teuve>2EUVE\\s(?<teuveID>.*))"));
//        regexes.put("EUVE",Pattern.compile("(?<euve>EUVE\\s(?<euveID>.*))"));
//        regexes.put("GCRV",Pattern.compile("(?<gcrv>GCRV\\s(?<gcrvID>.*))"));
//        regexes.put("GEN#",Pattern.compile("(?<gen>GEN#\\s(?<genID>.*))"));
//        regexes.put("1H",Pattern.compile("(?<oh>1H\\s(?<ohID>.*))"));
//        regexes.put("HD",Pattern.compile("(?<hd>HD\\s(?<hdID>.*))"));
//        regexes.put("INTEGRAL1",Pattern.compile("(?<integral1>INTEGRAL1\\s(?<integral1ID>.*))"));
//        regexes.put("KPD",Pattern.compile("(?<kpd>KPD\\s(?<kpdID>.*))"));
//        regexes.put("PBC",Pattern.compile("(?<pbc>PBC\\s(?<pbcID>.*))"));
//        regexes.put("PLX",Pattern.compile("(?<plx>PLX\\s(?<plxID>.*))"));
//        regexes.put("PM",Pattern.compile("(?<pm>PM\\s(?<pmID>.*))"));
//        regexes.put("2RE",Pattern.compile("(?<re>2RE\\s(?<reID>.*))"));
//        regexes.put("RE",Pattern.compile("(?<re>RE\\s(?<reID>.*))"));
//        regexes.put("1RXS",Pattern.compile("(?<rxs>1RXS\\s(?<rxsID>.*))"));
//        regexes.put("SBC7",Pattern.compile("(?<sbc7>SBC7\\s(?<sbc7ID>.*))"));
//        regexes.put("SBC9",Pattern.compile("(?<sbc9>SBC9\\s(?<sbc9ID>.*))"));
//        regexes.put("SV*",Pattern.compile("(?<svs>SV\\*\\s(?<svsID>.*))"));
//        regexes.put("SWIFT",Pattern.compile("(?<swift>SWIFT\\s(?<swiftID>.*))"));
//        regexes.put("UBV",Pattern.compile("(?<ubv>UBV\\s(?<ubvID>.*))"));
//        regexes.put("UCAC2",Pattern.compile("(?<ucac2>UCAC2\\s(?<ucac2ID>.*))"));
//        regexes.put("UCAC3",Pattern.compile("(?<ucac3>UCAC3\\s(?<ucac3ID>.*))"));
//        regexes.put("USNO",Pattern.compile("(?<usno>USNO\\s(?<usnoID>.*))"));
//        regexes.put("[BM83]",Pattern.compile("(?<bm83>\\[BM83\\]\\s(?<bm83ID>.*))"));
//        regexes.put("[FS2003]",Pattern.compile("(?<fs2003>\\[FS2003\\]\\s(?<fs2003ID>.*))"));
//        regexes.put("[KRL2007b]",Pattern.compile("(?<krl2007b>\\[KRL2007b\\]\\s(?<krl2007bID>.*))"));
//        regexes.put("[KW97]",Pattern.compile("(?<kw97>\\[KW97\\]\\s(?<kw97ID>.*))"));
//        regexes.put("AAVSO",Pattern.compile("(?<aavso>AAVSO\\s(?<aavsoID>.*))"));
//        regexes.put("[SPB96]",Pattern.compile("(?<spb96>\\[SPB96\\]\\s(?<spb96ID>.*))"));
//        regexes.put("GSC",Pattern.compile("(?<gsc>GSC\\s(?<gscID>.*))"));
//        regexes.put("1SWASP",Pattern.compile("(?<swasp>1SWASP\\s(?<swaspID>.*))"));
//        regexes.put("ASAS",Pattern.compile("(?<asas>ASAS\\s(?<asasID>.*))"));
//        regexes.put("IOMC",Pattern.compile("(?<iomc>IOMC\\s(?<iomcID>.*))"));
//        regexes.put("GSC",Pattern.compile("(?<gsc>GSC\\s(?<gscID>.*))"));
//        regexes.put("LINEAR",Pattern.compile("(?<linear>LINEAR\\s(?<linearID>.*))"));
//
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
        initializer.start();

        app.initRootLayout();
        app.showSearch();

    }

    @Override
    public void init() throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        app = context.getBean(AppConfig.class);
        initializer = context.getBean(AppInitializer.class);
        initializer.initialize(this);
    }

    @Override
    public void stop() {
        app.getPrimaryStage().close();
    }
}
