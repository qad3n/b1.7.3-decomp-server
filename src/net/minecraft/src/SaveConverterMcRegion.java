package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class SaveConverterMcRegion extends SaveFormatOld {
    public SaveConverterMcRegion(File var1) {
        super(var1);
    }

    @Override
    public ISaveHandler get(String saveName, boolean createPlayerDataDir) {
        return new SaveOldDir(this.dir, saveName, createPlayerDataDir);
    }

    @Override
    public boolean isOldSaveType(String saveName) {
        WorldInfo worldinfo = this.getWorldInfo(saveName);
        return worldinfo != null && worldinfo.getVersion() == 0;
    }

    @Override
    public boolean converMapToMCRegion(String saveName, IProgressUpdate listener) {
        listener.setLoadingProgress(0);
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList();
        ArrayList arraylist2 = new ArrayList();
        ArrayList arraylist3 = new ArrayList();
        File file1 = new File(this.dir, saveName);
        File file2 = new File(file1, "DIM-1");
        System.out.println("Scanning folders...");
        this.collectAlphaChunkFiles(file1, arraylist, arraylist1);
        if (file2.exists()) {
            this.collectAlphaChunkFiles(file2, arraylist2, arraylist3);
        }

        int i = arraylist.size() + arraylist2.size() + arraylist1.size() + arraylist3.size();
        System.out.println("Total conversion count is " + i);
        this.convertAlphaChunksToRegions(file1, arraylist, 0, i, listener);
        this.convertAlphaChunksToRegions(file2, arraylist2, arraylist.size(), i, listener);
        WorldInfo worldinfo = this.getWorldInfo(saveName);
        worldinfo.setVersion(19132);
        ISaveHandler isavehandler = this.get(saveName, false);
        isavehandler.saveData(worldinfo);
        this.deleteAlphaChunkFiles(arraylist1, arraylist.size() + arraylist2.size(), i, listener);
        if (file2.exists()) {
            this.deleteAlphaChunkFiles(arraylist3, arraylist.size() + arraylist2.size() + arraylist1.size(), i, listener);
        }

        return true;
    }

    private void collectAlphaChunkFiles(File dir, ArrayList<ChunkFile> chunkFiles, ArrayList<File> chunkDirectories) {
        SaveConverterMcRegion.ChunkFolderPattern saveconvertermcregion$chunkfolderpattern = new SaveConverterMcRegion.ChunkFolderPattern();
        SaveConverterMcRegion.ChunkFilePattern saveconvertermcregion$chunkfilepattern = new SaveConverterMcRegion.ChunkFilePattern();
        File[] afile = dir.listFiles(saveconvertermcregion$chunkfolderpattern);

        for (File file1 : afile) {
            chunkDirectories.add(file1);
            File[] afile1 = file1.listFiles(saveconvertermcregion$chunkfolderpattern);

            for (File file2 : afile1) {
                File[] afile2 = file2.listFiles(saveconvertermcregion$chunkfilepattern);

                for (File file3 : afile2) {
                    chunkFiles.add(new ChunkFile(file3));
                }
            }
        }
    }

    private void convertAlphaChunksToRegions(File dir, ArrayList<ChunkFile> chunkFiles, int index, int total, IProgressUpdate listener) {
        Collections.sort(chunkFiles);
        byte[] abyte = new byte[4096];

        for (ChunkFile chunkfile : chunkFiles) {
            int i = chunkfile.getChunkX();
            int j = chunkfile.getChunkZ();
            RegionFile regionfile = RegionFileCache.getRegionFile(dir, i, j);
            if (!regionfile.isChunkSaved(i & 31, j & 31)) {
                try {
                    DataInputStream datainputstream = new DataInputStream(new GZIPInputStream(new FileInputStream(chunkfile.getFile())));
                    DataOutputStream dataoutputstream = regionfile.getChunkDataOutputStream(i & 31, j & 31);
                    int k = 0;

                    while ((k = datainputstream.read(abyte)) != -1) {
                        dataoutputstream.write(abyte, 0, k);
                    }

                    dataoutputstream.close();
                    datainputstream.close();
                } catch (IOException ioexception) {
                    ioexception.printStackTrace();
                }
            }

            index++;
            int l = (int)Math.round(100.0 * index / total);
            listener.setLoadingProgress(l);
        }

        RegionFileCache.flush();
    }

    private void deleteAlphaChunkFiles(ArrayList<File> chunkDirectories, int index, int total, IProgressUpdate listener) {
        for (File file1 : chunkDirectories) {
            File[] afile = file1.listFiles();
            deleteFilesAndDirs(afile);
            file1.delete();
            index++;
            int i = (int)Math.round(100.0 * index / total);
            listener.setLoadingProgress(i);
        }
    }

    public static class ChunkFilePattern implements FilenameFilter {
        public static final Pattern PATTERN = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+)\\.dat");

        private ChunkFilePattern() {
        }

        @Override
        public boolean accept(File file, String name) {
            Matcher matcher = PATTERN.matcher(name);
            return matcher.matches();
        }
    }

    public static class ChunkFolderPattern implements FileFilter {
        public static final Pattern PATTERN = Pattern.compile("[0-9a-z]|([0-9a-z][0-9a-z])");

        private ChunkFolderPattern() {
        }

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                Matcher matcher = PATTERN.matcher(file.getName());
                return matcher.matches();
            } else {
                return false;
            }
        }
    }
}
