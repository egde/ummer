package de.kazkazi.webmethods.ummer.impl.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcbsys.nirvana.client.nChannel;
import com.pcbsys.nirvana.client.nChannelAlreadySubscribedException;
import com.pcbsys.nirvana.client.nChannelAttributes;
import com.pcbsys.nirvana.client.nChannelNotFoundException;
import com.pcbsys.nirvana.client.nConsumeEvent;
import com.pcbsys.nirvana.client.nEventListener;
import com.pcbsys.nirvana.client.nIllegalArgumentException;
import com.pcbsys.nirvana.client.nIllegalChannelMode;
import com.pcbsys.nirvana.client.nQueue;
import com.pcbsys.nirvana.client.nQueuePeekContext;
import com.pcbsys.nirvana.client.nQueueReader;
import com.pcbsys.nirvana.client.nQueueReaderContext;
import com.pcbsys.nirvana.client.nRequestTimedOutException;
import com.pcbsys.nirvana.client.nSecurityException;
import com.pcbsys.nirvana.client.nSession;
import com.pcbsys.nirvana.client.nSessionAttributes;
import com.pcbsys.nirvana.client.nSessionFactory;
import com.pcbsys.nirvana.client.nSessionNotConnectedException;
import com.pcbsys.nirvana.client.nSessionPausedException;
import com.pcbsys.nirvana.client.nUnexpectedResponseException;
import com.pcbsys.nirvana.client.nUnknownRemoteRealmException;

import de.kazkazi.webmethods.ummer.intf.backend.UniversalMessagingInterface;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.ActionNotPossibleException;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.CannotReadFromChannelException;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.CannotReadFromQueueException;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.SessionCreationException;

public class UniversalMessagingServer implements UniversalMessagingInterface {
	
	private Logger logger = LoggerFactory.getLogger(UniversalMessagingServer.class);
	
	@Override
	public nSession createSession(String[] RNAME) throws SessionCreationException {
		nSession session = null;
		try {
			nSessionAttributes nsa = new nSessionAttributes(RNAME);
			session = nSessionFactory.create(nsa);
		} catch (nIllegalArgumentException e) {
			throw new SessionCreationException(e);
		}
		return session;
	}
	
	@Override
	public boolean connect(nSession session) throws SessionCreationException {
		try {
			session.init();
		} catch (Exception e) {
			throw new SessionCreationException(e);
		}
		return true;
	}

	@Override
	public boolean disconnect(nSession session) {
		session.close();
		return true;
	}

	@Override
	public List<nQueue> getAllQueues(nSession session) throws ActionNotPossibleException {
		List<nQueue> result = new ArrayList<nQueue>();
		
		try {
			nChannelAttributes[] channelAttributes = session.getChannels();
			for ( nChannelAttributes channelAttribute : channelAttributes) {
				if (channelAttribute.getChannelMode() == nChannelAttributes.QUEUE_MODE) {
					logger.debug("Got Queue:" + channelAttribute.getFullName());
					result.add(session.findQueue(channelAttribute));
				}
			}
		} catch (Exception e) {
			throw new ActionNotPossibleException(e);
		}
		return result;
	}

	@Override
	public List<nChannel> getAllTopics(nSession session) throws ActionNotPossibleException {
		List<nChannel> result = new ArrayList<nChannel>();

		try {
			nChannelAttributes[] channelAttributes = session.getChannels();
			for ( nChannelAttributes channelAttribute : channelAttributes) {
				if (channelAttribute.getChannelMode() == nChannelAttributes.CHANNEL_MODE) {
					logger.debug("Got Topic:" + channelAttribute.getFullName());
					result.add(session.findChannel(channelAttribute));
				}
			}
		} catch (Exception e) {
			throw new ActionNotPossibleException(e);
		}
		return result;
	}

	@Override
	public List<nConsumeEvent> getAllMessagesFromQueue(nQueue queue) throws CannotReadFromQueueException {
		List<nConsumeEvent> eventMessageList = new ArrayList<nConsumeEvent>();
		try {
			nQueueReader queueReader = queue.createReader(new nQueueReaderContext());
			nQueuePeekContext context = nQueueReader.createContext(10);
			nConsumeEvent[] eventMessages = queueReader.peek(context);
			if (eventMessages != null) {
				eventMessageList = Arrays.asList(eventMessages);
			}
			logger.debug(String.format("Peeked at %d messages from queue %s", eventMessageList.size(), queue.getName()));
		} catch (Exception e) {
			throw new CannotReadFromQueueException(e);
		}
		return eventMessageList;
	}

	@Override
	public nQueue getQueue(nSession session, String queueName) throws ActionNotPossibleException {
		nQueue queue = null;
		try {
			nChannelAttributes channelAttributes = new nChannelAttributes();
			channelAttributes.setName(queueName);
			queue = session.findQueue(channelAttributes);
		} catch(nIllegalArgumentException | nChannelNotFoundException | nSessionPausedException | nUnknownRemoteRealmException | nSecurityException | nSessionNotConnectedException | nUnexpectedResponseException | nRequestTimedOutException | nIllegalChannelMode e) {
			throw new ActionNotPossibleException(e);
		}
		return queue;
	}
	
	@Override
	public nChannel getChannel(nSession session, String channelName) throws ActionNotPossibleException {
		nChannel channel = null;
		try {
			nChannelAttributes channelAttributes = new nChannelAttributes();
			channelAttributes.setName(channelName);
			channel = session.findChannel(channelAttributes);
		} catch(nIllegalArgumentException | nChannelNotFoundException | nSessionPausedException | nUnknownRemoteRealmException | nSecurityException | nSessionNotConnectedException | nUnexpectedResponseException | nRequestTimedOutException | nIllegalChannelMode e) {
			throw new ActionNotPossibleException(e);
		}
		return channel;
	}

	@Override
	public void tailMessagesFromTopic(nChannel channel, nEventListener eventListener) throws CannotReadFromChannelException {
		try {
			channel.addSubscriber(eventListener);
			InputStream inputStream = System.in;
			System.out.println("Press a key to stop");
			if (inputStream.read() > 0) {
				channel.removeSubscriber(eventListener);
			}
		} catch (nIllegalArgumentException | nSecurityException | nChannelNotFoundException
				| nChannelAlreadySubscribedException | nSessionNotConnectedException | nRequestTimedOutException
				| nUnexpectedResponseException | nSessionPausedException | IOException e) {
			throw new CannotReadFromChannelException(e);
		}
	}
}
