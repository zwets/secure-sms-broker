package it.zwets.sms.utils;


/**
 * Runtime exception thrown by the various SmsServices.
 * @author zwets
 */
public final class SmsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SmsException() {
		super();
	}

	public SmsException(String arg0) {
		super(arg0);
	}

	public SmsException(Throwable arg0) {
		super(arg0);
	}

	public SmsException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SmsException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}
}
