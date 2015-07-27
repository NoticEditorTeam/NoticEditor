#!/bin/sh

classpackages="com/temporaryteam/noticeditor"
sourcepackages="../src/com/temporaryteam/noticeditor"
jc=javac

$jc $sourcepackages/*.java && mv $sourcepackages/*.class $classpackages && jar -cfm NoticEditor.jar Manifest com
