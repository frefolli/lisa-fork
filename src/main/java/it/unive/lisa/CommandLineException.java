package it.unive.lisa;
import java.lang.IllegalArgumentException;

/**
 * Exception thrown whenever there is a Command Line parsing error.
 * 
 * @author Francesco Refolli
 */
public class CommandLineException extends IllegalArgumentException {
  /**
   * 
   */
  private static final long serialVersionUID = -3157123456413714993L;
  private boolean isError = true;

  public CommandLineException(Throwable param, boolean isError) {
    super(param);
    this.isError = isError;
  }

  public CommandLineException(String param, boolean isError) {
    super(param);
    this.isError = isError;
  }

  public CommandLineException(Throwable param) {
    this(param, true);
  }

  public CommandLineException(String param) {
    this(param, true);
  }

  public boolean isError() {
    return this.isError;
  }
}
