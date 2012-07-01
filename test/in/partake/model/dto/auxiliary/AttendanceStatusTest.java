package in.partake.model.dto.auxiliary;

import org.junit.Assert;
import org.junit.Test;

public class AttendanceStatusTest {

    @Test
    public void testToSafeValueOf() throws Exception {
        Assert.assertEquals(AttendanceStatus.UNKNOWN, AttendanceStatus.safeValueOf("UNKNOWN"));
        Assert.assertEquals(AttendanceStatus.PRESENT, AttendanceStatus.safeValueOf("PRESENT"));
        Assert.assertEquals(AttendanceStatus.PRESENT, AttendanceStatus.safeValueOf("present"));
        Assert.assertEquals(AttendanceStatus.ABSENT,  AttendanceStatus.safeValueOf("ABSENT"));
        Assert.assertEquals(AttendanceStatus.ABSENT,  AttendanceStatus.safeValueOf("absent"));
        
        Assert.assertEquals(AttendanceStatus.UNKNOWN, AttendanceStatus.safeValueOf(null));
        Assert.assertEquals(AttendanceStatus.UNKNOWN, AttendanceStatus.safeValueOf(""));
        Assert.assertEquals(AttendanceStatus.UNKNOWN, AttendanceStatus.safeValueOf("HOGE"));
        Assert.assertEquals(AttendanceStatus.UNKNOWN, AttendanceStatus.safeValueOf("FUGA"));
        Assert.assertEquals(AttendanceStatus.UNKNOWN, AttendanceStatus.safeValueOf("dakljfdlsakfdlasjflksa"));
        Assert.assertEquals(AttendanceStatus.UNKNOWN, AttendanceStatus.safeValueOf("PRESEN"));
        Assert.assertEquals(AttendanceStatus.UNKNOWN, AttendanceStatus.safeValueOf("ABSEN"));        
    }
}
