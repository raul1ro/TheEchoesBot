package com.bot.theechoesbot.command.template;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public abstract class SlashCommand extends Command<SlashCommandInteraction>{

	protected final String description;
	protected final List<OptionData> inputs;

	public SlashCommand(String name, List<Long> allowedRolesIds, String description, List<OptionData> inputs){
		super(name, allowedRolesIds);
		this.description = description;
		this.inputs = inputs;
	}

	@Override
	public SlashCommandData buildCommand(){
		return Commands.slash(name, description)
			.addOptions(inputs);
	}

	protected abstract void executeImpl(SlashCommandInteraction interaction);

}
