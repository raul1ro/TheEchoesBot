package com.bot.theechoesbot.eventhandler;

import com.bot.theechoesbot.core.Core;
import com.bot.theechoesbot.core.Globals;
import com.bot.theechoesbot.eventhandler.template.EventHandler;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implement Handler for ScheduledEventCreateEvent
 */
public class EventCreateEventHandler implements EventHandler<ScheduledEventCreateEvent>{

	private final static Logger logger = LoggerFactory.getLogger(EventCreateEventHandler.class);

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE (dd MMM)");

	@Override
	public void handle(ScheduledEventCreateEvent event){

		try{

			ScheduledEvent scheduledEvent = event.getScheduledEvent();

			OffsetDateTime startTime = scheduledEvent.getStartTime();
			String startDay = startTime.atZoneSameInstant(Globals.ZONE_ID_SERVER).format(dateTimeFormatter);

			String message = "- "
				+ startDay
				+ " - "
				+ MarkdownUtil.maskedLink(scheduledEvent.getName(), "https://discord.com/events/" + Core.getServerData().getGuildId() + "/" + scheduledEvent.getId());

			Core.getServerData().getChannelTextSchedule().sendMessage(message).queue();

			logger.info("Created event was announced. " + scheduledEvent.getId());

		}catch(Exception e){
			logger.error("Error adding the event in schedule", e);
		}

	}

}
