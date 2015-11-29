rm -rf deploy
xcopy /s .\worlds .\deploy\worlds\
cd ./thesis_build_root
./gradlew deployLocal
cd ..
