#!/bin/bash

DESKTOP_FILE=$HOME/.local/applications/filerenamer.desktop
folder=$PWD
STARTER_FILE=$PWD/run_filerenamer.sh

if [ ! -f "$STARTER_FILE" ]; then
  echo "cd $folder" > $STARTER_FILE
  echo "./filerenamer.sh" >> $STARTER_FILE
  chmod +x $STARTER_FILE
fi

if [ ! -f "$DESKTOP_FILE" ]; then
  cp ./filerenamer.desktop $HOME/.local/share/applications
  echo "Icon=$folder/icon.png" >> $HOME/.local/share/applications/filerenamer.desktop
  echo "Exec=$folder/run_filerenamer.sh" >> $HOME/.local/share/applications/filerenamer.desktop
fi
