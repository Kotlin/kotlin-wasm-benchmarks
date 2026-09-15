{
  lib,
  stdenvNoCC,
  fetchurl,
  unzip,
}:

stdenvNoCC.mkDerivation {
  pname = "xcodes";
  version = "2.1.0";

  src = fetchurl {
    url = "https://github.com/XcodesOrg/xcodes/releases/download/2.1.0/xcodes.zip";
    hash = "sha256-8VGa/pNKUT6F3Zsy/IcjlL7Lu2pB2xXZrDkmoJqJGIg=";
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
