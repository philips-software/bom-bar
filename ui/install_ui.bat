echo "(1/4) Static code check"
flutter analyze

echo "(1/4) Run unit tests"
dart run build_runner build
flutter test

echo "(2/4) Build release executable"
flutter build web

echo "(3/4) Install resources in server"
rmdir ../src/main/resources/static
copy build/web/. ../src/main/resources/static

echo "Done!"
