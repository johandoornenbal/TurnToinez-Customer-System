package domainapp.dom.syncengine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class SyncAbleForTest implements SyncAble {

    @Getter @Setter
    private String uniqueId;

    @Getter @Setter
    private String field;

    @Override public String getETag() {
        return getField();
    }
}
