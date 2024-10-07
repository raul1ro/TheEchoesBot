package com.bot.theechoesbot.handler;

import com.bot.theechoesbot.core.Cache;
import com.bot.theechoesbot.core.Core;
import com.bot.theechoesbot.handler.template.Handler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventActiveHandler implements Handler<ScheduledEvent>{

	private static final Logger logger = LoggerFactory.getLogger(EventActiveHandler.class);

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
					" is starting. Get Ready.";

				//find message in cache and attach it
				String startMessage = Cache.Event.getAndRemove("start_" + eventId);
				if(startMessage != null){
					announceMessage += "\n" + startMessage;
				}

				//send it
				Core.getServerData().getNewsAnnouncesChannel().sendMessage(announceMessage).queue();

				logger.info("Start event was announced. " + eventId);

			}

		}catch(Exception e){
			logger.error("Error updating the event in schedule.", e);
		}

	}

}
