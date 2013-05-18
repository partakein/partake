package in.partake.app;

import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.User;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.dto.auxiliary.EnqueteQuestion;
import in.partake.resource.Constants;

import java.util.ArrayList;
import java.util.UUID;

import play.Logger;

final class DemoEventCreator extends DBAccess<Event> {

    private static final long AUTHOR_TWITTER_ID = 163779313L;

    @Override
    protected Event doExecute(PartakeConnection con, IPartakeDAOs daos)
            throws DAOException, PartakeException {
        String demoEventId = Constants.DEMO_ID.toString();
        Event demoEvent = EventDAOFacade.getEventEx(con, daos, demoEventId);
        if (demoEvent != null) {
            return demoEvent;
        }

        Logger.info("No demo event found. Try to create...");
        String ownerId = findOwner(con, daos);
        if (ownerId == null) {
            ownerId = createOwner(con, daos);
        }

        demoEvent = buildDemoEvent(demoEventId, ownerId);
        EventDAOFacade.createWithSpecifiedId(con, daos, demoEvent);
        EventTicket ticket = EventTicket.createDefaultTicket(daos.getEventTicketAccess().getFreshId(con), demoEventId);
        daos.getEventTicketAccess().put(con, ticket);
        Logger.info("Demo event has been created. Event ID is: " + demoEventId);
        return demoEvent;
    }

    private String findOwner(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        DataIterator<User> iterator = daos.getUserAccess().getIterator(con);
        while (iterator.hasNext()) {
            User user = iterator.next();
            if ("partakein".equals(user.getScreenName())) {
                return user.getId();
            }
        }
        return null;
    }

    private String createOwner(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserTwitterLinkAccess twitterLinkageAccess = daos.getTwitterLinkageAccess();
        UUID twitterLinkageId = twitterLinkageAccess.getFreshId(con);
        String userId = daos.getUserAccess().getFreshId(con);
        UserTwitterLink twitterLinkage = twitterLinkageAccess.findByTwitterId(con, AUTHOR_TWITTER_ID);
        if (twitterLinkage == null) {
            twitterLinkage = new UserTwitterLink(twitterLinkageId,
                    AUTHOR_TWITTER_ID, userId, "partakein", "Partake", null, null, "https://si0.twimg.com/profile_images/1378901095/_.png");
            twitterLinkageAccess.put(con, twitterLinkage);
        }
        UserEx owner = UserDAOFacade.create(con, daos, twitterLinkage);
        return owner.getId();
    }

    private Event buildDemoEvent(String demoEventId, String ownerId) {
        DateTime startDate = TimeUtil.create(2017, 2, 13, 14, 00, 00);
        DateTime endDate = TimeUtil.create(2017, 2, 13, 18, 00, 00);
        String foreImageId = null;
        String backImageId = null;

        return new Event(demoEventId,
                "[Sample] ムサンガー君のクッキーを作ろう会",
                "\"パーテイク\" へようこそ。このようなイベント告知ページがカンタンに作れるよ！",
                "meeting",
                startDate,
                endDate,
                "http://partake.in/",
                "世田谷クッキングスタジオ",
                "東京都世田谷区東玉川2-15",
                DESCRIPTION,
                "#partake",
                ownerId,
                foreImageId,
                backImageId,
                null,
                false,
                new ArrayList<String>(),
                new ArrayList<String>(),
                new ArrayList<EnqueteQuestion>(),
                new DateTime(System.currentTimeMillis()),
                new DateTime(System.currentTimeMillis()),
                0);
    }

