package com.bot.theechoesbot.entity;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class ServerData{

	private final long guildId;
	private final long botChannelId;

	private final long channelNewsAnnouncesId;
	private final long channelVoiceEventId;
	private final long channelTextScheduleId;
	private final long channelTextRegisterId;

	private final long roleInternId;
	private final long roleMemberId;
	private final long roleEliteId;
	private final long roleMasterId;

	private Guild guild;
	private TextChannel botChannel;

	private NewsChannel channelNewsAnnounces;
	private VoiceChannel channelVoiceEvent;
	private TextChannel channelTextSchedule;
	private TextChannel channelTextRegister;

	private Role roleIntern;
	private Role roleMember;
	private Role roleElite;
	private Role roleMaster;

	public ServerData(
		long guildId, long botChannelId,
		long channelNewsAnnouncesId, long channelVoiceEventId, long channelTextScheduleId, long channelTextRegisterId,
		long roleInternId, long roleMemberId, long roleEliteId, long roleMasterId
	){
		this.guildId = guildId;
		this.botChannelId = botChannelId;
		this.channelNewsAnnouncesId = channelNewsAnnouncesId;
		this.channelVoiceEventId = channelVoiceEventId;
		this.channelTextScheduleId = channelTextScheduleId;
		this.channelTextRegisterId = channelTextRegisterId;
		this.roleInternId = roleInternId;
		this.roleMemberId = roleMemberId;
		this.roleEliteId = roleEliteId;
		this.roleMasterId = roleMasterId;
	}

	@SuppressWarnings("DataFlowIssue")
	public void init(JDA jda){
		guild = jda.getGuildById(guildId);
		botChannel = guild.getTextChannelById(botChannelId);
		channelNewsAnnounces = guild.getNewsChannelById(channelNewsAnnouncesId);
		channelVoiceEvent = guild.getVoiceChannelById(channelVoiceEventId);
		channelTextSchedule = guild.getTextChannelById(channelTextScheduleId);
		channelTextRegister = guild.getTextChannelById(channelTextRegisterId);
		roleIntern = guild.getRoleById(roleInternId);
		roleMember = guild.getRoleById(roleMemberId);
		roleElite = guild.getRoleById(roleEliteId);
		roleMaster = guild.getRoleById(roleMasterId);
	}

	public long getGuildId(){ return guildId; }

	public long getBotChannelId(){ return botChannelId; }

	public long getChannelNewsAnnouncesId(){ return channelNewsAnnouncesId; }

	public long getChannelVoiceEventId(){ return channelVoiceEventId; }

	public long getChannelTextScheduleId(){ return channelTextScheduleId; }

	public long getChannelTextRegisterId(){ return channelTextRegisterId; }

	public long getRoleInternId(){ return roleInternId; }

	public long getRoleMemberId(){ return roleMemberId; }

	public long getRoleEliteId(){ return roleEliteId; }

	public long getRoleMasterId(){ return roleMasterId; }

	public Guild getGuild(){ return guild; }

	public TextChannel getBotChannel(){ return botChannel; }

	public NewsChannel getChannelNewsAnnounces(){ return channelNewsAnnounces; }

	public VoiceChannel getChannelVoiceEvent(){ return channelVoiceEvent; }

	public TextChannel getChannelTextSchedule(){ return channelTextSchedule; }

	public TextChannel getChannelTextRegister(){ return channelTextRegister; }

	public Role getRoleIntern(){ return roleIntern; }

	public Role getRoleMember(){ return roleMember; }

	public Role getRoleElite(){ return roleElite; }

	public Role getRoleMaster(){ return roleMaster; }

}