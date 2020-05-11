/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.owner;

import java.io.IOException;
import java.util.List;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
import com.github.LastorderDC.josaformatter.KoreanUtils;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlaylistCmd extends OwnerCommand 
{
    private final Bot bot;
    public PlaylistCmd(Bot bot)
    {
        this.bot = bot;
        this.guildOnly = false;
        this.name = "playlist";
        this.arguments = "<append|delete|make|setdefault>";
        this.help = "재생목록 관리";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.children = new OwnerCommand[]{
            new ListCmd(),
            new AppendlistCmd(),
            new DeletelistCmd(),
            new MakelistCmd(),
            new DefaultlistCmd(bot)
        };
    }

    @Override
    public void execute(CommandEvent event) 
    {
        StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" 재생목록 관리 명령:\n");
        for(Command cmd: this.children)
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                    .append(" ").append(cmd.getArguments()==null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
        event.reply(builder.toString());
    }
    
    public class MakelistCmd extends OwnerCommand 
    {
        public MakelistCmd()
        {
            this.name = "make";
            this.aliases = new String[]{"create"};
            this.help = "새 재생목록을 만듭니다";
            this.arguments = "<이름>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if(bot.getPlaylistLoader().getPlaylist(pname)==null)
            {
                try
                {
                    bot.getPlaylistLoader().createPlaylist(pname);
                    event.reply(KoreanUtils.format(event.getClient().getSuccess()+" 재생목록 `%s`을 만들었습니다!",pname));
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" 재생목록을 만들수 없었습니다: "+e.getLocalizedMessage());
                }
            }
            else
                event.reply(KoreanUtils.format(event.getClient().getError()+" 재생목록 `%s`은 이미 있습니다!",pname));
        }
    }
    
    public class DeletelistCmd extends OwnerCommand 
    {
        public DeletelistCmd()
        {
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "재생목록을 삭제합니다";
            this.arguments = "<이름>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if(bot.getPlaylistLoader().getPlaylist(pname)==null)
                event.reply(KoreanUtils.format(event.getClient().getError()+" 재생목록 `%s`은 존재하지 않습니다!",pname));
            else
            {
                try
                {
                    bot.getPlaylistLoader().deletePlaylist(pname);
                    event.reply(KoreanUtils.format(event.getClient().getSuccess()+" 재생목록 `%s`을 삭제했습니다!",pname));
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" 재생목록을 삭제할수 없었습니다: "+e.getLocalizedMessage());
                }
            }
        }
    }
    
    public class AppendlistCmd extends OwnerCommand 
    {
        public AppendlistCmd()
        {
            this.name = "append";
            this.aliases = new String[]{"add"};
            this.help = "재생목록에 노래를 추가합니다";
            this.arguments = "<이름> <주소> | <주소> | ...";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<2)
            {
                event.reply(event.getClient().getError()+" 재생목록 이름과 추가할 주소를 적어주세요!");
                return;
            }
            String pname = parts[0];
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(pname);
            if(playlist==null)
                event.reply(KoreanUtils.format(event.getClient().getError()+" 재생목록 `%s`은 존재하지 않습니다!",pname));
            else
            {
                StringBuilder builder = new StringBuilder();
                playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
                String[] urls = parts[1].split("\\|");
                for(String url: urls)
                {
                    String u = url.trim();
                    if(u.startsWith("<") && u.endsWith(">"))
                        u = u.substring(1, u.length()-1);
                    builder.append("\r\n").append(u);
                }
                try
                {
                    bot.getPlaylistLoader().writePlaylist(pname, builder.toString());
                    event.reply(event.getClient().getSuccess()+" "+urls.length+" 개의 아이템을 재생목록 `"+pname+"`에 추가했습니다!");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" 재생목록에 노래를 추가할수 없었습니다: "+e.getLocalizedMessage());
                }
            }
        }
    }
    
    public class DefaultlistCmd extends AutoplaylistCmd 
    {
        public DefaultlistCmd(Bot bot)
        {
            super(bot);
            this.name = "setdefault";
            this.aliases = new String[]{"default"};
            this.arguments = "<재생목록 이름|NONE>";
            this.guildOnly = true;
        }
    }
    
    public class ListCmd extends OwnerCommand 
    {
        public ListCmd()
        {
            this.name = "all";
            this.aliases = new String[]{"available","list"};
            this.help = "사용 가능한 재생목록을 표시합니다";
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            if(!bot.getPlaylistLoader().folderExists())
                bot.getPlaylistLoader().createFolder();
            if(!bot.getPlaylistLoader().folderExists())
            {
                event.reply(event.getClient().getWarning()+" 재생목록 폴더가 존재하지 않으며 생성할 수 없습니다!");
                return;
            }
            List<String> list = bot.getPlaylistLoader().getPlaylistNames();
            if(list==null)
                event.reply(event.getClient().getError()+" 사용 가능한 재생목록을 불러올 수 없습니다!");
            else if(list.isEmpty())
                event.reply(event.getClient().getWarning()+" 재생목록 폴더에 재생목록이 없습니다!");
            else
            {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess()+" 사용 가능한 재생목록:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString());
            }
        }
    }
}
