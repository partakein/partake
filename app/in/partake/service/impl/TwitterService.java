package in.partake.service.impl;

import in.partake.model.dto.UserTwitterLink;
import in.partake.resource.PartakeProperties;
import in.partake.service.ITwitterService;
import in.partake.session.TwitterLoginInformation;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterService implements ITwitterService {
    public TwitterLoginInformation createLoginInformation(String redirectURL) throws TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyForTwitter";
        RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL);

        return new TwitterLoginInformation(twitter, requestToken, redirectURL);
    }

    public UserTwitterLink createTwitterLinkageFromLoginInformation(TwitterLoginInformation information, String verifier) throws TwitterException {
        Twitter twitter = information.getTwitter();
        RequestToken requestToken = information.getRequestToken();
        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

        twitter4j.User twitterUser = twitter.showUser(twitter.getId());
        return new UserTwitterLink(
                null, twitter.getId(), null, twitter.getScreenName(), twitterUser.getName(), accessToken.getToken(), accessToken.getTokenSecret(),
                twitter.showUser(twitter.getId()).getProfileImageURL().toString());
    }

    @Override
    public void sendDirectMesage(String token, String tokenSecret, long twitterId, String message) throws TwitterException {
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        Twitter twitter = new TwitterFactory().getInstance(accessToken);
        twitter.sendDirectMessage(twitterId, message);
    }

    @Override
    public void updateStatus(String token, String tokenSecret, String message) throws TwitterException {
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        Twitter twitter = new TwitterFactory().getInstance(accessToken);
        twitter.updateStatus(message);
    }
}
