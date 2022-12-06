package qd_jpt.patch;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarPatcher {
    public static void applyPatch(File targetFile, File patchFile, URI patchedFileLocation) throws IOException {
        // create backup of existing jar file in case the patch is not successful
        File backup = new File(targetFile.getAbsolutePath() + ".backup");
        Files.copy(targetFile.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);

        JarFile targetJar = new JarFile(targetFile);
        ZipFile patch = new ZipFile(patchFile);

        // read list of removed files from patch file
        ZipEntry removedEntryListZipEntry = patch.getEntry(".removed");
        List<String> removedEntryList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(patch.getInputStream(removedEntryListZipEntry), StandardCharsets.UTF_8))){
            while(br.ready()){
                removedEntryList.add(br.readLine());
            }
        }

        // extract list of names of all entries in the patch file
        ArrayList<? extends ZipEntry> newEntries = Collections.list(patch.entries());
        List<String> newEntryNames = newEntries.stream().map(ZipEntry::getName).collect(Collectors.toList());

        // extract list of all entries in the existing jar file
        List<JarEntry> oldEntries = Collections.list(targetJar.entries());
        List<JarEntry> oldEntriesFiltered = new ArrayList<>();
        for(JarEntry entry : oldEntries){
            // only add entry to new jar file if it is not on the removed list and is present in the new entries
            if(!removedEntryList.contains(entry.getRealName()) && !newEntryNames.contains(entry.getName())){
                oldEntriesFiltered.add(entry);
            }
        }

        try(JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(patchedFileLocation.toString()))){
            // transfer all unmodified entries from old jar to patched jar
            for(JarEntry entry : oldEntriesFiltered){
                JarEntry dest = new JarEntry(entry.getName());
                jarOutputStream.putNextEntry(dest);
                try (InputStream content = targetJar.getInputStream(entry)) {
                    content.transferTo(jarOutputStream);
                }
                jarOutputStream.closeEntry();
            }

            // transfer all new or modified entries from patch file to patched jar
            for(ZipEntry entry : newEntries){
                // ignore patch file specific entries
                if(!entry.getName().equals(".removed") && !entry.getName().equals(".target")){
                    JarEntry dest = new JarEntry(entry.getName());
                    jarOutputStream.putNextEntry(dest);
                    try (InputStream content = patch.getInputStream(entry)) {
                        content.transferTo(jarOutputStream);
                    }
                    jarOutputStream.closeEntry();
                }
            }
            jarOutputStream.finish();
        }
    }
}
