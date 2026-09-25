{
  description = "The nix-darwin system flake I use at work in JetBrains";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    nix-darwin.url = "github:nix-darwin/nix-darwin/master";
    nix-darwin.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = inputs@{ self, nix-darwin, nixpkgs }:
  let
    configuration = { pkgs, lib, ... }: {
      # List packages installed in system profile. To search by name, run:
      # $ nix-env -qaP | grep wget
      environment.systemPackages =
        [
          (pkgs.callPackage ./packages/xcodes/package.nix {})
          pkgs.bat
          pkgs.bytecode-viewer
          pkgs.clang-tools
          pkgs.fd
          pkgs.fzf
          pkgs.git
          pkgs.github-cli
          pkgs.jdk8
          pkgs.jdk17
          pkgs.jdk21
          (lib.hiPrio pkgs.jdk25)
          pkgs.micro
          pkgs.miller
          pkgs.mosh
          pkgs.ripgrep
        ];

      environment.variables = {
        EDITOR = "micro";
      };

      # Necessary for using flakes on this system.
      nix.settings.experimental-features = "nix-command flakes";

      # Enable alternative shell support in nix-darwin.
      programs.fish.enable = true;

      # Fonts
      fonts.packages = with pkgs; [
        jetbrains-mono
      ];

      # Set Git commit hash for darwin-version.
      system.configurationRevision = self.rev or self.dirtyRev or null;

      # Used for backwards compatibility, please read the changelog before changing.
      # $ darwin-rebuild changelog
      system.stateVersion = 6;

      # The platform the configuration will be used on.
      nixpkgs.hostPlatform = "aarch64-darwin";

      # sudo with Touch ID
      security.pam.services.sudo_local.touchIdAuth = true;

      system.activationScripts.extraActivation.text = ''
        ln -sf "${pkgs.jdk8}/Library/Java/JavaVirtualMachines/zulu-8.jdk" "/Library/Java/JavaVirtualMachines/"
        ln -sf "${pkgs.jdk17}/Library/Java/JavaVirtualMachines/zulu-17.jdk" "/Library/Java/JavaVirtualMachines/"
        ln -sf "${pkgs.jdk21}/Library/Java/JavaVirtualMachines/zulu-21.jdk" "/Library/Java/JavaVirtualMachines/"

        # Make these declarative once nix-darwin supports -currentHost: https://github.com/nix-darwin/nix-darwin/issues/1721
        # Trackpad
        defaults -currentHost write NSGlobalDomain com.apple.mouse.tapBehavior -bool true
        defaults -currentHost write NSGlobalDomain com.apple.trackpad.enableSecondaryClick -bool true
        defaults -currentHost write NSGlobalDomain com.apple.trackpad.threeFingerDragGesture -bool true

        # Following line is to apply user defaults without a logout/login cycle
        /System/Library/PrivateFrameworks/SystemAdministration.framework/Resources/activateSettings -u
      '';

      system.defaults = {
        NSGlobalDomain = {
          AppleKeyboardUIMode = 2;
          NSNavPanelExpandedStateForSaveMode = true;
          NSNavPanelExpandedStateForSaveMode2 = true;
        };

        dock = {
          autohide = true;
          autohide-delay = 0.0;
          autohide-time-modifier = 0.2;
          expose-animation-duration = 0.2;tilesize = 48;
          launchanim = false;
          static-only = false;
          showhidden = false;
          show-recents = true;
          show-process-indicators = true;
          orientation = "bottom";
          mru-spaces = false;
          # mouse in top right corner will (5) start screensaver
          wvous-tr-corner = 5;
        };

        finder = {
          AppleShowAllExtensions = true;
          FXDefaultSearchScope = "SCcf";
          FXPreferredViewStyle = "Nlsv";
          _FXSortFoldersFirst = true;
        };

        screensaver = {
          # hasn't been working since macOS 13: https://github.com/nix-darwin/nix-darwin/issues/908
        };

        trackpad = {
          # Use once nix-darwin starts doing the right thing (https://github.com/nix-darwin/nix-darwin/issues/1721)
        };

        CustomUserPreferences = {
          "com.apple.desktopservices" = {
            # Avoid creating .DS_Store files on network or USB volumes
            DSDontWriteNetworkStores = true;
            DSDontWriteUSBStores = true;
          };

          "com.apple.dt.Xcode" = {
            DVTTextEditorTrimTrailingWhitespace = true;
            DVTTextEditorTrimWhitespaceOnlyLines = true;
          };

          "com.apple.Safari" = {
            AutoOpenSafeDownloads = false;
          };
        };
      };

      system.primaryUser = "nikolai";
      users.knownUsers = [ "nikolai" ];
      users.users.nikolai = {
        uid = 501;
        shell = pkgs.fish;
      };
    };
  in
  {
    # Build darwin flake using:
    # $ darwin-rebuild build --flake .#DE-UNIT-2458
    darwinConfigurations."DE-UNIT-2458" = nix-darwin.lib.darwinSystem {
      modules = [ configuration ];
    };
  };
}
