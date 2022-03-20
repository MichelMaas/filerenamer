#!/bin/bash

DESKTOP_FILE=$HOME/.local/applications/filerenamer.desktop
function setFolder() {
#  pushd $PWD/..
  folder=${project.basedir}/target
#  popd
}

function createShortcut() {
  if [ ! -f "$DESKTOP_FILE" ]; then
    echo "[Desktop Entry]
        Version=${project.version}
        Type=Application
        Name=Filerenamer
        Comment=The Drive to Develop
        Categories=Tools;Files;
        Terminal=false
        Icon=$folder/bin/icon.png
        Exec=java -Xms256m -Xmx512m -server -jar $folder/filerenamer-frontend-${project.version}-exec.war" >$HOME/.local/share/applications/filerenamer.desktop
  fi
}

setFolder
createShortcut
