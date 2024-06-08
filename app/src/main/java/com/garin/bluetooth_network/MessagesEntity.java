package com.garin.bluetooth_network;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class MessagesEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String text;
}
