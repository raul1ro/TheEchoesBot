package com.bot.theechoesbot.handler.template;

/**
 * Handler interface for different events
 */
public interface Handler<T>{
	void handle(T event);
}
