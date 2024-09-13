package com.bot.theechoesbot.core.handler;

import com.bot.theechoesbot.core.Globals;
import com.bot.theechoesbot.core.handler.template.Handler;
import com.bot.theechoesbot.core.listener.BotListener;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implement Handler for ScheduledEvent
 */
public class EventCreateHandler implements Handler<ScheduledEventCreateEvent>{

	private final BotListener bot;

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE (dd MMM)");

	public EventCreateHandler(BotListener bot){
		this.bot = bot;
	}

	@Override
	public void handle(ScheduledEventCreateEvent event){

		ScheduledEvent scheduledEvent = event.getScheduledEvent();

		OffsetDateTime startTime = scheduledEvent.getStartTime();
		String startDay = startTime.atZoneSameInstant(Globals.ZONE_ID_SERVER).format(dateTimeFormatter);

		String message = "- "
			+ startDay
			+ " - [" + scheduledEvent.getName() + "](https://discord.com/events/" + this.bot.getGuildId() + "/" + scheduledEvent.getId() + ")";

		bot.getScheduleChannel().sendMessage(message).queue();

	}

}
