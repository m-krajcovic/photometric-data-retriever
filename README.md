# A tool for retrieving photometric data of stellar objects

Goal of this thesis is to create a tool for the Department of Theoretical Physics and Astrophysics of the Faculty of Science, MU, which would help with obtaining photometric data, i.e. entries of measured brightness, from online databases.

Astronomers currently use a VSX database (https://www.aavso.org/vsx/) to search for a star manually. Numerous links lead from there to circa twenty different databases that provide data in a form of charts which include time, measured brightness and other statistics. However, each of these databases has a slightly different chart format, therefore, the data has to be edited by using a method specific for every database.

This tool finds the specified object in every available database and transfers the data into a singular text format which takes a form of columns each divided from one another; the first column shows the time (HJD – Heliocentric Julian Day), the second one shows the stellar magnitude, and the other columns provide supplementary info. If chosen, this unified data is recorded into the system for managing an observation journal i.e. MECA.

It should be easy to add other databases, thus, part of the assignment is to create a system of plugins allowing access to individual databases and programmable data editing. It should be possible to implement a plugin for another database in any programming language, especially in Python that is known by astronomers and is easy to use for text data processing.

Throughout the development you ought to use iterative approach during which you will consult your work with your supervisor. Supervisor’s overall satisfaction with a finished application will be an important criteria of evaluation.

# Installation

> Go to release tab for installation package

1. Install package
2. Default settings and plugins will be loaded on first startup
