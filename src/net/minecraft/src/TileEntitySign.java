package net.minecraft.src;

public class TileEntitySign extends TileEntity {
    public String[] signText = new String[]{"", "", "", ""};
    public int lineBeingEdited = -1;
    private boolean isEditAble = true;

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setString("Text1", this.signText[0]);
        nbt.setString("Text2", this.signText[1]);
        nbt.setString("Text3", this.signText[2]);
        nbt.setString("Text4", this.signText[3]);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.isEditAble = false;
        super.readFromNBT(nbt);

        for (int i = 0; i < 4; i++) {
            this.signText[i] = nbt.getString("Text" + (i + 1));
            if (this.signText[i].length() > 15) {
                this.signText[i] = this.signText[i].substring(0, 15);
            }
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        String[] astring = new String[4];

        for (int i = 0; i < 4; i++) {
            astring[i] = this.signText[i];
        }

        return new Packet130UpdateSign(this.xCoord, this.yCoord, this.zCoord, astring);
    }

    public boolean getIsEditAble() {
        return this.isEditAble;
    }

    public void setEditable(boolean editable) {
        this.isEditAble = editable;
    }
}
