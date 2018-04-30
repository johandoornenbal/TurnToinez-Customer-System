package domainapp.dom.syncengine;

public interface SyncAbleStatusEntry {

    /**
     * unique identifier of the instance
     * @return
     */
    String getUniqueId();

    /**
     * identifier (hash, timestamp, ...) of the local "contents" of the mutable instance, unique to the state of the single instance
     */
    String getLocalETag();

    /**
     * identifier (hash, timestamp, ...) of the remote "contents" of the mutable instance, unique to the state of the single instance
     */
    String getRemoteETag();

    /**
     *
     * @return true if removed successfully, false otherwise
     */
    boolean remove();

    /**
     * Should set both remote and local etag to the given value
     * @param eTag
     * @return this
     */
    SyncAbleStatusEntry harmonizeETags(final String eTag);

}
