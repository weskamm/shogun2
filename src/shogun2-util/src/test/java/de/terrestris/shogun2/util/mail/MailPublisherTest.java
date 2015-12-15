package de.terrestris.shogun2.util.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/mail-test-context.xml" })
public class MailPublisherTest {

	/**
	 * The class to test.
	 */
	@Autowired
	private MailPublisher mailPublisher;

	/**
	 * A simple mail template
	 */
	@Autowired
	private SimpleMailMessage registrationMailMessageTemplate;

	/**
	 * The test-suite mail server.
	 */
	private GreenMail greenMail;

	/**
	 * Start the mail server before each test.
	 */
	@Before
	public void startMailServer() {
		greenMail = new GreenMail(ServerSetupTest.SMTP);
		greenMail.start();
	}

	/**
	 * Stop the mail server after each test.
	 */
	@After
	public void stopMailServer() {
		greenMail.stop();
	}

	/**
	 *
	 * @throws MessagingException
	 */
	@Test
	public void sendMail_minimal_configuration() throws MessagingException {

		String from = "from@shogun2.de";
		String[] to = {"to@shogun2.de"};

		String subject = "Kagawa!";
		String msg = "Shinji!";

		mailPublisher.sendMail(from, null, to, null, null, subject, msg);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(1, messages.length);

		assertEquals(from, messages[0].getFrom()[0].toString());

		assertEquals(to[0], messages[0].getRecipients(Message.RecipientType.TO)[0].toString());

		assertEquals(subject, messages[0].getSubject());

		assertEquals(msg, GreenMailUtil.getBody(messages[0]).trim());
	}

	/**
	 *
	 * @throws MessagingException
	 */
	@Test
	public void sendMail_replyTo() throws MessagingException {

		String from = "from@shogun2.de";
		String[] to = {"to@shogun2.de"};
		String replyTo = "reply@shogun2.de";

		String subject = "Kagawa!";
		String msg = "Shinji!";

		mailPublisher.sendMail(from, replyTo, to, null, null, subject, msg);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(1, messages.length);

		assertEquals(from, messages[0].getFrom()[0].toString());

		assertEquals(to[0], messages[0].getRecipients(Message.RecipientType.TO)[0].toString());

		assertEquals(replyTo, messages[0].getReplyTo()[0].toString());

	}

	/**
	 *
	 * @throws MessagingException
	 */
	@Test
	public void sendMail_cc() throws MessagingException {

		String from = "from@shogun2.de";
		String[] to = {"to@shogun2.de"};
		String[] cc = {"cc1@shogun2.de", "cc2@shogun2.de"};

		String subject = "Kagawa!";
		String msg = "Shinji!";

		mailPublisher.sendMail(from, null, to, cc, null, subject, msg);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(3, messages.length);

		assertEquals(from, messages[0].getFrom()[0].toString());

		assertEquals(to[0], messages[0].getRecipients(Message.RecipientType.TO)[0].toString());

		assertEquals(cc[0], messages[1].getRecipients(Message.RecipientType.CC)[0].toString());
		assertEquals(cc[1], messages[2].getRecipients(Message.RecipientType.CC)[1].toString());

	}

	/**
	 *
	 * @throws MessagingException
	 */
	@Test
	public void sendMail_bcc() throws MessagingException {

		String from = "from@shogun2.de";
		String[] to = {};
		String[] bcc = {"bcc1@shogun2.de", "bcc2@shogun2.de"};

		String subject = "Kagawa!";
		String msg = "Shinji!";

		mailPublisher.sendMail(from, null, to, null, bcc, subject, msg);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		// the BCC header isn't carried in the message-header, therefore we
		// can't access it directly and we can only test if two mails were
		// sent without having a TO-header set
		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(2, messages.length);

		assertEquals(from, messages[0].getFrom()[0].toString());

		assertNull(messages[0].getRecipients(Message.RecipientType.TO));

	}

	/**
	 * @throws MessagingException
	 *
	 */
	@Test
	public void sendMail_template() throws MessagingException {

		String to = "to@shogun2.de";

		String str1 = "The Username";
		String str2 = "http://tokenized-registration-shogun2.de";

		registrationMailMessageTemplate.setTo(to);

		registrationMailMessageTemplate.setText(
				String.format(
						registrationMailMessageTemplate.getText(),
						str1,
						str2
				)
		);

		mailPublisher.sendMail(registrationMailMessageTemplate);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		// the BCC header isn't carried in the message-header, therefore we
		// can't access it directly and we can only test if two mails were
		// sent without having a TO-header set
		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(1, messages.length);

		assertEquals(mailPublisher.getDefaultMailSender(),
				messages[0].getFrom()[0].toString());

		assertEquals(to, messages[0].getRecipients(
				Message.RecipientType.TO)[0].toString());

		assertTrue(GreenMailUtil.getBody(
				messages[0]).contains(str1));

		assertTrue(GreenMailUtil.getBody(
				messages[0]).contains(str2));
	}

