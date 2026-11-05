package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataWatcher {
    private static final HashMap<Class<?>, Integer> dataTypes = new HashMap<>();
    private final Map<Integer, DataWatcher.WatchableObject> watchedObjects = new HashMap<>();
    private boolean objectChanged;

    public void addObject(int id, Object value) {
        Integer integer = dataTypes.get(value.getClass());
        if (integer == null) {
            throw new IllegalArgumentException("Unknown data type: " + value.getClass());
        }

        if (id > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
        }

        if (this.watchedObjects.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        }

        DataWatcher.WatchableObject datawatcher$watchableobject = new DataWatcher.WatchableObject(integer, id, value);
        this.watchedObjects.put(id, datawatcher$watchableobject);
    }

    public byte getWatchableObjectByte(int id) {
        return (Byte)this.watchedObjects.get(id).getObject();
    }

    public int getWatchableObjectInteger(int id) {
        return (Integer)this.watchedObjects.get(id).getObject();
    }

    public String getWatchableObjectString(int id) {
        return (String)this.watchedObjects.get(id).getObject();
    }

    public void updateObject(int id, Object value) {
        DataWatcher.WatchableObject datawatcher$watchableobject = this.watchedObjects.get(id);
        if (!value.equals(datawatcher$watchableobject.getObject())) {
            datawatcher$watchableobject.setObject(value);
            datawatcher$watchableobject.setWatching(true);
            this.objectChanged = true;
        }
    }

    public boolean hasObjectChanged() {
        return this.objectChanged;
    }

    public static void writeObjectsInListToStream(List<DataWatcher.WatchableObject> entries, DataOutputStream output) throws IOException {
        if (entries != null) {
            for (DataWatcher.WatchableObject datawatcher$watchableobject : entries) {
                writeWatchableObject(output, datawatcher$watchableobject);
            }
        }

        output.writeByte(127);
    }

    public ArrayList<DataWatcher.WatchableObject> getChangedObjects() {
        ArrayList arraylist = null;
        if (this.objectChanged) {
            for (DataWatcher.WatchableObject datawatcher$watchableobject : this.watchedObjects.values()) {
                if (datawatcher$watchableobject.getWatching()) {
                    datawatcher$watchableobject.setWatching(false);
                    if (arraylist == null) {
                        arraylist = new ArrayList();
                    }

                    arraylist.add(datawatcher$watchableobject);
                }
            }
        }

        this.objectChanged = false;
        return arraylist;
    }

    public void writeWatchableObjects(DataOutputStream output) throws IOException {
        for (DataWatcher.WatchableObject datawatcher$watchableobject : this.watchedObjects.values()) {
            writeWatchableObject(output, datawatcher$watchableobject);
        }

        output.writeByte(127);
    }

    private static void writeWatchableObject(DataOutputStream output, DataWatcher.WatchableObject entry) throws IOException {
        int i = (entry.getObjectType() << 5 | entry.getDataValueId() & 31) & 0xFF;
        output.writeByte(i);
        switch (entry.getObjectType()) {
            case 0:
                output.writeByte((Byte)entry.getObject());
                break;
            case 1:
                output.writeShort((Short)entry.getObject());
                break;
            case 2:
                output.writeInt((Integer)entry.getObject());
                break;
            case 3:
                output.writeFloat((Float)entry.getObject());
                break;
            case 4:
                Packet.writeString((String)entry.getObject(), output);
                break;
            case 5:
                ItemStack itemstack = (ItemStack)entry.getObject();
                output.writeShort(itemstack.getItem().shiftedIndex);
                output.writeByte(itemstack.stackSize);
                output.writeShort(itemstack.getItemDamage());
                break;
            case 6:
                ChunkCoordinates chunkcoordinates = (ChunkCoordinates)entry.getObject();
                output.writeInt(chunkcoordinates.posX);
                output.writeInt(chunkcoordinates.posY);
                output.writeInt(chunkcoordinates.posZ);
        }
    }

    public static List<DataWatcher.WatchableObject> readWatchableObjects(DataInputStream input) throws IOException {
        ArrayList arraylist = null;

        for (byte b0 = input.readByte(); b0 != 127; b0 = input.readByte()) {
            if (arraylist == null) {
                arraylist = new ArrayList();
            }

            int i = (b0 & 224) >> 5;
            int j = b0 & 31;
            DataWatcher.WatchableObject datawatcher$watchableobject = null;
            switch (i) {
                case 0:
                    datawatcher$watchableobject = new DataWatcher.WatchableObject(i, j, input.readByte());
                    break;
                case 1:
                    datawatcher$watchableobject = new DataWatcher.WatchableObject(i, j, input.readShort());
                    break;
                case 2:
                    datawatcher$watchableobject = new DataWatcher.WatchableObject(i, j, input.readInt());
                    break;
                case 3:
                    datawatcher$watchableobject = new DataWatcher.WatchableObject(i, j, input.readFloat());
                    break;
                case 4:
                    datawatcher$watchableobject = new DataWatcher.WatchableObject(i, j, Packet.readString(input, 64));
                    break;
                case 5:
                    short short1 = input.readShort();
                    byte b1 = input.readByte();
                    short short2 = input.readShort();
                    datawatcher$watchableobject = new DataWatcher.WatchableObject(i, j, new ItemStack(short1, b1, short2));
                    break;
                case 6:
                    int k = input.readInt();
                    int l = input.readInt();
                    int i1 = input.readInt();
                    datawatcher$watchableobject = new DataWatcher.WatchableObject(i, j, new ChunkCoordinates(k, l, i1));
            }

            arraylist.add(datawatcher$watchableobject);
        }

        return arraylist;
    }

    static {
        dataTypes.put(Byte.class, 0);
        dataTypes.put(Short.class, 1);
        dataTypes.put(Integer.class, 2);
        dataTypes.put(Float.class, 3);
        dataTypes.put(String.class, 4);
        dataTypes.put(ItemStack.class, 5);
        dataTypes.put(ChunkCoordinates.class, 6);
    }

    public static class WatchableObject {
        private final int objectType;
        private final int dataValueId;
        private Object watchedObject;
        private boolean isWatching;

        public WatchableObject(int type, int id, Object value) {
            this.dataValueId = id;
            this.watchedObject = value;
            this.objectType = type;
            this.isWatching = true;
        }

        public int getDataValueId() {
            return this.dataValueId;
        }

        public void setObject(Object value) {
            this.watchedObject = value;
        }

        public Object getObject() {
            return this.watchedObject;
        }

        public int getObjectType() {
            return this.objectType;
        }

        public boolean getWatching() {
            return this.isWatching;
        }

        public void setWatching(boolean dirty) {
            this.isWatching = dirty;
        }
    }
}
