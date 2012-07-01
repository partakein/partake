package in.partake.model.dto;

import net.sf.json.JSONObject;

/**
 * PARTAKE で用いるモデルのベースクラス。
 *
 * キャッシュをのちのち使いたいので、データベースから取得してきたオブジェクトは freeze() される。freeze() されたオブジェクトは変更不能である。
 * 新たにデータを変更したい場合、copy() を取得してから変更のこと。copy() されたオブジェクトは freeze() が解除される。
 *
 * なお、BinaryData などの大きなオブジェクトでは、unsafe なコピーがなされることがあるので注意すること。
 *
 * @author shinyak
 *
 * @param <T>
 */
public abstract class PartakeModel<T extends PartakeModel<?>> {
    /** Model が freeze されていれば true */
    private volatile boolean frozen;

    protected PartakeModel() {
        this.frozen = false;
    }

    /**
     * @return primary key
     */
    public abstract Object getPrimaryKey();

    /** @return a json object. */
    public abstract JSONObject toJSON();

    // ----------------------------------------------------------------------
    // frozen

    /**
     * check the object is frozen. If frozen, UnsupportedOperationException will be raised.
     */
    protected final void checkFrozen() {
        if (frozen) { throw new UnsupportedOperationException(); }
    }

    /**
     * @return true if frozen
     */
    public final boolean isFrozen() {
        return frozen;
    }

    /**
     * freeze the object and return itself.
     * @return itself.
     */
    @SuppressWarnings("unchecked")
    public final T freeze() {
        this.frozen = true;
        return (T)this;
    }
}
