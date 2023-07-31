package it.zwets.sms.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class offering the functionality of {@link SmsMessage} in a fluent API way.
 * 
 * Use one of the static (create, edit, parse, read) methods to create an instance.
 * Operate on the message managed by the instance, then obtain the resulting message,
 * its string representation, or file representation.
 * 
 * For instance: SmsUtil.createMessage("This is the body").setHeader("To", "555").asString();
 *  
 * @author zwets
 */
public class SmsUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(SmsUtil.class);

	/**
	 * Convenience class to have single point of definition for SMS headers
	 */
	public static final class HEADER {
	    /** Creation timestamp of the SM */
        public static final String CREATED = "Created";
        /** SMSBroker extension: bounce SM back as incoming message */
        public static final String BOUNCE = "Bounce";
        /** Timestamp SC delivered or errored out the SM */
        public static final String DISCHARGED = "Discharged";
        /** Textual explanation set when FAILED is set */
        public static final String FAIL_REASON = "Fail_reason";
        /** Timestamp of failure of SM */
        public static final String FAILED = "Failed";
        /** Set true to make SMS flash on screen */
        public static final String FLASH = "Flash";
        /** Number sending incoming SM or returning delivery report */
        public static final String FROM = "From";
        /** Number of handling message centre */
        public static final String FROM_SMSC = "From_SMSC";
        /** International Mobile Subscriber Identity (I think) */
        public static final String IMSI = "IMSI";
        /** Set by SMSTools3 to correlate delivery reports */
        public static final String MESSAGE_ID = "Message_id";
        /** SMSBroker extension: don't actually send the SM but make MockBackend handle it */
        public static final String MOCK = "Mock";
        /** Set by SMSTools3: the broker handling the incoming SM or report */
        public static final String MODEM = "Modem";
        /** Set by SMSTools3: timestamp incoming SM was received */
        public static final String RECEIVED = "Received";
        /** Timestamp the delivery report was received? */
        public static final String REPORT_RECEIVED = "Report_received";
        /** Timestamp of the send to which this delivery report relates */
        public static final String REPORT_SENT = "Report_sent";
        /** Status code (0..255) on delivery report. */
        public static final String REPORT_STATUS_CODE = "Report_status_code";
        /** Status line on delivery report, comma-separated code, type, text. */
        public static final String REPORT_STATUS_LINE = "Report_status_line";
        /** Timestamp the message was sent to the message centre. */
	    public static final String SENT = "Sent";
        /** Not sure what goes here */
        public static final String SUBJECT = "Subject";
        /** Number of the addressee of the SM. */
        public static final String TO = "To";
        /** SMSBroker extension: ISO 8601 timestamp of expiry of the SM. */
        public static final String VALID_UNTIL = "Valid_until";
        /** Integer coded validity (message expiry) value for the message centre. */
        public static final String VALIDITY = "Validity";
	}
	
	/* Holds the message that is being worked on. */
	private SmsMessage message = null;

	/* Private constructor, used by thestatic create methods instead.
	 */
	private SmsUtil(SmsMessage message) {
		this.message = message;
	}

	/**
	 * Public constructor.  Note that you must invoke one of the create, edit, read
	 * methods to start constructing a message.
	 */
	public SmsUtil() {
	}
	
	/* Invoked locally to obtain the contained message.  It will be null only
	 * when the appropriate create method has not been called.
	 */
	private SmsMessage getMessage() {
		if (message == null) {
			LOG.error("SmsUtil method invoked before createMessage(), editMessage(), or readMessage()");
			throw new UnsupportedOperationException(
					"You must first invoke createMessage(), editMessage(), or readMessage()");
		}
		return this.message;
	}
	
	/**
	 * Create new empty message, see {@link SmsMessage()}
	 * @return SmsUtil managing the message
	 */
	public static SmsUtil createMessage() {
		return new SmsUtil(new SmsMessage());
	}

	/**
	 * Operate on an existing message, modifying it.
	 * @param message the message to modify
	 * @return an SmsUtil managing the message
	 * @see {{@link #createMessage(SmsMessage)}
	 */
	public static SmsUtil editMessage(SmsMessage message) {
		return new SmsUtil(message);
	}

	/**
	 * Create new message parsed from the given messageString
	 * @param messageString the on-the-wire representation of the message
	 * @return an SmsUtils managing the parsed message
	 * @throws SmsException if the messageString could not be parsed as a valid message
	 */
	public static SmsUtil readMessage(String messageString) {
		SmsMessage msg = new SmsMessage();
		msg.read(messageString);
		return new SmsUtil(msg);
	}
	
	/**
	 * Create new message read from the given file path.
	 * @param path 
	 * @return
	 */
	public static SmsUtil readMessageFile(Path path) {
		try {
			SmsMessage msg = new SmsMessage();
			msg.read(path);
			return new SmsUtil(msg);
		} catch (IOException e) {
			throw new SmsException(e);
		}
	}

	/**
	 * The message as String
	 * @return the string representation of the message 
	 */
	public String asString() {
		return getMessage().asString();
	}

	/**
	 * The message as SmsMessage
	 * @return the string representation of the message 
	 */
	public SmsMessage asSmsMessage() {
		return getMessage();
	}

	/**
	 * Return true if message has header
	 * @param header the header
	 * @return true iff the header is set on the message
	 */
	public boolean hasHeader(String header) {
		return getMessage().hasHeader(header);
	}

	/**
	 * Get a header value or null if it does not exist.
	 * @param header the header
	 * @return the value of the header or null
	 */
	public String getHeader(String header) {
		return getMessage().getHeader(header);
	}
	
	/**
	 * Get a header or default value if it does not exist.
	 * @param header the header
	 * @param defaultValue the value to return if header is not present
	 * @return the value of the header or defaultValue
	 */
	public String getHeader(String header, String defaultValue) {
		return getMessage().getHeader(header, defaultValue);
	}
	
	/**
	 * Add or set header to value.
	 * @param name
	 * @param value
	 * @return the SmsUtil instance wrapping the instance
	 */	
	public SmsUtil setTimestampHeader(String header, Date date) {
		getMessage().setTimestampHeader(header, date);
		return this;
	}

	/**
	 * Add or set header to value.
	 * @param name
	 * @param value
	 * @return the SmsUtil instance wrapping the instance
	 */	
	public SmsUtil setHeader(String header, String value) {
		getMessage().setHeader(header, value);
		return this;
	}

	/**
	 * Set the message body
	 * @param body the new body
	 * @return the SmsUtil instance wrapping the message
	 */
	public SmsUtil setBody(String body) {
		getMessage().setBody(body);
		return this;
	}

    /**
     * Computes the Validity header from ISO date isoExpiry
     * 
     * Returns -1 if the expiry time has already past or is too close from now,
     * or when isoExpiry cannot be parsed as a full ISO date with offset.
     * 
     * When mock is true, interprets seconds as minutes.
     * 
     * @param isoExpiry an ISO8601 date and time with offset
     * @return the validity value (0..255), or -1 when already expired
     */
    public static int computeValidity(String isoExpiry, boolean mock) {

        int validityValue = -1;

        try {
            ZonedDateTime zdt = ZonedDateTime.parse(isoExpiry, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Instant now = Instant.now();

            long secondsLeft = now.until(zdt, ChronoUnit.SECONDS);
            
            if (mock) {
                validityValue = convertToValidity((int)secondsLeft);
                LOG.debug("[MOCK] computeValidity({}) = {}s -> {}", isoExpiry, secondsLeft, validityValue);
            }
            else {
                validityValue = convertToValidity(((int)secondsLeft / 60));
                LOG.debug("computeValidity({}) = {}m -> {}", isoExpiry, secondsLeft/60, validityValue);
            }

            if (validityValue < 0)
                LOG.info("SMS expired with {}m ({}s) left till {}", secondsLeft/60, secondsLeft, isoExpiry);
        }
        catch (DateTimeParseException e) {
            LOG.error("failed to parse Valid_until ISO date: {}: {}", isoExpiry, e.getMessage());
        }

        return validityValue;
    }

    /**
     * Converts a number of minutes to the the nearest SMS validity not exceeding it.
     * 
     * The SMS coding works like this:
     * - 0..143:   blocks of 5m where 0 is 0:05, so 0:05 .. 12:00
     * - 144..167: blocks of 30m where 144 is 12:30, so 12:30..24:00 (1d)
     * - 168..196: blocks of 1d where 168 is 2d, so 2d..30d
     * - 197..255: blocks of 1w where 197 is 5 weeks, so 5w..63w
     * 
     * However we ignore any values about 167 = 24h as SMS centre do not honour them.
     * 
     * @param minutes a positive number of whole minutes
     * @return a number in the range 0..255
     */
    private static int convertToValidity(int minutes) {

        final int FIVE = 5;
        final int HALF_HOUR = 30;
        final int HALF_DAY = 24*HALF_HOUR;
        final int DAY = 2*HALF_DAY;
        final int WEEK = 7*DAY;

        int units = 
             minutes > 63*WEEK ? 255 :
             minutes > 30*DAY ? (196 + (minutes - (4*WEEK)) / WEEK) :
             minutes > DAY ? (167 + (minutes - DAY) / DAY) : 
             minutes > HALF_DAY ? (143 + (minutes - HALF_DAY) / HALF_HOUR) :
             minutes > FIVE ? ((minutes - FIVE) / FIVE) :
             -1;

        return units;
    }
}
