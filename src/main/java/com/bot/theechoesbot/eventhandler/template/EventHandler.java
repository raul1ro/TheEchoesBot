package com.bot.theechoesbot.eventhandler.template;

import net.dv8tion.jda.api.events.Event;

/**
 * EventHandler interface to handle different events
 */
public interface EventHandler<T extends Event>{
	void handle(T event);
}
