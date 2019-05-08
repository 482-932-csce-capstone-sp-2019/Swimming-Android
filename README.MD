# SwimmingWearable Android Application Readme file

## More in depth documentation available on the wiki

The application was designed to be carefully partitioned, each class handles a specific aspect of the app, generally a single page and its functionality.
Functions were named to be descriptive and have comments explaining their functionality, and hopefully you will find behavior obvious.

# Basic description:
Two utility classes exist to be used as structs by multiple classes.
Two Tensorflow related class were created but not implemented into the rest of the app.
Layout files for all pages (and one alternative for the workout input) are included and carefully constrained.'


# Building:
Running the gradle script should build the app without issue, no dependencies are required that were not easily found by the app.

### The two libraries you must ensure you have for functionality are:
JSCH and SQLite

# Contribution:
A large ammount of the code for the actual functionality is in some strange practices, if alternatives to SFTP can be found I recommend you use them
We'd appreciate credit in the future for the code we've written, but please feel free to go on to modify or delete whatever code is necessary.

# API:
As this is an application for a phone, no true API exists, each class/activity is isolated and functions mostly independently of the functionality of oter classes. For this reason attempting to call these functions outside of the context they were created in is not recommended.

# Notable Issues to continued development:
To overcome the dynamic nature of IP addresses used by raspberry pis on the campus wifi a strange workaround was used which can be viewed in the "ipfetchertask" subclass of both WorkoutCreationActivity.Java and SwimActivity.Java which connects to a server one of our members had for personal use and parses a text file containing an up to date IP address for each of the raspberry pis we used as a team. We highly recommend a different method be used in the future, such as bluetooth connectivity (will lose contact in water, will be difficult to reestablish) or some other method.
