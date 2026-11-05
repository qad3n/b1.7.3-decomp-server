package net.minecraft.src;

public interface IProgressUpdate {
    public void progressStartNoAbort(String var1);

    public void displayLoadingString(String var1);

    public void setLoadingProgress(int var1);
}

