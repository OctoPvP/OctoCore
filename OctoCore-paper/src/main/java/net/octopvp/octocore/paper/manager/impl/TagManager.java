package net.octopvp.octocore.paper.manager.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.DatabaseManager;
import net.octopvp.octocore.paper.database.redis.packets.server.ReloadTagsPacket;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TagManager extends Manager {
    @Getter
    private static final List<PlayerTag> tags = new ArrayList<>();
    private static MongoCollection<Document> tagsCollection;

    public static void deleteTag(PlayerTag tag) {
        deleteTag(tag.getId());
    }

    public static void deleteTag(UUID id) {
        tagsCollection.deleteOne(Filters.eq("id", id.toString()));
        new ReloadTagsPacket().send();
        //reloadTags();
    }

    public static void reloadTags() {
        tags.clear();
        loadTags();
    }

    public static void saveTag(PlayerTag tag) {
        String json = OctoCore.getGson().toJson(tag);
        tagsCollection.replaceOne(Filters.eq("id", tag.getId().toString()), Document.parse(json), new ReplaceOptions().upsert(true));
        new ReloadTagsPacket().send();
        //reloadTags();
    }

    public static void createTag(PlayerTag tag) {
        String json = OctoCore.getGson().toJson(tag);
        tagsCollection.insertOne(Document.parse(json));
        new ReloadTagsPacket().send();
        //reloadTags();
    }

    public static void setPlayerTag(Player player, String tagName) {
        PlayerTag tag = getTagByName(tagName);
        if (tag == null)
            return;
        PlayerData pdata = PlayerManager.getProfile(player.getUniqueId());
        pdata.addTag(tag);
        pdata.setTag(tag);
    }

    public static PlayerTag getTag(UUID id) {
        return tags.stream().filter(tag -> tag.getId().equals(id)).findFirst().orElse(null);
    }

    public static PlayerTag[] convertId(List<String> ids) {
        return ids.toArray(new PlayerTag[0]);
    }

    public static PlayerTag getTagByName(String name) { //fixme fix the tag matching
        PlayerTag retTag = null;
        for (PlayerTag tag : tags) {
            if (tag.getName().equalsIgnoreCase(name)) {
                retTag = tag;
                break;
            }
        }
        return retTag;
    }

    private static void loadTags() {
        JsonWriterSettings settings = JsonWriterSettings.builder()
                .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
                .build();
        FindIterable<Document> documents = tagsCollection.find();
        for (Document document : documents) {
            String json = document.toJson(settings);
            tags.add(deserializeTag(json));
            Logger.debug("Loaded tag: " + json);
        }
    }

    private static Document getTagDoc(PlayerTag tag) {
        return getTagDoc(tag.getId());
    }

    public static Document getTagDoc(UUID tagID) {
        return tagsCollection.find(Filters.eq("id", tagID.toString())).first();
    }

    private static PlayerTag deserializeTag(String json) {
        return OctoCore.getGson().fromJson(json, PlayerTag.class);
    }

    @Override
    public void init(OctoCore plugin) {
        Logger.debug("Loading Tags");
        tagsCollection = DatabaseManager.getMongoDatabase().getCollection("tags");
        loadTags();
    }

    @Override
    public void disable() {

    }

}
