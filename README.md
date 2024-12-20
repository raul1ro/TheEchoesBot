<h1>The Echoes Bot</h1>
<code>Java 21</code>
<code>Spring Boot 3.2.3</code>
<code>JDA 5.1.0</code>
<code>OkHttp3 4.12.0</code>
<code>Jackson Databind 2.14.0</code>
<p>The Echoes Bot is a Discord application used in TheEchoes server.</p>
<p>Features:</p>
<ul>
    <li>Slash commands:<ul>
        <li><code>roll [*upper_limit]</code> - roll a number in a range.</li>
        <li><code>event-new [title, date, time, *description, *leader]</code> - create a new event.</li>
        <li><code>event-start [event_id, *message]</code> - start the event.</li>
        <li><code>event-cancel [event_id, *reason]</code> - cancel the event.</li>
        <li><code>clear-channel [channel, only_app]</code> - delete all (or only app) messages from a channel.</li>
    </ul></li>
    <li>Message commands:<ul>
        <li><code>Edit Message</code> - edit the message of the bot.</li>
    </ul></li>
    <li>Listen events:<ul>
        <li><code>ScheduledEventCreateEvent</code> - create a message with the new event.</li>
        <li><code>ScheduledEventUpdateStatusEvent</code> - update the message when the event status changes.</li>
    </ul></li>
    <li>Register new members:<ul>
        <li>Create registration message (with buttons).</li>
        <li>Buttons:<ul>
            <li>Intern - assign the role <code>Intern</code>.</li>
            <li>Member - open a modal which requires character name. Validate it and assign the role <code>Member</code>.</li>
        </ul></li>
    </ul></li>
    <li>Crawl data of the character for registration.</li>
</ul>