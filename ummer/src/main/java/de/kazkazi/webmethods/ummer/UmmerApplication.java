package de.kazkazi.webmethods.ummer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcbsys.nirvana.client.nChannel;
import com.pcbsys.nirvana.client.nQueue;
import com.pcbsys.nirvana.client.nSession;

import de.kazkazi.webmethods.ummer.impl.backend.UniversalMessagingServer;
import de.kazkazi.webmethods.ummer.intf.backend.UniversalMessagingInterface;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.ActionNotPossibleException;
import de.kazkazi.webmethods.ummer.intf.backend.exceptions.SessionCreationException;

public class UmmerApplication {

	private static final String LIST_TOPICS_LONG_OPTION = "listTopics";
	private static final String LIST_TOPICS_SHORT_OPTION = "lst";
	private static final String LIST_QUEUES_SHORT_OPTION = "lsq";
	private static final String LIST_QUEUES_LONG_OPTION = "listQueues";
	private static final String RNAME_SHORT_OPTION = "r";
	private static final String RNAME_LONG_OPTION = "RNAME";
	private static Options options;
	private static Logger logger = LoggerFactory.getLogger(UmmerApplication.class);

	public static void main(String[] args) {
		
		UniversalMessagingInterface um = new UniversalMessagingServer();
		
		createCommandLineOptions();
		
		CommandLineParser parser = new BasicParser();

		try {
			CommandLine line = parser.parse( options, args );
			if (line.hasOption(RNAME_SHORT_OPTION) || (line.hasOption(RNAME_LONG_OPTION))) {
				ArrayList<String> RNAME = new ArrayList<String>();
				String rVal = line.getOptionValue(RNAME_SHORT_OPTION);
				if (StringUtils.isEmpty(rVal)) {
					rVal = line.getOptionValue(RNAME_LONG_OPTION);
				}
				if (StringUtils.isEmpty(rVal)) {
					throw new ParseException("RNAME missing - Value for RNAME is missing");
				}
				RNAME.add(rVal);
				nSession session = um.createSession(RNAME.toArray(new String[0]));
				um.connect(session);
				logger.info("Session connected");
				
				handleListQueues(um, line, session);
				handleListTopics(um, line, session);
				
				um.disconnect(session);
				logger.info("Session disconnected");
			} else {
				throw new ParseException("RNAME missing - the UM Realm is not specified");
			}
		}
		catch( ParseException e ) {
			System.err.println(e.getMessage());
			printUsage();
		}
		catch( SessionCreationException e) {
			logger.error("Session could not be created!", e);
		} catch (ActionNotPossibleException e) {
			logger.error("Action is not possible!", e);
		}		
	}

	private static void handleListQueues(UniversalMessagingInterface um, CommandLine line, nSession session)
			throws ActionNotPossibleException {
		logger.info("Listing Queues");
		if (line.hasOption(LIST_QUEUES_SHORT_OPTION) || line.hasOption(LIST_QUEUES_LONG_OPTION)) {
			List<nQueue> queues = um.getAllQueues(session);
			for (nQueue queue : queues) {
				System.out.println(queue.getName());
				logger.info(queue.getName());
			}
		}
	}
	
	private static void handleListTopics(UniversalMessagingInterface um, CommandLine line, nSession session)
			throws ActionNotPossibleException {
		logger.info("Listing topics");
		if (line.hasOption(LIST_TOPICS_SHORT_OPTION) || line.hasOption(LIST_TOPICS_LONG_OPTION)) {
			List<nChannel> topics = um.getAllTopics(session);
			for (nChannel topic : topics) {
				System.out.println(topic.getName());
				logger.info(topic.getName());
			}
		}
	}

	private static void createCommandLineOptions() {
		options = new Options();
		Option rname = new Option(RNAME_SHORT_OPTION, RNAME_LONG_OPTION, true, "Provide the UM Realm to connect to.");
		options.addOption(rname);
		Option optShowAllQueues = new Option(LIST_QUEUES_SHORT_OPTION, LIST_QUEUES_LONG_OPTION, false, "Lists all queues");
		options.addOption(optShowAllQueues);
		Option optShowAllTopics = new Option(LIST_TOPICS_SHORT_OPTION, LIST_TOPICS_LONG_OPTION, false, "Lists all topics");
		options.addOption(optShowAllTopics);
	}

	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ummer -r REALM", options);
		
	}
}