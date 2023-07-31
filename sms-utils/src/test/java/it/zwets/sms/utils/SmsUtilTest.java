package it.zwets.sms.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Test;

public class SmsUtilTest {

	@Test(expected=UnsupportedOperationException.class)
	public void testSmsUtil() {
		SmsUtil util = new SmsUtil();
		util.setBody("Must fail");
	}

	@Test
	public void testCreateMessage() {
		SmsUtil u = SmsUtil.createMessage();
		assertEquals("\n", u.asString());
	}

	@Test
	public void testEditMessage() {
		SmsMessage msg = new SmsMessage("Original Body");
		msg.setHeader("Header", "value");
		
		SmsUtil.editMessage(msg).setBody("New Body").setHeader("Header", "new value");
		
		assertEquals("New Body", msg.getBody());
		assertEquals("new value", msg.getHeader("Header"));
	}

	@Test
	public void testHasHeader() {
		SmsUtil u = SmsUtil.readMessage("Header: value\n\n");
		assertTrue(u.hasHeader("Header"));
		assertFalse(u.hasHeader("Notthisone"));
	}

	@Test
	public void testGetHeader() {
		SmsUtil u = SmsUtil.readMessage("Header: value\n\n");
		assertEquals("value", u.getHeader("Header"));
		assertNull(u.getHeader("nonexistingheader"));
	}

	@Test
	public void testGetDefaultHeader() {
		SmsUtil u = SmsUtil.readMessage("Header: value\n\n");
		assertEquals("value", u.getHeader("Header", "Default"));
		assertEquals("default", u.getHeader("nonexistingheader", "default"));
	}
	
    @Test
    public void testEmptyHeader() {
        SmsUtil u = SmsUtil.readMessage("Header: \n\n");
        assertNull(u.getHeader("Header"));
    }

    @Test
    public void testSpacesHeader() {
        SmsUtil u = SmsUtil.readMessage("Header:   \t   \t   \t\n\n");
        assertNull(u.getHeader("Header"));
    }

    @Test
    public void testTrimsHeader() {
        SmsUtil u = SmsUtil.readMessage("Header:  \t  value \t  \n\n");
        assertEquals("value", u.getHeader("Header"));
    }
	@Test
	public void testReadMessage() {
		String msgString = SmsUtil.createMessage()
				.setBody("The message body")
				.setHeader("H", "v")
				.setHeader("J", "w")
				.asString();
		assertEquals(msgString, SmsUtil.readMessage(msgString).asString());
	}

	@Test
	public void testReadMessageFile() throws Exception {
		SmsMessage msg = SmsUtil.createMessage()
				.setBody("The message body")
				.setHeader("H", "v")
				.setHeader("J", "w")
				.asSmsMessage();
		
		Path path = FileSystems.getDefault().getPath(".", "testfile.sms");
		msg.writeFile(path);
		
		assertEquals(msg.asString(), SmsUtil.readMessageFile(path).asString());
	}
}
