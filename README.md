# SoundHabit
Repository for Data Mining and Machine Learning project.

## The application
*SoundHabit* is an application whose main purpose is to help users discover new songs that they will most likely enjoy.

The application can automatically extract music information and classify the songs into their musical genres. Moreover, the application generates for each user a customized list of recommended songs, based on their tastes and especially on the genres they appear to like best.

The only thing users need to do is to select, from the list of featured songs, the ones they like the most and let the application do all the work. As a result, users can find out more about their personal tastes and hopefully answer the recurring question: *"What kind of music are you into?"*.

## Repository Organization
The project repository is organized as follows:
* *FeatureExtractor*: module that contains the Python server that extracts audio features from songs
* *SoundHabit*: module that contains the actual application
* *Dump Neo4j*: directory that contains a dump of the database
* *Test Set*: directory that contains some songs to test the genre classification
