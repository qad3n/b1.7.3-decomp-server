package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase {
    private String key = null;

    abstract void writeTagContents(DataOutput var1) throws IOException;

    abstract void readTagContents(DataInput var1) throws IOException;

    public abstract byte getType();

    public String getKey() {
        if (this.key == null) {
            return "";
        }
        return this.key;
    }

    public NBTBase setKey(String name) {
        this.key = name;
        return this;
    }

    public static NBTBase readTag(DataInput input) throws IOException {
        byte by = input.readByte();
        if (by == 0) {
            return new NBTTagEnd();
        }
        NBTBase nBTBase = NBTBase.createTagOfType(by);
        nBTBase.key = input.readUTF();
        nBTBase.readTagContents(input);
        return nBTBase;
    }

    public static void writeTag(NBTBase nbt, DataOutput output) throws IOException {
        output.writeByte(nbt.getType());
        if (nbt.getType() == 0) {
            return;
        }
        output.writeUTF(nbt.getKey());
        nbt.writeTagContents(output);
    }

    public static NBTBase createTagOfType(byte type) {
        switch (type) {
            case 0: {
                return new NBTTagEnd();
            }
            case 1: {
                return new NBTTagByte();
            }
            case 2: {
                return new NBTTagShort();
            }
            case 3: {
                return new NBTTagInt();
            }
            case 4: {
                return new NBTTagLong();
            }
            case 5: {
                return new NBTTagFloat();
            }
            case 6: {
                return new NBTTagDouble();
            }
            case 7: {
                return new NBTTagByteArray();
            }
            case 8: {
                return new NBTTagString();
            }
            case 9: {
                return new NBTTagList();
            }
            case 10: {
                return new NBTTagCompound();
            }
        }
        return null;
    }

    public static String getTagName(byte type) {
        switch (type) {
            case 0: {
                return "TAG_End";
            }
            case 1: {
                return "TAG_Byte";
            }
            case 2: {
                return "TAG_Short";
            }
            case 3: {
                return "TAG_Int";
            }
            case 4: {
                return "TAG_Long";
            }
            case 5: {
                return "TAG_Float";
            }
            case 6: {
                return "TAG_Double";
            }
            case 7: {
                return "TAG_Byte_Array";
            }
            case 8: {
                return "TAG_String";
            }
            case 9: {
                return "TAG_List";
            }
            case 10: {
                return "TAG_Compound";
            }
        }
        return "UNKNOWN";
    }
}

