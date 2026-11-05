package net.minecraft.src;


public interface ISaveFormat {
    public boolean isOldSaveType(String var1);

    public boolean converMapToMCRegion(String var1, IProgressUpdate var2);
}

