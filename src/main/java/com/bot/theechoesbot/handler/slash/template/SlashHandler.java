package com.bot.theechoesbot.handler.slash.template;

import com.bot.theechoesbot.entity.ServerData;
import com.bot.theechoesbot.handler.template.Handler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Handler for slash commands
 */
public interface SlashHandler extends Handler<SlashCommandInteractionEvent>{

	@Override
	abstract public void handle(SlashCommandInteractionEvent event, ServerData serverData);

}