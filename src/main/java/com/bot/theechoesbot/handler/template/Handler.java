package com.bot.theechoesbot.handler.template;

import com.bot.theechoesbot.entity.ServerData;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

/**
 * Handler interface for different events
 */
public interface Handler<T>{
	void handle(T event, ServerData serverData);
}
