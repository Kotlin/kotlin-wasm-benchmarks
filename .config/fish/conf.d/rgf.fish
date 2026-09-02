function rgf
    set -l query ""
    set -l rg_args --line-number --no-heading

    # Parse arguments
    for arg in $argv
        switch $arg
            case '--no-ignore'
                set -a rg_args --no-ignore
            case '*'
                if test -z "$query"
                    set query "$arg"
                else
                    echo "Error: Multiple queries provided. Only one query is allowed."
                    return 1
                end
        end
    end

    # Check if query is provided
    if test -z "$query"
        echo "Error: Query argument is required."
        echo "Usage: rgf [--no-ignore] <query>"
        return 1
    end

    # Build and execute the command
    rg $rg_args "$query" | fzf --delimiter : --preview 'bat --style=numbers --color=always --highlight-line {2} {1}' --preview-window=right:60%:+{2}/2
end