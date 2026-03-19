package it.unive.lisa.analysis.avase;

/**
 * Exception thrown whenever there is a Avase implementation error.
 * Shall not be caught, an error of this type means that the implementation is not correct.
 * 
 * @author Francesco Refolli
 */
public class AvaseImplException extends RuntimeException {
  /**
   * 
   */
  private static final long serialVersionUID = -3157254296413714993L;

  public AvaseImplException(Throwable param) {
    super(param);
    System.err.println(getMessage());
  }

  public AvaseImplException(String param) {
    super("AvaseImplException: " + param);
    System.err.println(getMessage());
  }
}
