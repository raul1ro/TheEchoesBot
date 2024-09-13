package com.bot.theechoesbot.core.handler.template;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

/**
 * Handler interface for different events
 */
public interface Handler<T>{
	void handle(T event);
}
