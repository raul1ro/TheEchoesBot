package com.bot.theechoesbot.core.handler;

import com.bot.theechoesbot.core.handler.template.Handler;
import com.bot.theechoesbot.core.service.RegisterService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle for Modal
 */
public class ModalInteractionHandler implements Handler<ModalInteractionEvent>{

	private final static Logger logger = LoggerFactory.getLogger(ModalInteractionHandler.class);

	private final RegisterService registerService;
	public ModalInteractionHandler(RegisterService registerService) {
		this.registerService = registerService;
	}

	@Override
	public void handle(ModalInteractionEvent event){

		Member member = event.getMember();
		String modalId = event.getModalId();

		switch(modalId){

			case "modal_register_member": this.registerService.registerMember(event, member); break;
			default: {
				event.reply("Unknown modal id: " + modalId).setEphemeral(true).queue();
				logger.warn("Unknown modal id: " + modalId);
				break;
			}

		}

	}

}
