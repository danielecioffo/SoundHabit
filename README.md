# SoundHabit
Repository for Data Mining and Machine Learning project.

## The application
*SoundHabit* is an application whose main purpose is to help users discover new songs that they will most likely enjoy.

The application can automatically extract music information and classify the songs into their musical genres. Moreover, the application generates for each user a customized list of recommended songs, based on their tastes and especially on the genres they appear to like best.

The only thing users need to do is to select, from the list of featured songs, the ones they like the most and let the application do all the work. As a result, users can find out more about their personal tastes and hopefully answer the recurring question: *"What kind of music are you into?"*.

The complete documentation can be found [here](https://github.com/danielecioffo/SoundHabit/blob/main/SoundHabit%20documentation.pdf), along with the presentation of the project [here](https://github.com/danielecioffo/SoundHabit/blob/main/SoundHabit%20Presentation.pdf).

## Repository Organization
The project repository is organized as follows:
* *FeatureExtractor*: module that contains the Python server that extracts audio features from songs
* *SoundHabit*: module that contains the actual Java application
* *Dump Neo4j*: directory that contains a dump of the database
* *Test Set*: directory that contains some songs to test the genre classification

## Execute
To start the client side of the application, please load the SoundHabit module into IntelliJ (or others IDE) and run the main function of the it.unipi.dii.inginf.dmml.soundhabit.app class. It is an academic project, an executable file has not been prepared.
For using this application, it is strongly recommended to load the Neo4j Dump on Neo4j. This because we have prepared some users and some songs; if you don't load the dump, of course you will see nothing, and you have to manually create one administrator user, inserting manually the label :Administrator on some user. This because we have only one administrator, and so there is no possibility inside the application to elect one user as Administrator.
There is an administrator in the dump, with username "oliver.smith" and password "oliver.smith". (for simplicity in the pre-set users the username is always the same as the password).
Logging in with Oliver will open the administration page, where you can insert new songs, also using the music classifier. Instead, the other users are standard users (jessica.evans and jack.jones) and they can like songs, see personalized suggestions, etc.
Note: To use the music classifier you need to activate the server, otherwise a "Service not available" message will be shown. To be able to run the server, you must have all the requirements, see [here](https://github.com/danielecioffo/SoundHabit/blob/main/FeatureExtractor/Requirements.txt).
In general, it is possible to modify all the parameters concerning access to the database and the server through the configuration file, which is called config.xml.
