package domainapp.dom.syncengine;

public interface SyncAble {

    // implementing https://unterwaditzer.net/2016/sync-algorithm.html

    /**
     * unique identifier of the instance
     * @return
     */
    String getUniqueId();

    /**
     * sets unique identifier of the instance
     */
    void setUniqueId(final String id);

    /**
     * identifier (hash, timestamp, ...) of the "contents" of the mutable instance, unique to the state of the single instance     * @return
     */
    String getETag();

}
