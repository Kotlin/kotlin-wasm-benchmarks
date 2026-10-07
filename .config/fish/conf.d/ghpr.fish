function ghpr
    set -l gh_pr_args "--author" "@me"
    set -l query

    for arg in $argv
        switch $arg
            case '--all'
                set -e gh_pr_args
            case '*'
                if set -q query[1]
                    echo "ghpr: expected only one query" >&2
                    return 2
                end
                set query "$arg"
        end
    end

    if set -q query[1]
        set -a gh_pr_args --search "$query"
    end

    set -l selected (gh pr list $gh_pr_args --json number,title --jq '.[] | [.number, .title] | @tsv' | fzf --delimiter='\t' --with-nth=2..)

    if test -z "$selected"
        return
    end

    set -l pr_number (string split -f1 \t -- $selected)
    gh pr checkout $pr_number
end
