package com.enlivenhq.teamcity;

import com.enlivenhq.slack.SlackWrapper;
import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.serverSide.problems.BuildProblemInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.lang.reflect.*;

public class SlackNotificator implements Notificator {

    private static final Logger log = Logger.getLogger(SlackNotificator.class);

    private static final String type = "SlackNotificator";

    private static final String slackChannelKey = "slack.Channel";
    private static final String slackUsernameKey = "slack.Username";
    private static final String slackUrlKey = "slack.Url";

    private static final PropertyKey slackChannel = new NotificatorPropertyKey(type, slackChannelKey);
    private static final PropertyKey slackUsername = new NotificatorPropertyKey(type, slackUsernameKey);
    private static final PropertyKey slackUrl = new NotificatorPropertyKey(type, slackUrlKey);

    public SlackNotificator(NotificatorRegistry notificatorRegistry) {
        registerNotificatorAndUserProperties(notificatorRegistry);
    }

    @NotNull
    public String getNotificatorType() {
        return type;
    }

    @NotNull
    public String getDisplayName() {
        return "Slack Notifier";
    }

    public void notifyBuildFailed(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
         sendNotification(sRunningBuild, "danger", users);
    }

    public void notifyBuildFailedToStart(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
        sendNotification(sRunningBuild, "danger", users);
    }

    public void notifyBuildSuccessful(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
        sendNotification(sRunningBuild, "good", users);
    }

    public void notifyLabelingFailed(@NotNull Build build, @NotNull VcsRoot vcsRoot, @NotNull Throwable throwable, @NotNull Set<SUser> users) {
        sendBuildNotification(build.getFullName(), build.getBuildNumber(), "labeling failed", "danger", "", users);
    }

    public void notifyBuildFailing(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
        sendNotification(sRunningBuild, "danger", users);
    }

    public void notifyBuildProbablyHanging(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
        sendNotification(sRunningBuild, "warning", users);
    }

    public void notifyBuildStarted(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> users) {
        sendNotification(sRunningBuild, "warning", users);
    }

    public void notifyResponsibleChanged(@NotNull SBuildType sBuildType, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleAssigned(@NotNull SBuildType sBuildType, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleChanged(@Nullable TestNameResponsibilityEntry testNameResponsibilityEntry, @NotNull TestNameResponsibilityEntry testNameResponsibilityEntry2, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleAssigned(@Nullable TestNameResponsibilityEntry testNameResponsibilityEntry, @NotNull TestNameResponsibilityEntry testNameResponsibilityEntry2, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleChanged(@NotNull Collection<TestName> testNames, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyResponsibleAssigned(@NotNull Collection<TestName> testNames, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyBuildProblemResponsibleAssigned(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyBuildProblemResponsibleChanged(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> sUsers) {

    }

    public void notifyTestsMuted(@NotNull Collection<STest> sTests, @NotNull MuteInfo muteInfo, @NotNull Set<SUser> sUsers) {

    }

    public void notifyTestsUnmuted(@NotNull Collection<STest> sTests, @NotNull MuteInfo muteInfo, @Nullable SUser sUser, @NotNull Set<SUser> sUsers) {

    }

    public void notifyBuildProblemsMuted(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull MuteInfo muteInfo, @NotNull Set<SUser> sUsers) {

    }

    public void notifyBuildProblemsUnmuted(@NotNull Collection<BuildProblemInfo> buildProblemInfos, @NotNull MuteInfo muteInfo, @Nullable SUser sUser, @NotNull Set<SUser> sUsers) {

    }

    private void registerNotificatorAndUserProperties(NotificatorRegistry notificatorRegistry) {
        ArrayList<UserPropertyInfo> userPropertyInfos = getUserPropertyInfosList();
        notificatorRegistry.register(this, userPropertyInfos);
    }

    private ArrayList<UserPropertyInfo> getUserPropertyInfosList() {
        ArrayList<UserPropertyInfo> userPropertyInfos = new ArrayList<UserPropertyInfo>();

        userPropertyInfos.add(new UserPropertyInfo(slackChannelKey, "Slack Channel"));
        userPropertyInfos.add(new UserPropertyInfo(slackUsernameKey, "Slack Username"));
        userPropertyInfos.add(new UserPropertyInfo(slackUrlKey, "Slack Instance URL"));

        return userPropertyInfos;
    }

    private void sendNotification(@NotNull SRunningBuild sRunningBuild, String statusColor, Set<SUser> users) {
        for (SUser user : users) {
            SlackWrapper slackWrapper = getSlackWrapperWithUser(user);
            try {
                String project = sRunningBuild.getFullName();
                String buildNumber = sRunningBuild.getBuildNumber();
                String status = sRunningBuild.getStatusDescriptor().getStatus().getText();
                String message = sRunningBuild.getStatusDescriptor().getText();
                //jetbrains.buildServer.users.UserSet<SUser> committers = sRunningBuild.getCommitters(jetbrains.buildServer.vcs.SelectPrevBuildPolicy.SINCE_LAST_BUILD);
                String committers = convertUserSetToCsv(sRunningBuild.getCommitters(SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD).getUsers());
                
                slackWrapper.send(project, buildNumber, status, statusColor, message, committers);
            }
            catch (IllegalAccessException e) {
                log.error(e);
            }
            catch (InvocationTargetException e) {
                log.error(e);
            }
            catch (NoSuchMethodException e) {
                log.error(e);
            }
            catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
    
    private void sendBuildNotification(String project, String build, String statusText, String statusColor, String message, Set<SUser> users) {
        for (SUser user : users) {
            SlackWrapper slackWrapper = getSlackWrapperWithUser(user);
            try {
                slackWrapper.send(project, build, statusText, statusColor, message, "");
            }
            catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    private SlackWrapper getSlackWrapperWithUser(SUser user) {
        String channel = user.getPropertyValue(slackChannel);
        String username = user.getPropertyValue(slackUsername);
        String url = user.getPropertyValue(slackUrl);

        if (slackConfigurationIsInvalid(channel, username, url)) {
            log.error("Could not send Slack notification. The Slack channel, username, or URL was null. " +
                      "Double check your Notification settings");

            return new SlackWrapper();
        }

        return constructSlackWrapper(channel, username, url);
    }

    private boolean slackConfigurationIsInvalid(String channel, String username, String url) {
        return channel == null || username == null || url == null;
    }

    private SlackWrapper constructSlackWrapper(String channel, String username, String url) {
        SlackWrapper slackWrapper = new SlackWrapper();

        slackWrapper.setChannel(channel);
        slackWrapper.setUsername(username);
        slackWrapper.setSlackUrl(url);

        return slackWrapper;
    }
    
    public static String convertUserSetToCsv(Set<SUser> set)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return convertSetToCsv(set, SUser.class.getMethod("getUsername"), ",");
	}

	public static String convertSetToCsv(Set<?> set, Method method,
			String delimiter) throws IllegalAccessException,
			InvocationTargetException {
		StringBuffer builder = new StringBuffer();
		Iterator<?> iter = set.iterator();
		while (iter.hasNext()) {
			Object e = iter.next();
			if (method != null) {
				builder.append(method.invoke(e));
			} else {
				builder.append(e);
			}
			if (!iter.hasNext()) {
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}
}
