# JewishDay
Android app to display Jewish Daily information

A friend of mine told me that the reason people do not know what the jewish day is because it is not in front of their eyes all the time.

There are multiple applications out there that will give you the current jewish day, but you need to open the app. So what I decided to do is write an application that has the current day in the status bar on top. This way just be looking at your phone you can know the jewish day.

Of course once I started the app I have added more features. The major features are:

Daily time in notification bar
Detailed daily times including shabat, daf yomi and others.
Daily time be default is by current location (uses GPS), but you have the option to add different locations around the globe, and to choose them for times.
Reminders for davening during the day.
Reminider for daily time before they pass.
Option to find mizrach using the accelerometer
I enjoyed writing the app. For the GUI to give the card view I used CardLib. For the astronomical calculations and jewish calculations I used KosherJava.

Many people have asked for code examples to use in their own app, so I have decided to opensource my app.

The application can be found in the google store: https://play.google.com/store/apps/details?id=com.turel.jewishday
I have updated it to support the new shortcut API, so you can use the mizrach feature easily.

Please see:

https://chaimturkel.wordpress.com/2016/10/21/jewishapp-open-source/
