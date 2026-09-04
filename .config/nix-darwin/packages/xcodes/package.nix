{
  lib,
  stdenvNoCC,
  fetchurl,
  unzip,
}:

stdenvNoCC.mkDerivation {
  pname = "xcodes";
  version = "2.0.3";

  src = fetchurl {
    url = "https://github.com/XcodesOrg/xcodes/releases/download/2.0.3/xcodes.zip";
    hash = "sha256-nMszmNxyrKFxdLcF7hZXxpdH/sJWY/yF0t/7hwL/N6o=";
  };

  nativeBuildInputs = [ unzip ];

  sourceRoot = ".";

  installPhase = ''
    runHook preInstall

    mkdir -p "$out/bin"
    install -m755 xcodes "$out/bin/xcodes"

    runHook postInstall
  '';

  meta = {
    description = "Manage the Xcode versions installed on your Mac";
    homepage = "https://github.com/XcodesOrg/xcodes";
    platforms = lib.platforms.darwin;
    mainProgram = "xcodes";
  };
}