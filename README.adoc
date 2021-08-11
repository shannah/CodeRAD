= CodeRAD

Rapid Application Development toolkit and templates for Java developers using https://www.codenameone.com/[Codename One]. It focuses on higher reusability, rich UI components and MVC (Model-View-Controller) principles.

image::https://www.codenameone.com/wp-content/uploads/2021/08/CodeRAD-GitHub.jpg[width=1500]

Watch the https://youtu.be/x7qaWBTjwMI[CodeRAD 2 Introduction Video] for a short introduction to features and concepts.

== Features

. *Reusable Components* - Facilitates the creation of View components that can be easily reused across projects without requiring any dependencies.
. *Rich UI Components* - Components built with CodeRAD are richer than the standard Codename One UI components.  They are designed to be useful right out of the box, while still being customizable.
. *Clean Separation of Code* - Provides model, view, and https://github.com/shannah/CodeRAD/wiki/Controllers[controller classes] which facilitate the clean separation of code following MVC (Model-View-Controller) principles.
. *Loose Coupling* - Views can be "loosely" coupled to their model, allowing them to be used with many different model classes.  https://shannah.github.io/CodeRAD/manual/#entities-properties-schemas-tags[Learn more]
. *Declarative UI Syntax* - Build your user interfaces declaratively using a light and expressive XML-based syntax.



== Documentation

. https://shannah.github.io/CodeRAD/manual/[Code RAD Developers Guide]
.. https://shannah.github.io/CodeRAD/manual/#getting-started[Getting Started Tutorial] and https://youtu.be/QdyO4tpYOHs[companion screencast].
.. https://shannah.github.io/CodeRAD/manual/#_app_example_1_a_twitter_clone[Building a Twitter Clone (tutorial)]

. https://github.com/shannah/CodeRAD/wiki[CodeRAD Wiki] - Best source for reference documentation on CodeRAD components.

. https://shannah.github.io/CodeRAD/javadoc[Java Docs]


=== Code RAD 1.x Tutorials

_The following tutorials were written for CodeRAD 1.0 and are in the process of being updated to CodeRAD 2.  There have been substantial changes in the way you build apps using CodeRAD 2, so bear that in mind when going through them_

. *https://shannah.github.io/RADChatApp/getting-started-tutorial.html[How to Build a Messenging App in Codename One]* - A good startng place for getting a feel for what it is like to develop an app using CodeRAD.

== Videos

. https://youtu.be/x7qaWBTjwMI[Introduction to CodeRAD 2]
. https://youtu.be/QdyO4tpYOHs[Getting Started Tutorial companion screencast].
.. https://youtu.be/QdyO4tpYOHs[Intro]
.. https://youtu.be/QdyO4tpYOHs?t=191[Under the Hood]
.. https://youtu.be/QdyO4tpYOHs?t=471[Hot Reload]
.. https://youtu.be/QdyO4tpYOHs?t=598[Changing the Styles]
.. https://youtu.be/QdyO4tpYOHs?t=1118[Adding more Components]
.. https://youtu.be/QdyO4tpYOHs?t=1586[Adding Actions]
.. https://youtu.be/QdyO4tpYOHs?t=1983[Creating Menus]
.. https://youtu.be/QdyO4tpYOHs?t=2392[Form Navigation]
.. https://youtu.be/QdyO4tpYOHs?t=2650[Models]
.. https://youtu.be/QdyO4tpYOHs?t=3568[Fun with Bindings]
.. https://youtu.be/QdyO4tpYOHs?t=3897[Transitions]
.. https://youtu.be/QdyO4tpYOHs?t=4324[Entity Lists]
.. https://youtu.be/QdyO4tpYOHs?t=5391[Intra-form Navigation]
.. https://youtu.be/QdyO4tpYOHs?t=5976[Custom View Controllers]
.. https://youtu.be/QdyO4tpYOHs?t=6290[Views within Views]

== Sample Projects

. https://github.com/shannah/tweetapp[Tweetapp] - A Twitter mobile app clone.  (For demonstration only - is not a complete clone).
. https://github.com/shannah/RADChatApp/tree/master/cn1chat-demo[RADChat Demo] - RADChatApp is a full-featured chat room app UI component.  UI only.  For demonstration purposes. _Developed using CodeRAD 1.0.  Currently being updated to use 2.0_.
. https://github.com/sergeyCodenameOne/UberEatsClone[Grub] - UberEats Clone.  _Developed using CodeRAD 1.0_
. https://github.com/shannah/coderad2-samples[CodeRAD2 Samples] - A collection of samples using CodeRAD components.  This serves as a living testbed for CodeRAD components.

== UI Kits (Libraries)

. https://github.com/shannah/TweetAppUIKit/[Tweet App UI Kit] - A CodeRAD 2 library with several Twitter-like UI components.  Developed for CodeRAD 2.0.
. https://github.com/shannah/RADChatApp[RADChat App] - A library including full-featured components for building a Chat application.  (Currently for CodeRAD 1.0, and being adapted to CodeRAD 2).

== Building from Source

The following instructions are for Mac and Linux.  May work on windows using Git Bash, but not
sure.

1. Open your terminal
2. Make sure that JAVA_HOME is set to a valid JDK8
3. Make sure `mvn` is in your PATH

[source,bash]
----
git clone https://github.com/shannah/CodeRAD
mvn install
----

== Support

CodeRAD is developed and supported by https://www.codenameone.com[Codename One].

== Credits

Developed by Steve Hannah, Codename One.
