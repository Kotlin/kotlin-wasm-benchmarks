{
  description = "Example nix-darwin system flake";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    nix-darwin.url = "github:nix-darwin/nix-darwin/master";
    nix-darwin.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = inputs@{ self, nix-darwin, nixpkgs }:
  let
    configuration = { pkgs, ... }: {
      # List packages installed in system profile. To search by name, run:
      # $ nix-env -qaP | grep wget
      environment.systemPackages =
        [
          (pkgs.callPackage ./packages/xcodes/package.nix {})
          pkgs.bat
          pkgs.bytecode-viewer
          pkgs.fzf
          pkgs.git
          pkgs.github-cli
          pkgs.jdk8
          pkgs.jdk17
          pkgs.jdk21
          pkgs.micro
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
      '';

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
