package qd_jpt.patch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarAnalyzer {

    public static void createPatchFile(Difference difference, JarFile jarFileOld, JarFile jarFileNew, File fileToSave) throws IOException {
        try (ZipOutputStream destFile = new ZipOutputStream(new FileOutputStream(fileToSave))) {
            // write added and modified files to the patch file
            for (ZipEntry entry : difference.getAdded()) {
                writeEntryToZipFile(jarFileNew, destFile, entry);
            }
            for (ZipEntry entry : difference.getModified()) {
                writeEntryToZipFile(jarFileNew, destFile, entry);
            }

            // write list of removed files to a special entry in the patch file
            ZipEntry patchRemovedFile = new JarEntry(".removed");
            destFile.putNextEntry(patchRemovedFile);
            for(JarEntry entry: difference.getRemoved()){
                String name = entry.getRealName();
                name += '\n';
                destFile.write(name.getBytes(StandardCharsets.UTF_8));
            }
            destFile.closeEntry();

            // write name of original (old) target jar to special entry in the patch file for comparison during patch
            ZipEntry patchTargetFile = new JarEntry(".target");
            destFile.putNextEntry(patchTargetFile);
            String targetFileName = jarFileOld.getName().substring(jarFileOld.getName().lastIndexOf(File.separatorChar) + 1);
            destFile.write(targetFileName.getBytes(StandardCharsets.UTF_8));
            destFile.closeEntry();

            destFile.finish();
        }
    }

    private static void writeEntryToZipFile(JarFile jarFileNew, ZipOutputStream destFile, ZipEntry entry) throws IOException {
        ZipEntry dest = new ZipEntry(entry.getName());
        destFile.putNextEntry(dest);
        try (InputStream content = jarFileNew.getInputStream(entry)) {
            content.transferTo(destFile);
        }
        destFile.closeEntry();
    }

    public static Difference calculateDifference(JarFile jarFileOld, JarFile jarFileNew) throws IOException {
        List<JarEntry> oldEntries = Collections.list(jarFileOld.entries());
        List<JarEntry> newEntries = Collections.list(jarFileNew.entries());
        Difference.DifferenceBuilder differenceBuilder = new Difference.DifferenceBuilder();

        for(JarEntry entryA : oldEntries){
            boolean found = false;
            boolean modified = false;
            for(JarEntry entryB : newEntries){
                if(entryA.getRealName().equals(entryB.getRealName())){ // file is present in both jars
                    if(entryA.getSize() != entryB.getSize()){
                        modified = true; // difference in file size is indicative of modification, no deeper check needed
                    } else {
                        // check all files of equal size for differences in content
                        InputStream oldFileInputStream = jarFileOld.getInputStream(entryA);
                        InputStream newFileInputStream = jarFileNew.getInputStream(entryB);
                        while(oldFileInputStream.available() > 0 && newFileInputStream.available() > 0 && !modified){
                            byte[] oldBytes = oldFileInputStream.readNBytes(64);
                            byte[] newBytes = newFileInputStream.readNBytes(64);
                            modified = !Arrays.equals(oldBytes, newBytes);
                        }
                    }
                    if(modified){
                        differenceBuilder.modify(entryA); // add to list of modified files
                    }
                    found = true;
                    break; // file has been found, no further search necessary
                }
            }
            if(!found){
                differenceBuilder.remove(entryA); // add to list of removed files
            }

        }

        for(JarEntry entryA : newEntries){ // check for files that are only present in the new jar
            boolean found = false;
            for(JarEntry entryB : oldEntries){
                if(entryA.getRealName().equals(entryB.getRealName())){
                    found = true;
                    break;
                }
            }
            if(!found){
                differenceBuilder.add(entryA); // add to list of added files
            }
        }

        Difference difference = differenceBuilder.build();
        System.out.println(difference);

        return difference;
    }

}
