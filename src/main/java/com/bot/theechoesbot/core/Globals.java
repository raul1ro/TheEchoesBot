package com.bot.theechoesbot.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class Globals{

	public static final Random RANDOM = new Random();

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
		.followRedirects(false)
		.readTimeout(Duration.ofSeconds(5))
		.retryOnConnectionFailure(true)
		.build();

	public static final ZoneId ZONE_ID_SERVER = ZoneId.of("Europe/Brussels");
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm");

}
