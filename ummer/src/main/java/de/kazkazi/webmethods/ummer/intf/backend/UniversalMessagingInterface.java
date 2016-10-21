package de.kazkazi.webmethods.ummer.intf.backend;

import java.util.List;

import com.pcbsys.nirvana.client.nChannel;
import com.pcbsys.nirvana.client.nConsumeEvent;
import com.pcbsys.nirvana.client.nQueue;
import com.pcbsys.nirvana.client.nSession;

import de.kazkazi.webmethods.ummer.intf.backend.exceptions.ActionNotPossibleException;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.CannotReadFromQueueException;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.SessionCreationException;

public interface UniversalMessagingInterface {

	public nSession createSession(String[] RNAME) throws SessionCreationException;

	public boolean connect(nSession session) throws SessionCreationException;
	public boolean disconnect(nSession session);
	
	public List<nQueue> getAllQueues(nSession session) throws ActionNotPossibleException;
	public List<nChannel> getAllTopics(nSession session)  throws ActionNotPossibleException;
	public List<nConsumeEvent> getAllMessagesFromQueue(nQueue queue) throws CannotReadFromQueueException;
	
}
