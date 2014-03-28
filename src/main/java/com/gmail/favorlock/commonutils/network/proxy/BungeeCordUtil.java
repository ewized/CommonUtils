package com.gmail.favorlock.commonutils.network.proxy;

import com.gmail.favorlock.commonutils.CommonUtils;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeCordUtil {

    public static void sendPlayerToServer(Player player, String server) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeUTF("Connect");
            dos.writeUTF(server);

            player.sendPluginMessage(CommonUtils.getPlugin(), "BungeeCord", baos.toByteArray());
        } catch (IOException e) {
            CommonUtils.getPlugin().getLogger().severe(String.format("Error sending %s to server: %s",
                    player.getName(),
                    server));
        }
    }

}
