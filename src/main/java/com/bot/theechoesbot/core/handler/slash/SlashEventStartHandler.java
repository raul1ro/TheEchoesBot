package com.bot.theechoesbot.core.handler.slash;

import com.bot.theechoesbot.core.handler.slash.template.SlashHandler;
import com.bot.theechoesbot.core.listener.BotListener;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement /event-start
 */
public class SlashEventStartHandler implements SlashHandler{

	private final Logger logger = LoggerFactory.getLogger(SlashEventStartHandler.class);

	private final BotListener bot;

	public SlashEventStartHandler(BotListener bot){
		this.bot = bot;
	}

	@Override
	public void handle(SlashCommandInteractionEvent event){

		try{

			//extract the input event id
			String eventId = event.getOptions().stream()
				.filter(e -> e.getName().equals("event_id"))
				.findFirst()
				.map(OptionMapping::getAsString)
				.get(); //the input event_id is required, the discord client is forcing the user to give value.

			//get the schedule and validate it
			ScheduledEvent scheduledEvent = event.getJDA().getScheduledEventById(eventId);
			if(scheduledEvent == null){
				event.reply("Could not find event with id " + eventId).queue();
				return;
			}
			if(scheduledEvent.getStatus() != ScheduledEvent.Status.SCHEDULED){
				event.reply("The event has already started.").queue();
				return;
			}

			//modify status to active
			//and announce it
			scheduledEvent.getManager()
				.setStatus(ScheduledEvent.Status.ACTIVE)
				.and(
					bot.getAnnouncesChannel().sendMessage(
						"[" + scheduledEvent.getName() + "](https://discord.com/events/" + this.bot.getGuildId() + "/" + eventId + ") is starting. Get Ready."
					)
				).queue();

			//reply
			event.reply("Event started.").queue();

		}catch(Exception e){

			logger.error("Error starting event", e);
			event.reply("Error starting event: " + e.getMessage()).queue();

		}

	}

}
