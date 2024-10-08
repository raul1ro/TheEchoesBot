package com.bot.theechoesbot.handler;

import com.bot.theechoesbot.core.Cache;
import com.bot.theechoesbot.core.Core;
import com.bot.theechoesbot.handler.template.Handler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implement Handler for ScheduledEventUpdateStatusEvent
 */
public class EventCancelHandler implements Handler<ScheduledEvent>{

	private final static Logger logger = LoggerFactory.getLogger(EventCancelHandler.class);

	@Override
	public void handle(ScheduledEvent event){

		try{

			//get the id
			String eventId = event.getId();

			//find the message in schedule
			TextChannel scheduleChannel = Core.getServerData().getTextScheduleChannel();
			List<Message> messageList = scheduleChannel.getHistory().retrievePast(10).complete();
			Message message = messageList.stream()
				.filter(m -> m.getContentRaw().contains(eventId))
				.findFirst()
				.orElse(null);
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
				Core.getServerData().getNewsAnnouncesChannel().sendMessage(announceMessage).queue();

				logger.info("Canceled event was announced. " + eventId);

			}

		}catch(Exception e){
			logger.error("Error updating the event in schedule.", e);
		}

	}

}