    private static final String DESCRIPTION = "<p>&nbsp;</p><div style=\"color: #000000; font-family: Verdana , Arial , Helvetica , sans-serif; font-size: 10.0px; background-color: #ffffff; margin: 8.0px;\"><p style=\"color: #000000;\"><span style=\"font-size: small;\"><strong><span style=\"color: #333333;\"><strong><span style=\"color: #000000; font-weight: normal;\"><span style=\"color: #000000;\"> <img src=\"../assets/images/logo.png\" alt=\"\" width=\"137\" height=\"21\" /></span></span></strong></span></strong></span></p><p style=\"color: #000000;\"><span style=\"font-size: small;\"><strong><span style=\"color: #99cc00;\">&nbsp;Twitterのアカウントを使って今すぐログイン！</span></strong></span></p><p style=\"color: #000000;\"><span style=\"font-size: xx-small;\">&nbsp;</span></p><p style=\"color: #000000;\"><span style=\"font-size: small; color: #333333;\"><strong><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline; color: #333333;\"><strong>イベント告知ページをカンタンに作れる</strong></span></span></strong></span></p><p style=\"color: #000000;\"><span style=\"font-size: small; color: #333333;\"><strong><span style=\"color: #333333;\"><strong><span style=\"font-weight: normal;\"><span style=\"color: #99cc00;\">&nbsp;&nbsp; &nbsp; ●</span>&nbsp;<span style=\"color: #333333;\"><strong>イメージ画像と</strong>、<strong>背景に表示させる画像</strong>を設定できます</span></span></strong></span></strong></span></p><p style=\"color: #000000; font-size: 10.0px;\"><span style=\"color: #333333; font-size: small;\"><span style=\"color: #333333;\"><span style=\"color: #000000;\"><span style=\"color: #99cc00;\">&nbsp;&nbsp; &nbsp; ●&nbsp;</span><span style=\"color: #99cc00;\"><span style=\"color: #333333;\">住所を入力すると、Google Mapを自動表示</span></span></span></span></span></p><p style=\"color: #000000; font-size: 10.0px;\"><span style=\"font-size: small; color: #333333;\"><span style=\"color: #99cc00;\">&nbsp;&nbsp; &nbsp; ●&nbsp;</span><span style=\"color: #333333;\">説明文の<span style=\"color: #ff9900;\"><strong>色</strong></span>をつけたり<em>フォント</em>を変えたりできます</span></span></p><p style=\"color: #000000; font-size: 10.0px;\"><span style=\"color: #333333; font-size: x-small;\">&nbsp;</span></p><p style=\"color: #000000; font-size: 10.0px;\"><span style=\"color: #333333; font-size: small;\"><strong><span style=\"text-decoration: underline;\"><span style=\"color: #333333;\"><strong><span style=\"text-decoration: underline;\">一般公開 or 非公開 を選べる</span></strong></span></span></strong></span></p><p style=\"color: #000000;\"><span style=\"color: #333333; font-size: small;\"><span style=\"color: #333333;\"><span style=\"color: #99cc00;\">&nbsp;&nbsp; &nbsp; ●&nbsp;</span>非公開にしたら、公開したい相手だけに、イベントページのURLとパスワードを教えましょう</span></span></p><p style=\"color: #000000;\"><span style=\"color: #333333; font-size: small;\"><strong><span style=\"text-decoration: underline;\">&nbsp;</span></strong></span></p><p style=\"color: #000000;\"><span style=\"color: #333333; font-size: small;\"><strong><span style=\"text-decoration: underline;\"><span style=\"text-decoration: underline;\"><strong><span style=\"color: #333333;\">参加者管理がカンタンに</span></strong></span></span></strong></span></p><div style=\"color: #000000; font-size: 10.0px;\"><p style=\"color: #000000;\"><span style=\"color: #ff6600;\"><span style=\"color: #000000;\"><span style=\"color: #99cc00;\">&nbsp;&nbsp; &nbsp; ●&nbsp;</span><span style=\"color: #000000;\"><span style=\"color: #333333;\"><span style=\"color: #3366ff;\">Twitter</span>経由で、<strong>参加者全員にメッセージを送信できます</strong></span></span></span></span></p><p style=\"color: #000000;\"><span style=\"color: #ff6600;\"><span style=\"color: #000000;\"><span style=\"color: #000000;\"><span style=\"color: #333333;\"><strong>&nbsp;</strong></span></span></span></span><span style=\"color: #99cc00;\">&nbsp;&nbsp; &nbsp;●&nbsp;<span style=\"color: #000000;\">イベントの前日などに、参加者に対して</span></span><strong>リマインドメッセージ</strong>を自動送信</p><p style=\"color: #000000;\"><span style=\"color: #99cc00;\">&nbsp;&nbsp; &nbsp; ●&nbsp;</span><span style=\"color: #99cc00;\"><span style=\"color: #333333;\"><strong>管理者を複数人設定</strong>できます</span></span></p><p style=\"color: #000000;\"><span style=\"color: #99cc00;\">&nbsp;&nbsp; &nbsp; ● </span><span style=\"color: #99cc00;\"><span style=\"color: #333333;\">参加者リストの印刷用ページもあるよ</span></span></p></div><div style=\"color: #000000; font-size: 10.0px;\"><p style=\"color: #000000;\">&nbsp;</p></div><div style=\"color: #000000; font-size: 10.0px;\"><p style=\"color: #000000;\"><span style=\"color: #333333; font-size: small;\"><strong><span style=\"text-decoration: underline;\">予定をカレンダーに自動登録可能</span></strong></span></p><p style=\"color: #000000;\"><span style=\"font-size: small;\"><span style=\"color: #99cc00;\"><strong><span style=\"font-weight: normal;\">&nbsp;&nbsp; &nbsp; ●&nbsp;</span></strong></span><span style=\"color: #333333;\"><strong><span style=\"color: #333333;\"><strong><span style=\"color: #000000; font-weight: normal;\"><span style=\"color: #000000;\">Googleなどのカレンダーに、管理する/参加するイベントのスケジュールを自動登録</span></span></strong></span></strong></span></span></p><p style=\"color: #000000;\"><span style=\"font-size: small;\"><strong><span style=\"color: #333333;\"><strong><span style=\"color: #000000; font-weight: normal;\"><span style=\"color: #000000;\">&nbsp;&nbsp; &nbsp; &nbsp;( 「設定」ページでこの設定をおこなってね</span></span></strong></span></strong></span><span style=\"font-size: small;\"><strong><span style=\"color: #333333;\"><strong><span style=\"color: #000000; font-weight: normal;\"><span style=\"color: #000000;\">)</span></span></strong></span></strong></span></p><p style=\"color: #000000;\"><span style=\"font-size: x-small;\">&nbsp;</span></p><p style=\"color: #000000;\">&nbsp;</p><p style=\"color: #000000;\"><span style=\"text-decoration: underline;\"><span style=\"color: #333333; font-size: small;\"><strong>PARTAKE と ATND の比較表</strong></span></span></p><p style=\"color: #000000;\"><span style=\"font-size: small;\"><span style=\"color: #99cc00;\"><strong><span style=\"font-weight: normal;\">&nbsp;&nbsp; &nbsp; ● <span style=\"color: #000000;\">大雑把に PARTAKE と ATND の比較をしてみました</span></span></strong></span></span></p><table style=\"border: 1.0px solid #b7b7b7; padding: 2.0px 14.0px; color: #000000; font-weight: normal; font-size: 11.0px; margin-left: auto; margin-right: auto; border-collapse: collapse; border-spacing: 0.0pt; line-height: 20.0px;\" border=\"1\"><thead><tr><th style=\"padding: 2.0px 14.0px; text-align: center; background-color: #a4d6e3;\">&nbsp;</th><th style=\"padding: 2.0px 14.0px; text-align: center; background-color: #a4d6e3;\">PARTAKE</th><th style=\"padding: 2.0px 14.0px; text-align: center; background-color: #a4d6e3;\">ATND</th></tr></thead><tbody><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">イベントの作成・管理</td><td style=\"text-align: center;\"><strong><span style=\"color: #ff6600;\">✓</span></strong></td><td style=\"text-align: center;\"><strong><span style=\"color: #ff6600;\">✓</span></strong></td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">複数管理者の設定</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\">&times;</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">参加者リストの CSV 取得</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\"><strong><span style=\"color: #ff6600;\">✓</span></strong></td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">イベントの検索</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\">API で提供</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">関連イベントの設定</td><td style=\"text-align: center;\"><strong><span style=\"color: #ff6600;\">✓</span></strong></td><td style=\"text-align: center;\">&times;</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">仮参加機能</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\">&times;</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">締切り機能</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\">&times;</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">twitter 経由のリマンダー<br />(１日前、繰り上がり時など)&nbsp;</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\">&times;</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">参加者へのメッセージ送信</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\"><p><span style=\"color: #ff6600;\"><strong><span style=\"font-weight: normal;\"><strong>✓</strong></span></strong></span></p><p><span style=\"color: #000000;\"><span style=\"color: #ff6600;\"><strong><span style=\"font-weight: normal;\"><strong><span style=\"color: #000000; font-weight: normal;\">(2011-03-02)</span></strong></span></strong></span></span></p></td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">公開・非公開設定</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\">&times;</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">カレンダーへの自動登録</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\">非公式</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">説明文の GUI エディタ</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\">&times;</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">背景画像の設定</td><td style=\"text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\">&times;</td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">Open ID によるログイン</td><td style=\"text-align: center;\"><p><span style=\"color: #ff6600;\"><strong>✓</strong></span><br />(twitter との結びつけが必要)</p></td><td style=\"text-align: center;\"><strong><span style=\"color: #ff6600;\">✓</span></strong></td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">RSS</td><td style=\"padding: 2.0px 14.0px; text-align: center;\"><span style=\"color: #ff6600;\"><strong>✓</strong></span></td><td style=\"text-align: center;\"><strong><span style=\"color: #ff6600;\">✓</span></strong></td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">API</td><td style=\"padding: 2.0px 14.0px; text-align: center;\"><strong style=\"color: #ff6600;\">✓</strong></td><td style=\"text-align: center;\"><strong><span style=\"color: #ff6600;\">✓</span></strong></td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">アンケート機能</td><td style=\"padding: 2.0px 14.0px; text-align: center;\"><strong style=\"color: #ff6600;\">✓</strong></td><td style=\"text-align: center;\"><strong><span style=\"color: #ff6600;\">✓</span></strong></td></tr><tr><td style=\"padding: 2.0px 14.0px; text-align: left;\">日時調整機能</td><td style=\"padding: 2.0px 14.0px; text-align: center;\">将来的に追加予定</td><td style=\"text-align: center;\">調整さん</td></tr></tbody></table></div><div style=\"color: #000000; font-size: 10.0px;\"><span style=\"color: #99cc00; font-size: x-small;\">&nbsp;</span></div></div>";
}
