#!/bin/bash
set -e

echo "(1/3) Run unit tests"
flutter test

echo "(2/3) Build release executable"
flutter build web

echo "(3/3) Install resources in server"
rm -rf ../src/main/resources/static
cp -R build/web/. ../src/main/resources/static

echo "Done!"
