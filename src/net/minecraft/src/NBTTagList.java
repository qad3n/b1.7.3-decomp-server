package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NBTTagList extends NBTBase {
    private List<NBTBase> tagList = new ArrayList<>();
    private byte tagType;

    @Override
    void writeTagContents(DataOutput output) throws IOException {
        if (this.tagList.size() > 0) {
            this.tagType = this.tagList.get(0).getType();
        } else {
            this.tagType = 1;
        }

        output.writeByte(this.tagType);
        output.writeInt(this.tagList.size());

        for (int i = 0; i < this.tagList.size(); i++) {
            this.tagList.get(i).writeTagContents(output);
        }
    }

    @Override
    void readTagContents(DataInput input) throws IOException {
        this.tagType = input.readByte();
        int i = input.readInt();
        this.tagList = new ArrayList<>();

        for (int j = 0; j < i; j++) {
            NBTBase nbtbase = NBTBase.createTagOfType(this.tagType);
            nbtbase.readTagContents(input);
            this.tagList.add(nbtbase);
        }
    }

    @Override
    public byte getType() {
        return 9;
    }

    @Override
    public String toString() {
        return "" + this.tagList.size() + " entries of type " + NBTBase.getTagName(this.tagType);
    }

    public void setTag(NBTBase element) {
        this.tagType = element.getType();
        this.tagList.add(element);
    }

    public NBTBase tagAt(int index) {
        return this.tagList.get(index);
    }

    public int tagCount() {
        return this.tagList.size();
    }
}
