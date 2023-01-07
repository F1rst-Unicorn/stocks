#!/bin/bash

set -ex

STOCKS_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/../.."

cd $STOCKS_ROOT/manual/development

for file in $(ls -1 diagrams/*.plantuml) ; do

    if [[ -f ${file%.plantuml}.png ]] ; then
        continue
    fi

    if [[ "$file" = "diagrams/header.plantuml" ]] ; then
        continue
    fi

    plantuml -config diagrams/header.plantuml $file

done

pdflatex spec.tex
bibtex spec.aux
pdflatex spec.tex
pdflatex spec.tex

