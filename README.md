# NoticEditor

A hierarchical note editor with markdown support.

![](examples/screenshot_1.png) ![](examples/screenshot_2.png)

## Features

 - Hierarchical structure of notes
 - Markdown support
 - Syntax highlighting
 - Light and Dark themes
 - Attachments
 - Json / Zip / Password protected Zip formats
 - Export to HTML
 - Import notes/attachments from Web
 - Attachments importer plugin support


## Build

### Gradle-based (Cross-platform)

1. `git clone https://github.com/NoticEditorTeam/NoticEditor.git`
2. `gradlew jar` — this will put jar into `./build/libs/`.
3. `gradlew run` — this will run the app w/o building a jar.
4. To build platform specific jar, set `os.name` system property: `gradlew jar -Dos.name=linux`. Available options: 'windows', 'linux', 'osx'

### Makefile (Unix)

``` bash
$ git clone https://github.com/NoticEditorTeam/NoticEditor.git
$ make
```

## License

Apache 2.0 - see [Apache 2.0 license information](LICENSE)