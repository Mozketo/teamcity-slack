TeamCity Slack Notifier
=======================

A configurable TeamCity plugin that notifies your [Slack](https://slack.com) channel.

Because it is a [TeamCity Custom Notifier](http://confluence.jetbrains.com/display/TCD8/Custom+Notifier) plugin, it extends the existing user interface and allows for easy configuration directly within your TeamCity server. Once installed, you can configure the plugin for multiple TeamCity projects and multiple build conditions (i.e. Build failures, successes, hangs, etc.)

![Notification example](/notification-example.png)

## Installation

1. Download the [latest plugin zip package](/releases/download/v1.0/teamcity-slack-integration.zip), or a specific GitHub [Release](/releases).
2. Follow the TeamCity [plugin installation directions](http://confluence.jetbrains.com/display/TCD8/Installing+Additional+Plugins).

## Configuration

1. Create an [incoming webook](https://my.slack.com/services/new/incoming-webhook) in Slack and configure the default Slack channel and username.
2. Copy the URL for the webhook.
3. As an admin, navigate to your TeamCity profile page ("My Settings & Tools") and click "Edit".
4. Enter the channel name, username, and full webhook URL in the Notification settings as seen below.
5. Add notification rules as appropriate.

## Configuration Example

![Configuration Settings](/configuration%20example.png)

## Compatibility

* Tested exclusively with TeamCity version 8.1.1
* June 2015 tested with TeamCity version 9.0.5.

## How to Build and Package

1. Install Java [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. Install Maven follow installation instructions [here](https://maven.apache.org/download.cgi).
3. Build the project with the command `mvn package`.
4. Wait.
5. Open the target/ folder > `teamcity-slack-integration.zip` contains the built and packaged app.

## Contributors

[Jesse Dunlap](https://twitter.com/jessedunlap)

[Andrew Clark](https://twitter.com/andrew_jclark)

[Ian Robinson](https://twitter.com/irobinson)

## License
MIT