@echo off

echo "(1/5) Generate mocks"
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs

echo "(2/5) Static code check"
flutter analyze

echo "(3/5) Run unit tests"
flutter test

echo "(4/5) Build release executable"
flutter build web

echo "(5/5) Install resources in server"
rmdir ..\src\main\resources\static\* /s /q
xcopy build\web\. ..\src\main\resources\static /s /q

echo "Done!"
