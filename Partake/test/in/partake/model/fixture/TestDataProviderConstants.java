package in.partake.model.fixture;

import java.util.UUID;

public interface TestDataProviderConstants {
    // Users
    public static final String INVALID_USER_ID = new UUID(1, -1).toString();

    public static final String DEFAULT_USER_ID = new UUID(1, 0).toString();
    public static final UUID DEFAULT_TWITTER_LINK_ID = new UUID(100, 0);
    public static final long DEFAULT_TWITTER_ID = 0;
    public static final String DEFAULT_TWITTER_SCREENNAME = "testUser";
    public static final UUID DEFAULT_USER_OPENID_ID = new UUID(101, 1);
    public static final String DEFAULT_USER_OPENID_IDENTIFIER = "http://www.example.com/ident";
    public static final UUID DEFAULT_USER_OPENID_ALTERNATIVE_ID = new UUID(101, 2);
    public static final String DEFAULT_USER_OPENID_ALTERNATIVE_IDENTIFIER = "http://www.example.com/alternative";

    // Users who do not have any specific rolls.
    public static final String[] DEFAULT_USER_IDS = new String[] {
        new UUID(1, 100).toString(), new UUID(1, 101).toString(), new UUID(1, 102).toString(), new UUID(1, 103).toString(), new UUID(1, 104).toString(),
    };
    public static final UUID[] DEFAULT_TWITTER_LINK_IDS = new UUID[] {
        new UUID(100, 100), new UUID(1, 101), new UUID(1, 102), new UUID(1, 103), new UUID(1, 104),
    };
    public static final long[] DEFAULT_USER_TWITTER_IDS = new long[] {
        100, 101, 102, 103, 104
    };
    public static final String[] DEFAULT_USER_TWITTER_SCREENNAME = new String[] {
        "Kotori Otonashi", "Chihaya Kisaragi", "Haruka Amami", "Miki Hoshii", "Mami Tokachi Futami"
    };

    public static final String DEFAULT_ANOTHER_USER_ID = new UUID(1, 1).toString();
    public static final UUID DEFAULT_ANOTHER_TWITTER_LINK_ID = new UUID(100, 1);
    public static final long DEFAULT_ANOTHER_TWITTER_ID = 1;
    public static final String DEFAULT_ANOTHER_TWITTER_SCREENNAME = "testUser1";

    public static final String USER_WITHOUT_PREF_ID = new UUID(1, 2).toString();
    public static final UUID USER_WITHOUT_PREF_TWITTER_LINK_ID = new UUID(100, 2);
    public static final long USER_WITHOUT_PREF_TWITTER_ID = 2;
    public static final String USER_WITHOUT_PREF_SCREENNAME = "testUser2";

    public static final String USER_WITH_PRIVATE_PREF_ID = new UUID(1, 3).toString();
    public static final UUID USER_WITH_PRIVATE_PREF_TWITTER_LINK_ID = new UUID(100, 3);
    public static final long USER_WITH_PRIVATE_PREF_TWITTER_ID = 3;
    public static final String USER_WITH_PRIVATE_PREF_SCREENNAME = "testUser3";

    public static final String ADMIN_USER_ID = new UUID(1, 8).toString();
    public static final UUID ADMIN_USER_TWITTER_LINK_ID = new UUID(100, 8);
    public static final long ADMIN_USER_TWITTER_ID = 8;
    public static final String ADMIN_USER_SCREENNAME = "partakein";

    public static final String EVENT_OWNER_ID = new UUID(1, 10).toString();
    public static final UUID EVENT_OWNER_TWITTER_LINK_ID = new UUID(100, 10);
    public static final long EVENT_OWNER_TWITTER_ID = 10;
    public static final String EVENT_OWNER_TWITTER_SCREENNAME = "eventOwner";

    public static final String EVENT_EDITOR_ID = new UUID(1, 20).toString();
    public static final UUID EVENT_EDITOR_TWITTER_LINK_ID = new UUID(100, 20);
    public static final long EVENT_EDITOR_TWITTER_ID = 20;
    public static final String EVENT_EDITOR_TWITTER_SCREENNAME = "eventEditor";

    public static final String EVENT_COMMENTOR_ID = new UUID(1, 30).toString();
    public static final UUID EVENT_COMMENTOR_TWITTER_LINK_ID = new UUID(100, 30);
    public static final long EVENT_COMMENTOR_TWITTER_ID = 30;
    public static final String EVENT_COMMENTOR_TWITTER_SCREENNAME = "eventCommentor";

