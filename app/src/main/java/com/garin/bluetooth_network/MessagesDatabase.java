package com.garin.bluetooth_network;
import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {MessagesEntity.class}, version = 1)
public abstract class MessagesDatabase extends RoomDatabase {
    public abstract MessagesDao messagesDao();
}
