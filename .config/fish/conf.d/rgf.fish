function rgf
    set -l query
    set -l rg_args --line-number --no-heading

    for arg in $argv
        switch $arg
            case '--no-ignore'
                set -a rg_args --no-ignore
            case '*'
                if set -q query[1]
                    echo "rgf: expected only one query" >&2
                    return 2
                end
                set query "$arg"
        end
    end

    if test -z "$query"
        echo "rgf: query argument is required" >&2
        echo "Usage: rgf [--no-ignore] <query>" >&2
        return 2
    end

    rg $rg_args "$query" | fzf --delimiter : --preview 'bat --style=numbers --color=always --highlight-line {2} {1}' --preview-window=right:60%:+{2}/2
end
