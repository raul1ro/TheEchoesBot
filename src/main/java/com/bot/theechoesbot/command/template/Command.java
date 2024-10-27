package com.bot.theechoesbot.command.template;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public abstract class Command<T extends CommandInteraction>{

	protected final String name;
	protected final List<Long> allowedRolesIds;

	/**
	 * @param allowedRolesIds Null for access to everyone. Empty list for access to none.
	 */
	protected Command(String name, List<Long> allowedRolesIds){
		this.name = name;
		this.allowedRolesIds = allowedRolesIds;
	}

	public String getName(){
		return this.name;
	}

	public abstract CommandData buildCommand();

	public void execute(T interaction){

		//if the member is admin or allowed roles is null -> skip filter
		boolean skipFilter = interaction.getMember().hasPermission(Permission.ADMINISTRATOR) || allowedRolesIds == null;

		roleFilter:
		if(!skipFilter){

			//check if one of the member roles is in allowed roles
			List<Role> userRoles = interaction.getMember().getRoles();
			for(Role role : userRoles){
				if(allowedRolesIds.contains(role.getIdLong())){
					break roleFilter;
				}
			}

			interaction.reply("You are not allowed to use the command: " + this.name).setEphemeral(true).queue();
			return;

		}

		//execute the implementation
		executeImpl(interaction);

	}

	protected abstract void executeImpl(T interaction);

}