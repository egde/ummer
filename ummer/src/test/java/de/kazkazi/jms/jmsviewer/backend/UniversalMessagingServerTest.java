package de.kazkazi.jms.jmsviewer.backend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.pcbsys.nirvana.client.nChannel;
import com.pcbsys.nirvana.client.nConsumeEvent;
import com.pcbsys.nirvana.client.nQueue;
import com.pcbsys.nirvana.client.nSession;
import com.pcbsys.nirvana.client.nSessionNotConnectedException;

import de.kazkazi.webmethods.ummer.impl.backend.UniversalMessagingServer;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.ActionNotPossibleException;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.CannotReadFromQueueException;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.SessionCreationException;

public class UniversalMessagingServerTest {

	private static final String[] RNAME = { "nsp://127.0.0.1:9000" };

	@Test
	public void test_Connection() {
		UniversalMessagingServer um = new UniversalMessagingServer();
		nSession session = null;
		try {
			session = um.createSession(RNAME);
			um.connect(session);
		} catch (SessionCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(session);
		try {
			assertNotNull(session.getServerRealmName());
		} catch (nSessionNotConnectedException e) {
			fail("session not connected");
		}
	}

	@Test
	public void test_getAllQueues() {
		UniversalMessagingServer um = new UniversalMessagingServer();
		nSession session = null;
		try {
			session = um.createSession(RNAME);
			um.connect(session);
		} catch (SessionCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(session);
		try {
			assertNotNull(session.getServerRealmName());
		} catch (nSessionNotConnectedException e) {
			fail("session not connected");
		}

		List<nQueue> queueList = null;
		try {
			queueList = um.getAllQueues(session);
		} catch (ActionNotPossibleException e) {
			fail("Action not possible");
		}
		assertNotNull(queueList);
	}
	
	@Test
	public void test_getAllTopics() {
		UniversalMessagingServer um = new UniversalMessagingServer();
		nSession session = null;
		try {
			session = um.createSession(RNAME);
			um.connect(session);
		} catch (SessionCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(session);
		try {
			assertNotNull(session.getServerRealmName());
		} catch (nSessionNotConnectedException e) {
			fail("session not connected");
		}

		List<nChannel> topicList = null;
		try {
			topicList = um.getAllTopics(session);
		} catch (ActionNotPossibleException e) {
			fail("Action not possible");
		}
		assertNotNull(topicList);
	}

	@Test
	public void test_getAllMessagesFromQueue() {
		UniversalMessagingServer um = new UniversalMessagingServer();
		nSession session = null;
		try {
			session = um.createSession(RNAME);
			um.connect(session);
		} catch (SessionCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(session);
		try {
			assertNotNull(session.getServerRealmName());
		} catch (nSessionNotConnectedException e) {
			fail("session not connected");
		}

		List<nQueue> queueList = null;
		try {
			queueList = um.getAllQueues(session);
		} catch (ActionNotPossibleException e) {
			fail("Action not possible");
		}
		assertNotNull(queueList);

		List<nConsumeEvent> messages = null;
		try {
			for (nQueue queue : queueList) {
				messages = um.getAllMessagesFromQueue(queue);
				assertNotNull(messages);
			}
		} catch (CannotReadFromQueueException e) {
			fail("CannotReadFromQueueException occured");
		}
	}

}