	/**
	 * @throws MessagingException
	 *
	 */
	@Test
	public void sendMimeMail_minimal_configuration() throws MessagingException {

		String from = "from@shogun2.de";
		String[] to = {"to@shogun2.de"};

		String subject = "Kagawa!";
		String msg = "Shinji!";
		Boolean html = false;

		mailPublisher.sendMimeMail(from, null, to, null, null, subject,
				msg, html, null, null);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(1, messages.length);

		assertEquals(from, messages[0].getFrom()[0].toString());

		assertEquals(to[0], messages[0].getRecipients(Message.RecipientType.TO)[0].toString());

		assertEquals(subject, messages[0].getSubject());

		assertEquals(msg, GreenMailUtil.getBody(messages[0]).trim());

		assertEquals("text/plain; charset=us-ascii", messages[0].getContentType());

	}

	/**
	 *
	 * @throws MessagingException
	 */
	@Test
	public void sendMimeMail_replyTo() throws MessagingException {

		String from = "from@shogun2.de";
		String[] to = {"to@shogun2.de"};
		String replyTo = "reply@shogun2.de";

		String subject = "Kagawa!";
		String msg = "Shinji!";
		Boolean html = false;

		mailPublisher.sendMimeMail(from, replyTo, to, null, null, subject,
				msg, html, null, null);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(1, messages.length);

		assertEquals(from, messages[0].getFrom()[0].toString());

		assertEquals(to[0], messages[0].getRecipients(Message.RecipientType.TO)[0].toString());

		assertEquals(replyTo, messages[0].getReplyTo()[0].toString());
	}

	/**
	 *
	 * @throws MessagingException
	 */
	@Test
	public void sendMimeMail_cc() throws MessagingException {

		String from = "from@shogun2.de";
		String[] to = {"to@shogun2.de"};
		String[] cc = {"cc1@shogun2.de", "cc2@shogun2.de"};

		String subject = "Kagawa!";
		String msg = "Shinji!";
		Boolean html = false;

		mailPublisher.sendMimeMail(from, null, to, cc, null, subject,
				msg, html, null, null);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(3, messages.length);

		assertEquals(from, messages[0].getFrom()[0].toString());

		assertEquals(to[0], messages[0].getRecipients(Message.RecipientType.TO)[0].toString());

		assertEquals(cc[0], messages[1].getRecipients(Message.RecipientType.CC)[0].toString());
		assertEquals(cc[1], messages[2].getRecipients(Message.RecipientType.CC)[1].toString());
	}

	/**
	 *
	 * @throws MessagingException
	 */
	@Test
	public void sendMimeMail_bcc() throws MessagingException {

		String from = "from@shogun2.de";
		String[] to = {};
		String[] bcc = {"bcc1@shogun2.de", "bcc2@shogun2.de"};

		String subject = "Kagawa!";
		String msg = "Shinji!";
		Boolean html = true;

		mailPublisher.sendMimeMail(from, null, to, null, bcc, subject,
				msg, html, null, null);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		// the BCC header isn't carried in the message-header, therefore we
		// can't access it directly and we can only test if two mails were
		// sent without having a TO-header set
		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(2, messages.length);

		assertEquals(from, messages[0].getFrom()[0].toString());

		assertNull(messages[0].getRecipients(Message.RecipientType.TO));

	}

	/**
	 *
	 * @throws MessagingException
	 */
	@Test
	public void sendMimeMail_html() throws MessagingException {

		String from = "from@shogun2.de";
		String[] to = {"to@shogun2.de"};

		String subject = "Kagawa!";
		String msg = "Shinji!";
		Boolean html = true;

		mailPublisher.sendMimeMail(from, null, to, null, null, subject,
				msg, html, null, null);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		// the BCC header isn't carried in the message-header, therefore we
		// can't access it directly and we can only test if two mails were
		// sent without having a TO-header set
		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(1, messages.length);

		assertEquals("text/html; charset=us-ascii", messages[0].getContentType());

	}

	/**
	 *
	 * @throws MessagingException
	 * @throws IOException
	 */
	@Test
	public void sendMimeMail_attachment() throws MessagingException, IOException {

		String from = "from@shogun2.de";
		String[] to = {"to@shogun2.de"};

		String subject = "Kagawa!";
		String msg = "Shinji!";
		Boolean html = false;

		ClassLoader classLoader = getClass().getClassLoader();

		String attachmentFilename = "logo.png";
		File attachmentFile = new File(classLoader.getResource(
				"META-INF/logo.png").getFile());

		assertTrue(attachmentFile.exists());

		mailPublisher.sendMimeMail(from, null, to, null, null, subject,
				msg, html, attachmentFilename, attachmentFile);

		// wait for max 5s for 1 email to arrive
		// waitForIncomingEmail() is useful if you're sending stuff
		// asynchronously in a separate thread
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));

		// the BCC header isn't carried in the message-header, therefore we
		// can't access it directly and we can only test if two mails were
		// sent without having a TO-header set
		Message[] messages = greenMail.getReceivedMessages();
		assertEquals(1, messages.length);

		Multipart multiPart = (Multipart) messages[0].getContent();

		assertEquals(attachmentFilename, multiPart.getBodyPart(1).getFileName());

	}

}
