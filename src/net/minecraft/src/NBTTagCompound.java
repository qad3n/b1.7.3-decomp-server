package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class NBTTagCompound
extends NBTBase {
    private Map<String, NBTBase> tagMap = new HashMap<String, NBTBase>();

    @Override
    void writeTagContents(DataOutput output) throws IOException {
        for (NBTBase nBTBase : this.tagMap.values()) {
            NBTBase.writeTag(nBTBase, output);
        }
        output.writeByte(0);
    }

    @Override
    void readTagContents(DataInput input) throws IOException {
        NBTBase nBTBase;
        this.tagMap.clear();
        while ((nBTBase = NBTBase.readTag(input)).getType() != 0) {
            this.tagMap.put(nBTBase.getKey(), nBTBase);
        }
    }

    public Collection<NBTBase> getValues() {
        return this.tagMap.values();
    }

    @Override
    public byte getType() {
        return 10;
    }

    public void setTag(String key, NBTBase element) {
        this.tagMap.put(key, element.setKey(key));
    }

    public void setByte(String key, byte value) {
        this.tagMap.put(key, new NBTTagByte(value).setKey(key));
    }

    public void setShort(String key, short value) {
        this.tagMap.put(key, new NBTTagShort(value).setKey(key));
    }

    public void setInteger(String key, int value) {
        this.tagMap.put(key, new NBTTagInt(value).setKey(key));
    }

    public void setLong(String key, long value) {
        this.tagMap.put(key, new NBTTagLong(value).setKey(key));
    }

    public void setFloat(String key, float value) {
        this.tagMap.put(key, new NBTTagFloat(value).setKey(key));
    }

    public void setDouble(String key, double value) {
        this.tagMap.put(key, new NBTTagDouble(value).setKey(key));
    }

    public void setString(String key, String value) {
        this.tagMap.put(key, new NBTTagString(value).setKey(key));
    }

    public void setByteArray(String key, byte[] value) {
        this.tagMap.put(key, new NBTTagByteArray(value).setKey(key));
    }

    public void setCompoundTag(String key, NBTTagCompound nbt) {
        this.tagMap.put(key, nbt.setKey(key));
    }

    public void setBoolean(String key, boolean value) {
        this.setByte(key, value ? (byte)1 : 0);
    }

    public boolean hasKey(String key) {
        return this.tagMap.containsKey(key);
    }

    public byte getByte(String key) {
        if (!this.tagMap.containsKey(key)) {
            return 0;
        }
        return ((NBTTagByte)this.tagMap.get((Object)key)).byteValue;
    }

    public short getShort(String key) {
        if (!this.tagMap.containsKey(key)) {
            return 0;
        }
        return ((NBTTagShort)this.tagMap.get((Object)key)).shortValue;
    }

    public int getInteger(String key) {
        if (!this.tagMap.containsKey(key)) {
            return 0;
        }
        return ((NBTTagInt)this.tagMap.get((Object)key)).intValue;
    }

    public long getLong(String key) {
        if (!this.tagMap.containsKey(key)) {
            return 0L;
        }
        return ((NBTTagLong)this.tagMap.get((Object)key)).longValue;
    }

    public float getFloat(String key) {
        if (!this.tagMap.containsKey(key)) {
            return 0.0f;
        }
        return ((NBTTagFloat)this.tagMap.get((Object)key)).floatValue;
    }

    public double getDouble(String key) {
        if (!this.tagMap.containsKey(key)) {
            return 0.0;
        }
        return ((NBTTagDouble)this.tagMap.get((Object)key)).doubleValue;
    }

    public String getString(String key) {
        if (!this.tagMap.containsKey(key)) {
            return "";
        }
        return ((NBTTagString)this.tagMap.get((Object)key)).stringValue;
    }

    public byte[] getByteArray(String key) {
        if (!this.tagMap.containsKey(key)) {
            return new byte[0];
        }
        return ((NBTTagByteArray)this.tagMap.get((Object)key)).byteArray;
    }

    public NBTTagCompound getCompoundTag(String key) {
        if (!this.tagMap.containsKey(key)) {
            return new NBTTagCompound();
        }
        return (NBTTagCompound)this.tagMap.get(key);
    }

    public NBTTagList getTagList(String key) {
        if (!this.tagMap.containsKey(key)) {
            return new NBTTagList();
        }
        return (NBTTagList)this.tagMap.get(key);
    }

    public boolean getBoolean(String key) {
        return this.getByte(key) != 0;
    }

    public String toString() {
        return "" + this.tagMap.size() + " entries";
    }
}

