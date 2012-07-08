package in.partake.base;

import java.util.UUID;

/**
 * Id is intended to use like UUID. Actually we would like to extend Id class to create
 * EventId, UserId, etc., but UUID is a final class.
 *
 * @author shinyak
 *
 */
public class Id {
    final private UUID id;

    public static Id randomId() {
        return new Id(UUID.randomUUID());
    }

    public Id(String uuid) {
        assert uuid != null;
        if (uuid == null)
            throw new IllegalArgumentException();

        this.id = UUID.fromString(uuid);
    }

    public Id(UUID uuid) {
        assert uuid != null;
        if (uuid == null)
            throw new IllegalArgumentException();

        this.id = uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Id))
            return false;

        Id lhs = this;
        Id rhs = (Id) obj;

        return lhs.id.equals(rhs.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
