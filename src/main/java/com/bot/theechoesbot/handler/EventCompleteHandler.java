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

public class EventCompleteHandler implements Handler<ScheduledEvent>{

	private final static Logger logger = LoggerFactory.getLogger(EventCompleteHandler.class);

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


}
