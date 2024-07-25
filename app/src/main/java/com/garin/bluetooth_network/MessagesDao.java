package com.garin.bluetooth_network;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
@Dao
public interface MessagesDao {
    @Insert
    void insert(MessagesEntity messages);

    @Query("SELECT * FROM messages")
    List<Message> getAllMessages();
}
