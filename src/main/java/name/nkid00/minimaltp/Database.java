package name.nkid00.minimaltp;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

// stores dynamic data
public class Database {
    public static File file;

    private Connection connection;

    public static void connect() {
        MinimalTp.LOGGER.info("Connecting database");
        if (MinimalTp.database == null) {
            MinimalTp.database = new Database();
        }
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath())) {
            MinimalTp.database.connection = connection;
        } catch (SQLException e) {
            throw new CrashException(new CrashReport("数据库连接失败", e));
        }
    }

    public static void close() {
        MinimalTp.LOGGER.info("Closing database");
        try {
            MinimalTp.database.connection.close();
        } catch (SQLException e) {
            MinimalTp.LOGGER.error("Database threw an exception when closing", (Object)e);
        }
    }
}
