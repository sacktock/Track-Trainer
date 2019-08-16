Track Trainer
=============

Project by Alex Goodall for A-level computer science (AQA).

Overview
--------

This project is an android fitness app - specifically for running and track athletes.

Prelims
-------

Android studio is required to run this project and view it in a suitable environment.

Running
-------

To run the project, 'git clone' this repository to a suitable directory. From android studio 'file -> open project' and then navigate to this directory and open with 'Track Trainer.iml'.

Installing
----------

The only way to install this project onto a device at the moment is by running it from android studio onto the desired device, using a USB connection.

Configuration
-------------

This project requires an SQLite database and won't work properly if the project can't find a '.db' file. The application will automatically create an empty database when it is run for the first time. If you want to initalise the database with example data; go into the 'StaticDataBaseAccessor' class 'app -> src -> main -> java -> alexw.classes' and change the 'DB_PATH' value so it matches the location of the 'athlete_database.db' file within the 'athlete_database' sub-directory - make sure this is done before the appliaction is run on a device for the first time or you have to rename the database.

Code
----

The main bulk of code can be found in 'app -> src -> main -> java'.

Testing
-------

This is an old project and unfortunately does not use/support eslint currently.

Further Documentaion
--------------------

This original files that were submitted for this project can be found in the 'Documentaion - Copy' sub-directory. These files include the analysis of the problem this project was intended to solve, the design stage, testing and evaluation of the project. The implemenation file is omitted because it is large and unecessary.

License
-------

This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.

