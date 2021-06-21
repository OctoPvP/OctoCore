package net.octopvp.octocore.paper.manager.impl.autoinit;

import litebans.api.Database;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.litebans.BanObject;
import net.octopvp.octocore.paper.utils.MojangUtil;
import net.octopvp.octocore.paper.utils.nametag.MojangAPIUtil;
import net.octopvp.octocore.paper.utils.runnable.Tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LiteBansManager extends Manager {
    @Override
    public void init(OctoCore plugin) {}
    @Override
    public void disable() {}
    public static CompletableFuture<ArrayList<BanObject>> getBans(String name){
        CompletableFuture<ArrayList<BanObject>> completableFuture = new CompletableFuture<>();
        UUID uuid = MojangAPIUtil.getUUIDFromString(name);
        ArrayList<BanObject> banObject = null;
        try {
            banObject = getBans(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        completableFuture.complete(banObject);
        return completableFuture;
    }
    public static CompletableFuture<ArrayList<BanObject>> getBans(UUID uuid){
        CompletableFuture<ArrayList<BanObject>> completableFuture = new CompletableFuture<>();
        Tasks.runAsync(()->{
            String query = "SELECT * FROM {bans} WHERE uuid=?";
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, uuid.toString());
                try (ResultSet rs = st.executeQuery()) {
                    ArrayList<BanObject> banObjects = new ArrayList<>();
                    while (rs.next()) {
                        BanObject banObject = new BanObject();
                        String reason = rs.getString("reason");
                        String bannedByUuid = rs.getString("banned_by_uuid");
                        long time = rs.getLong("time");
                        long until = rs.getLong("until");
                        long id = rs.getLong("id");
                        boolean active = rs.getBoolean("active");
                        banObject.setReason(reason);
                        banObject.setBannedby(bannedByUuid + "");
                        banObject.setUntil(until);
                        banObject.setId(id);
                        banObject.setActive(active);
                        banObject.setTime(time);
                        banObjects.add(banObject);
                    }
                    completableFuture.complete(banObjects);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                completableFuture.complete(null);
            }
        });
        return completableFuture;
    }
}
