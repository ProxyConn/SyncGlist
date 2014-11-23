package net.portalblock.syncglist;

import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * Created by portalBlock on 11/23/2014.
 */
public class SyncGlist extends Plugin {

    @Override
    public void onEnable() {
        /*
        We have to delay by 2 seconds here to be safe because its originally a Bungee command so ProxyConn waits 1 second to register
        it and we should wait 2.
        */
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                getProxy().getPluginManager().registerCommand(SyncGlist.this, new GlistCommand());
            }
        }, 2, TimeUnit.SECONDS);
    }
}
