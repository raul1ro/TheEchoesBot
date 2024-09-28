package com.bot.theechoesbot.handler;

import com.bot.theechoesbot.entity.ServerData;
import com.bot.theechoesbot.handler.template.Handler;
import com.bot.theechoesbot.service.RegisterService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handle for buttons
 */
@SuppressWarnings("DataFlowIssue")
public class ButtonInteractionHandler implements Handler<ButtonInteractionEvent>{

	private final static Logger logger = LoggerFactory.getLogger(ButtonInteractionHandler.class);

	private final RegisterService registerService;

	public ButtonInteractionHandler(RegisterService registerService) {
		this.registerService = registerService;
	}

	@Override
	public void handle(ButtonInteractionEvent event, ServerData serverData){

		Member member = event.getMember();
		String buttonId = event.getButton().getId();

		//get the roles of user and validate it
		List<Role> roles = event.getMember().getRoles();
		if(!roles.isEmpty()){
			event.reply("You are already registered.\nIf you think this is an error contact <@328569043974094849>.").setEphemeral(true).queue();
			logger.info("User already registered: " + member.getEffectiveName());
			return;
		}

		switch(buttonId){

			case "register_intern": registerService.registerIntern(event, member, serverData.getGuild(), serverData.getInternRole()); break;
			case "register_member": registerService.createModalRegisterMember(event); break;
			default: {
				event.reply("Unknown button id: " + buttonId).setEphemeral(true).queue();
				logger.warn("Unknown button id: " + buttonId);
				break;
			}

		}

	}

}