    public static final String EVENT_ENROLLED_USER_ID = new UUID(1, 40).toString();
    public static final UUID EVENT_ENROLLED_USER_TWITTER_LINK_ID = new UUID(100, 40);
    public static final long EVENT_ENROLLED_USER_TWITTER_ID = 40;
    public static final String EVENT_ENROLLED_USER_TWITTER_SCREENNAME = "eventEnrolledUser";

    public static final String EVENT_RESERVED_USER_ID = new UUID(1, 50).toString();
    public static final UUID EVENT_RESERVED_USER_TWITTER_LINK_ID = new UUID(100, 50);
    public static final long EVENT_RESERVED_USER_TWITTER_ID = 50;
    public static final String EVENT_RESERVED_USER_TWITTER_SCREENNAME = "eventReservedUser";

    public static final String EVENT_CANCELLED_USER_ID = new UUID(1, 60).toString();
    public static final UUID EVENT_CANCELLED_USER_TWITTER_LINK_ID = new UUID(100, 60);
    public static final long EVENT_CANCELLED_USER_TWITTER_ID = 60;
    public static final String EVENT_CANCELLED_USER_TWITTER_SCREENNAME = "eventCancelledUser";

    public static final String EVENT_UNRELATED_USER_ID = new UUID(1, 70).toString();
    public static final UUID EVENT_UNRELATED_USER_TWITTER_LINK_ID = new UUID(100, 70);
    public static final long EVENT_UNRELATED_USER_TWITTER_ID = 70;
    public static final String EVENT_UNRELATED_USER_TWITTER_SCREENNAME = "eventUnrelatedUser";

    public static final String ATTENDANCE_PRESENT_USER_ID = new UUID(1, 80).toString();
    public static final UUID ATTENDANCE_PRESENT_USER_TWITTER_LINK_ID = new UUID(100, 80);
    public static final long ATTENDANCE_PRESENT_USER_TWITTER_ID = 80;
    public static final String ATTENDANCE_PRESENT_USER_TWITTER_SCREENNAME = "attendancePresentUser";

    public static final String ATTENDANCE_ABSENT_USER_ID = new UUID(1, 81).toString();
    public static final UUID ATTENDANCE_ABSENT_USER_TWITTER_LINK_ID = new UUID(100, 81);
    public static final long ATTENDANCE_ABSENT_USER_TWITTER_ID = 81;
    public static final String ATTENDANCE_ABSENT_USER_TWITTER_SCREENNAME = "attendanceAbsentUser";

    public static final String ATTENDANCE_UNKNOWN_USER_ID = new UUID(1, 82).toString();
    public static final UUID ATTENDANCE_UNKNOWN_USER_TWITTER_LINK_ID = new UUID(100, 82);
    public static final long ATTENDANCE_UNKNOWN_USER_TWITTER_ID = 82;
    public static final String ATTENDANCE_UNKNOWN_USER_TWITTER_SCREENNAME = "attendanceUnknownUser";

    public static final String DEFAULT_SENDER_ID = new UUID(1, 90).toString();
    public static final UUID DEFAULT_SENDER_TWITTER_LINK_ID = new UUID(100, 90);
    public static final long DEFAULT_SENDER_TWITTER_ID = 90;
    public static final String DEFAULT_SENDER_TWITTER_SCREENNAME = "sender";

    public static final String DEFAULT_RECEIVER_ID = new UUID(1, 91).toString();
    public static final UUID DEFAULT_RECEIVER_TWITTER_LINK_ID = new UUID(100, 91);
    public static final long DEFAULT_RECEIVER_TWITTER_ID = 91;
    public static final String DEFAULT_RECEIVER_TWITTER_SCREENNAME = "receiver";

    public static final String IMAGE_OWNER_ID = new UUID(1, 92).toString();
    public static final UUID IMAGE_OWNER_TWITTER_LINK_ID = new UUID(100, 92);
    public static final long IMAGE_OWNER_TWITTER_ID = 92;
    public static final String IMAGE_OWNER_TWITTER_SCREENNAME = "imageowner";

    public static final String USER_NO_TWITTER_LINK_ID = new UUID(1, 93).toString();
    public static final String USER_NO_TWITTER_LINK_SCREEN_NAME = "notwitter";

