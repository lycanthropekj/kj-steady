#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_BUILD_TOOLS:?Set ANDROID_BUILD_TOOLS to Android SDK build-tools/35.0.0}"
: "${ANDROID_PLATFORM_JAR:?Set ANDROID_PLATFORM_JAR to platforms/android-35/android.jar}"
mkdir -p build/classes build/dex
"$ANDROID_BUILD_TOOLS/aapt2" compile --dir res -o build/resources.zip
"$ANDROID_BUILD_TOOLS/aapt2" link -I "$ANDROID_PLATFORM_JAR" --manifest AndroidManifest.xml -A assets -o build/base.apk build/resources.zip
java com.sun.tools.javac.Main -source 8 -target 8 -classpath "$ANDROID_PLATFORM_JAR" -d build/classes src/com/kj/steady/MainActivity.java
python3 - <<'PY'
import zipfile,pathlib
with zipfile.ZipFile('build/classes.jar','w') as z:
 for p in pathlib.Path('build/classes').rglob('*.class'): z.write(p,str(p.relative_to('build/classes')))
PY
"$ANDROID_BUILD_TOOLS/d8" --lib "$ANDROID_PLATFORM_JAR" --min-api 26 --output build/dex build/classes.jar
cp build/base.apk build/unsigned.apk
(cd build/dex && zip -q ../unsigned.apk classes.dex)
"$ANDROID_BUILD_TOOLS/zipalign" -f -p 4 build/unsigned.apk build/aligned.apk
if [ ! -f signing/kj-steady.jks ]; then
 mkdir -p signing
 keytool -genkeypair -keystore signing/kj-steady.jks -storepass android -keypass android -alias kj-steady -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=KJ Steady Personal Build" -noprompt
fi
"$ANDROID_BUILD_TOOLS/apksigner" sign --ks signing/kj-steady.jks --ks-key-alias kj-steady --ks-pass pass:android --key-pass pass:android --out build/KJ-Steady-v1.0.apk build/aligned.apk
"$ANDROID_BUILD_TOOLS/apksigner" verify --verbose build/KJ-Steady-v1.0.apk
