JC=javac
JCOPTS=-sourcepath $(SRC) -d $(BUILD) -g $(SRC)$(MAIN_CLASS)
PACKCMD=jar cfm $(DIST)$(OUTPUT) $(MANIFEST) -C $(BUILD) .
RM=rm -rf

BUILD=./build/
DIST=./dist/
SRC=./src/
XMLFOLDER=./src/com/temporaryteam/noticeditor/view/
XMLRESULTFOLDER=./build/com/temporaryteam/noticeditor/view/

MANIFEST=$(DIST)/Manifest
OUTPUT=/NoticEditor.jar
MAIN_CLASS=com/temporaryteam/noticeditor/Main.java

all: init clean compile pack

init:
	mkdir -p $(BUILD)
	mkdir -p $(DIST)

clean:
	$(RM) $(BUILD)*
	$(RM) $(DIST)$(OUTPUT)

compile:
	$(JC) $(JCOPTS)

pack:
	cp -rf $(XMLFOLDER)* $(XMLRESULTFOLDER)
	$(PACKCMD)
