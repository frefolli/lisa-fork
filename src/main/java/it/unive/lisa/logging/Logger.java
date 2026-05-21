package it.unive.lisa.logging;
import java.util.List;
import java.util.ArrayList;
import it.unive.lisa.analysis.avase.AvaseImplException;

public class Logger {
  public static enum LogLevel {
    FATAL(0), WARN(1), INFO(2), DEBUG(3);

    private final int rawLevel;
    private LogLevel(int rawLevel) {
      this.rawLevel = rawLevel;
    };

    public boolean isAtMost(LogLevel logLevel) {
      return this.rawLevel >= logLevel.rawLevel;
    }
  };

  private final LogLevel logLevel;
  private static Logger INSTANCE = null;

  public static void init(LogLevel logLevel) {
    if (INSTANCE != null) {
      throw new AvaseImplException("Trying to Re-Initialize the Master Logger");
    }
    INSTANCE = new Logger(logLevel);
  }

  private Logger(LogLevel logLevel) {
    this.logLevel = logLevel;
  }

  public static void logFatal(String msg) {
    if (INSTANCE.logLevel.isAtMost(LogLevel.FATAL)) {
      System.out.println("[FATAL] " + msg);
    }
  }
  public static void logWarn(String msg) {
    if (INSTANCE.logLevel.isAtMost(LogLevel.WARN)) {
      System.out.println("[WARN] " + msg);
    }
  }
  public static void logInfo(String msg) {
    if (INSTANCE.logLevel.isAtMost(LogLevel.INFO)) {
      System.out.println("[INFO] " + msg);
    }
  }
  public static void logDebug(String msg) {
    if (INSTANCE.logLevel.isAtMost(LogLevel.DEBUG)) {
      System.out.println("[DEBUG] " + msg);
    }
  }
}
