package com.bot.theechoesbot.command.template;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.context.MessageContextInteraction;

import java.util.List;

public abstract class MessageCommand extends Command<MessageContextInteraction>{

	public MessageCommand(String name, List<Long> allowedRolesIds){
		super(name, allowedRolesIds);
	}

	@Override
	public CommandData buildCommand(){
		return Commands.message(name);
	}

}
