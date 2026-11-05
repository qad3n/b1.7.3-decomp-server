package net.minecraft.src;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedStreamTools {
    public static NBTTagCompound readCompressed(InputStream is) throws IOException {
        DataInputStream datainputstream = new DataInputStream(new GZIPInputStream(is));

        try {
            return read(datainputstream);
        } finally {
            datainputstream.close();
        }
    }

    public static void writeGzippedCompoundToOutputStream(NBTTagCompound nbt, OutputStream os) throws IOException {
        DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(os));

        try {
            write(nbt, dataoutputstream);
        } finally {
            dataoutputstream.close();
        }
    }

    public static NBTTagCompound read(DataInput input) throws IOException {
        NBTBase nbtbase = NBTBase.readTag(input);
        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound)nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(NBTTagCompound nbt, DataOutput output) throws IOException {
        NBTBase.writeTag(nbt, output);
    }
}
