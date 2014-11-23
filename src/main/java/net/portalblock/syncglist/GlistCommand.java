package net.portalblock.syncglist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.portalblock.pc.publicapi.API;
import net.portalblock.pc.publicapi.APIAccess;
import net.portalblock.pc.publicapi.NetworkPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by portalBlock on 11/23/2014.
 */
public class GlistCommand extends Command {

    private API api;

    public GlistCommand() {
        super("glist", "bungeecord.command.glist", "players");
        /*
        We check to make sure that the API is the correct minimum version if so we set the API.
        We use the method API#getTotalOnlinePlayers() that is only included in version 0.3
         */
        if(APIAccess.getApiVersion() >= 0.3){
            //API is the required version.
            api = APIAccess.getApi(); //Set the API for use later.
            ProxyServer.getInstance().getLogger().warning("SyncGlist has loaded the ProxyConn API!");
        }else{
            //API is not the required version.
            ProxyServer.getInstance().getLogger().warning("SyncGlist requires a newer ProxyConn API version!");
            api = null; //Make the API null here.
            ProxyServer.getInstance().getPluginManager().unregisterCommand(this); //Remove the command.
        }

    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(api == null) return; //Always a good thing to check because something could stop the API from loading.

        HashMap<String, ArrayList<String>> players = new HashMap<String, ArrayList<String>>();

        for(NetworkPlayer player : api.getAllPlayers()){ //Get all the players across the network here.
            /*
            The player's server will be "Connecting" during the time from login to connecting to the lobby server.
            In this case we will not count them as logged in and just ignore them.
             */
            if(player.getServer().equals("Connecting")) continue;

            //Now we make sure there is an entry for the server they are on.
            if(!players.containsKey(player.getServer())) players.put(player.getServer(), new ArrayList<String>());

            //And finally we can add them to the list.
            players.get(player.getServer()).add(player.getName());
        }

        //Now we format the HashMap and send it to the player.
        for(Map.Entry<String, ArrayList<String>> entry : players.entrySet()){
            StringBuilder glistBuilder = new StringBuilder();
            glistBuilder.append(ChatColor.GREEN).append(String.format("[%s] ", entry.getKey())); //Add the server.
            glistBuilder.append(ChatColor.YELLOW).append(String.format("(%s): ", entry.getValue().size())); //Add the player amount.
            glistBuilder.append(ChatColor.RESET); //Reset the colors.

            boolean isFirst = true;
            for(String name : entry.getValue()){
                glistBuilder.append(isFirst ? name : ", " + name);
                isFirst = false;
            }

            /*
            Build it all and send it to the player so we can move the the next server.
             */
            sender.sendMessage(TextComponent.fromLegacyText(glistBuilder.toString()));
        }
        /*
        Now we send the number of total players online.
         */
        sender.sendMessage(TextComponent.fromLegacyText("Total players online: " + api.getTotalOnlinePlayers()));
    }
}
