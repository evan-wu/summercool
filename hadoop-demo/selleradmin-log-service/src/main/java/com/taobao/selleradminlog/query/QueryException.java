/**
 * 
 */
package com.taobao.selleradminlog.query;

/**
 * @author guoyou
 *
 */
public class QueryException extends Exception {
	private static final long serialVersionUID = 537497174715190863L;

	/**
     * Constructs an <code>LineFormatException</code> with no
     * specified detail message.
     * @since JDK1.1
     */
    public QueryException() {
	super();
    }

    /**
     * Constructs an <code>LineFormatException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     */
    public QueryException(String s) {
	super(s);
    }
    
    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