    public static final String USER_TWITTER_NOAUTH_ID = new UUID(1, 94).toString();
    public static final UUID USER_TWITTER_NOAUTH_TWITTER_LINK_ID = new UUID(100, 94);
    public static final long USER_TWITTER_NOAUTH_TWITTER_ID = 94;
    public static final String USER_TWITTER_NOAUTH_TWITTER_SCREENNAME = "notwitterauth";

    // Events
    public static final String INVALID_EVENT_ID = new UUID(2, -1).toString();
    public static final String DEFAULT_EVENT_ID = new UUID(2, 0).toString();
    public static final String PRIVATE_EVENT_ID = new UUID(2, 10).toString();
    public static final String JAPANESE_EVENT_ID = new UUID(2, 20).toString();
    public static final String UNIQUEIDENTIFIER_EVENT_ID = new UUID(2, 30).toString();
    public static final String UNPUBLISHED_EVENT_ID = new UUID(2, 40).toString();
    public static final String PUBLISHED_EVENT_ID = new UUID(2, 41).toString();
    public static final String NO_PARTICIPANTS_EVENT_ID = new UUID(2, 50).toString();

    // Event Tickets
    public static final UUID INVALID_EVENT_TICKET_ID = new UUID(21, -1);
    public static final UUID DEFAULT_EVENT_TICKET_ID = new UUID(21, 0);
    public static final UUID PRIVATE_EVENT_TICKET_ID = new UUID(21, 10);
    public static final UUID JAPANESE_EVENT_TICKET_ID = new UUID(21, 20);
    public static final UUID UNIQUEIDENTIFIER_EVENT_TICKET_ID = new UUID(21, 30);
    public static final UUID UNPUBLISHED_EVENT_TICKET_ID = new UUID(21, 40);
    public static final UUID NO_PARTICIPANTS_EVENT_TICKET_ID = new UUID(21, 50);

    // Event Comments
    public static final String INVALID_COMMENT_ID = new UUID(4, -1).toString();
    public static final String OWNER_COMMENT_ID = new UUID(4, 1).toString();
    public static final String EDITOR_COMMENT_ID = new UUID(4, 2).toString();
    public static final String COMMENTOR_COMMENT_ID = new UUID(4, 3).toString();
    public static final String UNRELATED_USER_COMMENT_ID = new UUID(4, 4).toString();

    // Images
    public static final String EVENT_FOREIMAGE_ID = new UUID(3, 1).toString();
    public static final String EVENT_BACKIMAGE_ID = new UUID(3, 2).toString();
    public static final String IMAGE_OWNER_IMAGE_ID = new UUID(3, 3).toString();

    public static final String[] IMAGE_OWNED_BY_DEFAULT_USER_ID = new String[] {
        new UUID(3, 10).toString(), new UUID(3, 11).toString(), new UUID(3, 12).toString(), new UUID(3, 13).toString(), new UUID(3, 14).toString(),
        new UUID(3, 15).toString(), new UUID(3, 16).toString(), new UUID(3, 17).toString(), new UUID(3, 18).toString(), new UUID(3, 19).toString(),
    };
    // IMAGE_OWNED_BY_DEFAULT_USER_ID contains DEFAULT_IMAGE_ID.
    public static final String DEFAULT_IMAGE_ID = IMAGE_OWNED_BY_DEFAULT_USER_ID[0];
    public static final String IMAGE_HAVING_NO_THUMBNAIL_ID = IMAGE_OWNED_BY_DEFAULT_USER_ID[1];

    // Calendar Id
    public static final String DEFAULT_CALENDAR_ID = new UUID(5, 1).toString();
    public static final String ENROLLED_USER_CALENDAR_ID = new UUID(5, 2).toString();
    public static final String INVALID_CALENDAR_ID = new UUID(5, -1).toString();

    // Message
    public static final UUID DEFAULT_MESSAGE_ID = new UUID(6, 1);

    // TwitterMessage
    public static final String TWITTER_MESSAGE_INQUEUE_ID = new UUID(10, 1).toString();

    // UserNotificationMessage
    public static final String USER_NOTIFICATION_INQUEUE_ID = new UUID(11, 1).toString();

    // UserReceivedMessage
    public static final UUID USER_RECEIVED_MESSAGE_INQUEUE_ID = new UUID(12, 1);
    public static final UUID INVALID_USER_RECEIVED_MESSAGE_ID = new UUID(12, -1);

}
