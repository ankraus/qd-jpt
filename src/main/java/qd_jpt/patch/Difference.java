package qd_jpt.patch;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

public class Difference {
    private final List<JarEntry> removed;
    private final List<JarEntry> added;
    private final List<JarEntry> modified;

    private Difference(List<JarEntry> removed, List<JarEntry> added, List<JarEntry> modified){
        this.removed = removed;
        this.added = added;
        this.modified = modified;
    }

    public List<JarEntry> getRemoved() {
        return removed;
    }

    public List<JarEntry> getAdded() {
        return added;
    }

    public List<JarEntry> getModified() {
        return modified;
    }

    @Override
    public String toString() {
        return "Difference{" +
                "removed=" + removed.size() +
                ", added=" + added.size() +
                ", modified=" + modified.size() +
                '}';
    }

    public static class DifferenceBuilder{
        private final List<JarEntry> removed = new ArrayList<>();
        private final List<JarEntry> added = new ArrayList<>();
        private final List<JarEntry> modified = new ArrayList<>();

        public Difference build(){
            return new Difference(removed, added, modified);
        }

        public void remove(JarEntry entry){
            this.removed.add(entry);
        }

        public void add(JarEntry entry){
            this.added.add(entry);
        }

        public void modify(JarEntry entry){
            this.modified.add(entry);
        }
    }
}
