package com.bot.theechoesbot.core.handler.slash.template;

import com.bot.theechoesbot.core.handler.template.Handler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Handler for slash commands
 */
public interface SlashHandler extends Handler<SlashCommandInteractionEvent>{

	@Override
	abstract public void handle(SlashCommandInteractionEvent event);

}