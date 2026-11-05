package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.DataOutput;
import java.util.Iterator;
import java.io.DataInput;
import java.io.OutputStream;
import java.io.InputStream;

public class MapStorage {
    private ISaveHandler storage;
    private Map<String, MapDataBase> savedDataById = new HashMap<>();
    private List<MapDataBase> savedData = new ArrayList<>();
    private Map<String, Short> idCounts = new HashMap<>();

    public MapStorage(ISaveHandler storage) {
        this.storage = storage;
        this.loadIdCounts();
    }

    public <T extends MapDataBase> T load(Class<T> type, String id) {
        MapDataBase mapdatabase = this.savedDataById.get(id);
        if (mapdatabase != null) {
            return (T)mapdatabase;
        }

        if (this.storage != null) {
            try {
                File file1 = this.storage.getDataFile(id);
                if (file1 != null && file1.exists()) {
                    try {
                        mapdatabase = (MapDataBase)type.getConstructor(String.class).newInstance(id);
                    } catch (Exception exception) {
                        throw new RuntimeException("Failed to instantiate " + type.toString(), exception);
                    }

                    FileInputStream fileinputstream = new FileInputStream(file1);
                    NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
                    fileinputstream.close();
                    mapdatabase.readNbt(nbttagcompound.getCompoundTag("data"));
                }
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }

        if (mapdatabase != null) {
            this.savedDataById.put(id, mapdatabase);
            this.savedData.add(mapdatabase);
        }

        return (T)mapdatabase;
    }

    public void set(String id, MapDataBase data) {
        if (data == null) {
            throw new RuntimeException("Can't set null data");
        }

        if (this.savedDataById.containsKey(id)) {
            this.savedData.remove(this.savedDataById.remove(id));
        }

        this.savedDataById.put(id, data);
        this.savedData.add(data);
    }

    public void save() {
        for (int i = 0; i < this.savedData.size(); i++) {
            MapDataBase mapdatabase = this.savedData.get(i);
            if (mapdatabase.isDirty()) {
                this.save(mapdatabase);
                mapdatabase.setDirty(false);
            }
        }
    }

    private void save(final MapDataBase data) {
        if (this.storage == null) {
            return;
        }
        try {
            final File dataFile = this.storage.getDataFile(data.id);
            if (dataFile != null) {
                final NBTTagCompound nbtTagCompound = new NBTTagCompound();
                data.writeNbt(nbtTagCompound);
                final NBTTagCompound nbt = new NBTTagCompound();
                nbt.setCompoundTag("data", nbtTagCompound);
                final FileOutputStream os = new FileOutputStream(dataFile);
                CompressedStreamTools.writeGzippedCompoundToOutputStream(nbt, os);
                os.close();
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadIdCounts() {
        try {
            this.idCounts.clear();
            if (this.storage == null) {
                return;
            }

            File file1 = this.storage.getDataFile("idcounts");
            if (file1 != null && file1.exists()) {
                DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
                NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
                datainputstream.close();

                for (NBTBase nbtbase : nbttagcompound.getValues()) {
                    if (nbtbase instanceof NBTTagShort) {
                        NBTTagShort nbttagshort = (NBTTagShort)nbtbase;
                        String s = nbttagshort.getKey();
                        short short1 = nbttagshort.shortValue;
                        this.idCounts.put(s, short1);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int getNextCount(String id) {
        Short oshort = this.idCounts.get(id);
        if (oshort == null) {
            oshort = (short)0;
        } else {
            oshort = (short)(oshort + 1);
        }

        this.idCounts.put(id, oshort);
        if (this.storage == null) {
            return oshort;
        }

        try {
            File file1 = this.storage.getDataFile("idcounts");
            if (file1 != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                for (String s : this.idCounts.keySet()) {
                    short short1 = this.idCounts.get(s);
                    nbttagcompound.setShort(s, short1);
                }

                DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));
                CompressedStreamTools.write(nbttagcompound, dataoutputstream);
                dataoutputstream.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return oshort;
    }
}
