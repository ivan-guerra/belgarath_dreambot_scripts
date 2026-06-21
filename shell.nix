# Creates an FHS-compatible environment for running the DreamBot client on
# NixOS. Provides all required system libraries (X11, OpenGL, GTK, etc.) and
# automatically downloads and launches the DreamBot Launcher.jar if not already
# present. Tested on NixOS 26.05.
{ pkgs ? import <nixpkgs> {} }:

(pkgs.buildFHSEnv {
  name = "dreambot-env";
  targetPkgs = pkgs: with pkgs; [
    openjdk17
    # X11
    libX11
    libXcomposite
    libXcursor
    libXdamage
    libXext
    libXfixes
    libXi
    libXrandr
    libXrender
    libXtst
    libXxf86vm
    libxcb
    libxshmfence
    # Graphics
    libGL
    libGLU
    mesa
    libdrm
    libxkbcommon
    # GTK / GLib
    gtk3
    gtk2
    libGL
    libGLU
    mesa
    libdrm
    libgbm
    glib
    cairo
    pango
    atk
    at-spi2-atk
    at-spi2-core
    # System
    nss
    nspr
    alsa-lib
    cups
    dbus
    expat
    udev
    zlib
    # Fonts
    fontconfig
    freetype
    # Tools
    wget
  ];
  runScript = pkgs.writeScript "dreambot-launcher" ''
    #!/bin/bash
    if [ ! -f Launcher.jar ]; then
      echo "Downloading DreamBot launcher..."
      wget -q https://downloads.dreambot.org/launcher/Launcher.jar
    fi
    exec java -jar Launcher.jar
  '';
}).env
