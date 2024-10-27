package com.bot.theechoesbot.eventhandler;

import com.bot.theechoesbot.core.Cache;
import com.bot.theechoesbot.core.Core;
import com.bot.theechoesbot.eventhandler.template.EventHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventUpdateStatusEventHandler implements EventHandler<ScheduledEventUpdateStatusEvent>{

	private final static Logger logger = LoggerFactory.getLogger(EventUpdateStatusEventHandler.class);

	@Override
	public void handle(ScheduledEventUpdateStatusEvent event){

		ScheduledEvent.Status status = event.getNewStatus();
		ScheduledEvent scheduledEvent = event.getScheduledEvent();
		switch(status){

			case ScheduledEvent.Status.ACTIVE -> activeEvent(scheduledEvent);
			case ScheduledEvent.Status.COMPLETED -> completeEvent(scheduledEvent);
			case ScheduledEvent.Status.CANCELED -> cancelEvent(scheduledEvent);

		}

	}

	private void activeEvent(ScheduledEvent event){

		try{

			//get the id
			String eventId = event.getId();

			//find the message in schedule
			Message message = getEventMessage(eventId);
			if(message != null){

				//modify the content
				String content = message.getContentRaw();
				content = content + " - Ongoing";

				//edit the message
				message.editMessage(content).queue();

				logger.info("Active event was updated in schedule. " + eventId);

				/* announce it */

				//build the message
				String announceMessage = "@everyone\n" +
					MarkdownUtil.maskedLink(
						event.getName(),
						"https://discord.com/events/" + Core.getServerData().getGuildId() + "/" + eventId
					) +
					" is starting. Get Ready." +
					"\nLet us know if you'll be late.";

				//find message in cache and attach it
				String startMessage = Cache.Event.getAndRemove("start_" + eventId);
				if(startMessage != null){
					announceMessage += "\n" + startMessage;
				}

				//send it
				Core.getServerData().getChannelNewsAnnounces().sendMessage(announceMessage).queue();

				logger.info("Start event was announced. " + eventId);

			}

		}catch(Exception e){
			logger.error("Error updating the event in schedule.", e);
		}

	}

	private void completeEvent(ScheduledEvent event){

		try{

			//get the id
			String eventId = event.getId();

			Message message = getEventMessage(eventId);
			if(message != null){

				//modify the content
				String content = message.getContentRaw();
				content = content.substring(0, content.lastIndexOf("]"))
					.replace("[", "");

				//check if there is a cancel reason in cache
				//otherwise just simply type Completed
				String reason = Cache.Event.getAndRemove("cancel_" + eventId);
				if(reason != null){
					content += " - Canceled. Reason: " + reason + ".";
				}else{
					content += " - Completed.";
				}

				//edit the message
				message.editMessage(content).queue();

				logger.info("Completed event was updated in schedule. " + eventId);

			}

		}catch(Exception e){
			logger.error("Error updating the event in schedule.", e);
		}

	}

	private void cancelEvent(ScheduledEvent event){

		try{

			//get the id
			String eventId = event.getId();

			Message message = getEventMessage(eventId);
			if(message != null){

				//modify the content
				//remove the link and replace [] with ~~
				String content = message.getContentRaw();
				content = content.substring(0, content.indexOf("]"))
					.replace("[", "~~") +
					"~~ - Canceled";

				//find reason in cache and attach it
				String reason = Cache.Event.getAndRemove("cancel_" + eventId);
				if(reason != null){
					content += ". Reason: " + reason + ".";
				}

				//edit the message
				message.editMessage(content).queue();

				logger.info("Canceled event was updated in schedule. " + eventId);

				/* announce it */

				//split the date and title
				String date = content.substring(2, content.indexOf(")")+1);
				String title = content.substring(content.indexOf("~~")+2, content.lastIndexOf("~~"));

				//build the message
				String announceMessage = "@everyone\nThe event " + title + " from " + date + " was canceled.";
				if(reason != null){
					announceMessage += " Reason: " + reason + ".";
				}

				//send it
				Core.getServerData().getChannelNewsAnnounces().sendMessage(announceMessage).queue();

				logger.info("Canceled event was announced. " + eventId);

			}

		}catch(Exception e){
			logger.error("Error updating the event in schedule.", e);
		}

	}

	private Message getEventMessage(String eventId){

		TextChannel scheduleChannel = Core.getServerData().getChannelTextSchedule();
		List<Message> messageList = scheduleChannel.getHistory().retrievePast(10).complete();
		return messageList.stream()
			.filter(m -> m.getContentRaw().contains(eventId))
			.findFirst()
			.orElse(null);

	}

}
