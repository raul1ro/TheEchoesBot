package com.bot.theechoesbot.core.handler;

import com.bot.theechoesbot.core.Globals;
import com.bot.theechoesbot.core.handler.template.Handler;
import com.bot.theechoesbot.object.ServerData;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implement Handler for ScheduledEvent
 */
public class EventCreateHandler implements Handler<ScheduledEventCreateEvent>{

	private final static Logger logger = LoggerFactory.getLogger(EventCreateHandler.class);

	private final ServerData serverData;

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE (dd MMM)");

	public EventCreateHandler(ServerData serverData){
		this.serverData = serverData;
	}

	@Override
	public void handle(ScheduledEventCreateEvent event){

		try{

			ScheduledEvent scheduledEvent = event.getScheduledEvent();

			OffsetDateTime startTime = scheduledEvent.getStartTime();
			String startDay = startTime.atZoneSameInstant(Globals.ZONE_ID_SERVER).format(dateTimeFormatter);

			String message = "- "
				+ startDay
				+ " - "
				+ MarkdownUtil.maskedLink(scheduledEvent.getName(), "https://discord.com/events/" + this.serverData.getGuildId() + "/" + scheduledEvent.getId());

			this.serverData.getScheduleChannel().sendMessage(message).queue();

		}catch(Exception e){
			logger.error("Error adding the event in schedule", e);
		}

	}

}
