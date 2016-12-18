package skadistats.clarity.model.state;

import skadistats.clarity.util.TextTable;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class EntityDumper {

    private static final ReentrantLock DEBUG_LOCK = new ReentrantLock();
    private static final TextTable DEBUG_DUMPER = new TextTable.Builder()
            .setFrame(TextTable.FRAME_COMPAT)
            .addColumn("FP")
            .addColumn("Property")
            .addColumn("Value")
            .build();

    public static String dump(String title, EntityState state) {
        List<DumpEntry> entries = state.collectDump();
        DEBUG_LOCK.lock();
        try {
            DEBUG_DUMPER.clear();
            DEBUG_DUMPER.setTitle(title);
            int r = 0;
            for (DumpEntry entry : entries) {
                DEBUG_DUMPER.setData(r, 0, entry.fieldPath);
                DEBUG_DUMPER.setData(r, 1, entry.name);
                DEBUG_DUMPER.setData(r, 2, entry.value);
                r++;
            }
            return DEBUG_DUMPER.toString();
        } finally {
            DEBUG_LOCK.unlock();
        }
    }

}
