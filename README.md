<h1>The Echoes Bot</h1>
<sup>Java 21</sup>

<p>The Echoes Bot is a Discord application used in TheEchoes server.</p>
<p>Features:</p>
<ul>
    <li>Slash commands:<ul>
        <li>/roll [*upper_limit] - roll a number in a range.</li>
        <li>/register [role, character_name] - registering the member by assigning a discord role.</li>
        <li>/event-new [title, date, time, *description] - create a new event</li>
        <li>/event-start [event_id] - start the event.</li>
    </ul></li>
    <li>Listen for `SCHEDULED_EVENTS` and post the new events in the channel #schedule.</li>
    <li>Crawl data of the character for registration.</li>
</ul>