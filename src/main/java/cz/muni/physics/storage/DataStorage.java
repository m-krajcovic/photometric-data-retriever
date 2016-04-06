package cz.muni.physics.storage;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.model.DatabaseRecord;
import cz.muni.physics.storage.converter.DatabaseRecordConverter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO this is horrible, make it better somehow?
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
public class DataStorage {
    private static final File datadir = new File(System.getProperty("user.home"), ".pdr");
    private static final File dbRecordsFile = new File(datadir, "db_records.xml");
    private static final File pluginsDir = new File(datadir, "plugins");

    static {
        if (!datadir.exists()) {
            datadir.mkdir();
        }
        if (!dbRecordsFile.exists()){
            try {
                System.out.println("creating file");
                dbRecordsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveDbRecords(List<DatabaseRecord> records) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new DatabaseRecordConverter());
        try (Writer writer = new FileWriter(dbRecordsFile)){
            xStream.toXML(records, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<DatabaseRecord> loadDbRecords(){
        List<DatabaseRecord> result = new ArrayList<>();
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new DatabaseRecordConverter());
        try (Reader reader = new FileReader(dbRecordsFile)){
            result.addAll((List<DatabaseRecord>) xStream.fromXML(reader));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static File getDatadir() {
        return datadir;
    }

    public static File getDbRecordsFile() {
        return dbRecordsFile;
    }

    public static File getPluginsDir() {
        return pluginsDir;
    }
}
