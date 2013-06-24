Android Cloud Photos
====================

# IN DEVELOPMENT: #

## What this is ##

This is An Android application to sync photos to a variety of online cloud services.

## What's working? ## 

Currently, Rackspace Cloud Files (UK LON Accounts) work. You can log in, select a container (or create a container) and your photos will automatically be uploaded.

## What's broken? ##

Plenty.

A simple queuing service still needs to be implemented, more than likely in SQLite. This needs to store the file paths and run uploads one at a time, until the queue is cleared.

The queue needs to be controlled by different network states. So a user can choose to upload their photos on Mobile data or WiFi only.


### Honorable Mentions ###

First, I'm not a designer. So I'd like to thank emey87 over at DeviantArt for the icons. They are CC non-commercial. You can see the designers work here: http://emey87.deviantart.com/


