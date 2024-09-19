<h1>The Echoes Bot</h1>
<sup>Java 21</sup>

<p>The Echoes Bot is a Discord application used in TheEchoes server.</p>
<p>Features:</p>
<ul>
    <li>Slash commands:<ul>
        <li>/roll [*upper_limit] - roll a number in a range.</li>
        <li>/event-new [title, date, time, *description] - create a new event.</li>
        <li>/event-start [event_id] - start the event.</li>
    </ul></li>
    <li>Register new members:<ul>
        <li>Create a message with buttons in register channel.</li>
        <li>Buttons:<ul>
            <li>Intern - assign the role Intern.</li>
            <li>Member - open a modal which requires character name. Validate it and assign the role Member.</li>
        </ul></li>
    </ul></li>
    <li>Listen for `SCHEDULED_EVENTS` and post the new events in the channel #schedule.</li>
    <li>Crawl data of the character for registration.</li>
</ul>