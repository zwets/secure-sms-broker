package it.zwets.sms.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SmsMessageTest {

	@Test
	public void testSmsMessage() {
		SmsMessage msg = new SmsMessage();
		assertEquals("", msg.getBody());
		assertEquals(0, msg.getHeaders().size());
	}

	@Test
	public void testSmsMessageWithBody() {
		String body = "This is the body\nAnd this is the second line";
		SmsMessage msg = new SmsMessage(body);
		assertEquals(body, msg.getBody());
		assertEquals(0, msg.getHeaders().size());
	}

	@Test
	public void testSmsMessageWithHeaders() {
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("LegalHeader", "legal value");
		headers.put("AnotherHeader", "another value");

		SmsMessage msg = new SmsMessage(headers);
		assertEquals("", msg.getBody());
		assertEquals(2, msg.getHeaders().size());				
	}

	@Test
	public void testSmsMessagWithBodyAndHeaders() {
		String body = "Test body content";
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("LegalHeader", "legal value");
		headers.put("AnotherHeader", "another value");
		
		SmsMessage msg = new SmsMessage(headers, body);
		assertEquals(body, msg.getBody());
		assertEquals(2, msg.getHeaders().size());				
	}

	@Test
	public void testGetHeaderString() {
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("LegalHeader", "  legal value  ");
		headers.put("AnotherHeader", "another value");
		SmsMessage msg = new SmsMessage(headers);
		
		assertEquals("legal value", msg.getHeader("LegalHeader"));
	}

	@Test
	public void testGetHeaderStringString() {
		SmsMessage msg = new SmsMessage();
		
		assertEquals("default", msg.getHeader("Non-existing", "default"));
	}

	@Test
	public void testHasHeader() {
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("LegalHeader", "  legal value  ");
		SmsMessage msg = new SmsMessage(headers);
		
		assertTrue(msg.hasHeader("LegalHeader"));
	}

	@Test
	public void testSetHeader() {
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("LegalHeader", "legal value");
		headers.put("AnotherHeader", "another value");

		SmsMessage msg = new SmsMessage(headers);
		assertEquals("legal value", msg.getHeader("LegalHeader"));
	}

	@Test
	public void testDateHeader() throws Exception {
		SmsMessage msg = new SmsMessage();
		
		Date set = new Date(0L);
		msg.setTimestampHeader("TimeStamp", set);
		
		Date read = new SimpleDateFormat("yy-MM-dd HH:mm:ss").parse(msg.getHeader("TimeStamp"));
		assertEquals(read.getTime(), set.getTime());
	}

	@Test
	public void testAddHeader() {
		SmsMessage msg = new SmsMessage();
		msg.addHeader("HeaderName: headerValue");
		assertEquals("headerValue", msg.getHeader("HeaderName"));
	}

	@Test
	public void testRemoveHeader() {
		SmsMessage msg = new SmsMessage();
		msg.addHeader("HeaderName: headerValue");
		assertEquals("headerValue", msg.getHeader("HeaderName"));
		msg.removeHeader("HeaderName");
		assertFalse(msg.hasHeader("HeaderName"));
	}

	@Test
	public void testAsString() {
		String body = "Test body content";
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("To", "123456789");
		
		SmsMessage msg = new SmsMessage(headers, body);
		assertEquals("To: 123456789\n\nTest body content", msg.asString());
	}

	@Test
	public void testReadEmptyString() {
		SmsMessage msg = new SmsMessage();
		msg.read("\n");
		assertEquals(0, msg.getHeaders().size());
		assertEquals(0, msg.getBody().length());
	}

	@Test
	public void testReadIteratorOfString() {
		List<String> lines = new ArrayList<String>();
		lines.add("H1:v1  ");
		lines.add("");
		lines.add("Body");
		
		SmsMessage msg = new SmsMessage();
		msg.read(lines.iterator());
		
		assertEquals("Body", msg.getBody());
		assertEquals("v1", msg.getHeader("H1"));
	}

	@Test
	public void testWriteReadFile() throws IOException {
		SmsMessage msg1 = new SmsMessage();
		msg1.setHeader("Header1", "value1");
		msg1.setHeader("Header2", "value2");
		msg1.setBody("Message body");
		
		Path filePath = FileSystems.getDefault().getPath(".", "testfile.sms");
		msg1.writeFile(filePath);
		
		SmsMessage msg2 = new SmsMessage();
		msg2.read(filePath);
		
		assertEquals(msg1.getBody(), msg2.getBody());
		assertEquals(msg1.getHeaders().size(), msg2.getHeaders().size());
	}
}
