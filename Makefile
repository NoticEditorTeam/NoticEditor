JC=javac
JCOPTS=-classpath `./configure` -sourcepath $(SRC) -d $(BUILD) -g $(SRC)/$(MAIN_CLASS)
PACKCMD=jar cfm $(DIST)/$(OUTPUT) $(MANIFEST) -C $(BUILD) .
RM=rm -rf

BUILD=./build
LIBS=./libs
DIST=./dist
SRC=./src
EXAMPLES=./examples

XMLIN=$(SRC)/fxml
XMLOUT=$(BUILD)/fxml
RESIN=$(SRC)/resources
RESOUT=$(BUILD)/resources

MANIFEST=manifest.mf
OUTPUT=NoticEditor.jar
MAIN_CLASS=com/temporaryteam/noticeditor/Main.java

all: init clean compile pack

init:
	mkdir -p $(BUILD)
	mkdir -p $(DIST)

clean:
	$(RM) $(BUILD)/*
	$(RM) $(DIST)/$(OUTPUT)

compile:
	$(JC) $(JCOPTS)

pack:
	mkdir -p $(BUILD)/{fxml,resources}
	cp -rf $(XMLIN)/* $(XMLOUT)
	cp -rf $(RESIN)/* $(RESOUT)
	for j in $(LIBS)/*.jar ; do \
		unzip $$j -x META-INF/* -d $(BUILD) > /dev/null ; \
	done
	$(PACKCMD)

install:
	mkdir -p $(DESTDIR)$(PREFIX)/bin
	mkdir -p $(DESTDIR)$(PREFIX)/share/NoticEditor/examples
	install -m 0755 $(DIST)/$(OUTPUT) $(DESTDIR)$(PREFIX)/bin/$(OUTPUT)
	cp -rf $(EXAMPLES) $(DESTDIR)$(PREFIX)/share/NoticEditor/examples
	chmod -R 0755 $(DESTDIR)$(PREFIX)/share/NoticEditor/examples
