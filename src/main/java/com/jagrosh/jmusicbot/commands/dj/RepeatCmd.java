/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RepeatCmd extends DJCommand
{
    public RepeatCmd(Bot bot)
    {
        super(bot);
        this.name = "repeat";
        this.help = "음악 재생이 완료되었을때 반복할지 설정합니다";
        this.arguments = "[on|off]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }
    
    // override musiccommand's execute because we don't actually care where this is used
    @Override
    protected void execute(CommandEvent event) 
    {
        boolean value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().isEmpty())
        {
            value = !settings.getRepeatMode();
        }
        else if(event.getArgs().equalsIgnoreCase("true") || event.getArgs().equalsIgnoreCase("on"))
        {
            value = true;
        }
        else if(event.getArgs().equalsIgnoreCase("false") || event.getArgs().equalsIgnoreCase("off"))
        {
            value = false;
        }
        else
        {
            event.replyError("유효한 옵션은 `on`이나 `off`입니다. (비워 두면 켜고 끕니다)");
            return;
        }
        settings.setRepeatMode(value);
        event.replySuccess("이제 반복 모드는 `"+(value ? "켜짐" : "꺼짐")+"` 입니다.");
    }

    @Override
    public void doCommand(CommandEvent event) { /* Intentionally Empty */ }
}
