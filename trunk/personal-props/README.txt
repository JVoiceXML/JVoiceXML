This directory provides a developer with the possibility to override any 
default properties defined by files in config-props. Any file that is to be
substituted must be copied from config-props (preserving the directory
structure) to this directory and can then be modified.

The CLASSPATH settings in the build.xml files asure that the files
found in this directory have precedence over those in config-props.

Files in this directory (except this file and .cvsignore) may never
be checked in CVS.
