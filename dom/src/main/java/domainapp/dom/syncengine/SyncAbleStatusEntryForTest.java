package domainapp.dom.syncengine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class SyncAbleStatusEntryForTest implements SyncAbleStatusEntry {

    @Getter @Setter
    private String uniqueId;

    @Getter @Setter
    private String localETag;

    @Getter @Setter
    private String remoteETag;

    @Override public boolean remove() {
        return true;
    }

    @Override public SyncAbleStatusEntry harmonizeETags(final String eTag) {
        setLocalETag(eTag);
        setRemoteETag(eTag);
        return this;
    }

}
